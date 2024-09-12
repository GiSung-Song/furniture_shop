package furniture.shop.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "주문 상품 Response Dto")
public class OrderProductResponseDto {

    @Schema(description = "주문 상품 ID")
    private Long orderProductId;

    @Schema(description = "상품 ID")
    private Long productId;

    @Schema(description = "상품 코드")
    private String productCode;

    @Schema(description = "상품명")
    private String productName;

    @Schema(description = "가격")
    private int price;

    @Schema(description = "개수")
    private int count;

    @Schema(description = "총 가격")
    private int totalPrice;

}
