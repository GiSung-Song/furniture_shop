package furniture.shop.member;

import furniture.shop.configure.response.ApiResponse;
import furniture.shop.member.dto.MemberInfoDto;
import furniture.shop.member.dto.MemberJoinDto;
import furniture.shop.member.dto.MemberUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Member API", description = "Member API")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    @Operation(summary = "회원가입", description = "회원가입 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입에 성공했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "중복된 이메일 계정입니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 입력입니다.", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "username", description = "이름", example = "테스터"),
            @Parameter(name = "password", description = "비밀번호", example = "asdf1234"),
            @Parameter(name = "phone", description = "휴대폰 번호", example = "01012345678"),
            @Parameter(name = "email", description = "이메일", example = "test@test.com"),
            @Parameter(name = "zipCode", description = "우편번호", example = "12345"),
            @Parameter(name = "city", description = "도시", example = "서울시 강남구"),
            @Parameter(name = "street", description = "상세주소", example = "강남대로 1234 6층 605호"),
            @Parameter(name = "memberGender", description = "성별", example = "MALE")
    })
    public ResponseEntity<ApiResponse<String>> joinMember(@Valid @RequestBody MemberJoinDto memberJoinDto) {
        memberService.joinMember(memberJoinDto);

        return ResponseEntity.ok(ApiResponse.res(HttpStatus.OK, "회원가입에 성공했습니다."));
    }

    @GetMapping("/member")
    @Operation(summary = "회원정보 조회", description = "회원정보 조회 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원정보를 조회했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "권한이 없습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponse<MemberInfoDto>> getMemberInfo() {
        MemberInfoDto memberInfo = memberService.getMemberInfo();

        return ResponseEntity.ok(ApiResponse.res(HttpStatus.OK, "회원정보를 조회했습니다.", memberInfo));
    }

    @PatchMapping("/member")
    @Operation(summary = "회원정보 수정", description = "회원정보 수정 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원정보를 수정했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "권한이 없습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponse<MemberInfoDto>> updateMemberInfo(@RequestBody MemberUpdateDto memberUpdateDto) {
        MemberInfoDto memberInfoDto = memberService.updateMember(memberUpdateDto);

        return ResponseEntity.ok(ApiResponse.res(HttpStatus.OK, "회원정보를 수정했습니다.", memberInfoDto));
    }
}