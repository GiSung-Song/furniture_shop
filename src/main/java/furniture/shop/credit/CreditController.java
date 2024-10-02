package furniture.shop.credit;

import furniture.shop.configure.response.ApiResponse;
import furniture.shop.credit.dto.CreditRefundRequestDto;
import furniture.shop.credit.dto.CreditRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    @PostMapping("/credit/{id}/complete")
    @Operation(summary = "결제 검증", description = "결제 검증 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결제가 완료되었습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 입력입니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "권한이 없습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponse<?>> doCreditComplete(@PathVariable("id") Long orderId, CreditRequestDto dto) {
        dto.setOrderId(orderId);

        creditService.createAndVerifyPayment(dto);

        return ResponseEntity.ok(ApiResponse.res(HttpStatus.OK, "결제가 완료되었습니다."));
    }

    @PostMapping("/refund/{impUID}")
    @Operation(summary = "환불 요청", description = "환불 요청 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "환불이 완료되었습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 입력입니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "권한이 없습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponse<?>> doRefund(@PathVariable("impUID") String impUID, CreditRefundRequestDto dto) {
        dto.setImpUID(impUID);

        creditService.cancelPayment(dto);

        return ResponseEntity.ok(ApiResponse.res(HttpStatus.OK, "환불이 완료되었습니다."));
    }

}
