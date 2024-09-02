package furniture.shop.product.dto;

import furniture.shop.configure.valid.EnumValue;
import furniture.shop.product.constant.ProductCategory;
import furniture.shop.product.constant.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "상품등록 Request DTO")
public class ProductRegisterDto {

    @Schema(description = "상품코드")
    @NotBlank(message = "상품코드를 입력해주세요.")
    @Size(max = 15)
    private String productCode;

    @Schema(description = "상품명")
    @NotBlank(message = "상품명을 입력해주세요.")
    private String productName;

    @Schema(description = "카테고리", example = "CHAIR, TABLE, CLOSET ...")
    @NotBlank(message = "카테고리를 입력해주세요.")
    @EnumValue(enumClass = ProductCategory.class, message = "CHAIR, TABLE, CLOSET, BED 중 입력해주세요.")
    private String productCategory;

    @NotBlank(message = "상품상태를 입력해주세요.")
    @Schema(description = "상품상태", example = "SELLING, READY ...")
    @EnumValue(enumClass = ProductStatus.class, message = "SELLING, READY, STOP 중 입력해주세요.")
    private String productStatus;

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
    @NotBlank(message = "상품 설명을 입력해주세요.")
    private String description;
}
