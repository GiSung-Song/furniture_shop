package furniture.shop.product;

import furniture.shop.configure.response.ApiResponse;
import furniture.shop.product.constant.ProductCategory;
import furniture.shop.product.constant.ProductStatus;
import furniture.shop.product.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Product API", description = "Product API")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/product")
    @Operation(summary = "상품 조회", description = "상품 조회 (조건 가능) API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품을 조회했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 입력입니다.", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponse<Page<ProductListDto>>> getProductList(
            @RequestParam(value = "productCode"    , required = false, defaultValue = "") String productCode,
            @RequestParam(value = "productName"    , required = false, defaultValue = "") String productName,
            @RequestParam(value = "productCategory", required = false, defaultValue = "") String productCategory,
            @RequestParam(value = "productStatus"  , required = false, defaultValue = "") String productStatus,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {

        ProductSearchCondition condition = new ProductSearchCondition();
        condition.setProductCode(productCode);
        condition.setProductName(productName);

        if (EnumUtils.isValidEnum(ProductCategory.class, productCategory.toUpperCase())) {
            condition.setProductCategory(ProductCategory.valueOf(productCategory));
        } else {
            condition.setProductCategory(null);
        }

        if (EnumUtils.isValidEnum(ProductStatus.class, productStatus.toUpperCase())) {
            condition.setProductStatus(ProductStatus.valueOf(productStatus));
        } else {
            condition.setProductStatus(null);
        }

        Page<ProductListDto> productList = productService.getProductList(condition, pageable);

        return ResponseEntity.ok(ApiResponse.res(HttpStatus.OK, "상품을 조회했습니다.", productList));
    }

    @PostMapping("/product")
    @Operation(summary = "상품 등록", description = "상품 등록 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품을 등록했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 입력입니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "권한이 없습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 등록된 상품입니다.", content = @Content(mediaType = "application/json"))
    })
    @Parameters(value = {
            @Parameter(name = "productCode", description = "상품 코드", example = "test-1234"),
            @Parameter(name = "productName", description = "상품명", example = "테스트 상품"),
            @Parameter(name = "productCategory", description = "상품 카테고리", example = "CHAIR"),
            @Parameter(name = "productStatus", description = "상품 상태", example = "SELLING"),
            @Parameter(name = "stock", description = "재고", example = "13"),
            @Parameter(name = "price", description = "가격", example = "103000"),
            @Parameter(name = "width", description = "길이", example = "10.5"),
            @Parameter(name = "height", description = "높이", example = "30.2"),
            @Parameter(name = "length", description = "너비", example = "17.9"),
            @Parameter(name = "description", description = "상품 설명", example = "테스트 상품입니다.")
    })
    public ResponseEntity<ApiResponse<String>> registerProduct(@Valid @RequestBody ProductRegisterDto registerDto) {
        productService.registerProduct(registerDto);

        return ResponseEntity.ok(ApiResponse.res(HttpStatus.OK, "상품을 등록했습니다."));
    }

    @GetMapping("/product/{id}")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품을 조회했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 입력입니다.", content = @Content(mediaType = "application/json")),
    })
    @Operation(summary = "상품 상세 조회", description = "상품 상세 조회 API")
    public ResponseEntity<ApiResponse<ProductDetailDto>> getProductDetail(@PathVariable("id") Long productId) {
        ProductDetailDto productDetail = productService.getProductDetail(productId);

        return ResponseEntity.ok(ApiResponse.res(HttpStatus.OK, "상품을 조회했습니다.", productDetail));
    }

    @PatchMapping("/product/{id}")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품을 수정했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 입력입니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "권한이 없습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content(mediaType = "application/json"))
    })
    @Operation(summary = "상품 수정", description = "상품 수정 API")
    @Parameters(value = {
            @Parameter(name = "productStatus", description = "상품 상태", example = "SELLING"),
            @Parameter(name = "stock", description = "재고", example = "13"),
            @Parameter(name = "price", description = "가격", example = "103000"),
            @Parameter(name = "width", description = "길이", example = "10.5"),
            @Parameter(name = "height", description = "높이", example = "30.2"),
            @Parameter(name = "length", description = "너비", example = "17.9"),
            @Parameter(name = "description", description = "상품 설명", example = "테스트 상품입니다.")
    })
    public ResponseEntity<ApiResponse<ProductDetailDto>> updateProduct(@PathVariable("id") Long productId, @RequestBody ProductUpdateDto productUpdateDto) {
        ProductDetailDto productDetailDto = productService.updateProduct(productId, productUpdateDto);

        return ResponseEntity.ok(ApiResponse.res(HttpStatus.OK, "상품을 수정했습니다.", productDetailDto));
    }


}
