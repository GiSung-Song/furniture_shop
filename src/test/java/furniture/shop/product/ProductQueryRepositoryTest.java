package furniture.shop.product;

import furniture.shop.global.TestQueryDslConfig;
import furniture.shop.product.constant.ProductCategory;
import furniture.shop.product.constant.ProductStatus;
import furniture.shop.product.dto.ProductListDto;
import furniture.shop.product.dto.ProductSearchCondition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestQueryDslConfig.class)
@DataJpaTest
@ActiveProfiles("test")
public class ProductQueryRepositoryTest {

    @Autowired
    private ProductQueryRepository repository;

    @Autowired
    private ProductRepository productRepository;

    void setUp() {
        for (int i = 0; i <= 15; i++) {
            productRepository.save(Product.builder()
                    .productCategory(ProductCategory.CHAIR)
                    .productStatus(ProductStatus.SELLING)
                    .productName("name" + i)
                    .productCode("code" + i)
                    .description("description" + i + "description")
                    .build());
        }
    }

    @Test
    @DisplayName("조회 테스트")
    void 조회_테스트() {
        setUp();

        ProductSearchCondition condition = new ProductSearchCondition();

        Page<ProductListDto> productListDtos =
                repository.searchProductPage(condition, PageRequest.of(0, 10));

        Assertions.assertEquals(productListDtos.getTotalElements(), 16);
        Assertions.assertEquals(productListDtos.getTotalPages(), 2);
    }

}
