package furniture.shop.configure.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomExceptionCode {

    JOIN_DUPLICATE_EXCEPTION(HttpStatus.CONFLICT, "이미 등록된 이메일입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
