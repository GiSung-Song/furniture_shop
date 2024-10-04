package furniture.shop.review;

import furniture.shop.configure.exception.CustomException;
import furniture.shop.configure.exception.CustomExceptionCode;
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
import furniture.shop.review.dto.ReviewAddRequestDto;
import furniture.shop.review.dto.ReviewEditRequestDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ReviewServiceIntegrationTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Product product;
    private Member member;
    private Orders orders;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .productCode("code-1111")
                .productName("product1111")
                .productCategory(ProductCategory.CHAIR)
                .stock(100)
                .price(10)
                .size(new ProductSize(10.5, 10.2, 10.4))
                .description("테스트1111 상품입니다.")
                .build();

        productRepository.save(product);

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
        OrdersProduct.createOrdersProduct(orders, product, 5);
        orders.updateOrdersStatus(OrdersStatus.FINISH);

        ordersRepository.save(orders);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("리뷰 등록 성공 테스트")
    @WithMockCustomMember
    void 리뷰_등록_성공_테스트() {
        ReviewAddRequestDto reviewAddRequestDto = new ReviewAddRequestDto();

        reviewAddRequestDto.setProductId(product.getId());
        reviewAddRequestDto.setRate(2.9);
        reviewAddRequestDto.setComment("테스트 리뷰입니다.");

        reviewService.addReview(reviewAddRequestDto);

        Review review = reviewRepository.findAll().get(0);

        assertEquals(2.9, review.getRate());
        assertEquals(product.getId(), review.getProduct().getId());
        assertEquals("테스트 리뷰입니다.", review.getComment());
        assertEquals(member.getId(), review.getMember().getId());
    }

    @Test
    @DisplayName("리뷰 등록 실패 테스트 - 상품 오류")
    @WithMockCustomMember
    void 리뷰_등록_실패_테스트_상품() {
        ReviewAddRequestDto reviewAddRequestDto = new ReviewAddRequestDto();

        reviewAddRequestDto.setProductId(4324L);
        reviewAddRequestDto.setRate(2.9);
        reviewAddRequestDto.setComment("테스트 리뷰입니다.");

        CustomException customException = assertThrows(CustomException.class, () -> reviewService.addReview(reviewAddRequestDto));
        assertEquals(CustomExceptionCode.NOT_VALID_ERROR, customException.getCode());
    }

    @Test
    @DisplayName("리뷰 등록 실패 테스트 - 구매 이력 X")
    @WithMockCustomMember(email = "test2@test2.com")
    void 리뷰_등록_실패_테스트_구매() {
        Member member2 = Member.builder()
                .username("테스터")
                .email("test2@test2.com")
                .password("password")
                .phone("01012341234")
                .gender(MemberGender.MALE)
                .address(new Address("11232", "서울시 서울구 서울로", "11 서울아파트 11동 111호"))
                .build();

        //member 미리 저장 하여 @WithMockCustomMember 에서 findByEmail != null 을 하기 위함.
        memberRepository.saveAndFlush(member2);

        ReviewAddRequestDto reviewAddRequestDto = new ReviewAddRequestDto();

        reviewAddRequestDto.setProductId(4324L);
        reviewAddRequestDto.setRate(2.9);
        reviewAddRequestDto.setComment("테스트 리뷰입니다.");

        CustomException customException = assertThrows(CustomException.class, () -> reviewService.addReview(reviewAddRequestDto));
        assertEquals(CustomExceptionCode.NOT_VALID_ERROR, customException.getCode());
    }

    @Test
    @DisplayName("리뷰 등록 실패 테스트 - 입력 오류")
    @WithMockCustomMember
    void 리뷰_등록_실패_테스트_입력() {
        ReviewAddRequestDto reviewAddRequestDto = new ReviewAddRequestDto();

        reviewAddRequestDto.setProductId(product.getId());
        reviewAddRequestDto.setRate(5.9);
        reviewAddRequestDto.setComment("테스트 리뷰입니다.");

        CustomException customException = assertThrows(CustomException.class, () -> reviewService.addReview(reviewAddRequestDto));
        assertEquals(CustomExceptionCode.NOT_VALID_ERROR, customException.getCode());
    }

    @Test
    @DisplayName("리뷰 수정 성공 테스트")
    @WithMockCustomMember
    void 리뷰_수정_성공_테스트() {
        Review review = Review.createReview(product, member, "테스트 리뷰입니다.", 2.9);
        reviewRepository.saveAndFlush(review);

        ReviewEditRequestDto reviewEditRequestDto = new ReviewEditRequestDto();

        reviewEditRequestDto.setReviewId(review.getId());
        reviewEditRequestDto.setRate(4.9);
        reviewEditRequestDto.setComment("수정된 리뷰입니다.");

        reviewService.editReview(reviewEditRequestDto);

        Review findReview = reviewRepository.findAll().get(0);

        assertNotNull(findReview);
        assertEquals(reviewEditRequestDto.getRate(), findReview.getRate());
        assertEquals(reviewEditRequestDto.getComment(), findReview.getComment());
    }

    @Test
    @DisplayName("리뷰 수정 실패 테스트 - 리뷰 오류")
    @WithMockCustomMember
    void 리뷰_수정_실패_테스트_리뷰() {
        Review review = Review.createReview(product, member, "테스트 리뷰입니다.", 2.9);
        reviewRepository.saveAndFlush(review);

        ReviewEditRequestDto reviewEditRequestDto = new ReviewEditRequestDto();

        reviewEditRequestDto.setReviewId(4324L);
        reviewEditRequestDto.setRate(4.9);
        reviewEditRequestDto.setComment("수정된 리뷰입니다.");

        CustomException customException = assertThrows(CustomException.class, () -> reviewService.editReview(reviewEditRequestDto));
        assertEquals(CustomExceptionCode.NOT_VALID_ERROR, customException.getCode());
    }

    @Test
    @DisplayName("리뷰 수정 실패 테스트 - 작성자 오류")
    @WithMockCustomMember(email = "test2@test2.com")
    void 리뷰_수정_실패_테스트_작성자() {
        Member member2 = Member.builder()
                .username("테스터")
                .email("test2@test2.com")
                .password("password")
                .phone("01012341234")
                .gender(MemberGender.MALE)
                .address(new Address("11232", "서울시 서울구 서울로", "11 서울아파트 11동 111호"))
                .build();

        //member 미리 저장 하여 @WithMockCustomMember 에서 findByEmail != null 을 하기 위함.
        memberRepository.saveAndFlush(member2);

        ReviewEditRequestDto reviewEditRequestDto = new ReviewEditRequestDto();

        reviewEditRequestDto.setReviewId(4324L);
        reviewEditRequestDto.setRate(4.9);
        reviewEditRequestDto.setComment("수정된 리뷰입니다.");

        CustomException customException = assertThrows(CustomException.class, () -> reviewService.editReview(reviewEditRequestDto));
        assertEquals(CustomExceptionCode.NOT_VALID_ERROR, customException.getCode());
    }

    @Test
    @DisplayName("리뷰 삭제 성공 테스트")
    @WithMockCustomMember
    void 리뷰_삭제_성공_테스트() {
        Review review = Review.createReview(product, member, "테스트 리뷰입니다.", 2.9);
        reviewRepository.saveAndFlush(review);

        assertEquals(1, product.getReviews().size());

        reviewService.deleteReview(review.getId());

        assertEquals(0, product.getReviews().size());
    }

    @Test
    @DisplayName("리뷰 삭제 실패 테스트 - 리뷰 오류")
    @WithMockCustomMember
    void 리뷰_삭제_실패_테스트_리뷰() {
        Review review = Review.createReview(product, member, "테스트 리뷰입니다.", 2.9);
        reviewRepository.saveAndFlush(review);

        CustomException customException = assertThrows(CustomException.class, () -> reviewService.deleteReview(231L));
        assertEquals(CustomExceptionCode.NOT_VALID_ERROR, customException.getCode());
    }

    @Test
    @DisplayName("리뷰 삭제 실패 테스트 - 회원 오류")
    @WithMockCustomMember(email = "test2@test2.com")
    void 리뷰_삭제_실패_테스트_회원() {
        Member member2 = Member.builder()
                .username("테스터")
                .email("test2@test2.com")
                .password("password")
                .phone("01012341234")
                .gender(MemberGender.MALE)
                .address(new Address("11232", "서울시 서울구 서울로", "11 서울아파트 11동 111호"))
                .build();

        //member 미리 저장 하여 @WithMockCustomMember 에서 findByEmail != null 을 하기 위함.
        memberRepository.saveAndFlush(member2);

        Review review = Review.createReview(product, member, "테스트 리뷰입니다.", 2.9);
        reviewRepository.saveAndFlush(review);

        CustomException customException = assertThrows(CustomException.class, () -> reviewService.deleteReview(review.getId()));
        assertEquals(CustomExceptionCode.NOT_VALID_AUTH_ERROR, customException.getCode());
    }
}
