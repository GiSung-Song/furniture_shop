package furniture.shop.product.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ProductStatus {

    SELLING("판매중"),
    STOP("판매중단"),
    READY("준비중")
    ;

    @Getter
    private final String korean;
}
