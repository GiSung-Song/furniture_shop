package furniture.shop.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import furniture.shop.order.dto.OrderSingleRequestDto;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = OrdersController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {OncePerRequestFilter.class}))
class OrdersControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    OrdersService ordersService;

    @Test
    @DisplayName("주문 목록 가져오기")
    void 주문_목록_가져오기_테스트() throws Exception {
        mockMvc.perform(get("/orders")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("단건 주문")
    void 단건_주문_테스트() throws Exception {
        OrderSingleRequestDto orderSingleRequestDto = new OrderSingleRequestDto();

        orderSingleRequestDto.setCount(10);

        mockMvc.perform(post("/product/{id}/orders", 0L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(orderSingleRequestDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("주문 현황 조회")
    void 주문_현황_조회_테스트() throws Exception {
        mockMvc.perform(get("/orders/{id}", 0L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니 상품 주문 테스트")
    void 장바구니_상품_주문_테스트() throws Exception {
        mockMvc.perform(post("/cart/orders")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

}