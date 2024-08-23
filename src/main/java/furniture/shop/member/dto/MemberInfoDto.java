package furniture.shop.member.dto;

import furniture.shop.member.constant.MemberGender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "회원 정보 Response DTO")
public class MemberInfoDto {

    @Schema(description = "이름")
    private String username;

    @Schema(description = "핸드폰 번호")
    private String phone;

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "우편번호")
    private String zipCode;

    @Schema(description = "도시")
    private String city;

    @Schema(description = "상세주소")
    private String street;

    @Schema(description = "성별")
    private MemberGender memberGender;

    @Schema(description = "마일리지")
    private int mileage;

    @Schema(description = "가입 일자")
    private LocalDateTime registerDate;

    @Schema(description = "마지막 수정 일자")
    private LocalDateTime updateDate;
}
