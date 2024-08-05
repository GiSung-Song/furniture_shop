package furniture.shop.member;

import furniture.shop.member.embed.Address;
import furniture.shop.member.constant.MemberGender;
import furniture.shop.member.constant.MemberRole;
import furniture.shop.member.constant.MemberStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@Builder
public class Member {

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
    private MemberGender gender;

    @CreatedDate
    private LocalDateTime registerDate;

    @LastModifiedDate
    private LocalDateTime updateDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MemberStatus memberStatus = MemberStatus.ACTIVE;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MemberRole role = MemberRole.MEMBER;

}
