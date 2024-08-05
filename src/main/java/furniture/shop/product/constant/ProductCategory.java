package furniture.shop.product.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ProductCategory {
    CHAIR("의자"),
    TABLE("테이블"),
    CLOSET("옷장"),
    BED("침대");

    @Getter
    private final String korean;
}