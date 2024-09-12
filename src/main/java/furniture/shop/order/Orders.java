package furniture.shop.order;

import furniture.shop.configure.BaseTimeEntity;
import furniture.shop.global.embed.Address;
import furniture.shop.member.Member;
import furniture.shop.order.contsant.OrdersStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Orders extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdersProduct> ordersProducts = new ArrayList<>();

    private int totalPrice;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrdersStatus ordersStatus = OrdersStatus.READY;

    @Embedded
    private Address address;

    private String memo;

    @Column(nullable = false)
    private String receiver;

    @Column(nullable = false)
    private String phone;

    private int mileage;

    public void addTotalPrice(int price) {
        this.totalPrice += price;
    }

    private void setMember(Member member) {
        this.member = member;
    }

    private void setAddress(Address address) {
        this.address = address;
    }

    private void setPhone(String phone) {
        this.phone = phone;
    }

    private void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void updateOrdersStatus(OrdersStatus ordersStatus) {
        this.ordersStatus = ordersStatus;
    }

    public static Orders createOrders(Member member) {
        Orders orders = new Orders();

        orders.setMember(member);
        orders.setAddress(member.getAddress());
        orders.setReceiver(member.getUsername());
        orders.setPhone(member.getPhone());

        return orders;
    }

}
