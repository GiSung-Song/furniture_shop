package furniture.shop.credit;

import com.fasterxml.jackson.databind.ObjectMapper;
import furniture.shop.credit.dto.CreditRefundRequestDto;
import furniture.shop.credit.dto.CreditRequestDto;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.filter.OncePerRequestFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = CreditController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {OncePerRequestFilter.class}))
class CreditControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private CreditService creditService;

    @Test
    @DisplayName("결제 검증 테스트")
    void 결제_검증_테스트() throws Exception {
        doNothing().when(creditService).createAndVerifyPayment(any(CreditRequestDto.class));

        CreditRequestDto dto = new CreditRequestDto();

        dto.setOrderId(0L);
        dto.setAmount(100000);
        dto.setPayMethod("card");

        mockMvc.perform(post("/credit/{id}/complete", 0L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("결제가 완료되었습니다."));
    }

    @Test
    @DisplayName("환불 테스트")
    void 환불_테스트() throws Exception {
        doNothing().when(creditService).cancelPayment(any(CreditRefundRequestDto.class));

        CreditRefundRequestDto dto = new CreditRefundRequestDto();

        dto.setImpUID("imp_1234");

        mockMvc.perform(post("/refund/{id}", "imp_1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("환불이 완료되었습니다."));
    }
}