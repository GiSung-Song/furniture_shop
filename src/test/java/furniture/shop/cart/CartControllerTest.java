package furniture.shop.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import furniture.shop.cart.dto.CartDto;
import furniture.shop.cart.dto.CartProductAddDto;
import furniture.shop.cart.dto.CartProductDto;
import furniture.shop.cart.dto.CartProductEditDto;
import furniture.shop.configure.exception.CustomException;
import furniture.shop.configure.exception.CustomExceptionCode;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = CartController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {OncePerRequestFilter.class}))
class CartControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CartService cartService;

    @Test
    @DisplayName("장바구니 추가 테스트")
    void 장바구니_추가_테스트() throws Exception {
        CartProductAddDto dto = new CartProductAddDto();
        dto.setCount(100);

        mockMvc.perform(post("/product/{id}/cart", 0L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니 추가 실패 테스트")
    void 장바구니_추가_실패_테스트() throws Exception {
        doThrow(new CustomException(CustomExceptionCode.NOT_VALID_ERROR)).when(cartService).addCart(any());

        CartProductAddDto dto = new CartProductAddDto();
        dto.setCount(100);

        mockMvc.perform(post("/product/{id}/cart", 0L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니 조회 테스트")
    void 장바구니_조회_테스트() throws Exception {
        CartDto cartDto = new CartDto();
        List<CartProductDto> cartProductDtoList = new ArrayList<>();

        CartProductDto cartProductDto = new CartProductDto();
        cartProductDto.setProductCode("test-1234");
        cartProductDto.setProductName("테스트 상품");
        cartProductDto.setPrice(500);
        cartProductDto.setCount(5);

        cartProductDtoList.add(cartProductDto);

        cartDto.setCartProductDtoList(cartProductDtoList);
        cartDto.setTotalPrice(500);

        when(cartService.getCart()).thenReturn(cartDto);

        mockMvc.perform(get("/cart")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니 수정 테스트")
    void 장바구니_수정_테스트() throws Exception {
        CartProductEditDto dto = new CartProductEditDto();
        dto.setCount(100);
        dto.setProductId(0L);

        mockMvc.perform(patch("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}