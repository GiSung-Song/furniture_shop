package furniture.shop.coupon;

import furniture.shop.configure.BooleanToYNConverter;
import furniture.shop.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private Member member;

    @Column(nullable = false)
    private String couponName;

    @ColumnDefault("0.0")
    private double discountRate;

    @Convert(converter = BooleanToYNConverter.class)
    private boolean useYN;
}
