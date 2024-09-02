package furniture.shop.product.dto;

import furniture.shop.product.constant.ProductCategory;
import furniture.shop.product.constant.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "상품정보 Response DTO")
public class ProductDetailDto {

    @Schema(description = "상품코드")
    private String productCode;

    @Schema(description = "상품명")
    private String productName;

    @Schema(description = "카테고리")
    private ProductCategory category;

    @Schema(description = "상품상태")
    private ProductStatus productStatus;

    @Schema(description = "재고")
    private int stock;

    @Schema(description = "가격")
    private int price;

    @Schema(description = "길이")
    private double width;

    @Schema(description = "높이")
    private double height;

    @Schema(description = "너비")
    private double length;

    @Schema(description = "상품 설명")
    private String description;

    @Schema(description = "총 판매량")
    private Long sellingCount;
}
