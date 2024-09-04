package furniture.shop.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "장바구니 상품 Response DTO")
public class CartProductDto {

    @Schema(description = "상품 코드")
    private String productCode;

    @Schema(description = "상품명")
    private String productName;

    @Schema(description = "개수")
    private int count;

    @Schema(description = "가격")
    private int price;
}
