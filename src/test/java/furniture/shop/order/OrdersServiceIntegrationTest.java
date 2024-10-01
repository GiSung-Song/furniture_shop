package furniture.shop.order;

import furniture.shop.cart.*;
import furniture.shop.configure.exception.CustomException;
import furniture.shop.configure.exception.CustomExceptionCode;
import furniture.shop.global.WithMockCustomMember;
import furniture.shop.global.embed.Address;
import furniture.shop.member.Member;
import furniture.shop.member.MemberRepository;
import furniture.shop.member.constant.MemberGender;
import furniture.shop.order.dto.OrderResponseDto;
import furniture.shop.order.dto.OrderSingleRequestDto;
import furniture.shop.order.dto.OrdersListResponseDto;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OrdersServiceIntegrationTest {

    @Autowired
    private OrdersService ordersService;

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
    @DisplayName("단건 주문 성공 테스트")
    @WithMockCustomMember
    void 단건_주문_성공_테스트() {
        OrderSingleRequestDto orderSingleRequestDto = new OrderSingleRequestDto();

        orderSingleRequestDto.setProductId(product1.getId());
        orderSingleRequestDto.setCount(50);

        ordersService.createSingleOrder(orderSingleRequestDto);

        Orders orders = ordersRepository.findAll().get(0);

        assertNotNull(orders);
        assertEquals(orders.getOrdersProducts().get(0).getProduct().getProductCode(), product1.getProductCode());
    }

    @Test
    @DisplayName("단건 주문 실패 테스트 - 상품ID 오류")
    @WithMockCustomMember
    void 단건_주문_실패_테스트_상품ID() {
        OrderSingleRequestDto orderSingleRequestDto = new OrderSingleRequestDto();

        orderSingleRequestDto.setProductId(43213421L);
        orderSingleRequestDto.setCount(50);

        CustomException customException = assertThrows(CustomException.class, () -> ordersService.createSingleOrder(orderSingleRequestDto));
        assertEquals(CustomExceptionCode.NOT_VALID_ERROR, customException.getCode());
    }

    @Test
    @DisplayName("단건 주문 실패 테스트 - 상품 판매중이 아닌 경우")
    @WithMockCustomMember
    void 단건_주문_실패_테스트_상품_판매X() {
        product1.updateProductStatus(ProductStatus.STOP);
        productRepository.saveAndFlush(product1);

        OrderSingleRequestDto orderSingleRequestDto = new OrderSingleRequestDto();

        orderSingleRequestDto.setProductId(product1.getId());
        orderSingleRequestDto.setCount(50);

        CustomException customException = assertThrows(CustomException.class, () -> ordersService.createSingleOrder(orderSingleRequestDto));
        assertEquals(CustomExceptionCode.NOT_SELLING_PRODUCT_EXCEPTION, customException.getCode());
    }

    @Test
    @DisplayName("단건 주문 실패 테스트 - 재고보다 수량이 큰 경우")
    @WithMockCustomMember
    void 단건_주문_실패_테스트_상품_수량() {
        OrderSingleRequestDto orderSingleRequestDto = new OrderSingleRequestDto();

        orderSingleRequestDto.setProductId(product1.getId());
        orderSingleRequestDto.setCount(150);

        CustomException customException = assertThrows(CustomException.class, () -> ordersService.createSingleOrder(orderSingleRequestDto));
        assertEquals(CustomExceptionCode.NOT_ENOUGH_PRODUCT_EXCEPTION, customException.getCode());
    }

    @Test
    @DisplayName("장바구니 상품 주문 성공 테스트")
    @WithMockCustomMember
    void 장바구니_상품_주문_성공_테스트() {
        ordersService.createCartOrder();

        Orders orders = ordersRepository.findAll().get(0);

        assertNotNull(orders);
        assertEquals(orders.getOrdersProducts().get(0).getProduct().getProductName(), cartProduct1.getProduct().getProductName());
        assertEquals(orders.getOrdersProducts().get(1).getProduct().getProductName(), cartProduct2.getProduct().getProductName());
    }

    @Test
    @DisplayName("장바구니 상품 주문 실패 테스트")
    @WithMockCustomMember
    void 장바구니_상품_주문_실패_테스트() {
        cart.getCartProductList().remove(cartProduct1);
        cart.getCartProductList().remove(cartProduct2);

        cartRepository.saveAndFlush(cart);

        CustomException customException = assertThrows(CustomException.class, () -> ordersService.createCartOrder());
        assertEquals(CustomExceptionCode.NOT_VALID_ERROR, customException.getCode());
    }

    @Test
    @DisplayName("장바구니 상품 주문 실패 테스트 - 수량")
    @WithMockCustomMember
    void 장바구니_상품_주문_실패_테스트_수량() {
        cartProduct1.editCount(150);
        cartRepository.saveAndFlush(cart);

        CustomException customException = assertThrows(CustomException.class, () -> ordersService.createCartOrder());
        assertEquals(CustomExceptionCode.NOT_ENOUGH_PRODUCT_EXCEPTION, customException.getCode());
    }

    @Test
    @DisplayName("장바구니 상품 주문 실패 테스트 - 상품 판매 X")
    @WithMockCustomMember
    void 장바구니_상품_주문_실패_테스트_상품_판매X() {
        product1.updateProductStatus(ProductStatus.STOP);
        productRepository.saveAndFlush(product1);

        CustomException customException = assertThrows(CustomException.class, () -> ordersService.createCartOrder());
        assertEquals(CustomExceptionCode.NOT_SELLING_PRODUCT_EXCEPTION, customException.getCode());
    }

    @Test
    @DisplayName("주문 목록 조회 테스트")
    @WithMockCustomMember
    void 주문_목록_테스트() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<OrdersListResponseDto> ordersList = ordersService.getOrdersList(pageable);

        assertNotNull(ordersList);
        assertEquals(ordersList.getTotalElements(), 0);
    }

    @Test
    @DisplayName("정상 주문인지 확인 성공 테스트")
    @WithMockCustomMember
    void 정상_주문_확인_성공_테스트() {
        Orders orders = Orders.createOrders(member);
        OrdersProduct.createOrdersProduct(orders, product1, 5);
        OrdersProduct.createOrdersProduct(orders, product2, 10);

        ordersRepository.saveAndFlush(orders);

        ordersService.isRightOrder(orders.getId());
    }

    @Test
    @DisplayName("정상 주문인지 확인 실패 테스트 - 주문자 다름")
    @WithMockCustomMember
    void 정상_주문_확인_실패_테스트_주문자_다름() {
        Orders orders = Orders.createOrders(member);
        OrdersProduct.createOrdersProduct(orders, product1, 5);
        OrdersProduct.createOrdersProduct(orders, product2, 10);

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
        ordersRepository.saveAndFlush(orders);
        ordersRepository.saveAndFlush(otherOrders);

        CustomException customException = assertThrows(CustomException.class, () -> ordersService.isRightOrder(otherOrders.getId()));
        assertEquals(CustomExceptionCode.NOT_VALID_AUTH_ERROR, customException.getCode());
    }

    @Test
    @DisplayName("주문 상세조회 성공 테스트")
    @WithMockCustomMember
    void 주문_상세조회_성공_테스트() {
        Orders orders = Orders.createOrders(member);
        OrdersProduct.createOrdersProduct(orders, product1, 5);
        OrdersProduct.createOrdersProduct(orders, product2, 10);

        ordersRepository.saveAndFlush(orders);

        OrderResponseDto orderDetail = ordersService.getOrderDetail(orders.getId());

        assertNotNull(orderDetail);
        assertEquals(orderDetail.getCity(), member.getAddress().getCity());
        assertEquals(orderDetail.getOrderProductList().get(0).getProductName(), product1.getProductName());
    }

    @Test
    @DisplayName("주문 상세조회 실패 테스트")
    @WithMockCustomMember
    void 주문_상세조회_실패_테스트() {
        CustomException customException = assertThrows(CustomException.class, () -> ordersService.getOrderDetail(1L));
        assertEquals(CustomExceptionCode.NOT_VALID_ERROR, customException.getCode());
    }
}
