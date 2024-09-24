package furniture.shop.order;

import furniture.shop.cart.Cart;
import furniture.shop.cart.CartProduct;
import furniture.shop.cart.CartRepository;
import furniture.shop.configure.exception.CustomException;
import furniture.shop.global.MemberAuthorizationUtil;
import furniture.shop.global.embed.Address;
import furniture.shop.member.Member;
import furniture.shop.order.contsant.OrdersStatus;
import furniture.shop.order.dto.OrderProductResponseDto;
import furniture.shop.order.dto.OrderResponseDto;
import furniture.shop.order.dto.OrderSingleRequestDto;
import furniture.shop.order.dto.OrdersListResponseDto;
import furniture.shop.product.Product;
import furniture.shop.product.ProductRepository;
import furniture.shop.product.constant.ProductCategory;
import furniture.shop.product.constant.ProductStatus;
import furniture.shop.product.embed.ProductSize;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {

    @InjectMocks
    private OrdersService ordersService;

    @Mock
    private MemberAuthorizationUtil memberAuthorizationUtil;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private OrdersProductRepository ordersProductRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrdersQueryRepository ordersQueryRepository;

    Member member;
    Product product;
    Product product2;
    Cart cart;
    Cart cart2;
    Orders orders;
    OrdersProduct ordersProduct;

    @Test
    @DisplayName("단품 주문 테스트")
    void 단품_주문_테스트() {
        setMember();
        setProduct();

        OrderSingleRequestDto orderSingleRequestDto = new OrderSingleRequestDto();

        orderSingleRequestDto.setCount(10);
        orderSingleRequestDto.setProductId(0L);

        OrderResponseDto singleOrder = ordersService.createSingleOrder(orderSingleRequestDto);

        verify(ordersRepository, times(1)).save(any());

        OrderProductResponseDto orderProductResponseDto = singleOrder.getOrderProductList().get(0);

        Assertions.assertEquals(orderProductResponseDto.getProductName(), product.getProductName());
    }

    @Test
    @DisplayName("장바구니 주문 테스트")
    void 장바구니_주문_테스트() {
        setMember();
        setProduct();
        setCart();

        OrderResponseDto cartOrder = ordersService.createCartOrder();

        verify(ordersRepository, times(1)).save(any());
        OrderProductResponseDto orderProductResponseDto = cartOrder.getOrderProductList().get(0);

        Assertions.assertEquals(orderProductResponseDto.getProductName(), product.getProductName());
    }

    @Test
    @DisplayName("장바구니 주문 테스트2")
    void 장바구니_주문_테스트2() {
        setMember();
        setProduct();
        setProduct2();
        setCart2();

        OrderResponseDto cartOrder = ordersService.createCartOrder();

        verify(ordersRepository, times(1)).save(any());

        Assertions.assertEquals(cartOrder.getOrderProductList().size(), 2);
    }

    @Test
    @DisplayName("주문자 확인 테스트1")
    void 주문자_확인_테스트1() {
        setMember();
        setOrder();

        ordersService.isRightOrder(orders.getId());
        verify(ordersRepository, times(1)).findById(any());
    }

    @Test
    @DisplayName("주문자 확인 테스트2")
    void 주문자_확인_테스트2() {
        member = Member.builder()
                .address(new Address("12345", "서울시 강남구 강남대로 114", "테스트 빌딩 5층"))
                .email("test@test.com")
                .password("123456")
                .phone("01012345678")
                .username("테스터")
                .id(0L)
                .build();

        setOrder();

        Member member2 = Member.builder()
                .address(new Address("12345", "서울시 강남구 강남대로 114", "테스트 빌딩 5층"))
                .email("test@test.com")
                .password("123456")
                .phone("01012345678")
                .username("테스터")
                .id(1L)
                .build();

        given(memberAuthorizationUtil.getMember()).willReturn(member2);

        Assertions.assertThrows(CustomException.class, () -> ordersService.isRightOrder(orders.getId()));
    }

    @Test
    @DisplayName("주문 상세조회")
    void 주문_상세조회_테스트() {
        setOrders();

        OrderResponseDto orderDetail = ordersService.getOrderDetail(orders.getId());

        Assertions.assertEquals(orderDetail.getOrderProductList().size(), 2);
        Assertions.assertEquals(orderDetail.getCity(), member.getAddress().getCity());
    }

    @Test
    @DisplayName("주문 목록 조회 테스트")
    void 주문_목록_조회_테스트() {
        setMember();

        List<OrdersListResponseDto> ordersListResponseDtoList = new ArrayList<>();

        for (int i = 0; i <= 11; i++) {
            OrdersListResponseDto ordersListResponseDto = new OrdersListResponseDto();

            ordersListResponseDto.setOrderId(Long.valueOf(i));
            ordersListResponseDto.setOrdersStatus(OrdersStatus.READY);
            ordersListResponseDto.setCity("서울시");
            ordersListResponseDto.setReceiver("수령인");
            ordersListResponseDto.setStreet("강남구 2층");
            ordersListResponseDto.setZipCode("13362");
        }

        PageRequest pageable = PageRequest.of(0, 10);

        Page<OrdersListResponseDto> pageListDto = new PageImpl<>(ordersListResponseDtoList, pageable, 12);

        when(ordersQueryRepository.getOrderList(member, pageable)).thenReturn(pageListDto);

        Page<OrdersListResponseDto> ordersList = ordersService.getOrdersList(pageable);

        Assertions.assertEquals(ordersList.getTotalElements(), pageListDto.getTotalElements());
        Assertions.assertEquals(ordersList.getTotalPages(), pageListDto.getTotalPages());
    }

    private void setMember() {
        member = Member.builder()
                .address(new Address("12345", "서울시 강남구 강남대로 114", "테스트 빌딩 5층"))
                .email("test@test.com")
                .password("123456")
                .phone("01012345678")
                .username("테스터")
                .id(0L)
                .build();

        given(memberAuthorizationUtil.getMember()).willReturn(member);
    }

    private void setProduct() {
        product = Product.builder()
                .id(0L)
                .productName("테스트 상품")
                .productCode("test-1234")
                .productCategory(ProductCategory.CHAIR)
                .productStatus(ProductStatus.SELLING)
                .size(new ProductSize(50.7, 102.5, 100.3))
                .price(100)
                .sellingCount(0L)
                .stock(10)
                .description("테스트 상품입니다.")
                .build();

        when(productRepository.findById(any())).thenReturn(Optional.ofNullable(product));
    }

    private void setProduct2() {
        product2 = Product.builder()
                .id(1L)
                .productName("테스트 상품2")
                .productCode("test-12345")
                .productCategory(ProductCategory.BED)
                .productStatus(ProductStatus.SELLING)
                .size(new ProductSize(50.4, 102.2, 100.1))
                .price(1050)
                .sellingCount(0L)
                .stock(10754)
                .description("테스트2 상품입니다.")
                .build();

        when(productRepository.findById(any())).thenReturn(Optional.ofNullable(product2));
    }

    void setCart() {
        cart = Cart.createCart(member);
        CartProduct cartProduct = CartProduct.createCartProduct(cart, product, 5);

        given(cartRepository.findByMemberId(any())).willReturn(cart);
    }

    void setCart2() {
        cart2 = Cart.createCart(member);
        CartProduct cartProduct1 = CartProduct.createCartProduct(cart2, product, 5);
        CartProduct cartProduct2 = CartProduct.createCartProduct(cart2, product2, 5);

        given(cartRepository.findByMemberId(any())).willReturn(cart2);
    }

    void setOrder() {
        orders = Orders.createOrders(member);

        given(ordersRepository.findById(any())).willReturn(Optional.ofNullable(orders));
    }

    void setOrders() {
        member = Member.builder()
                .address(new Address("12345", "서울시 강남구 강남대로 114", "테스트 빌딩 5층"))
                .email("test@test.com")
                .password("123456")
                .phone("01012345678")
                .username("테스터")
                .id(0L)
                .build();

        product = Product.builder()
                .id(0L)
                .productName("테스트 상품")
                .productCode("test-1234")
                .productCategory(ProductCategory.CHAIR)
                .productStatus(ProductStatus.SELLING)
                .size(new ProductSize(50.7, 102.5, 100.3))
                .price(100)
                .sellingCount(0L)
                .stock(10)
                .description("테스트 상품입니다.")
                .build();

        product2 = Product.builder()
                .id(1L)
                .productName("테스트 상품2")
                .productCode("test-12345")
                .productCategory(ProductCategory.BED)
                .productStatus(ProductStatus.SELLING)
                .size(new ProductSize(50.4, 102.2, 100.1))
                .price(1050)
                .sellingCount(0L)
                .stock(10754)
                .description("테스트2 상품입니다.")
                .build();

        orders = Orders.createOrders(member);
        ordersProduct = OrdersProduct.createOrdersProduct(orders, product, 3);
        ordersProduct = OrdersProduct.createOrdersProduct(orders, product2, 10);

        given(ordersRepository.findById(any())).willReturn(Optional.ofNullable(orders));
    }

}