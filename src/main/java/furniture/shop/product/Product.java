package furniture.shop.product;

import furniture.shop.product.constant.ProductCategory;
import furniture.shop.product.embed.ProductSize;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 15)
    private String productCode;

    @Column(nullable = false, length = 20)
    private String productName;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

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
}
