package furniture.shop.member.dto;

import furniture.shop.member.constant.MemberGender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberJoinDto {

    @NotBlank(message = "이름을 입력해주세요.")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "핸드폰 번호를 입력해주세요.")
    private String phone;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;

    @NotBlank(message = "우편번호를 입력해주세요.")
    private String zipCode;

    @NotBlank(message = "도시를 입력해주세요.")
    private String city;

    @NotBlank(message = "상세주소를 입력해주세요.")
    private String street;

    private MemberGender memberGender;
}
