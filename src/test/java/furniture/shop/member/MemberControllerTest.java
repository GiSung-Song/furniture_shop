package furniture.shop.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import furniture.shop.configure.exception.CustomException;
import furniture.shop.configure.exception.CustomExceptionCode;
import furniture.shop.member.constant.MemberGender;
import furniture.shop.member.dto.MemberJoinDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 테스트")
    void 회원가입_테스트() throws Exception {
        MemberJoinDto memberJoinDto = getMemberJoinDto();

        given(memberService.joinMember(any())).willReturn(1L);

        mockMvc.perform(post("/member/join")
                .content(new ObjectMapper().writeValueAsString(memberJoinDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 테스트")
    void 회원가입_실패_테스트() throws Exception {
        MemberJoinDto memberJoinDto = getMemberJoinDto();
        memberJoinDto.setEmail("test");

        given(memberService.joinMember(any())).willThrow(new CustomException(CustomExceptionCode.JOIN_DUPLICATE_EXCEPTION));

        mockMvc.perform(post("/member/join")
                .content(new ObjectMapper().writeValueAsString(memberJoinDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 테스트2")
    void 회원가입_실패_테스트2() throws Exception {
        MemberJoinDto memberJoinDto = getMemberJoinDto();
        memberJoinDto.setEmail("test");

        mockMvc.perform(post("/member/join")
                        .content(new ObjectMapper().writeValueAsString(memberJoinDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    private static MemberJoinDto getMemberJoinDto() {
        MemberJoinDto memberJoinDto = new MemberJoinDto();

        memberJoinDto.setMemberGender(MemberGender.MALE);
        memberJoinDto.setEmail("test@test.com");
        memberJoinDto.setPhone("01012345678");
        memberJoinDto.setPassword("123456");
        memberJoinDto.setUsername("테스터");
        memberJoinDto.setZipCode("12345");
        memberJoinDto.setCity("서울시 강남구");
        memberJoinDto.setStreet("강남대로 123-456");

        return memberJoinDto;
    }

}