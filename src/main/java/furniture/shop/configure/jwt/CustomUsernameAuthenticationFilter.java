package furniture.shop.configure.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class CustomUsernameAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("======로그인 시도 START======");

        /* Rest API -> Web

        ObjectMapper om = new ObjectMapper();

        try {
            //1. email, password 받기
            Map<String, String> map = om.readValue(request.getInputStream(), Map.class);

            //principal : username, credential : password
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(map.get("email"), map.get("password"));

            //2. 정상적인 로그인 시도
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            //3. 로그인 성공 시
            UserDetails principal = (UserDetails) authentication.getPrincipal();

            return authentication;
        } catch (IOException e) {
            e.printStackTrace();
        }

         */

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        //principal : username, credential : password
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        //2. 정상적인 로그인 시도
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        //3. 로그인 성공 시
        UserDetails principal = (UserDetails) authentication.getPrincipal();

        log.info("======로그인 시도 E N D======");

        return authentication;
    }

    // attemptAuthentication() 실행 후 인증이 성공적으로 완료되면 실행
    // 로그인 성공 시 JWT 발급
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 -> JWT 발급 준비");
        log.info("authResult : {}", authResult);

        UserDetails principal = (UserDetails) authResult.getPrincipal();
        String email = principal.getUsername();

        String accessToken = tokenProvider.createToken(email);

        tokenProvider.sendAccessToken(response, accessToken);

        log.info("JWT 발급 완료");
        log.info("AccessToken : {}", accessToken);
    }
}
