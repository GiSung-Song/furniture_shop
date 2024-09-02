package furniture.shop.product.dto;

import furniture.shop.configure.valid.EnumValue;
import furniture.shop.product.constant.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "상품 수정 Request DTO")
public class ProductUpdateDto {

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
    private String description;

}
