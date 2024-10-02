package furniture.shop.credit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "결제 요청 Request DTO")
public class CreditRequestDto {

    @Schema(description = "주문 번호")
    private Long orderId;

    @Schema(description = "결제 금액")
    private int amount;

    @Schema(description = "Iamport 고유 결제 번호")
    private String impUID;

    @Schema(description = "가맹점 주문 번호")
    private String merchantUID;

    @Schema(description = "결제 방법", example = "vbank, kakaopay, naverpay, samsungpay, card ...")
    private String payMethod;
}
