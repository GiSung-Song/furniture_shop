package furniture.shop.member;

import furniture.shop.configure.response.ApiResponse;
import furniture.shop.member.dto.MemberJoinDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/member/join")
    public ResponseEntity<ApiResponse<String>> goJoinPage(@Valid @RequestBody MemberJoinDto memberJoinDto) {
        memberService.joinMember(memberJoinDto);

        return ResponseEntity.ok(ApiResponse.res(HttpStatus.OK, "회원가입에 성공했습니다."));
    }
}
