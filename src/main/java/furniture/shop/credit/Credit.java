package furniture.shop.credit;

import furniture.shop.configure.BaseTimeEntity;
import furniture.shop.order.Orders;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Credit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "orders_id")
    @OneToOne
    private Orders orders;

    private int amount; //결제 금액

    private int savedMileage; //저장된 마일리지

    @Column(unique = true)
    private String merchantUID; //고유 결제 번호

    @Column(unique = true)
    private String impUID; //고유 결제 번호(iamport)

    private String payMethod; //결제 방법

    private LocalDateTime paidAt; //결제 시각

    private LocalDateTime cancelledAt; //결제 취소 시각

    private void setOrders(Orders orders) {
        this.orders = orders;
    }

    private void setAmount(int amount) {
        this.amount = amount;
    }

    private void setSavedMileage(int savedMileage) {
        this.savedMileage = savedMileage;
    }

    private void setMerchantUID(String merchantUID) {
        this.merchantUID = merchantUID;
    }

    private void setImpUID(String impUID) {
        this.impUID = impUID;
    }

    private void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    private void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public void updateCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public static Credit createCredit(Orders orders, int amount, String merchantUID, String impUID, String payMethod) {
        Credit credit = new Credit();

        credit.setOrders(orders);
        credit.setAmount(amount);
        credit.setSavedMileage((int) (amount * 0.1));
        credit.setMerchantUID(merchantUID);
        credit.setImpUID(impUID);
        credit.setPayMethod(payMethod);
        credit.setPaidAt(LocalDateTime.now());

        return credit;
    }
}
