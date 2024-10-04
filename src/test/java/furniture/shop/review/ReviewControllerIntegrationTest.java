package furniture.shop.review;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class ReviewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

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
    }

    @Test
    @DisplayName("리뷰 등록 성공 테스트")
    @WithMockCustomMember
    void 리뷰_등록_성공_테스트() throws Exception {
        ReviewAddRequestDto dto = new ReviewAddRequestDto();

        dto.setComment("리뷰 등록 테스트입니다.");
        dto.setRate(4.9);

        mockMvc.perform(post("/product/{id}/review", product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andDo(print());

        Review review = reviewRepository.findAll().get(0);

        assertThat(review.getComment()).isEqualTo(dto.getComment());
        assertThat(review.getRate()).isEqualTo(dto.getRate());
        assertThat(product.getReviews().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("리뷰 등록 실패 테스트 - 입력")
    @WithMockCustomMember
    void 리뷰_등록_실패_테스트_입력() throws Exception {
        ReviewAddRequestDto dto = new ReviewAddRequestDto();

        dto.setRate(4.9);

        mockMvc.perform(post("/product/{id}/review", product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        assertThat(reviewRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("리뷰 등록 실패 테스트 - 권한")
    void 리뷰_등록_실패_테스트_권한() throws Exception {
        ReviewAddRequestDto dto = new ReviewAddRequestDto();

        dto.setRate(4.9);

        mockMvc.perform(post("/product/{id}/review", product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andDo(print());

        assertThat(reviewRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("리뷰 등록 실패 테스트 - 상품 없음")
    @WithMockCustomMember
    void 리뷰_등록_실패_테스트_상품() throws Exception {
        ReviewAddRequestDto dto = new ReviewAddRequestDto();

        dto.setComment("리뷰 등록 테스트입니다.");
        dto.setRate(4.9);

        mockMvc.perform(post("/product/{id}/review", 4321)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("리뷰 등록 실패 테스트 - 구매 이력 없음")
    @WithMockCustomMember(email = "test2@test2.com")
    void 리뷰_등록_실패_테스트_구매() throws Exception {
        createMember2();

        ReviewAddRequestDto dto = new ReviewAddRequestDto();

        dto.setComment("리뷰 등록 테스트입니다.");
        dto.setRate(4.9);

        mockMvc.perform(post("/product/{id}/review", product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("리뷰 수정 성공 테스트")
    @WithMockCustomMember
    void 리뷰_수정_성공_테스트() throws Exception {
        Review review = Review.createReview(product, member, "등록된 리뷰입니다.", 4.9);
        reviewRepository.saveAndFlush(review);

        ReviewEditRequestDto dto = new ReviewEditRequestDto();

        dto.setRate(1.1);
        dto.setComment("수정된 리뷰입니다.");

        mockMvc.perform(patch("/review/{id}", review.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andDo(print());

        assertThat(review.getRate()).isEqualTo(dto.getRate());
        assertThat(review.getComment()).isEqualTo(dto.getComment());
    }

    @Test
    @DisplayName("리뷰 수정 실패 테스트 - 입력 오류")
    @WithMockCustomMember
    void 리뷰_수정_실패_테스트_입력() throws Exception {
        Review review = Review.createReview(product, member, "등록된 리뷰입니다.", 4.9);
        reviewRepository.saveAndFlush(review);

        ReviewEditRequestDto dto = new ReviewEditRequestDto();

        dto.setRate(1.1);

        mockMvc.perform(patch("/review/{id}", review.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("리뷰 수정 실패 테스트 - 권한")
    void 리뷰_수정_실패_테스트_권한() throws Exception {
        Review review = Review.createReview(product, member, "등록된 리뷰입니다.", 4.9);
        reviewRepository.saveAndFlush(review);

        ReviewEditRequestDto dto = new ReviewEditRequestDto();

        dto.setRate(1.1);

        mockMvc.perform(patch("/review/{id}", review.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("리뷰 수정 실패 테스트 - 리뷰 없음")
    @WithMockCustomMember
    void 리뷰_수정_실패_테스트_리뷰() throws Exception {
        ReviewEditRequestDto dto = new ReviewEditRequestDto();

        dto.setComment("수정된 리뷰입니다.");
        dto.setRate(1.1);

        mockMvc.perform(patch("/review/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("리뷰 수정 실패 테스트 - 회원 다름")
    @WithMockCustomMember(email = "test2@test2.com")
    void 리뷰_수정_실패_테스트_회원() throws Exception {
        Review review = Review.createReview(product, member, "등록된 리뷰입니다.", 4.9);
        reviewRepository.saveAndFlush(review);

        createMember2();
        ReviewEditRequestDto dto = new ReviewEditRequestDto();

        dto.setComment("수정된 리뷰입니다.");
        dto.setRate(1.1);

        mockMvc.perform(patch("/review/{id}", review.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("리뷰 삭제 성공 테스트")
    @WithMockCustomMember
    void 리뷰_삭제_성공_테스트() throws Exception {
        Review review = Review.createReview(product, member, "등록된 리뷰입니다.", 4.9);
        reviewRepository.saveAndFlush(review);

        mockMvc.perform(delete("/review/{id}", review.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        assertThat(reviewRepository.findAll().size()).isEqualTo(0);
        assertThat(product.getReviews().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("리뷰 삭제 실패 테스트 - 권한")
    void 리뷰_삭제_실패_테스트_권한() throws Exception {
        Review review = Review.createReview(product, member, "등록된 리뷰입니다.", 4.9);
        reviewRepository.saveAndFlush(review);

        mockMvc.perform(delete("/review/{id}", review.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("리뷰 삭제 실패 테스트 - 회원 다름")
    @WithMockCustomMember(email = "test2@test2.com")
    void 리뷰_삭제_실패_테스트_회원() throws Exception {
        createMember2();

        Review review = Review.createReview(product, member, "등록된 리뷰입니다.", 4.9);
        reviewRepository.saveAndFlush(review);

        mockMvc.perform(delete("/review/{id}", review.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("리뷰 삭제 실패 테스트 - 리뷰 오류")
    @WithMockCustomMember()
    void 리뷰_삭제_실패_테스트_리뷰() throws Exception {
        mockMvc.perform(delete("/review/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    void createMember2() {
        Member member2 = Member.builder()
                .username("테스터")
                .email("test2@test2.com")
                .password("password")
                .phone("01012341234")
                .gender(MemberGender.MALE)
                .address(new Address("11232", "서울시 서울구 서울로", "11 서울아파트 11동 111호"))
                .build();

        //member 미리 저장 하여 @WithMockCustomMember 에서 findByEmail != null 을 하기 위함.
        memberRepository.save(member2);
    }
}
