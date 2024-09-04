package furniture.shop.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "장바구니 Response DTO")
public class CartDto {

    @Schema(description = "장바구니 상품 리스트")
    private List<CartProductDto> cartProductDtoList;

    @Schema(description = "총 가격")
    private int totalPrice;
}
