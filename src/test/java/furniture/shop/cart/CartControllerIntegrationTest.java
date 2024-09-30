package furniture.shop.cart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import furniture.shop.cart.dto.CartProductAddDto;
import furniture.shop.cart.dto.CartProductEditDto;
import furniture.shop.global.WithMockCustomMember;
import furniture.shop.global.embed.Address;
import furniture.shop.member.Member;
import furniture.shop.member.MemberRepository;
import furniture.shop.member.constant.MemberGender;
import furniture.shop.product.Product;
import furniture.shop.product.ProductRepository;
import furniture.shop.product.constant.ProductCategory;
import furniture.shop.product.embed.ProductSize;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartProductRepository cartProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CartService cartService;

    @PersistenceContext
    private EntityManager entityManager;

    Product product1;
    Product product2;
    Member member;

    @BeforeEach
    void setUp() {
        product1 = Product.builder()
                .productCode("code-1111")
                .productName("product1111")
                .productCategory(ProductCategory.CHAIR)
                .stock(100)
                .price(100000)
                .size(new ProductSize(10.5, 10.2, 10.4))
                .description("테스트1111 상품입니다.")
                .build();

        product2 = Product.builder()
                .productCode("code-2222")
                .productName("product2222")
                .productCategory(ProductCategory.BED)
                .stock(5)
                .price(2460000)
                .size(new ProductSize(150.3, 220.7, 30.6))
                .description("테스트2222 상품입니다.")
                .build();

        productRepository.save(product1);
        productRepository.save(product2);

        member = Member.builder()
                .username("테스터")
                .email("test@test.com")
                .password("password")
                .phone("01012341234")
                .gender(MemberGender.MALE)
                .address(new Address("11232", "서울시 서울구 서울로", "11 서울아파트 11동 111호"))
                .build();

        //member 미리 저장 하여 @WithMockCustomMember 에서 findByEmail != null 을 하기 위함.
        memberRepository.save(member);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("장바구니 상품 추가 성공 테스트")
    @WithMockCustomMember
    void 장바구니_상품_추가_성공_테스트() throws Exception {
        CartProductAddDto cartProductAddDto = new CartProductAddDto();

        cartProductAddDto.setCount(10);

        mockMvc.perform(post("/product/{id}/cart", product1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(cartProductAddDto)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니 상품 추가 실패 테스트")
    @WithMockCustomMember
    void 장바구니_상품_추가_실패_테스트() throws Exception {
        CartProductAddDto cartProductAddDto = new CartProductAddDto();

        cartProductAddDto.setCount(10);
        cartProductAddDto.setProductId(1032L);

        mockMvc.perform(post("/product/{id}/cart", product1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(cartProductAddDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니 조회 테스트")
    @WithMockCustomMember
    void 장바구니_조회_테스트() throws Exception {
        mockMvc.perform(get("/cart")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.totalPrice").value(0))
                .andExpect(jsonPath("$.result.cartProductDtoList.size()").value(0))
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니 수정 성공 테스트")
    @WithMockCustomMember
    void 장바구니_수정_성공_테스트() throws Exception {
        Cart cart = Cart.createCart(member);
        CartProduct cartProduct1 = CartProduct.createCartProduct(cart, product1, 10);
        CartProduct cartProduct2 = CartProduct.createCartProduct(cart, product2, 5);

        cartRepository.save(cart);

        entityManager.flush();
        entityManager.clear();

        CartProductEditDto cartProductEditDto = new CartProductEditDto();
        cartProductEditDto.setProductId(product1.getId());
        cartProductEditDto.setCount(2);

        mockMvc.perform(patch("/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(cartProductEditDto)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니 수정 실패 테스트")
    @WithMockCustomMember
    void 장바구니_수정_실패_테스트() throws Exception {
        Cart cart = Cart.createCart(member);
        CartProduct cartProduct1 = CartProduct.createCartProduct(cart, product1, 10);
        CartProduct cartProduct2 = CartProduct.createCartProduct(cart, product2, 5);

        cartRepository.save(cart);

        entityManager.flush();
        entityManager.clear();

        CartProductEditDto cartProductEditDto = new CartProductEditDto();
        cartProductEditDto.setProductId(372198L);
        cartProductEditDto.setCount(2);

        mockMvc.perform(patch("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(cartProductEditDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
