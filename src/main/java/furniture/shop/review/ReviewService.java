package furniture.shop.review;

import furniture.shop.configure.exception.CustomException;
import furniture.shop.configure.exception.CustomExceptionCode;
import furniture.shop.global.MemberAuthorizationUtil;
import furniture.shop.member.Member;
import furniture.shop.order.Orders;
import furniture.shop.order.OrdersRepository;
import furniture.shop.order.contsant.OrdersStatus;
import furniture.shop.product.Product;
import furniture.shop.product.ProductRepository;
import furniture.shop.review.dto.ReviewAddRequestDto;
import furniture.shop.review.dto.ReviewEditRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ProductRepository productRepository;
    private final MemberAuthorizationUtil memberAuthorizationUtil;
    private final ReviewRepository reviewRepository;
    private final OrdersRepository ordersRepository;

    @Transactional
    public void addReview(ReviewAddRequestDto dto) {
        Member member = memberAuthorizationUtil.getMember();

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_VALID_ERROR));

        List<Orders> ordersList = ordersRepository.findByMemberId(member.getId());

        boolean buy = ordersList.stream()
                .filter(order -> order.getOrdersStatus().equals(OrdersStatus.FINISH))
                .flatMap(order -> order.getOrdersProducts().stream())
                .map(orderProduct -> orderProduct.getProduct().getId())
                .anyMatch(productId -> productId.equals(product.getId()));

        // 구매한 적이 없다면
        if (buy == false) {
            throw new CustomException(CustomExceptionCode.NOT_VALID_ERROR);
        }

        if (dto.getRate() < 0 || dto.getRate() > 5) {
            throw new CustomException(CustomExceptionCode.NOT_VALID_ERROR);
        }

        Review review = Review.createReview(product, member, dto.getComment(), dto.getRate());
    }

    @Transactional
    public void editReview(ReviewEditRequestDto dto) {
        Member member = memberAuthorizationUtil.getMember();

        Review review = reviewRepository.findById(dto.getReviewId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_VALID_ERROR));

        // 리뷰 작성자와 로그인 한 회원이 다른 경우
        if (review.getMember().getId() != member.getId()) {
            throw new CustomException(CustomExceptionCode.NOT_VALID_AUTH_ERROR);
        }

        review.editComment(dto.getComment());
        review.editRate(dto.getRate());
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Member member = memberAuthorizationUtil.getMember();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_VALID_ERROR));

        // 리뷰 작성자와 로그인 한 회원이 다른 경우
        if (review.getMember().getId() != member.getId()) {
            throw new CustomException(CustomExceptionCode.NOT_VALID_AUTH_ERROR);
        }

        Product product = review.getProduct();

        product.getReviews().remove(review);
    }

}
