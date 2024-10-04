package furniture.shop.review;

import furniture.shop.configure.response.ApiResponse;
import furniture.shop.review.dto.ReviewAddRequestDto;
import furniture.shop.review.dto.ReviewEditRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Review API", description = "Review API")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/product/{id}/review")
    @Operation(summary = "리뷰 등록", description = "리뷰 등록 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "리뷰를 등록했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 입력입니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "권한이 없습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<ApiResponse<?>> addReview(@PathVariable("id") Long productId, @Valid @RequestBody ReviewAddRequestDto dto) {
        dto.setProductId(productId);
        reviewService.addReview(dto);

        return ResponseEntity.ok(ApiResponse.res(HttpStatus.OK, "리뷰를 등록했습니다."));
    }

    @PatchMapping("/review/{id}")
    @Operation(summary = "리뷰 수정", description = "리뷰 수정 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "리뷰를 수정했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 입력입니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "권한이 없습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<ApiResponse<?>> editReview(@PathVariable("id") Long reviewId, @Valid @RequestBody ReviewEditRequestDto dto) {
        dto.setReviewId(reviewId);
        reviewService.editReview(dto);

        return ResponseEntity.ok(ApiResponse.res(HttpStatus.OK, "리뷰를 수정했습니다."));
    }

    @DeleteMapping("/review/{id}")
    @Operation(summary = "리뷰 삭제", description = "리뷰 삭제 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "리뷰를 삭제했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 입력입니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "권한이 없습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<ApiResponse<?>> deleteReview(@PathVariable("id") Long reviewId) {
        reviewService.deleteReview(reviewId);

        return ResponseEntity.ok(ApiResponse.res(HttpStatus.OK, "리뷰를 삭제했습니다."));
    }
}
