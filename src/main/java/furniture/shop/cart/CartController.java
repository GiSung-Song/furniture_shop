package furniture.shop.cart;

import furniture.shop.cart.dto.CartDto;
import furniture.shop.cart.dto.CartProductAddDto;
import furniture.shop.cart.dto.CartProductEditDto;
import furniture.shop.configure.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
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
@Tag(name = "Cart API", description = "Cart API")
public class CartController {

    private final CartService cartService;

    @PostMapping("/product/{id}/cart")
    @Operation(summary = "장바구니 상품 추가", description = "장바구니 상품 추가 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "장바구니에 추가했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "권한이 없습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content(mediaType = "application/json"))
    })
    @Parameters(value = {
            @Parameter(name = "productId", description = "상품 ID"),
            @Parameter(name = "count", description = "수량")
    })
    public ResponseEntity<ApiResponse<String>> addCartProduct(@PathVariable("id") Long productId, @RequestBody @Valid CartProductAddDto cartProductAddDto) {
        if (cartProductAddDto.getProductId() == null) {
            cartProductAddDto.setProductId(productId);
        }

        cartService.addCart(cartProductAddDto);

        return ResponseEntity.ok(ApiResponse.res(HttpStatus.OK, "장바구니에 추가했습니다."));
    }

    @GetMapping("/cart")
    @Operation(summary = "장바구니 조회", description = "장바구니 조회 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "장바구니를 조회했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "권한이 없습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponse<CartDto>> getCart() {
        CartDto cart = cartService.getCart();

        return ResponseEntity.ok(ApiResponse.res(HttpStatus.OK, "장바구니를 조회했습니다.", cart));
    }

    @PatchMapping("/cart")
    @Operation(summary = "장바구니 수정", description = "장바구니 수정 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "장바구니 상품을 수정했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "권한이 없습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content(mediaType = "application/json"))
    })
    @Parameters(value = {
            @Parameter(name = "productId", description = "상품 ID"),
            @Parameter(name = "count", description = "수량")
    })
    public ResponseEntity<ApiResponse<String>> addCartProduct(@RequestBody @Valid CartProductEditDto cartProductEditDto) {
        cartService.editCartProduct(cartProductEditDto);

        return ResponseEntity.ok(ApiResponse.res(HttpStatus.OK, "장바구니 상품을 수정했습니다."));
    }
}
