package furniture.shop.order;

import furniture.shop.product.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrdersProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "orders_id")
    private Orders orders;

    private int count;

    private int totalPrice;

    private void setCount(int count) {
        this.count = count;
    }

    private void setOrders(Orders orders) {
        this.orders = orders;
    }

    private void setProduct(Product product) {
        this.product = product;
    }

    private void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public static OrdersProduct createOrdersProduct(Orders orders, Product product, int count) {
        OrdersProduct ordersProduct = new OrdersProduct();

        ordersProduct.setOrders(orders);
        ordersProduct.setProduct(product);
        ordersProduct.setCount(count);
        ordersProduct.setTotalPrice(count * product.getPrice());

        orders.getOrdersProducts().add(ordersProduct);

        return ordersProduct;
    }
}
