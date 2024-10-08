package furniture.shop.cart;

import furniture.shop.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class CartProduct {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ColumnDefault("1")
    private int count;

    private void setCart(Cart cart) {
        this.cart = cart;
    }

    private void setProduct(Product product) {
        this.product = product;
    }

    private void setCount(int count) {
        this.count = count;
    }

    public static CartProduct createCartProduct(Cart cart, Product product, int count) {
        CartProduct cartProduct = new CartProduct();

        cartProduct.setCart(cart);
        cartProduct.setProduct(product);
        cartProduct.setCount(count);

        cart.getCartProductList().add(cartProduct);
        cart.addTotalPrice(count * product.getPrice());

        return cartProduct;
    }

    public void editCount(int count) {
        int nowCount = this.count; //현재 개수
        int price = this.product.getPrice(); //가격

        // 개수가 줄어들면
        if (nowCount > count) {
            // 줄어든 만큼 총 가격 -
            this.cart.minusTotalPrice((this.count - count) * price);
        } else if (nowCount < count) { //개수가 늘어나면
            // 개수가 늘어난 만큼 총 가격 +
            this.cart.addTotalPrice((count - this.count) * price);
        }

        this.count = count;
    }

}
