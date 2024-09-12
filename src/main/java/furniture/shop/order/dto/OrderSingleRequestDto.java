package furniture.shop.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "단품 주문 Request DTO")
public class OrderSingleRequestDto {

    @Schema(description = "상품 ID")
    private Long productId;

    @Schema(description = "개수")
    @Min(value = 1, message = "최소 1개 이상 입력해주세요.")
    private int count;
}
