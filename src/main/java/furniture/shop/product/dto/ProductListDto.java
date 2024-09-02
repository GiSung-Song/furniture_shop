package furniture.shop.product.dto;

import com.querydsl.core.annotations.QueryProjection;
import furniture.shop.product.constant.ProductCategory;
import furniture.shop.product.constant.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "상품 목록 조회 Response DTO")
public class ProductListDto {

    @Schema(description = "상품 식별번호")
    private Long id;

    @Schema(description = "상품명")
    private String productName;

    @Schema(description = "상품 상태")
    private ProductStatus productStatus;

    @Schema(description = "카테고리")
    private ProductCategory productCategory;

    @QueryProjection
    public ProductListDto(Long id, String productName, ProductStatus productStatus, ProductCategory productCategory) {
        this.id = id;
        this.productName = productName;
        this.productStatus = productStatus;
        this.productCategory = productCategory;
    }
}
