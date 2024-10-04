package furniture.shop.review;

import furniture.shop.configure.exception.CustomException;
import furniture.shop.global.MemberAuthorizationUtil;
import furniture.shop.global.embed.Address;
import furniture.shop.member.Member;
import furniture.shop.order.Orders;
import furniture.shop.order.OrdersProduct;
import furniture.shop.order.OrdersRepository;
import furniture.shop.order.contsant.OrdersStatus;
import furniture.shop.product.Product;
import furniture.shop.product.ProductRepository;
import furniture.shop.product.constant.ProductCategory;
import furniture.shop.product.constant.ProductStatus;
import furniture.shop.product.embed.ProductSize;
import furniture.shop.review.dto.ReviewAddRequestDto;
import furniture.shop.review.dto.ReviewEditRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private MemberAuthorizationUtil memberAuthorizationUtil;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrdersRepository ordersRepository;

    Member member;
    Product product;
    Orders orders;
    OrdersProduct ordersProduct;
    Review review;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .productName("테스트 상품")
                .productCode("test-1234")
                .productCategory(ProductCategory.CHAIR)
                .productStatus(ProductStatus.SELLING)
                .size(new ProductSize(50.7, 102.5, 100.3))
                .price(102030)
                .stock(10)
                .description("테스트 상품입니다.")
                .build();

        member = Member.builder()
                .address(new Address("12345", "서울시 강남구 강남대로 114", "테스트 빌딩 5층"))
                .email("test@test.com")
                .password("123456")
                .phone("01012345678")
                .username("테스터")
                .build();

        orders = Orders.createOrders(member);
        orders.updateOrdersStatus(OrdersStatus.FINISH);
        ordersProduct = OrdersProduct.createOrdersProduct(orders, product, 3);
        review = Review.createReview(product, member, "테스트 리뷰입니다.", 5.0);
    }

    @Test
    @DisplayName("리뷰 작성 성공 테스트")
    void 리뷰_작성_성공_테스트() {
        ReviewAddRequestDto reviewAddRequestDto = new ReviewAddRequestDto();

        reviewAddRequestDto.setRate(2.0);
        reviewAddRequestDto.setComment("테스트 리뷰입니다.");
        reviewAddRequestDto.setProductId(1L);

        when(memberAuthorizationUtil.getMember()).thenReturn(member);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(ordersRepository.findByMemberId(any())).thenReturn(List.of(orders));

        reviewService.addReview(reviewAddRequestDto);

        assertEquals(2, product.getReviews().size());
    }

    @Test
    @DisplayName("리뷰 작성 실패 테스트 - 상품 오류")
    void 리뷰_작성_실패_테스트_상품() {
        ReviewAddRequestDto reviewAddRequestDto = new ReviewAddRequestDto();

        reviewAddRequestDto.setRate(2.0);
        reviewAddRequestDto.setComment("테스트 리뷰입니다.");
        reviewAddRequestDto.setProductId(1L);

        when(memberAuthorizationUtil.getMember()).thenReturn(member);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> reviewService.addReview(reviewAddRequestDto));
    }

    @Test
    @DisplayName("리뷰 작성 실패 테스트 - 주문 오류")
    void 리뷰_작성_실패_테스트_주문() {
        ReviewAddRequestDto reviewAddRequestDto = new ReviewAddRequestDto();

        reviewAddRequestDto.setRate(2.0);
        reviewAddRequestDto.setComment("테스트 리뷰입니다.");
        reviewAddRequestDto.setProductId(1L);

        when(memberAuthorizationUtil.getMember()).thenReturn(member);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        orders.updateOrdersStatus(OrdersStatus.READY);

        assertThrows(CustomException.class, () -> reviewService.addReview(reviewAddRequestDto));
    }

    @Test
    @DisplayName("리뷰 작성 실패 테스트 - 입력 오류")
    void 리뷰_작성_실패_테스트_입력() {
        ReviewAddRequestDto reviewAddRequestDto = new ReviewAddRequestDto();

        reviewAddRequestDto.setRate(5.8);
        reviewAddRequestDto.setComment("테스트 리뷰입니다.");
        reviewAddRequestDto.setProductId(1L);

        when(memberAuthorizationUtil.getMember()).thenReturn(member);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(CustomException.class, () -> reviewService.addReview(reviewAddRequestDto));
    }

    @Test
    @DisplayName("리뷰 수정 성공 테스트")
    void 리뷰_수정_성공_테스트() {
        ReviewEditRequestDto dto = new ReviewEditRequestDto();

        dto.setReviewId(6L);
        dto.setComment("수정된 리뷰입니다.");
        dto.setRate(2.7);

        when(memberAuthorizationUtil.getMember()).thenReturn(member);
        when(reviewRepository.findById(6L)).thenReturn(Optional.of(review));

        reviewService.editReview(dto);

        assertEquals("수정된 리뷰입니다.", review.getComment());
        assertEquals(2.7, review.getRate());
    }

    @Test
    @DisplayName("리뷰 수정 실패 테스트 - 리뷰 오류")
    void 리뷰_수정_실패_테스트_리뷰() {
        ReviewEditRequestDto dto = new ReviewEditRequestDto();

        dto.setReviewId(6L);
        dto.setComment("수정된 리뷰입니다.");
        dto.setRate(2.7);

        when(memberAuthorizationUtil.getMember()).thenReturn(member);
        when(reviewRepository.findById(6L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> reviewService.editReview(dto));
    }

    @Test
    @DisplayName("리뷰 수정 실패 테스트 - 작성자 오류")
    void 리뷰_수정_실패_테스트_작성자() {
        ReviewEditRequestDto dto = new ReviewEditRequestDto();

        dto.setReviewId(6L);
        dto.setComment("수정된 리뷰입니다.");
        dto.setRate(2.7);

        Member member2 = Member.builder()
                .address(new Address("12345", "서울시 강남구 강남대로 114", "테스트 빌딩 5층"))
                .email("test@test.com")
                .password("123456")
                .phone("01012345678")
                .username("테스터")
                .id(5L)
                .build();

        when(memberAuthorizationUtil.getMember()).thenReturn(member2);
        when(reviewRepository.findById(6L)).thenReturn(Optional.of(review));

        assertThrows(CustomException.class, () -> reviewService.editReview(dto));
    }

    @Test
    @DisplayName("리뷰 삭제 성공 테스트")
    void 리뷰_삭제_성공_테스트() {
        when(memberAuthorizationUtil.getMember()).thenReturn(member);
        when(reviewRepository.findById(6L)).thenReturn(Optional.of(review));

        reviewService.deleteReview(6L);

        assertEquals(0, product.getReviews().size());
    }

    @Test
    @DisplayName("리뷰 삭제 실패 테스트 - 리뷰 오류")
    void 리뷰_삭제_실패_테스트_리뷰() {
        when(memberAuthorizationUtil.getMember()).thenReturn(member);
        when(reviewRepository.findById(6L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> reviewService.deleteReview(6L));
    }

    @Test
    @DisplayName("리뷰 삭제 실패 테스트 - 회원 오류")
    void 리뷰_삭제_실패_테스트_회원() {
        Member member2 = Member.builder()
                .address(new Address("12345", "서울시 강남구 강남대로 114", "테스트 빌딩 5층"))
                .email("test@test.com")
                .password("123456")
                .phone("01012345678")
                .username("테스터")
                .id(5L)
                .build();

        when(memberAuthorizationUtil.getMember()).thenReturn(member2);
        when(reviewRepository.findById(6L)).thenReturn(Optional.of(review));

        assertThrows(CustomException.class, () -> reviewService.deleteReview(6L));
    }
}