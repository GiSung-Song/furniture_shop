package furniture.shop.product;

import furniture.shop.product.constant.ProductCategory;
import furniture.shop.product.embed.ProductSize;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    Product getProduct() {

        return Product.builder()
                .productCode("chair001")
                .productName("의자1")
                .category(ProductCategory.CHAIR)
                .size(new ProductSize(10, 10, 10))
                .description("테스트 설명입니다.")
                .build();
    }

    @Test
    @DisplayName("저장 테스트")
    void 저장_테스트() {
        Product product = getProduct();

        Product savedProduct = productRepository.save(product);

        Assertions.assertEquals(product.getProductName(), savedProduct.getProductName());
        Assertions.assertEquals(product.getProductCode(), savedProduct.getProductCode());
    }

    @Test
    @DisplayName("조회 테스트")
    void 조회_테스트() {
        Product product = getProduct();

        Product savedProduct = productRepository.save(product);

        Product findProduct = productRepository.findById(savedProduct.getId()).get();

        assertEquals(product.getProductCode(), findProduct.getProductCode());
        assertEquals(product.getProductName(), findProduct.getProductName());
    }
}