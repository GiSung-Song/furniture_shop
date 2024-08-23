package furniture.shop.configure.exception;

import furniture.shop.configure.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Custom Exception
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<?> handleCustomException(CustomException e) {
        ErrorResponse errorResponse = ErrorResponse.of(e.getCode());

        return new ResponseEntity<>(errorResponse, e.getCode().getHttpStatus());
    }

    //Validation Exception @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = ErrorResponse.of(CustomExceptionCode.NOT_VALID_ERROR, e.getBindingResult());

        return new ResponseEntity<>(errorResponse, e.getStatusCode());
    }
}
