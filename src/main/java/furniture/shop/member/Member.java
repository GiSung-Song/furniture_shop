package furniture.shop.member;

import furniture.shop.configure.BaseTimeEntity;
import furniture.shop.member.constant.MemberGender;
import furniture.shop.member.constant.MemberRole;
import furniture.shop.member.constant.MemberStatus;
import furniture.shop.member.embed.Address;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@Builder
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 14)
    private String phone;

    @Column(nullable = false, length = 30, unique = true)
    private String email;

    @Embedded
    private Address address;

    @ColumnDefault("0")
    private int mileage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberGender gender;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MemberStatus memberStatus = MemberStatus.ACTIVE;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MemberRole role = MemberRole.MEMBER;

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeAddress(Address address) {
        this.address = address;
    }

}
