package furniture.shop.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "리뷰 등록 Request DTO")
public class ReviewAddRequestDto {

    @Schema(description = "상품ID")
    public Long productId;

    @Schema(description = "코멘트")
    @NotBlank(message = "상품에 대한 코멘트를 작성해주세요.")
    public String comment;

    @Schema(description = "별점")
    @Min(value = 0, message = "0이상 입력해주세요.")
    @Max(value = 5, message = "5점 이하로 입력해주세요.")
    public double rate;

}
