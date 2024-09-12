package furniture.shop.global;

import furniture.shop.order.Orders;
import furniture.shop.order.OrdersQueryRepository;
import furniture.shop.order.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmptyOrderScheduling {
    private final OrdersQueryRepository ordersQueryRepository;
    private final OrdersRepository ordersRepository;

    //30분마다 주문하려다 취소한 것들 DB에서 삭제
    @Scheduled(fixedDelay = 1800000)
    public void run() {
        List<Orders> emptyOrders = ordersQueryRepository.getEmptyOrders();

        if (!emptyOrders.isEmpty()) {
            for (Orders order : emptyOrders) {
                ordersRepository.delete(order);
            }
        }
    }
}
