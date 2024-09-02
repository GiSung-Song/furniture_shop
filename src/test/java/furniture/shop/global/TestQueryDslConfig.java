package furniture.shop.global;

import com.querydsl.jpa.impl.JPAQueryFactory;
import furniture.shop.product.ProductQueryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestQueryDslConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public ProductQueryRepository productQueryRepository() {
        return new ProductQueryRepository(jpaQueryFactory());
    }
}
