package furniture.shop.configure.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {

    private T result;

    private int resultCode;

    private String resultMsg;

    @Builder
    public ApiResponse(final T result, final int resultCode, final String resultMsg) {
        this.result = result;
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    @Builder
    public ApiResponse(final int resultCode, final String resultMsg) {
        this.result = null;
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    public static <T> ApiResponse<T> res(final HttpStatus status, final String resultMsg) {
        return res(status, resultMsg, null);
    }

    public static <T> ApiResponse<T> res(final HttpStatus status, final String resultMsg, final T result) {
        return ApiResponse.<T>builder()
                .result(result)
                .resultCode(status.value())
                .resultMsg(resultMsg)
                .build();

    }

}
