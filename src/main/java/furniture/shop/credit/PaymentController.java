package furniture.shop.credit;

import furniture.shop.credit.dto.PaymentInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final CreditService creditService;

    @Value("${iamport.api.key}")
    private String iamportApiKey;

    @GetMapping("/payment/{id}")
    public String showPaymentPage(@PathVariable("id") String orderId) {
        return "/payment/payment";
    }

    @GetMapping("/payment/merchant")
    @ResponseBody
    public ResponseEntity<String> generateMerchantUID() {
        String merchantUID = makeMerchantUID();

        return ResponseEntity.ok(merchantUID);
    }

    @GetMapping("/payment/key")
    @ResponseBody
    public ResponseEntity<String> getIamportKey() {
        return ResponseEntity.ok(iamportApiKey);
    }

    @GetMapping("/payment/{id}/info")
    @ResponseBody
    public ResponseEntity<PaymentInfoDto> getPaymentInfo(@PathVariable("id") Long orderId) {
        PaymentInfoDto paymentInfo = creditService.getPaymentInfo(orderId);

        return ResponseEntity.ok(paymentInfo);
    }

    private String makeMerchantUID() {
        String today = new SimpleDateFormat("yyyyMMdd").format(new Date());

        String uuid = UUID.randomUUID().toString();

        return "order_" + today + "_" + uuid;
    }
}
