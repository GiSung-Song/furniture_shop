package furniture.shop.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "장바구니 상품 추가 Request DTO")
public class CartProductAddDto {

    @Schema(description = "상품 ID")
    private Long productId;

    @Min(value = 1, message = "최소 1이상 입력해주세요.")
    @Schema(description = "수량")
    private int count;
}
