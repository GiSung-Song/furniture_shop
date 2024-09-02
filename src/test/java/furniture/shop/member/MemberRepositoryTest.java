package furniture.shop.member;

import furniture.shop.member.Member;
import furniture.shop.member.MemberRepository;
import furniture.shop.member.constant.MemberGender;
import furniture.shop.member.constant.MemberRole;
import furniture.shop.member.embed.Address;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    Member getMember() {
        return Member.builder()
                .address(new Address("12345", "서울시 강남구 강남대로 114", "테스트 빌딩 5층"))
                .email("test@test.com")
                .password("123456")
                .phone("010-1234-5678")
                .username("테스터")
                .gender(MemberGender.MALE)
                .build();
    }

    @Test
    @DisplayName("저장 테스트")
    void 저장_테스트() {
        Member member = getMember();

        Member savedMember = memberRepository.save(member);

        Assertions.assertEquals(savedMember.getEmail(), member.getEmail());
        Assertions.assertEquals(savedMember.getMileage(), 0);
        Assertions.assertEquals(savedMember.getPhone(), member.getPhone());
        Assertions.assertEquals(savedMember.getRole(), MemberRole.MEMBER);
    }

    @Test
    @DisplayName("조회 테스트")
    void 조회_테스트() {
        Member member = getMember();

        Member savedMember = memberRepository.save(member);
        Long savedId = savedMember.getId();

        Member findMember = memberRepository.findById(savedId).get();

        Assertions.assertEquals(savedMember.getId(), findMember.getId());
        Assertions.assertEquals(savedMember.getPhone(), findMember.getPhone());
    }

    @Test
    @DisplayName("이메일로 조회 테스트")
    void 이메일_조회_테스트() {
        Member member = getMember();

        memberRepository.save(member);

        Member findMember = memberRepository.findByEmail(member.getEmail());

        Assertions.assertEquals(findMember.getUsername(), member.getUsername());
    }

    @Test
    @DisplayName("이메일로 조회 실패 테스트")
    void 이메일_조회_실패_테스트() {
        Member member = getMember();

        Assertions.assertNull(memberRepository.findByEmail(member.getEmail()));
    }
}