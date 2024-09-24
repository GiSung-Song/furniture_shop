package furniture.shop.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Schema(description = "핸드폰 번호", example = "01012345678")
    private String phone;

    @Schema(description = "우편번호", example = "12345")
    private String zipCode;

    @Schema(description = "도시", example = "서울시 강남구")
    private String city;

    @Schema(description = "상세주소", example = "강남대로 1234 6층 605호")
    private String street;

}
