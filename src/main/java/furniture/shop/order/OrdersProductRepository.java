package furniture.shop.order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersProductRepository extends JpaRepository<OrdersProduct, Long> {
}
