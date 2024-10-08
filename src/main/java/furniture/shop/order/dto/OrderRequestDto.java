package furniture.shop.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "주문 Request DTO")
public class OrderRequestDto {

    @Schema(description = "주문 ID")
    private Long orderId;

    @Schema(description = "주문 상품 리스트")
    private List<OrderProductResponseDto> orderProductList;

    @Schema(description = "수령인")
    @NotBlank(message = "수령인을 입력해주세요.")
    private String receiver;

    @Schema(description = "메모")
    private String memo;

    @Schema(description = "핸드폰 번호", example = "01012345678")
    @NotBlank(message = "핸드폰 번호를 입력해주세요.")
    private String phone;

    @Schema(description = "우편번호", example = "12345")
    @NotBlank(message = "우편번호를 입력해주세요.")
    private String zipCode;

    @Schema(description = "도시", example = "서울시 강남구")
    @NotBlank(message = "도시를 입력해주세요.")
    private String city;

    @Schema(description = "상세주소", example = "강남대로 1234 6층 605호")
    @NotBlank(message = "상세주소를 입력해주세요.")
    private String street;
}
