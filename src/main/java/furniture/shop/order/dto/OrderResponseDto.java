package furniture.shop.order.dto;

import furniture.shop.order.contsant.OrdersStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "주문 Response DTO")
public class OrderResponseDto {

    @Schema(description = "주문 ID")
    private Long orderId;

    @Schema(description = "주문 상품 리스트")
    private List<OrderProductResponseDto> orderProductList;

    @Schema(description = "수령인")
    private String receiver;

    @Schema(description = "메모")
    private String memo;

    @Schema(description = "핸드폰 번호")
    private String phone;

    @Schema(description = "우편번호")
    private String zipCode;

    @Schema(description = "도시")
    private String city;

    @Schema(description = "상세주소")
    private String street;

    @Schema(description = "주문 상태")
    private OrdersStatus ordersStatus;

    @Schema(description = "결제 금액")
    private int amount;

    @Schema(description = "적립된 마일리지")
    private int mileage;

    @Schema(description = "결제 고유 번호")
    private String impUID;

    @Schema(description = "결제 방법")
    private String payMethod;

    @Schema(description = "결제 시간")
    private LocalDateTime paidAt;

    @Schema(description = "결제 취소 시간")
    private LocalDateTime payCancelledAt;
}
