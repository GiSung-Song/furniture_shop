package furniture.shop.member;

import furniture.shop.configure.exception.CustomException;
import furniture.shop.member.constant.MemberGender;
import furniture.shop.member.dto.MemberJoinDto;
import furniture.shop.member.embed.Address;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    private Member member;

    void setUp() {
        member = Member.builder()
                .address(new Address("12345", "서울시 강남구 강남대로 114", "테스트 빌딩 5층"))
                .email("test@test.com")
                .password("123456")
                .phone("01012345678")
                .username("테스터")
                .build();

        given(memberRepository.findById(any())).willReturn(Optional.ofNullable(member));
    }

    @Test
    void 회원가입_테스트() {
        setUp();

        MemberJoinDto memberJoinDto = getMemberJoinDto();

        given(memberRepository.save(any())).willReturn(member);

        Long savedId = memberService.joinMember(memberJoinDto);
        Member findMember = memberRepository.findById(savedId).get();

        verify(memberRepository).save(any());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(memberJoinDto.getUsername());
    }

    @Test
    void 회원가입_실패_테스트() {
        MemberJoinDto joinDto = getMemberJoinDto();

        given(memberRepository.findByEmail(any())).willThrow(CustomException.class);

        assertThrows(CustomException.class, () -> memberService.joinMember(joinDto));
    }

    private static MemberJoinDto getMemberJoinDto() {
        MemberJoinDto memberJoinDto = new MemberJoinDto();

        memberJoinDto.setMemberGender(MemberGender.MALE);
        memberJoinDto.setEmail("test@test.com");
        memberJoinDto.setPhone("01012345678");
        memberJoinDto.setPassword("123456");
        memberJoinDto.setUsername("테스터");

        return memberJoinDto;
    }
}