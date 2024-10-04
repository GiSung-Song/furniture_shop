package furniture.shop.product;

import furniture.shop.configure.BaseTimeEntity;
import furniture.shop.product.constant.ProductCategory;
import furniture.shop.product.constant.ProductStatus;
import furniture.shop.product.embed.ProductSize;
import furniture.shop.review.Review;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@DynamicInsert
public class Product extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 15)
    private String productCode;

    @Column(nullable = false, length = 20)
    private String productName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProductStatus productStatus = ProductStatus.SELLING;

    @ColumnDefault("0")
    private int stock;

    @ColumnDefault("0")
    private int price;

    @Embedded
    private ProductSize size;

    @Column(nullable = false)
    private String description;

    @ColumnDefault("0")
    private Long sellingCount;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    public void updateProductStatus(ProductStatus productStatus) {
        this.productStatus = productStatus;
    }

    public void addSellCount(int count) {
        this.sellingCount += count;
    }

    public void minusSellCount(int count) {
        this.sellingCount -= count;
    }

    public void minusStock(int stock) {
        this.stock -= stock;
    }

    public void addStock(int stock) {
        this.stock += stock;
    }

    public void updateStock(int stock) {
        this.stock = stock;
    }

    public void updatePrice(int price) {
        this.price = price;
    }

    public void updateSize(ProductSize productSize) {
        this.size = productSize;
    }

    public void updateDescription(String description) {
        this.description = description;
    }
}
