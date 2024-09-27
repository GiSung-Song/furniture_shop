package furniture.shop.configure.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {

    private T result;

    private int resultCode;

    private String message;

    @Builder
    public ApiResponse(final T result, final int resultCode, final String message) {
        this.result = result;
        this.resultCode = resultCode;
        this.message = message;
    }

    @Builder
    public ApiResponse(final int resultCode, final String message) {
        this.result = null;
        this.resultCode = resultCode;
        this.message = message;
    }

    public static <T> ApiResponse<T> res(final HttpStatus status, final String resultMsg) {
        return res(status, resultMsg, null);
    }

    public static <T> ApiResponse<T> res(final HttpStatus status, final String message, final T result) {
        return ApiResponse.<T>builder()
                .result(result)
                .resultCode(status.value())
                .message(message)
                .build();

    }

}
