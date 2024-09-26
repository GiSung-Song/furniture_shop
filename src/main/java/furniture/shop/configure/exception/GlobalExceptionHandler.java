package furniture.shop.configure.exception;

import com.siot.IamportRestClient.exception.IamportResponseException;
import furniture.shop.configure.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Custom Exception
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<?> handleCustomException(CustomException e) {
        ErrorResponse errorResponse = ErrorResponse.of(e.getCode());

        return new ResponseEntity<>(errorResponse, e.getCode().getHttpStatus());
    }

    //IamportResponseException Exception
    @ExceptionHandler(IamportResponseException.class)
    protected ResponseEntity<?> handleIamportResponseException(IamportResponseException e) {
        ErrorResponse errorResponse = ErrorResponse.of(CustomExceptionCode.FAIL_PAYMENT, e.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
    }

    //IOException Exception
    @ExceptionHandler(IOException.class)
    protected ResponseEntity<?> handleIOExcedption(IOException e) {
        ErrorResponse errorResponse = ErrorResponse.of(CustomExceptionCode.NOT_VALID_ERROR, e.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    //Validation Exception @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = ErrorResponse.of(CustomExceptionCode.NOT_VALID_ERROR, e.getBindingResult());

        return new ResponseEntity<>(errorResponse, e.getStatusCode());
    }
}
