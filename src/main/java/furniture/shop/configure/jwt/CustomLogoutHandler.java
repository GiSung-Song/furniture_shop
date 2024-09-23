package furniture.shop.configure.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLogoutHandler implements LogoutHandler {

    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String accessToken = tokenProvider.resolveToken(request);

        Authentication auth = tokenProvider.getAuthentication(accessToken);

        if (accessToken == null || !tokenProvider.validateToken(accessToken)) {
            throw new RuntimeException("access token not valid error");
        }

        if (redisTemplate.opsForValue().get(auth.getName()) != null) {
            redisTemplate.delete(auth.getName());
        }

        Long expiration = tokenProvider.getExpiration(accessToken);

        redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
    }
}
