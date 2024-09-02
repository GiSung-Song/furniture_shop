package furniture.shop.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import furniture.shop.configure.exception.CustomException;
import furniture.shop.configure.exception.CustomExceptionCode;
import furniture.shop.member.constant.MemberGender;
import furniture.shop.member.dto.MemberInfoDto;
import furniture.shop.member.dto.MemberJoinDto;
import furniture.shop.member.dto.MemberUpdateDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.filter.OncePerRequestFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@WebMvcTest(controllers = MemberController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {OncePerRequestFilter.class}))
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

        mockMvc.perform(post("/join")
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

        mockMvc.perform(post("/join")
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

        mockMvc.perform(post("/join")
                        .content(new ObjectMapper().writeValueAsString(memberJoinDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    @DisplayName("회원정보 조회 테스트")
    void 회원정보_조회_테스트() throws Exception {
        MemberInfoDto memberInfoDto = getMemberInfoDto();

        given(memberService.getMemberInfo()).willReturn(memberInfoDto);

        mockMvc.perform(get("/member"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("회원정보 조회 실패 테스트")
    void 회원정보_조회_실패_테스트() throws Exception {
        given(memberService.getMemberInfo()).willThrow(new CustomException(CustomExceptionCode.NOT_VALID_ERROR));

        mockMvc.perform(get("/member"))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }


    @Test
    @DisplayName("회원정보 수정 테스트")
    void 회원정보_수정_테스트() throws Exception {
        given(memberService.updateMember(any())).willReturn(getMemberInfoDto());

        MemberUpdateDto memberUpdateDto = new MemberUpdateDto();
        memberUpdateDto.setStreet("12345");
        memberUpdateDto.setCity("12345");
        memberUpdateDto.setZipCode("12345");

        mockMvc.perform(patch("/member")
                        .content(new ObjectMapper().writeValueAsString(memberUpdateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    private static MemberInfoDto getMemberInfoDto() {
        MemberInfoDto memberInfoDto = new MemberInfoDto();
        memberInfoDto.setUsername("테스터");
        memberInfoDto.setPhone("01012345678");
        memberInfoDto.setEmail("test@test.com");
        memberInfoDto.setMileage(1092);
        memberInfoDto.setMemberGender(MemberGender.FEMALE);
        memberInfoDto.setZipCode("12345");
        memberInfoDto.setCity("서울시 은평구");
        memberInfoDto.setStreet("은평대로 123-34");

        return memberInfoDto;
    }

    private static MemberJoinDto getMemberJoinDto() {
        MemberJoinDto memberJoinDto = new MemberJoinDto();

        memberJoinDto.setMemberGender("MALE");
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