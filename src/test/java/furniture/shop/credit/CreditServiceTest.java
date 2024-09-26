package furniture.shop.credit;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import furniture.shop.configure.exception.CustomException;
import furniture.shop.credit.dto.CreditRefundRequestDto;
import furniture.shop.credit.dto.CreditRequestDto;
import furniture.shop.global.MemberAuthorizationUtil;
import furniture.shop.global.embed.Address;
import furniture.shop.member.Member;
import furniture.shop.order.Orders;
import furniture.shop.order.OrdersProduct;
import furniture.shop.order.OrdersRepository;
import furniture.shop.order.contsant.OrdersStatus;
import furniture.shop.product.Product;
import furniture.shop.product.constant.ProductCategory;
import furniture.shop.product.constant.ProductStatus;
import furniture.shop.product.embed.ProductSize;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CreditServiceTest {

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private CreditRepository creditRepository;

    @Mock
    private IamportClient iamportClient;

    @Mock
    private MemberAuthorizationUtil memberAuthorizationUtil;

    @InjectMocks
    private CreditService creditService;

    Member member;
    Product product;
    OrdersProduct ordersProduct;
    Orders orders;
    Credit credit;

    @Test
    @DisplayName("결제 실행 및 검증 성공 테스트")
    void 결제_성공_테스트() throws IamportResponseException, IOException {
        Long orderId = 0L;

        CreditRequestDto dto = new CreditRequestDto();

        dto.setAmount(100000);
        dto.setPayMethod("card");
        dto.setOrderId(orderId);

        when(ordersRepository.findById(orderId)).thenReturn(Optional.ofNullable(orders));
        when(memberAuthorizationUtil.getMember()).thenReturn(member);

        Payment payment = mock(Payment.class);
        when(payment.getImpUid()).thenReturn("imp_1234");
        when(payment.getStatus()).thenReturn("paid");

        IamportResponse<Payment> paymentResponse = mock(IamportResponse.class);
        when(paymentResponse.getResponse()).thenReturn(payment);

        when(iamportClient.paymentByImpUid(anyString())).thenReturn(paymentResponse);

        creditService.createAndVerifyPayment(dto);

        verify(ordersRepository, times(1)).findById(orderId);
        verify(creditRepository, times(1)).save(any(Credit.class));
        verify(iamportClient, times(2)).paymentByImpUid(anyString());
        verify(memberAuthorizationUtil, times(1)).getMember();
        Assertions.assertEquals(OrdersStatus.FINISH, orders.getOrdersStatus());
        Assertions.assertEquals(10000, member.getMileage());
    }

    @Test
    @DisplayName("결제 시 주문 오류")
    void 결제_주문_오류() {
        Long orderId = 0L;

        CreditRequestDto dto = new CreditRequestDto();

        dto.setAmount(100000);
        dto.setPayMethod("card");
        dto.setOrderId(orderId);

        when(ordersRepository.findById(orderId)).thenReturn(Optional.empty());

        Assertions.assertThrows(CustomException.class, () -> creditService.createAndVerifyPayment(dto));

        verify(ordersRepository, times(1)).findById(any());
        verify(creditRepository, never()).save(any());

    }

    @Test
    @DisplayName("결제 시 회원 오류")
    void 결제_회원_오류() {
        Long orderId = 0L;

        CreditRequestDto dto = new CreditRequestDto();

        dto.setAmount(100000);
        dto.setPayMethod("card");
        dto.setOrderId(orderId);

        when(ordersRepository.findById(orderId)).thenReturn(Optional.ofNullable(orders));

        Member newMember = Member.builder()
                .address(new Address("12345", "서울시 강남구 강남대로 114", "테스트 빌딩 5층"))
                .email("test@test.com")
                .password("123456")
                .phone("01012345678")
                .username("테스터")
                .id(1L)
                .build();

        when(memberAuthorizationUtil.getMember()).thenReturn(newMember);

        Assertions.assertThrows(CustomException.class, () -> creditService.createAndVerifyPayment(dto));

        verify(ordersRepository, times(1)).findById(any());
        verify(memberAuthorizationUtil, times(1)).getMember();
        verify(creditRepository, never()).save(any());
    }

    @Test
    @DisplayName("결제 완료 혹은 취소 인 경우 결제 실패 테스트")
    void 결제_준비상태_아닌_경우_실패_테스트() {
        Long orderId = 0L;

        CreditRequestDto dto = new CreditRequestDto();

        dto.setAmount(100000);
        dto.setPayMethod("card");
        dto.setOrderId(orderId);

        orders.updateOrdersStatus(OrdersStatus.FINISH);

        when(ordersRepository.findById(orderId)).thenReturn(Optional.ofNullable(orders));
        when(memberAuthorizationUtil.getMember()).thenReturn(member);

        Assertions.assertThrows(CustomException.class, () -> creditService.createAndVerifyPayment(dto));

        verify(ordersRepository, times(1)).findById(any());
        verify(memberAuthorizationUtil, times(1)).getMember();
        verify(creditRepository, never()).save(any());
    }

    @Test
    @DisplayName("결제 실패 테스트")
    void 결제_실패_테스트() throws IamportResponseException, IOException {
        Long orderId = 0L;

        CreditRequestDto dto = new CreditRequestDto();

        dto.setAmount(100000);
        dto.setPayMethod("card");
        dto.setOrderId(orderId);

        when(ordersRepository.findById(orderId)).thenReturn(Optional.ofNullable(orders));
        when(memberAuthorizationUtil.getMember()).thenReturn(member);

        Payment payment = mock(Payment.class);
        when(payment.getImpUid()).thenReturn("imp_1234");
        when(payment.getStatus()).thenReturn("failed");

        IamportResponse<Payment> paymentResponse = mock(IamportResponse.class);
        when(paymentResponse.getResponse()).thenReturn(payment);

        when(iamportClient.paymentByImpUid(anyString())).thenReturn(paymentResponse);

        Assertions.assertThrows(CustomException.class, () -> creditService.createAndVerifyPayment(dto));

        verify(ordersRepository, times(1)).findById(orderId);
        verify(creditRepository, never()).save(any(Credit.class));
        verify(iamportClient, times(2)).paymentByImpUid(anyString());
        verify(memberAuthorizationUtil, times(1)).getMember();
    }

    @Test
    @DisplayName("환불 성공 테스트")
    void 환불_성공_테스트() throws IamportResponseException, IOException {
        CreditRefundRequestDto dto = new CreditRefundRequestDto();

        dto.setImpUID("imp_1234");
        dto.setReason("단순 변심");
        dto.setBank("test bank");
        dto.setAccount("12341234");
        dto.setHolder("테스터");

        when(creditRepository.findByImpUID(anyString())).thenReturn(credit);

        Payment payment = mock(Payment.class);
        when(payment.getStatus()).thenReturn("paid");

        IamportResponse<Payment> paymentResponse = mock(IamportResponse.class);
        when(iamportClient.paymentByImpUid(any())).thenReturn(paymentResponse);
        when(paymentResponse.getResponse()).thenReturn(payment);
        when(iamportClient.cancelPaymentByImpUid(any(CancelData.class))).thenReturn(paymentResponse);

        creditService.cancelPayment(dto);

        Assertions.assertEquals(OrdersStatus.CANCEL, orders.getOrdersStatus());
        Assertions.assertNotNull(credit.getCancelledAt());
        Assertions.assertEquals(20, product.getStock());
        Assertions.assertEquals(-10, product.getSellingCount());
        Assertions.assertEquals(-10000, member.getMileage());
    }

    @Test
    @DisplayName("환불 실패 (결제 정보 없음) 테스트")
    void 환불_실패_결제_정보없음() {
        CreditRefundRequestDto dto = new CreditRefundRequestDto();

        dto.setImpUID("imp_1234");
        dto.setReason("단순 변심");
        dto.setBank("test bank");
        dto.setAccount("12341234");
        dto.setHolder("테스터");

        when(creditRepository.findByImpUID(anyString())).thenReturn(null);

        Assertions.assertThrows(CustomException.class, () -> creditService.cancelPayment(dto));
    }

    @Test
    @DisplayName("환불 실패 (결제상태가 아님) 테스트")
    void 환불_실패_결제상태아님() throws IamportResponseException, IOException {
        CreditRefundRequestDto dto = new CreditRefundRequestDto();

        dto.setImpUID("imp_1234");
        dto.setReason("단순 변심");
        dto.setBank("test bank");
        dto.setAccount("12341234");
        dto.setHolder("테스터");

        when(creditRepository.findByImpUID(anyString())).thenReturn(credit);

        Payment payment = mock(Payment.class);
        when(payment.getStatus()).thenReturn("cancelled");

        IamportResponse<Payment> paymentResponse = mock(IamportResponse.class);
        when(iamportClient.paymentByImpUid(any())).thenReturn(paymentResponse);
        when(paymentResponse.getResponse()).thenReturn(payment);

        Assertions.assertThrows(CustomException.class, () -> creditService.cancelPayment(dto));
    }

    @BeforeEach
    void setUp() {
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

        orders = Orders.createOrders(member);
        ordersProduct = OrdersProduct.createOrdersProduct(orders, product, 10);
        credit = Credit.createCredit(orders, 100000, "mer_1234", "imp_1234", "card");
    }

}