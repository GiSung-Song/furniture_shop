package furniture.shop.configure.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final CustomExceptionCode code;

    public CustomException(CustomExceptionCode code) {
        this.code = code;
    }
}
