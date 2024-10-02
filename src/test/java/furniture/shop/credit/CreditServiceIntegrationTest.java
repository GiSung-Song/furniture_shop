package furniture.shop.credit;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import furniture.shop.configure.exception.CustomException;
import furniture.shop.configure.exception.CustomExceptionCode;
import furniture.shop.credit.dto.CreditRefundRequestDto;
import furniture.shop.credit.dto.CreditRequestDto;
import furniture.shop.global.WithMockCustomMember;
import furniture.shop.global.embed.Address;
import furniture.shop.member.Member;
import furniture.shop.member.MemberRepository;
import furniture.shop.member.constant.MemberGender;
import furniture.shop.order.Orders;
import furniture.shop.order.OrdersProduct;
import furniture.shop.order.OrdersRepository;
import furniture.shop.order.contsant.OrdersStatus;
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
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CreditServiceIntegrationTest {

    @Autowired
    private CreditService creditService;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CreditRepository creditRepository;

    @MockBean
    private IamportClient iamportClient;

    @PersistenceContext
    private EntityManager entityManager;

    private Product product1;
    private Product product2;
    private Member member;
    private Orders orders;

    @BeforeEach
    void setUp() {
        product1 = Product.builder()
                .productCode("code-1111")
                .productName("product1111")
                .productCategory(ProductCategory.CHAIR)
                .stock(100)
                .price(10)
                .size(new ProductSize(10.5, 10.2, 10.4))
                .description("테스트1111 상품입니다.")
                .build();

        product2 = Product.builder()
                .productCode("code-2222")
                .productName("product2222")
                .productCategory(ProductCategory.BED)
                .stock(50)
                .price(5)
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

        orders = Orders.createOrders(member);
        OrdersProduct.createOrdersProduct(orders, product1, 5);
        OrdersProduct.createOrdersProduct(orders, product2, 10);

        ordersRepository.save(orders);

        entityManager.flush();
        entityManager.clear();
    }

    void setIamportClient(String status) throws IamportResponseException, IOException {
        IamportResponse<Payment> paymentResponse = mock(IamportResponse.class);

        Payment payment = mock(Payment.class);
        when(payment.getStatus()).thenReturn(status);
        when(iamportClient.paymentByImpUid(any())).thenReturn(paymentResponse);
        when(paymentResponse.getResponse()).thenReturn(payment);
    }

    @Test
    @DisplayName("결제 요청 및 검증 성공 테스트")
    @WithMockCustomMember
    void 결제요청_검증_성공_테스트() throws IamportResponseException, IOException {
        setIamportClient("paid");

        CreditRequestDto dto = new CreditRequestDto();

        dto.setAmount(100);
        dto.setPayMethod("card");
        dto.setOrderId(orders.getId());
        dto.setMerchantUID("merchant_1234");
        dto.setImpUID("imp_1234");

        creditService.createAndVerifyPayment(dto);

        Orders findOrders = ordersRepository.findById(orders.getId()).orElse(null);

        assertNotNull(findOrders);
        assertEquals(OrdersStatus.FINISH, findOrders.getOrdersStatus());

        Product findProduct1 = productRepository.findById(product1.getId()).orElse(null);
        Product findProduct2 = productRepository.findById(product2.getId()).orElse(null);

        assertNotNull(findProduct1);
        assertNotNull(findProduct2);
        assertEquals(95, findProduct1.getStock());
        assertEquals(40, findProduct2.getStock());
        assertEquals(5, findProduct1.getSellingCount());
        assertEquals(10, findProduct2.getSellingCount());

        Credit credit = creditRepository.findAll().get(0);

        assertNotNull(credit);
        assertEquals(100, credit.getAmount());
    }

    @Test
    @DisplayName("결제 실패 테스트 - 주문 오류")
    @WithMockCustomMember
    void 결제_실패_테스트_주문() {
        CreditRequestDto creditRequestDto = new CreditRequestDto();

        creditRequestDto.setAmount(100);
        creditRequestDto.setOrderId(4321L);
        creditRequestDto.setPayMethod("KAKAO_PAY");
        creditRequestDto.setMerchantUID("merchant_1234");
        creditRequestDto.setImpUID("imp_1234");

        CustomException customException = assertThrows(CustomException.class, () -> creditService.createAndVerifyPayment(creditRequestDto));
        assertEquals(CustomExceptionCode.NOT_VALID_ERROR, customException.getCode());
    }

    @Test
    @DisplayName("결제 실패 테스트 - 사용자 오류")
    @WithMockCustomMember(email = "test2@test2.com")
    void 결제_실패_테스트_사용자() {
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

        CreditRequestDto creditRequestDto = new CreditRequestDto();

        creditRequestDto.setAmount(100);
        creditRequestDto.setOrderId(orders.getId());
        creditRequestDto.setPayMethod("KAKAO_PAY");
        creditRequestDto.setMerchantUID("merchant_1234");
        creditRequestDto.setImpUID("imp_1234");

        CustomException customException = assertThrows(CustomException.class, () -> creditService.createAndVerifyPayment(creditRequestDto));
        assertEquals(CustomExceptionCode.NOT_VALID_AUTH_ERROR, customException.getCode());
    }

    @Test
    @DisplayName("결제 실패 테스트 - 주문 상태 오류")
    @WithMockCustomMember
    void 결제_실패_테스트_주문상태() {
        orders.updateOrdersStatus(OrdersStatus.FINISH);
        ordersRepository.saveAndFlush(orders);

        CreditRequestDto creditRequestDto = new CreditRequestDto();

        creditRequestDto.setAmount(100);
        creditRequestDto.setOrderId(orders.getId());
        creditRequestDto.setPayMethod("KAKAO_PAY");
        creditRequestDto.setMerchantUID("merchant_1234");
        creditRequestDto.setImpUID("imp_1234");

        CustomException customException = assertThrows(CustomException.class, () -> creditService.createAndVerifyPayment(creditRequestDto));
        assertEquals(CustomExceptionCode.NOT_VALID_ERROR, customException.getCode());
    }

    @Test
    @DisplayName("결제 실패 테스트 - 결제 실패")
    @WithMockCustomMember
    void 결제_실패_테스트_결제실패() throws IamportResponseException, IOException {
        setIamportClient("failed");

        CreditRequestDto creditRequestDto = new CreditRequestDto();

        creditRequestDto.setAmount(100);
        creditRequestDto.setOrderId(orders.getId());
        creditRequestDto.setPayMethod("KAKAO_PAY");
        creditRequestDto.setMerchantUID("merchant_1234");
        creditRequestDto.setImpUID("imp_1234");

        CustomException customException = assertThrows(CustomException.class, () -> creditService.createAndVerifyPayment(creditRequestDto));
        assertEquals(CustomExceptionCode.FAIL_PAYMENT, customException.getCode());
    }

    @Test
    @DisplayName("환불 성공 테스트")
    @WithMockCustomMember
    void 환불_성공_테스트() throws IamportResponseException, IOException {
        setIamportClient("paid");

        //결제 성공
        CreditRequestDto creditRequestDto = new CreditRequestDto();
        creditRequestDto.setAmount(100);
        creditRequestDto.setOrderId(orders.getId());
        creditRequestDto.setPayMethod("KAKAO_PAY");
        creditRequestDto.setMerchantUID("merchant_1234");
        creditRequestDto.setImpUID("imp_1234");

        creditService.createAndVerifyPayment(creditRequestDto);

        Payment payment = mock(Payment.class);
        when(payment.getStatus()).thenReturn("cancelled");

        IamportResponse<Payment> iamportResponse = mock(IamportResponse.class);
        when(iamportResponse.getResponse()).thenReturn(payment);
        when(iamportClient.cancelPaymentByImpUid(any())).thenReturn(iamportResponse);

        CreditRefundRequestDto creditRefundRequestDto = new CreditRefundRequestDto();
        creditRefundRequestDto.setImpUID("imp_1234");

        creditService.cancelPayment(creditRefundRequestDto);

        Orders findOrders = ordersRepository.findById(orders.getId()).orElse(null);
        assertNotNull(findOrders);
        assertEquals(OrdersStatus.CANCEL, findOrders.getOrdersStatus());

        Credit findCredit = creditRepository.findByOrdersId(orders.getId());
        assertNotNull(findCredit);
        assertNotNull(findCredit.getCancelledAt());
    }

    @Test
    @DisplayName("환불 실패 테스트")
    @WithMockCustomMember
    void 환불_실패_테스트() throws IamportResponseException, IOException {
        setIamportClient("ready");

        CreditRefundRequestDto creditRefundRequestDto = new CreditRefundRequestDto();
        creditRefundRequestDto.setImpUID("imp_1234");

        CustomException customException = assertThrows(CustomException.class, () -> creditService.cancelPayment(creditRefundRequestDto));
        assertEquals(CustomExceptionCode.NOT_VALID_ERROR, customException.getCode());
    }

}
