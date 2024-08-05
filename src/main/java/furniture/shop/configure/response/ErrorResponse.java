package furniture.shop.configure.response;

import furniture.shop.configure.exception.CustomExceptionCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private int status;
    private String message;
    private List<FieldError> errors;
    private String reason;

    @Builder
    protected ErrorResponse(final CustomExceptionCode code) {
        this.status = code.getHttpStatus().value();
        this.message = code.getMessage();
        this.errors = new ArrayList<>();
    }

    @Builder
    protected ErrorResponse(final CustomExceptionCode code, final String reason) {
        this.message = code.getMessage();
        this.status = code.getHttpStatus().value();
        this.reason = reason;
    }

    @Builder
    protected ErrorResponse(final CustomExceptionCode code, final List<FieldError> errors) {
        this.message = code.getMessage();
        this.status = code.getHttpStatus().value();
        this.errors = errors;
    }

    public static ErrorResponse of(final CustomExceptionCode code, final BindingResult bindingResult) {
        return new ErrorResponse(code, FieldError.of(bindingResult));
    }

    public static ErrorResponse of(final CustomExceptionCode code) {
        return new ErrorResponse(code);
    }

    public static ErrorResponse of(final CustomExceptionCode code, final String reason) {
        return new ErrorResponse(code, reason);
    }

    @Getter
    public static class FieldError {

        private final String field;
        private final String value;
        private final String reason;

        public static List<FieldError> of(final String field, final String value, final String reason) {
            List<FieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(new FieldError(field, value, reason));
            return fieldErrors;
        }

        private static List<FieldError> of(final BindingResult bindingResult) {
            final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }

        @Builder
        FieldError(String field, String value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }
    }

}
