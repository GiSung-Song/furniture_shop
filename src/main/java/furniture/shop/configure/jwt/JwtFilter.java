package furniture.shop.configure.jwt;

import furniture.shop.configure.exception.CustomJWTExceptionCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    private final RedisTemplate<String, String> redisTemplate;
    private final TokenProvider tokenProvider;

    //토큰의 인증정보를 SecurityContext에 저장하는 역할 수행
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().equals("/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = tokenProvider.resolveToken(request);
        String requestURI = request.getRequestURI();

        try {
            if (StringUtils.hasText(accessToken) && tokenProvider.validateToken(accessToken)) {
                String isLogout = redisTemplate.opsForValue().get(accessToken);

                // 로그아웃 하지 않은 경우
                if (!StringUtils.hasText(isLogout)) {
                    Authentication authentication = tokenProvider.getAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.info("Security Context에 '{}' 인증 정보를 저장했습니다, URI : {}", authentication.getName(), requestURI);
                }
            } else if (StringUtils.hasText(accessToken) && tokenProvider.isExpiredAccessToken(accessToken)) {
                log.info("AccessToken 만료되어 있는 경우 accessToken 재발급");

                String refreshToken = getRefreshToken(request);

                if (StringUtils.hasText(refreshToken) && tokenProvider.validateToken(refreshToken)) {
                    // refreshToken 유효하면 accessToken 재발급
                    Authentication authentication = tokenProvider.getAuthentication(refreshToken);

                    if (refreshToken.equals(redisTemplate.opsForValue().get(authentication.getName()))) {
                        log.info("AccessToken 재발급");

                        String newAccessToken = tokenProvider.createToken(authentication);
                        tokenProvider.sendAccessToken(response, newAccessToken);

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (SecurityException | MalformedJwtException e) {
            request.setAttribute("exception", CustomJWTExceptionCode.JWT_SIGN_ERROR.getCode());
        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", CustomJWTExceptionCode.JWT_EXPIRED_ERROR.getCode());
        } catch (UnsupportedJwtException e) {
            request.setAttribute("exception", CustomJWTExceptionCode.JWT_UNSUPPORTED_ERROR.getCode());
        } catch (IllegalArgumentException e) {
            request.setAttribute("exception", CustomJWTExceptionCode.JWT_INVALID_ERROR.getCode());
        } catch (Exception e) {
            request.setAttribute("exception", CustomJWTExceptionCode.JWT_UNKNOWN_ERROR.getCode());
        }

        filterChain.doFilter(request, response);
    }

    private String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

}
