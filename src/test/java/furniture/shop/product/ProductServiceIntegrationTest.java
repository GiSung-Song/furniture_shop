package furniture.shop.product;

import furniture.shop.configure.exception.CustomException;
import furniture.shop.configure.exception.CustomExceptionCode;
import furniture.shop.product.constant.ProductCategory;
import furniture.shop.product.constant.ProductStatus;
import furniture.shop.product.dto.*;
import furniture.shop.product.embed.ProductSize;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductQueryRepository productQueryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("상품 목록 조회 - 전체 조회")
    void 상품_목록_조회_전체() {
        Product product1 = Product.builder()
                .productCode("code-1111")
                .productName("product1111")
                .productCategory(ProductCategory.CHAIR)
                .stock(100)
                .price(100000)
                .size(new ProductSize(10.5, 10.2, 10.4))
                .description("테스트1111 상품입니다.")
                .build();

        Product product2 = Product.builder()
                .productCode("code-2222")
                .productName("product2222")
                .productCategory(ProductCategory.BED)
                .stock(5)
                .price(2460000)
                .size(new ProductSize(150.3, 220.7, 30.6))
                .description("테스트2222 상품입니다.")
                .build();

        productRepository.save(product1);
        productRepository.save(product2);

        entityManager.flush();
        entityManager.clear();

        ProductSearchCondition condition = new ProductSearchCondition();
        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductListDto> productList = productService.getProductList(condition, pageable);

        assertEquals(2, productList.getTotalElements());
        assertEquals("product1111", productList.getContent().get(0).getProductName());
        assertEquals("product2222", productList.getContent().get(1).getProductName());
    }

    @Test
    @DisplayName("상품 목록 조회 - 이름 조건")
    void 상품_목록_조회_이름_조건() {
        Product product1 = Product.builder()
                .productCode("code-1111")
                .productName("product1111")
                .productCategory(ProductCategory.CHAIR)
                .stock(100)
                .price(100000)
                .size(new ProductSize(10.5, 10.2, 10.4))
                .description("테스트1111 상품입니다.")
                .build();

        Product product2 = Product.builder()
                .productCode("code-2222")
                .productName("product2222")
                .productCategory(ProductCategory.BED)
                .stock(5)
                .price(2460000)
                .size(new ProductSize(150.3, 220.7, 30.6))
                .description("테스트2222 상품입니다.")
                .build();

        productRepository.save(product1);
        productRepository.save(product2);

        entityManager.flush();
        entityManager.clear();

        ProductSearchCondition condition = new ProductSearchCondition();
        condition.setProductName("product");
        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductListDto> productList = productService.getProductList(condition, pageable);

        assertEquals(2, productList.getTotalElements());
        assertEquals("product1111", productList.getContent().get(0).getProductName());
        assertEquals("product2222", productList.getContent().get(1).getProductName());
    }

    @Test
    @DisplayName("상품 목록 조회 - 상태 조건")
    void 상품_목록_조회_상태_조건() {
        Product product1 = Product.builder()
                .productCode("code-1111")
                .productName("product1111")
                .productCategory(ProductCategory.CHAIR)
                .stock(100)
                .price(100000)
                .size(new ProductSize(10.5, 10.2, 10.4))
                .description("테스트1111 상품입니다.")
                .productStatus(ProductStatus.READY)
                .build();

        Product product2 = Product.builder()
                .productCode("code-2222")
                .productName("product2222")
                .productCategory(ProductCategory.BED)
                .stock(5)
                .price(2460000)
                .size(new ProductSize(150.3, 220.7, 30.6))
                .description("테스트2222 상품입니다.")
                .build();

        productRepository.save(product1);
        productRepository.save(product2);

        entityManager.flush();
        entityManager.clear();

        ProductSearchCondition condition = new ProductSearchCondition();
        condition.setProductStatus(ProductStatus.SELLING);
        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductListDto> productList = productService.getProductList(condition, pageable);

        assertEquals(1, productList.getTotalElements());
        assertEquals("product2222", productList.getContent().get(0).getProductName());
    }

    @Test
    @DisplayName("상품 목록 조회 - 상태 + 카테고리 조건")
    void 상품_목록_조회_상태_카테고리() {
        Product product1 = Product.builder()
                .productCode("code-1111")
                .productName("product1111")
                .productCategory(ProductCategory.CHAIR)
                .stock(100)
                .price(100000)
                .size(new ProductSize(10.5, 10.2, 10.4))
                .description("테스트1111 상품입니다.")
                .productStatus(ProductStatus.READY)
                .build();

        Product product2 = Product.builder()
                .productCode("code-2222")
                .productName("product2222")
                .productCategory(ProductCategory.BED)
                .stock(5)
                .price(2460000)
                .size(new ProductSize(150.3, 220.7, 30.6))
                .description("테스트2222 상품입니다.")
                .build();

        productRepository.save(product1);
        productRepository.save(product2);

        entityManager.flush();
        entityManager.clear();

        ProductSearchCondition condition = new ProductSearchCondition();
        condition.setProductStatus(ProductStatus.SELLING);
        condition.setProductCategory(ProductCategory.BED);
        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductListDto> productList = productService.getProductList(condition, pageable);

        assertEquals(1, productList.getTotalElements());
        assertEquals("product2222", productList.getContent().get(0).getProductName());
    }

    @Test
    @DisplayName("상품 목록 조회 - 결과 없음")
    void 상품_목록_조회_결과_없음() {
        Product product1 = Product.builder()
                .productCode("code-1111")
                .productName("product1111")
                .productCategory(ProductCategory.CHAIR)
                .stock(100)
                .price(100000)
                .size(new ProductSize(10.5, 10.2, 10.4))
                .description("테스트1111 상품입니다.")
                .productStatus(ProductStatus.READY)
                .build();

        Product product2 = Product.builder()
                .productCode("code-2222")
                .productName("product2222")
                .productCategory(ProductCategory.BED)
                .stock(5)
                .price(2460000)
                .size(new ProductSize(150.3, 220.7, 30.6))
                .description("테스트2222 상품입니다.")
                .build();

        productRepository.save(product1);
        productRepository.save(product2);

        entityManager.flush();
        entityManager.clear();

        ProductSearchCondition condition = new ProductSearchCondition();
        condition.setProductName("product1");
        condition.setProductCategory(ProductCategory.BED);
        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductListDto> productList = productService.getProductList(condition, pageable);

        assertEquals(0, productList.getTotalElements());
    }

    @Test
    @DisplayName("상품 등록 성공 테스트")
    void 상품_등록_성공_테스트() {
        ProductRegisterDto productRegisterDto = new ProductRegisterDto();

        productRegisterDto.setProductCode("chair-1111");
        productRegisterDto.setProductName("의자1111");
        productRegisterDto.setStock(35);
        productRegisterDto.setPrice(120000);
        productRegisterDto.setProductCategory(ProductCategory.CHAIR);
        productRegisterDto.setProductStatus(ProductStatus.SELLING);
        productRegisterDto.setDescription("의자1111 입니다.");
        productRegisterDto.setWidth(50.7);
        productRegisterDto.setLength(100.4);
        productRegisterDto.setHeight(73.56);

        Long savedId = productService.registerProduct(productRegisterDto);

        Product product = productRepository.findById(savedId).orElse(null);

        assertEquals(productRegisterDto.getProductCode(), product.getProductCode());
        assertEquals(productRegisterDto.getDescription(), product.getDescription());
    }

    @Test
    @DisplayName("상품 등록 실패 테스트")
    void 상품_등록_실패_테스트() {
        ProductRegisterDto productRegisterDto = new ProductRegisterDto();

        productRegisterDto.setProductCode("chair-1111");
        productRegisterDto.setProductName("의자1111");
        productRegisterDto.setStock(35);
        productRegisterDto.setPrice(120000);
        productRegisterDto.setProductCategory(ProductCategory.CHAIR);
        productRegisterDto.setProductStatus(ProductStatus.SELLING);
        productRegisterDto.setDescription("의자1111 입니다.");
        productRegisterDto.setWidth(50.7);
        productRegisterDto.setLength(100.4);
        productRegisterDto.setHeight(73.56);

        Product product = Product.builder()
                .productCode("chair-1111")
                .productName("의자t1111")
                .productCategory(ProductCategory.CHAIR)
                .stock(35)
                .price(120000)
                .size(new ProductSize(50.7, 100.4, 73.56))
                .description("의자1111 입니다.")
                .productStatus(ProductStatus.SELLING)
                .build();

        productRepository.save(product);

        entityManager.flush();
        entityManager.clear();

        CustomException customException = assertThrows(CustomException.class, () -> productService.registerProduct(productRegisterDto));
        assertEquals(CustomExceptionCode.CODE_DUPLICATE_EXCEPTION, customException.getCode());
    }

    @Test
    @DisplayName("상품 상세 조회 성공 테스트")
    void 상품_상세_조회_성공_테스트() {
        Product product = Product.builder()
                .productCode("chair-1111")
                .productName("의자-1111")
                .productCategory(ProductCategory.CHAIR)
                .stock(35)
                .price(120000)
                .size(new ProductSize(50.7, 100.4, 73.56))
                .description("의자1111 입니다.")
                .productStatus(ProductStatus.SELLING)
                .build();

        productRepository.save(product);

        entityManager.flush();
        entityManager.clear();

        ProductDetailDto productDetail = productService.getProductDetail(product.getId());

        assertEquals(product.getProductName(), productDetail.getProductName());
        assertEquals(product.getProductCategory(), productDetail.getCategory());
        assertEquals(product.getPrice(), productDetail.getPrice());
    }

    @Test
    @DisplayName("상품 상세 조회 실패 테스트")
    void 상품_상세_조회_실패_테스트() {
        CustomException customException = assertThrows(CustomException.class, () -> productService.getProductDetail(0L));

        assertEquals(CustomExceptionCode.NOT_VALID_ERROR, customException.getCode());
    }

    @Test
    @DisplayName("상품 수정 성공 테스트")
    void 상품_수정_성공_테스트() {
        Product product = Product.builder()
                .productCode("chair-1111")
                .productName("의자-1111")
                .productCategory(ProductCategory.CHAIR)
                .stock(35)
                .price(120000)
                .size(new ProductSize(50.7, 100.4, 73.56))
                .description("의자1111 입니다.")
                .productStatus(ProductStatus.SELLING)
                .build();

        productRepository.save(product);

        entityManager.flush();
        entityManager.clear();

        ProductUpdateDto productUpdateDto = new ProductUpdateDto();

        productUpdateDto.setDescription("판매 중단된 상품 입니다.");
        productUpdateDto.setProductStatus(ProductStatus.STOP);

        ProductDetailDto productDetailDto = productService.updateProduct(product.getId(), productUpdateDto);

        assertEquals(productUpdateDto.getDescription(), productDetailDto.getDescription());
        assertEquals(productUpdateDto.getProductStatus(), productDetailDto.getProductStatus());
    }

    @Test
    @DisplayName("상품 수정 실패 테스트")
    void 상품_수정_실패_테스트() {
        ProductUpdateDto productUpdateDto = new ProductUpdateDto();

        productUpdateDto.setDescription("판매 중단된 상품 입니다.");
        productUpdateDto.setProductStatus(ProductStatus.STOP);
        productUpdateDto.setStock(2500);
        productUpdateDto.setPrice(1000000);

        CustomException customException = assertThrows(CustomException.class, () -> productService.updateProduct(0L, productUpdateDto));

        assertEquals(CustomExceptionCode.NOT_VALID_ERROR, customException.getCode());
    }
}
