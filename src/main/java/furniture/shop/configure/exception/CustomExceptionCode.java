package furniture.shop.configure.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomExceptionCode {

    JOIN_DUPLICATE_EXCEPTION(HttpStatus.CONFLICT, "이미 등록된 이메일입니다."),
    NOT_VALID_ERROR(HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),
    NOT_VALID_LOGIN_ERROR(HttpStatus.BAD_REQUEST, "잘못된 아이디 혹은 비밀번호입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
