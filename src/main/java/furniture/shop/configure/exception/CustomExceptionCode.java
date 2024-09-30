package furniture.shop.configure.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomExceptionCode {

    JOIN_DUPLICATE_EXCEPTION(HttpStatus.CONFLICT, "이미 등록된 이메일입니다."),
    NOT_VALID_ERROR(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    NOT_VALID_LOGIN_ERROR(HttpStatus.BAD_REQUEST, "잘못된 아이디 혹은 비밀번호입니다."),
    CODE_DUPLICATE_EXCEPTION(HttpStatus.CONFLICT, "이미 등록된 상품코드입니다."),
    NOT_ENOUGH_PRODUCT_EXCEPTION(HttpStatus.BAD_REQUEST, "남은 수량보다 많은 수량을 주문 할 수 없습니다."),
    NOT_SELLING_PRODUCT_EXCEPTION(HttpStatus.BAD_REQUEST, "판매중인 상품이 아닙니다."),
    NOT_VALID_AUTH_ERROR(HttpStatus.FORBIDDEN, "해당 권한이 없습니다."),
    FAIL_PAYMENT(HttpStatus.BAD_REQUEST, "결제가 실패했습니다."),
    NOT_VALID_PAYMENT(HttpStatus.BAD_REQUEST, "잘못된 결제 요청입니다."),
    DELETE_FAIL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "삭제 실패 오류입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
