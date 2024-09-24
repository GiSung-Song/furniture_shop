package furniture.shop.order.contsant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrdersStatus {

    READY("주문준비"),
    FINISH("주문완료"),
    CANCEL("주문취소")
    ;

    @Getter
    private final String korean;
}
