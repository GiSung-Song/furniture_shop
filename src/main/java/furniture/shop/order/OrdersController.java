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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequiredArgsConstructor
@Tag(name = "Orders API", description = "Orders API")
public class OrdersController {

    private final OrdersService ordersService;

    @GetMapping("/orders")
    @Operation(summary = "주문 조회", description = "주문 조회 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문을 조회했습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 입력입니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "권한이 없습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없습니다.", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponse<?>> getOrders(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        Page<OrdersListResponseDto> ordersList = ordersService.getOrdersList(pageable);

        return ResponseEntity.ok(ApiResponse.res(HttpStatus.OK, "주문을 조회했습니다.", ordersList));
    }

    @PostMapping("/product/{id}/orders")
    public String singleOrders(@PathVariable("id") Long productId, OrderSingleRequestDto orderSingleRequestDto, RedirectAttributes redirectAttributes) {
        orderSingleRequestDto.setProductId(productId);

        OrderResponseDto sampleOrder = ordersService.createSingleOrder(orderSingleRequestDto);

        redirectAttributes.addAttribute("sampleOrder", sampleOrder);

        return "redirect:/orders/" + sampleOrder.getOrderId();
    }

    @GetMapping("/orders/{id}")
    public String orderProcessing(@PathVariable("id") Long orderId, RedirectAttributes redirectAttributes, Model model) {

        ordersService.isRightOrder(orderId);
        OrderResponseDto sampleOrder = (OrderResponseDto) redirectAttributes.getAttribute("sampleOrder");

        model.addAttribute("sampleOrder", sampleOrder);

        return "";
    }

    @PostMapping("/orders/{id}")
    public String orderFinish(@PathVariable("id") Long orderId, @Valid @ModelAttribute OrderRequestDto orderRequestDto) {
        //post 후 redirect로 /orders/history/{id} 에서 주문 정보 보여주고 결제준비 시 결제 버튼 보이게 하기



        return "";
    }
}
