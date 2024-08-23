package furniture.shop.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "회원 정보 수정 Request DTO")
public class MemberUpdateDto {

    @Schema(description = "비밀번호")
    private String password;

    @Schema(description = "우편번호", example = "12345")
    private String zipCode;

    @Schema(description = "도시", example = "서울시 강남구")
    private String city;

    @Schema(description = "상세주소", example = "강남대로 1234 6층 605호")
    private String street;

}
