package furniture.shop.credit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "환불 요청 Request DTO")
public class CreditRefundRequestDto {

    @Schema(description = "결제 고유 번호")
    @NotBlank
    private String impUID;

    @Schema(description = "환불 사유")
    private String reason;

    @Schema(description = "환불 은행")
    private String bank;

    @Schema(description = "환불 계좌 예금주")
    private String holder;

    @Schema(description = "환불 계좌")
    private String account;
}
