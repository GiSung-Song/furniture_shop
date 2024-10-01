package furniture.shop.order;

import furniture.shop.configure.response.ApiResponse;
import furniture.shop.order.dto.OrderRequestDto;
import furniture.shop.order.dto.OrderResponseDto;
import furniture.shop.order.dto.OrderSingleRequestDto;
import furniture.shop.order.dto.OrdersListResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Orders API", description = "Orders API")
public class OrdersController {

    private final OrdersService ordersService;

    @GetMapping("/orders")
    @Operation(summary = "주문 조회", description = "주문 조회 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문을 조회했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "권한이 없습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponse<?>> getOrdersList(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        Page<OrdersListResponseDto> ordersList = ordersService.getOrdersList(pageable);

        return ResponseEntity.ok(ApiResponse.res(HttpStatus.OK, "주문을 조회했습니다.", ordersList));
    }

    @PostMapping("/product/{id}/orders")
    @Operation(summary = "상품 단건 주문", description = "상품 단건 주문 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문을 진행합니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 입력입니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "권한이 없습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponse<?>> singleOrders(@PathVariable("id") Long productId, @Valid @RequestBody OrderSingleRequestDto orderSingleRequestDto) {
        orderSingleRequestDto.setProductId(productId);

        ordersService.createSingleOrder(orderSingleRequestDto);

        return ResponseEntity.ok(ApiResponse.res(HttpStatus.OK, "주문을 진행합니다."));
    }

    @GetMapping("/orders/{id}")
    @Operation(summary = "상품 주문 현황 조회", description = "상품 주문 현황 조회 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문현황을 조회했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 입력입니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "권한이 없습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponse<?>> getOrdersDetail(@PathVariable("id") Long orderId) {
        //주문한 사용자인지 체크
        ordersService.isRightOrder(orderId);

        //주문 현황 가져오기
        OrderResponseDto orderDetail = ordersService.getOrderDetail(orderId);

        return ResponseEntity.ok(ApiResponse.res(HttpStatus.OK, "주문현황을 조회했습니다.", orderDetail));
    }

    @PostMapping("/cart/orders")
    @Operation(summary = "장바구니 상품 주문", description = "장바구니 상품 주문 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문을 진행합니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 입력입니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "권한이 없습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponse<?>> cartOrders() {
        ordersService.createCartOrder();

        return ResponseEntity.ok(ApiResponse.res(HttpStatus.OK, "주문을 진행합니다."));
    }

}
