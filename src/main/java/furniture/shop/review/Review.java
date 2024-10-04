package furniture.shop.review;

import furniture.shop.configure.BaseTimeEntity;
import furniture.shop.member.Member;
import furniture.shop.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    private String comment;

    @Column
    private double rate;

    private void setProduct(Product product) {
        this.product = product;
    }

    private void setMember(Member member) {
        this.member = member;
    }

    private void setComment(String comment) {
        this.comment = comment;
    }

    private void setRate(double rate) {
        this.rate = rate;
    }

    public static Review createReview(Product product, Member member, String comment, double rate) {
        Review review = new Review();

        review.setProduct(product);
        review.setMember(member);
        review.setComment(comment);
        review.setRate(rate);

        product.getReviews().add(review);

        return review;
    }

    public void editRate(double rate) {
        this.rate = rate;
    }

    public void editComment(String comment) {
        this.comment = comment;
    }
}
