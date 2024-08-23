package furniture.shop.member.dto;

import furniture.shop.member.constant.MemberGender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "회원가입 Request DTO")
public class MemberJoinDto {

    @Schema(description = "이름")
    @NotBlank(message = "이름을 입력해주세요.")
    private String username;

    @Schema(description = "비밀번호")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @Schema(description = "핸드폰 번호", example = "01012345678")
    @NotBlank(message = "핸드폰 번호를 입력해주세요.")
    private String phone;

    @Schema(description = "이메일", example = "test@google.com")
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;

    @Schema(description = "우편번호", example = "12345")
    @NotBlank(message = "우편번호를 입력해주세요.")
    private String zipCode;

    @Schema(description = "도시", example = "서울시 강남구")
    @NotBlank(message = "도시를 입력해주세요.")
    private String city;

    @Schema(description = "상세주소", example = "강남대로 1234 6층 605호")
    @NotBlank(message = "상세주소를 입력해주세요.")
    private String street;

    @Schema(description = "성별", example = "MALE")
    private MemberGender memberGender;
}
