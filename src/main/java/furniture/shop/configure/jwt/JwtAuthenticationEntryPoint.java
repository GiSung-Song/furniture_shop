package furniture.shop.configure.jwt;

import furniture.shop.configure.exception.CustomJWTExceptionCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Integer exception = (Integer)request.getAttribute("exception");

        if (exception == null) {
            setResponse(response, CustomJWTExceptionCode.JWT_UNKNOWN_ERROR);
        } else if (exception == 1111) {
            setResponse(response, CustomJWTExceptionCode.JWT_SIGN_ERROR);
        } else if (exception == 2222) {
            setResponse(response, CustomJWTExceptionCode.JWT_EXPIRED_ERROR);
        } else if (exception == 3333) {
            setResponse(response, CustomJWTExceptionCode.JWT_UNSUPPORTED_ERROR);
        } else if (exception == 4444) {
            setResponse(response, CustomJWTExceptionCode.JWT_INVALID_ERROR);
        }
    }

    private void setResponse(HttpServletResponse response, CustomJWTExceptionCode code) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, String> messageBody = new HashMap<>();
        messageBody.put("code", String.valueOf(code.getCode()));
        messageBody.put("message", code.getMessage());

        response.getWriter().print(messageBody);
    }
}
