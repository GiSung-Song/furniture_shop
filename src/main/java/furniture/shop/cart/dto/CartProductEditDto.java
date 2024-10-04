package furniture.shop.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "장바구니 상품 수정 DTO")
public class CartProductEditDto {

    @Schema(description = "상품 ID")
    private Long productId;

    @Schema(description = "개수")
    private int count;
}