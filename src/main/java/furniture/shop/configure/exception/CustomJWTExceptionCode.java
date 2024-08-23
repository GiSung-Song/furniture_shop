package furniture.shop.configure.exception;

import lombok.Getter;

@Getter
public enum CustomJWTExceptionCode {

    JWT_SIGN_ERROR(1111, "잘못된 JWT 서명입니다."),
    JWT_EXPIRED_ERROR(2222, "만료된 JWT 토큰입니다."),
    JWT_UNSUPPORTED_ERROR(3333, "지원되지 않는 JWT 토큰입니다."),
    JWT_INVALID_ERROR(4444, "잘못된 JWT 토큰입니다."),
    JWT_UNKNOWN_ERROR(5555, "알 수 없는 JWT 토큰입니다."),
    ACCESS_DENIED_ERROR(6666, "접근권한이 없습니다."),
    ;

    CustomJWTExceptionCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;

}
