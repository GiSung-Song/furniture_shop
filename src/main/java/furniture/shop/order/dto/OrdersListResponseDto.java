package furniture.shop.order.dto;

import com.querydsl.core.annotations.QueryProjection;
import furniture.shop.order.contsant.OrdersStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "주문 목록 Response DTO")
public class OrdersListResponseDto {

    @Schema(description = "주문 ID")
    private Long orderId;

    @Schema(description = "총 금액")
    private int totalPrice;

    @Schema(description = "주문 상태")
    private OrdersStatus ordersStatus;

    @Schema(description = "주문 시각")
    private LocalDateTime createdOrderTime;

    @Schema(description = "주문 변경 시각")
    private LocalDateTime lastModifiedTime;

    @Schema(description = "우편번호")
    private String zipCode;

    @Schema(description = "도시")
    private String city;

    @Schema(description = "상세주소")
    private String street;

    @Schema(description = "수령인")
    private String receiver;

    @QueryProjection
    public OrdersListResponseDto(Long orderId, int totalPrice, OrdersStatus ordersStatus,
                                 LocalDateTime createdOrderTime, LocalDateTime lastModifiedTime,
                                 String zipCode, String city, String street, String receiver) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.ordersStatus = ordersStatus;
        this.createdOrderTime = createdOrderTime;
        this.lastModifiedTime = lastModifiedTime;
        this.zipCode = zipCode;
        this.city = city;
        this.street = street;
        this.receiver = receiver;
    }
}
