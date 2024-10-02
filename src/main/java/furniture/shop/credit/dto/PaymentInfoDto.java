package furniture.shop.credit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInfoDto {

    private String email;
    private String name;
    private String phone;
    private String address;
    private String zipCode;
    private String productName;
    private int amount;

}
