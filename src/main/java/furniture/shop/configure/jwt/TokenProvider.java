package furniture.shop.configure.jwt;

import furniture.shop.configure.exception.CustomException;
import furniture.shop.configure.exception.CustomExceptionCode;
import furniture.shop.member.Member;
import furniture.shop.member.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
@Component
public class TokenProvider {

    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpiration;

    @Value("${jwt.access.header}")
    private String accessHeader;

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    //Authentication 객체의 권한정보를 이용해서 토큰을 생성
    public String createToken(Authentication authentication) {
        log.info("AccessToken 생성");

        Claims claims = Jwts.claims().setSubject(ACCESS_TOKEN_SUBJECT);
        claims.put(EMAIL_CLAIM, authentication.getName());

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(getSignKey())
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {
        log.info("RefreshToken 생성");

        Claims claims = Jwts.claims().setSubject(REFRESH_TOKEN_SUBJECT);
        claims.put(EMAIL_CLAIM, authentication.getName());

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + refreshTokenExpiration);

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(getSignKey())
                .compact();

        redisTemplate.opsForValue().set(
                authentication.getName(),
                refreshToken,
                refreshTokenExpiration,
                TimeUnit.MILLISECONDS
        );

        return refreshToken;
    }

    //Token에 담겨있는 정보를 이용해 Authentication 객체를 리턴하는 메서드
    public Authentication getAuthentication(String token) {
        log.info("인증 Authentication 가져오기");

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        String email = (String) claims.get("email");

        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            throw new CustomException(CustomExceptionCode.NOT_VALID_ERROR);
        }

        UserDetails userDetails = User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().name())
                .build();

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest request) {
        log.info("헤더에서 토큰 가져오기");

        String accessToken = request.getHeader(accessHeader);

        if (accessToken != null && accessToken.startsWith(TOKEN_PREFIX)) {
            return accessToken.substring(7);
        }

        return null;
    }

    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK); //200 성공
        response.addHeader(accessHeader, TOKEN_PREFIX + accessToken); //ex) AccessToken : BEARER fdjiaopjfdipoas
    }

    //Token Valid
    public boolean validateToken(String token) {
        log.info("유효한 토큰인지 check");

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }

        return false;
    }

    public boolean isExpiredAccessToken(String token) {
        log.info("만료된 토큰인지 체크");

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");

            return true;
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }

        return false;
    }

    public Long getExpiration(String accessToken) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getExpiration();

        Long now = new Date().getTime();

        return (expiration.getTime() - now);
    }
}
