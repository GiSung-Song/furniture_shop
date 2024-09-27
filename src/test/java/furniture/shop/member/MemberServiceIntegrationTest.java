package furniture.shop.member;

import furniture.shop.configure.exception.CustomException;
import furniture.shop.configure.exception.CustomExceptionCode;
import furniture.shop.global.WithMockCustomMember;
import furniture.shop.global.embed.Address;
import furniture.shop.member.constant.MemberGender;
import furniture.shop.member.dto.MemberInfoDto;
import furniture.shop.member.dto.MemberJoinDto;
import furniture.shop.member.dto.MemberUpdateDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MemberServiceIntegrationTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void 회원가입_성공_테스트() {
        MemberJoinDto memberJoinDto = new MemberJoinDto();

        memberJoinDto.setEmail("test@test.com");
        memberJoinDto.setPhone("01012341234");
        memberJoinDto.setMemberGender(MemberGender.MALE);
        memberJoinDto.setUsername("테스터");
        memberJoinDto.setPassword("password");
        memberJoinDto.setCity("서울시 서울구 서울로");
        memberJoinDto.setStreet("11 서울아파트 11동 111호");
        memberJoinDto.setZipCode("11223");

        Long savedMemberId = memberService.joinMember(memberJoinDto);

        Member savedMember = memberRepository.findById(savedMemberId).orElseThrow();

        assertEquals(memberJoinDto.getEmail(), savedMember.getEmail());
        assertEquals(memberJoinDto.getPhone(), savedMember.getPhone());
    }

    @Test
    @DisplayName("회원가입 실패 테스트")
    void 회원가입_실패_테스트() {
        MemberJoinDto memberJoinDto = new MemberJoinDto();

        memberJoinDto.setEmail("test@test.com");
        memberJoinDto.setPhone("01012341234");
        memberJoinDto.setMemberGender(MemberGender.MALE);
        memberJoinDto.setUsername("테스터");
        memberJoinDto.setPassword("password");
        memberJoinDto.setCity("서울시 서울구 서울로");
        memberJoinDto.setStreet("11 서울아파트 11동 111호");
        memberJoinDto.setZipCode("11223");

        memberService.joinMember(memberJoinDto);

        MemberJoinDto memberJoinDto2 = new MemberJoinDto();

        memberJoinDto2.setEmail("test@test.com");
        memberJoinDto2.setPhone("01012341234");
        memberJoinDto2.setMemberGender(MemberGender.MALE);
        memberJoinDto2.setUsername("테스터");
        memberJoinDto2.setPassword("password");
        memberJoinDto2.setCity("서울시 서울구 서울로");
        memberJoinDto2.setStreet("11 서울아파트 11동 111호");
        memberJoinDto2.setZipCode("11223");

        assertThrows(CustomException.class, () -> memberService.joinMember(memberJoinDto2));
    }

    @Test
    @DisplayName("회원 정보 조회 성공 테스트")
    @WithMockCustomMember(email = "test@test.com")
    void 회원정보_조회_성공_테스트() {
        Member member = Member.builder()
                .username("테스터")
                .email("test@test.com")
                .password("password")
                .phone("01012341234")
                .gender(MemberGender.MALE)
                .address(new Address("11232", "서울시 서울구 서울로", "11 서울아파트 11동 111호"))
                .build();

        //member 미리 저장 하여 @WithMockCustomMember 에서 findByEmail != null 을 하기 위함.
        memberRepository.save(member);

        MemberInfoDto memberInfo = memberService.getMemberInfo();

        assertEquals("test@test.com", memberInfo.getEmail());
    }

    @Test
    @DisplayName("회원 정보 조회 실패 테스트")
    @WithMockCustomMember(email = "test@test.com")
    void 회원정보_조회_실패_테스트() {
        CustomException customException = assertThrows(CustomException.class, () -> memberService.getMemberInfo());

        assertEquals(CustomExceptionCode.NOT_VALID_ERROR, customException.getCode());
    }

    @Test
    @DisplayName("회원 정보 수정 성공 테스트")
    @WithMockCustomMember(email = "test@test.com")
    void 회원정보_수정_성공_테스트() {
        Member member = Member.builder()
                .username("테스터")
                .email("test@test.com")
                .password("password")
                .phone("01012341234")
                .gender(MemberGender.MALE)
                .address(new Address("11232", "서울시 서울구 서울로", "11 서울아파트 11동 111호"))
                .build();

        //member 미리 저장 하여 @WithMockCustomMember 에서 findByEmail != null 을 하기 위함.
        memberRepository.save(member);

        MemberUpdateDto memberUpdateDto = new MemberUpdateDto();

        memberUpdateDto.setZipCode("12345");
        memberUpdateDto.setCity("미국시 미국구 미국로 11");
        memberUpdateDto.setStreet("미국주택 15-32");

        MemberInfoDto memberInfoDto = memberService.updateMember(memberUpdateDto);

        assertEquals(memberUpdateDto.getZipCode(), memberInfoDto.getZipCode());
        assertEquals(memberUpdateDto.getCity(), memberInfoDto.getCity());
        assertEquals(memberUpdateDto.getStreet(), memberInfoDto.getStreet());
    }

    @Test
    @DisplayName("회원 정보 수정 실패 테스트")
    @WithMockCustomMember(email = "test@test.com")
    void 회원정보_수정_실패_테스트() {
        MemberUpdateDto memberUpdateDto = new MemberUpdateDto();

        memberUpdateDto.setPassword("12341234");

        CustomException customException = assertThrows(CustomException.class, () -> memberService.updateMember(memberUpdateDto));

        assertEquals(CustomExceptionCode.NOT_VALID_ERROR, customException.getCode());
    }

}
