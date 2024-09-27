package furniture.shop.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import furniture.shop.global.WithMockCustomMember;
import furniture.shop.global.embed.Address;
import furniture.shop.member.constant.MemberGender;
import furniture.shop.member.dto.MemberJoinDto;
import furniture.shop.member.dto.MemberUpdateDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MemberControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void 회원가입_성공_테스트() throws Exception {
        MemberJoinDto memberJoinDto = new MemberJoinDto();

        memberJoinDto.setEmail("test@test.com");
        memberJoinDto.setPhone("01012341234");
        memberJoinDto.setMemberGender(MemberGender.MALE);
        memberJoinDto.setUsername("테스터");
        memberJoinDto.setPassword("password");
        memberJoinDto.setCity("서울시 서울구 서울로");
        memberJoinDto.setStreet("11 서울아파트 11동 111호");
        memberJoinDto.setZipCode("11223");

        mockMvc.perform(post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(memberJoinDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원가입에 성공했습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 이메일 중복 테스트")
    void 회원가입_실패_이메일_중복_테스트() throws Exception {
        MemberJoinDto memberJoinDto = new MemberJoinDto();

        memberJoinDto.setEmail("test@test.com");
        memberJoinDto.setPhone("01012341234");
        memberJoinDto.setMemberGender(MemberGender.MALE);
        memberJoinDto.setUsername("테스터");
        memberJoinDto.setPassword("password");
        memberJoinDto.setCity("서울시 서울구 서울로");
        memberJoinDto.setStreet("11 서울아파트 11동 111호");
        memberJoinDto.setZipCode("11223");

        mockMvc.perform(post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(memberJoinDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원가입에 성공했습니다."))
                .andDo(print());

        mockMvc.perform(post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(memberJoinDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 등록된 이메일입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 잘못된 입력 테스트")
    void 회원가입_실패_잘못된_입력_테스트() throws Exception {
        MemberJoinDto memberJoinDto = new MemberJoinDto();

        memberJoinDto.setEmail("test@test.com");
        memberJoinDto.setPhone("01012341234");
        memberJoinDto.setPassword("password");
        memberJoinDto.setCity("서울시 서울구 서울로");
        memberJoinDto.setStreet("11 서울아파트 11동 111호");
        memberJoinDto.setZipCode("11223");

        mockMvc.perform(post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(memberJoinDto)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 정보 조회 성공 테스트")
    @WithMockCustomMember(email = "test@test.com")
    void 회원_정보_조회_성공_테스트() throws Exception {
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

        mockMvc.perform(get("/member")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.email").value("test@test.com"))
                .andExpect(jsonPath("$.message").value("회원정보를 조회했습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 정보 조회 실패 테스트1")
    @WithMockCustomMember(email = "test@test.com")
    void 회원_정보_조회_실패_테스트1() throws Exception {
        mockMvc.perform(get("/member")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 정보 조회 실패 테스트2")
    void 회원_정보_조회_실패_테스트2() throws Exception {
        mockMvc.perform(get("/member")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("알 수 없는 JWT 토큰입니다.")))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 정보 수정 성공 테스트")
    @WithMockCustomMember(email = "test@test.com")
    void 회원_정보_수정_성공_테스트() throws Exception {
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

        mockMvc.perform(patch("/member")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(memberUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원정보를 수정했습니다."))
                .andExpect(jsonPath("$.result.zipCode").value("12345"))
                .andExpect(jsonPath("$.result.city").value("미국시 미국구 미국로 11"))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 정보 수정 실패 테스트")
    void 회원_정보_수정_실패_테스트() throws Exception {
        MemberUpdateDto memberUpdateDto = new MemberUpdateDto();

        memberUpdateDto.setZipCode("12345");
        memberUpdateDto.setCity("미국시 미국구 미국로 11");
        memberUpdateDto.setStreet("미국주택 15-32");

        mockMvc.perform(patch("/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(memberUpdateDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("알 수 없는 JWT 토큰입니다.")))
                .andDo(print());
    }
}
