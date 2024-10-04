package furniture.shop.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import furniture.shop.review.dto.ReviewAddRequestDto;
import furniture.shop.review.dto.ReviewEditRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = ReviewController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {OncePerRequestFilter.class}))
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Test
    @DisplayName("리뷰 등록 성공 테스트")
    void 리뷰_등록_성공_테스트() throws Exception {
        ReviewAddRequestDto dto = new ReviewAddRequestDto();

        dto.setComment("테스트 리뷰입니다.");
        dto.setRate(4.9);

        Mockito.doNothing().when(reviewService).addReview(dto);

        mockMvc.perform(post("/product/{id}/review", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("리뷰 등록 실패 테스트 - 입력 오류")
    void 리뷰_등록_실패_테스트_입력() throws Exception {
        ReviewAddRequestDto dto = new ReviewAddRequestDto();

        dto.setComment("테스트 리뷰입니다.");
        dto.setRate(5.9);

        mockMvc.perform(post("/product/{id}/review", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("리뷰 수정 성공 테스트")
    void 리뷰_수정_성공_테스트() throws Exception {
        ReviewEditRequestDto dto = new ReviewEditRequestDto();

        dto.setComment("수정된 리뷰입니다.");
        dto.setRate(3.8);

        Mockito.doNothing().when(reviewService).editReview(dto);

        mockMvc.perform(patch("/review/{id}", 3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("리뷰 수정 실패 테스트 - 입력 오류")
    void 리뷰_수정_실패_테스트_입력() throws Exception {
        ReviewEditRequestDto dto = new ReviewEditRequestDto();

        dto.setRate(3.8);

        Mockito.doNothing().when(reviewService).editReview(dto);

        mockMvc.perform(patch("/review/{id}", 3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("리뷰 삭제 성공 테스트")
    void 리뷰_삭제_성공_테스트() throws Exception {
        Mockito.doNothing().when(reviewService).deleteReview(3L);

        mockMvc.perform(delete("/review/{id}", 3)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}