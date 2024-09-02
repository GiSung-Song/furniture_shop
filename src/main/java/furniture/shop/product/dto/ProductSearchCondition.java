package furniture.shop.product.dto;

import furniture.shop.product.constant.ProductCategory;
import furniture.shop.product.constant.ProductStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductSearchCondition {

    private String productCode;
    private String productName;
    private ProductCategory productCategory;
    private ProductStatus productStatus;
}
