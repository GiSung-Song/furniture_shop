package furniture.shop.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import furniture.shop.cart.Cart;
import furniture.shop.cart.CartProduct;
import furniture.shop.cart.CartRepository;
import furniture.shop.global.WithMockCustomMember;
import furniture.shop.global.embed.Address;
import furniture.shop.member.Member;
import furniture.shop.member.MemberRepository;
import furniture.shop.member.constant.MemberGender;
import furniture.shop.order.dto.OrderSingleRequestDto;
import furniture.shop.product.Product;
import furniture.shop.product.ProductRepository;
import furniture.shop.product.constant.ProductCategory;
import furniture.shop.product.constant.ProductStatus;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class OrdersControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrdersProductRepository ordersProductRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    Product product1;
    Product product2;
    Member member;
    Cart cart;
    CartProduct cartProduct1;
    CartProduct cartProduct2;

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

        cart = Cart.createCart(member);
        cartProduct1 = CartProduct.createCartProduct(cart, product1, 10);
        cartProduct2 = CartProduct.createCartProduct(cart, product2, 5);

        cartRepository.save(cart);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("주문 목록 조회 성공 테스트")
    @WithMockCustomMember
    void 주문_목록_조회_성공_테스트() throws Exception {
        mockMvc.perform(get("/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.totalElements").value(0))
                .andDo(print());
    }

    @Test
    @DisplayName("주문 목록 조회 실패 테스트")
    void 주문_목록_조회_실패_테스트() throws Exception {
        mockMvc.perform(get("/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 단건 주문 성공 테스트")
    @WithMockCustomMember
    void 상품_단건_주문_성공_테스트() throws Exception {
        OrderSingleRequestDto orderSingleRequestDto = new OrderSingleRequestDto();

        orderSingleRequestDto.setCount(10);

        mockMvc.perform(post("/product/{id}/orders", product1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(orderSingleRequestDto)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 단건 주문 실패 테스트 - 재고 부족")
    @WithMockCustomMember
    void 상품_단건_주문_실패_테스트_재고() throws Exception {
        OrderSingleRequestDto orderSingleRequestDto = new OrderSingleRequestDto();

        orderSingleRequestDto.setCount(1000);

        mockMvc.perform(post("/product/{id}/orders", product1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(orderSingleRequestDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 단건 주문 실패 테스트 - 판매중단")
    @WithMockCustomMember
    void 상품_단건_주문_실패_테스트_판매중단() throws Exception {
        OrderSingleRequestDto orderSingleRequestDto = new OrderSingleRequestDto();
        product1.updateProductStatus(ProductStatus.READY);
        productRepository.saveAndFlush(product1);

        orderSingleRequestDto.setCount(10);

        mockMvc.perform(post("/product/{id}/orders", product1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(orderSingleRequestDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 단건 주문 실패 테스트 - 없는 상품")
    @WithMockCustomMember
    void 상품_단건_주문_실패_테스트_상품() throws Exception {
        OrderSingleRequestDto orderSingleRequestDto = new OrderSingleRequestDto();
        orderSingleRequestDto.setCount(10);

        mockMvc.perform(post("/product/{id}/orders", 321342L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(orderSingleRequestDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 상세 조회 성공 테스트")
    @WithMockCustomMember
    void 상품_상세_조회_성공_테스트() throws Exception {
        Orders orders = Orders.createOrders(member);
        OrdersProduct.createOrdersProduct(orders, product1, 5);
        OrdersProduct.createOrdersProduct(orders, product2, 5);

        ordersRepository.saveAndFlush(orders);

        mockMvc.perform(get("/orders/{id}", orders.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.orderProductList[0].productCode").value("code-1111"))
                .andExpect(jsonPath("$.result.orderProductList[1].productCode").value("code-2222"))
                .andDo(print());
    }

    @Test
    @DisplayName("상품 상세 조회 실패 테스트 - 사용자 다름")
    @WithMockCustomMember
    void 상품_상세_조회_실패_테스트() throws Exception {
        Member other = Member.builder()
                .username("테스터2")
                .email("test2@test2.com")
                .password("password")
                .phone("01012341231")
                .gender(MemberGender.MALE)
                .address(new Address("11232", "서울시 서울구 서울로", "11 서울아파트 11동 111호"))
                .build();

        //member 미리 저장 하여 @WithMockCustomMember 에서 findByEmail != null 을 하기 위함.
        memberRepository.save(other);

        Orders otherOrders = Orders.createOrders(other);
        ordersRepository.saveAndFlush(otherOrders);

        mockMvc.perform(get("/orders/{id}", otherOrders.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }
}
