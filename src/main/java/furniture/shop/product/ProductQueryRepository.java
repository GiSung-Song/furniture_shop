package furniture.shop.product;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import furniture.shop.product.constant.ProductCategory;
import furniture.shop.product.constant.ProductStatus;
import furniture.shop.product.dto.ProductListDto;
import furniture.shop.product.dto.ProductSearchCondition;
import furniture.shop.product.dto.QProductListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static furniture.shop.product.QProduct.product;

@Repository
public class ProductQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public ProductQueryRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Page<ProductListDto> searchProductPage(ProductSearchCondition searchCondition, Pageable pageable) {
        List<ProductListDto> content = jpaQueryFactory
                .select(new QProductListDto(
                        product.id,
                        product.productName,
                        product.productStatus,
                        product.productCategory
                ))
                .from(product)
                .where(
                        likeProductCode(searchCondition.getProductCode()),
                        likeProductName(searchCondition.getProductName()),
                        eqStatus(searchCondition.getProductStatus()),
                        eqCategory(searchCondition.getProductCategory())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(new OrderSpecifier<>(Order.ASC, product.id))
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(product.count())
                .from(product)
                .where(
                        likeProductCode(searchCondition.getProductCode()),
                        likeProductName(searchCondition.getProductName()),
                        eqStatus(searchCondition.getProductStatus()),
                        eqCategory(searchCondition.getProductCategory())
                );

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchOne());
    }

    private BooleanExpression likeProductCode(String productCode) {
        if (StringUtils.hasText(productCode)) {
            return product.productCode.like(productCode);
        }

        return null;
    }

    private BooleanExpression likeProductName(String productName) {
        if (StringUtils.hasText(productName)) {
            return product.productName.like(productName);
        }

        return null;
    }

    private BooleanExpression eqStatus(ProductStatus productStatus) {
        if (productStatus != null) {
            return product.productStatus.eq(productStatus);
        }

        return null;
    }

    private BooleanExpression eqCategory(ProductCategory productCategory) {

        if (productCategory != null) {
            return product.productCategory.eq(productCategory);
        }

        return null;
    }

}
