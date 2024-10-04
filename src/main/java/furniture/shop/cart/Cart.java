package furniture.shop.cart;

import furniture.shop.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class Cart {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartProduct> cartProductList = new ArrayList<>();

    @ColumnDefault("0")
    private int totalPrice;

    private void setMember(Member member) {
        this.member = member;
    }

    public void addTotalPrice(int price) {
        this.totalPrice += price;
    }

    public void minusTotalPrice(int price) {
        this.totalPrice -= price;
    }

    public void resetCart() {
        this.totalPrice = 0;
        this.cartProductList.clear();
    }

    public static Cart createCart(Member member) {
        Cart cart = new Cart();
        cart.setMember(member);

        return cart;
    }

}
