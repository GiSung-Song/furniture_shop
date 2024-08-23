package furniture.shop.configure.jwt;

import furniture.shop.configure.exception.CustomJWTExceptionCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    private TokenProvider tokenProvider;

    public JwtFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    //토큰의 인증정보를 SecurityContext에 저장하는 역할 수행
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().equals("/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = resolveToken(request);
        String requestURI = request.getRequestURI();

        try {
            if (StringUtils.hasText(jwtToken) && tokenProvider.validateToken(jwtToken)) {
                Authentication authentication = tokenProvider.getAuthentication(jwtToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("Security Context에 '{}' 인증 정보를 저장했습니다, URI : {}", authentication.getName(), requestURI);
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

    //Request Header에서 토큰 정보를 꺼내오기 위한 메서드
    private String resolveToken(HttpServletRequest request) {
        String barerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(barerToken) && barerToken.startsWith("Bearer ")) {
            return barerToken.substring(7);
        }

        return null;
    }

}
