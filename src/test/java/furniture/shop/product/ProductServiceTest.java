package furniture.shop.product;

import furniture.shop.configure.exception.CustomException;
import furniture.shop.configure.exception.CustomExceptionCode;
import furniture.shop.global.TestQueryDslConfig;
import furniture.shop.product.constant.ProductCategory;
import furniture.shop.product.constant.ProductStatus;
import furniture.shop.product.dto.*;
import furniture.shop.product.embed.ProductSize;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Import(TestQueryDslConfig.class)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductQueryRepository productQueryRepository;

    @Mock
    private ProductRepository productRepository;

    Product product;

    @Test
    @DisplayName("전체 조회 테스트")
    void 조회_테스트() {
        List<ProductListDto> productListDtoList = new ArrayList<>();

        for(int i = 0; i <= 10; i++) {
            ProductListDto productListDto = new ProductListDto(Long.valueOf(i), "name" + i, ProductStatus.SELLING, ProductCategory.CHAIR);

            productListDtoList.add(productListDto);
        }

        ProductSearchCondition productSearchCondition = new ProductSearchCondition();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<ProductListDto> pageListDto = new PageImpl<>(productListDtoList, pageable, 11);

        when(productQueryRepository.searchProductPage(productSearchCondition, pageable)).thenReturn(pageListDto);

        Page<ProductListDto> productList = productService.getProductList(productSearchCondition, pageable);

        Assertions.assertEquals(productList.getTotalElements(), pageListDto.getTotalElements());
        Assertions.assertEquals(productList.getTotalPages(), pageListDto.getTotalPages());
    }

    @Test
    @DisplayName("상품 저장 테스트")
    void 상품_저장_성공_테스트() {
        setUp();
        ProductRegisterDto dto = getRegisterDto();

        when(productRepository.findByProductCode(any())).thenReturn(null);
        when(productRepository.save(any())).thenReturn(product);

        Long savedId = productService.registerProduct(dto);
        Product savedProduct = productRepository.findById(savedId).get();

        Assertions.assertEquals(savedProduct.getProductName(), dto.getProductName());
        Assertions.assertEquals(savedProduct.getProductCode(), dto.getProductCode());
        Assertions.assertEquals(savedProduct.getDescription(), dto.getDescription());
    }

    @Test
    @DisplayName("상품 저장 실패 테스트")
    void 상품_저장_실패_테스트() {
        ProductRegisterDto dto = getRegisterDto();

        when(productRepository.findByProductCode(any())).thenThrow(CustomException.class);

        Assertions.assertThrows(CustomException.class, () -> productService.registerProduct(dto));
    }

    @Test
    @DisplayName("상품 조회 테스트")
    void 상품_조회_테스트() {
        setUp();

        ProductDetailDto productDetail = productService.getProductDetail(product.getId());

        Assertions.assertEquals(productDetail.getProductCode(), product.getProductCode());
        Assertions.assertEquals(productDetail.getProductName(), product.getProductName());
    }

    @Test
    @DisplayName("상품 조회 실패 테스트")
    void 상품_조회_실패_테스트() {
        when(productRepository.findById(any())).thenThrow(CustomException.class);

        Assertions.assertThrows(CustomException.class, () -> productService.getProductDetail(any()));
    }

    @Test
    @DisplayName("상품 수정 테스트")
    void 상품_수정_테스트() {
        setUp();

        ProductUpdateDto productUpdateDto = new ProductUpdateDto();
        productUpdateDto.setDescription("1234");

        ProductDetailDto productDetailDto = productService.updateProduct(0L, productUpdateDto);

        Assertions.assertEquals(productDetailDto.getDescription(), productUpdateDto.getDescription());
        Assertions.assertEquals(productDetailDto.getProductName(), product.getProductName());
    }

    @Test
    @DisplayName("상품 수정 실패 테스트")
    void 상품_수정_실패_테스트() {
        ProductUpdateDto productUpdateDto = new ProductUpdateDto();
        productUpdateDto.setDescription("1234");

        when(productRepository.findById(any())).thenThrow(new CustomException(CustomExceptionCode.NOT_VALID_ERROR));

        Assertions.assertThrows(CustomException.class, () -> productService.updateProduct(0L, productUpdateDto));
    }

    void setUp() {
        product = Product.builder()
                .productName("테스트 상품")
                .productCode("test-1234")
                .productCategory(ProductCategory.CHAIR)
                .productStatus(ProductStatus.SELLING)
                .size(new ProductSize(50.7, 102.5, 100.3))
                .price(102030)
                .stock(10)
                .description("테스트 상품입니다.")
                .build();

        when(productRepository.findById(any())).thenReturn(Optional.ofNullable(product));
    }

    ProductRegisterDto getRegisterDto() {
        ProductRegisterDto dto = new ProductRegisterDto();

        dto.setProductCode("test-1234");
        dto.setProductName("테스트 상품");
        dto.setProductCategory("CHAIR");
        dto.setProductStatus("SELLING");
        dto.setLength(102.5);
        dto.setHeight(100.3);
        dto.setWidth(50.7);
        dto.setPrice(102030);
        dto.setStock(10);
        dto.setDescription("테스트 상품입니다.");

        return dto;
    }

}