package furniture.shop.credit;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import furniture.shop.configure.exception.CustomException;
import furniture.shop.configure.exception.CustomExceptionCode;
import furniture.shop.credit.dto.CreditRefundRequestDto;
import furniture.shop.credit.dto.CreditRequestDto;
import furniture.shop.credit.dto.PaymentInfoDto;
import furniture.shop.global.MemberAuthorizationUtil;
import furniture.shop.member.Member;
import furniture.shop.order.Orders;
import furniture.shop.order.OrdersProduct;
import furniture.shop.order.OrdersRepository;
import furniture.shop.order.contsant.OrdersStatus;
import furniture.shop.product.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditService {

    private final MemberAuthorizationUtil memberAuthorizationUtil;
    private final OrdersRepository ordersRepository;
    private final IamportClient iamportClient;
    private final CreditRepository creditRepository;

    /**
     * 결제 검증
     * Credit entity 생성 및 저장
     * 상품 재고--, 판매량++
     * 회원 마일리지++
     * 주문 상태 변경
     * @param creditRequestDto 결제금액, 결제 고유번호, 결제 번호, 결제방법
     */
    @Transactional
    public void createAndVerifyPayment(CreditRequestDto creditRequestDto) {
        Long orderId = creditRequestDto.getOrderId();

        // 주문 정보 가져오기, 없다면 throw
        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_VALID_ERROR));

        Member member = memberAuthorizationUtil.getMember();

        // 현재 사용자와 주문자가 다른 경우
        if (orders.getMember().getId() != member.getId()) {
            log.info(">>> 현재 사용자와 주문자가 다른 경우 <<<");

            throw new CustomException(CustomExceptionCode.NOT_VALID_AUTH_ERROR);
        }

        // 주문 준비가 아니면 결제할 수 없음
        if (orders.getOrdersStatus() != OrdersStatus.READY) {
            log.info(">>> 결제 준비 상태가 아닌 경우 <<<");

            throw new CustomException(CustomExceptionCode.NOT_VALID_ERROR);
        }

        // 결제 고유번호 ImpUID를 이용하여 결제 검증
        String impUID = creditRequestDto.getImpUID();

        IamportResponse<Payment> validationResponse;

        try {
            validationResponse = iamportClient.paymentByImpUid(impUID);
        } catch (IamportResponseException e) {
            log.info(">>> 결제 검증 중 오류 : {} <<<", e.getMessage());

            throw new CustomException(CustomExceptionCode.FAIL_PAYMENT);
        } catch (IOException e) {
            log.info(">>> 결제 검증 중 오류 : {} <<<", e.getMessage());

            throw new CustomException(CustomExceptionCode.FAIL_PAYMENT);
        }

        // 결제 성공 여부 체크
        if (validationResponse.getResponse() != null && "paid".equals(validationResponse.getResponse().getStatus())) {
            // 결제 성공 시 결제 entity 생성 및 저장
            Credit credit = Credit.createCredit(orders, creditRequestDto.getAmount(), creditRequestDto.getMerchantUID(),
                    creditRequestDto.getImpUID(), creditRequestDto.getPayMethod());
            creditRepository.save(credit);

            // 결제 성공 시 상품 stock - , sellCount +
            for (OrdersProduct ordersProduct : orders.getOrdersProducts()) {
                Product product = ordersProduct.getProduct();

                product.addSellCount(ordersProduct.getCount());
                product.minusStock(ordersProduct.getCount());
            }

            // 주문 상태 변경, 회원 마일리지 적립
            orders.updateOrdersStatus(OrdersStatus.FINISH);
            member.savedMileage(credit.getSavedMileage());
        } else {
            log.info(">>> 결제 실패 <<<");

            throw new CustomException(CustomExceptionCode.FAIL_PAYMENT);
        }
    }

    /**
     * 결제 환불
     * 주문 상태 변경 및 결제 entity 취소 시간 추가
     * 상품 재고++, 판매량--
     * 회원 마일리지--
     * @Param creditRefundRequestDto 결제번호, 환불 사유, 환불 은행, 환불 계좌, 환불 계좌 예금주
     */
    @Transactional
    public void cancelPayment(CreditRefundRequestDto creditRefundRequestDto) {
        String impUID = creditRefundRequestDto.getImpUID();

        Credit credit = creditRepository.findByImpUID(impUID);

        if (credit == null) {
            log.info(">>> 결제 정보를 찾을 수 없음 <<<");

            throw new CustomException(CustomExceptionCode.NOT_VALID_ERROR);
        }

        IamportResponse<Payment> paymentResponse = null;

        // 결제 검증
        try {
            paymentResponse = iamportClient.paymentByImpUid(impUID);
        } catch (IamportResponseException | IOException e) {
            log.info(">>> 결제 검증 실패 : {} <<<", e.getMessage());

            throw new CustomException(CustomExceptionCode.NOT_VALID_PAYMENT);
        }

        if (paymentResponse == null) {
            log.info(">>> 결제 검증 실패 <<<");

            throw new CustomException(CustomExceptionCode.NOT_VALID_PAYMENT);
        }

        Payment payment = paymentResponse.getResponse();

        // 결제상태가 아니라면 취소할 수 없음
        if (!"paid".equalsIgnoreCase(payment.getStatus())) {
            log.info(">>> 결제 상태가 아님 <<<");

            throw new CustomException(CustomExceptionCode.NOT_VALID_PAYMENT);
        }

        //전체 환불
        CancelData cancelData = new CancelData(impUID, true, null);

        // 사유
        if (StringUtils.hasText(creditRefundRequestDto.getReason())) {
            cancelData.setReason(creditRefundRequestDto.getReason());
        }

        // 은행
        if (StringUtils.hasText(creditRefundRequestDto.getBank())) {
            cancelData.setRefund_bank(creditRefundRequestDto.getBank());
        }

        // 은행 예금주
        if (StringUtils.hasText(creditRefundRequestDto.getHolder())) {
            cancelData.setRefund_holder(creditRefundRequestDto.getHolder());
        }

        // 은행 계좌
        if (StringUtils.hasText(creditRefundRequestDto.getAccount())) {
            cancelData.setRefund_account(creditRefundRequestDto.getAccount());
        }

        IamportResponse<Payment> cancelResponse = null;

        //환불 요청 API 호출
        try {
            cancelResponse = iamportClient.cancelPaymentByImpUid(cancelData);
        } catch (IamportResponseException | IOException e) {
            log.info(">>> 환불 요청 실패 : {} <<<", e.getMessage());

            throw new CustomException(CustomExceptionCode.NOT_VALID_PAYMENT);
        }

        if (cancelResponse == null) {
            log.info(">>> 환불 요청 실패 <<<");

            throw new CustomException(CustomExceptionCode.NOT_VALID_PAYMENT);
        }

        //결제 취소 시간 추가
        credit.updateCancelledAt(LocalDateTime.now());

        //주문 취소 상태로 변경
        Orders orders = credit.getOrders();
        orders.updateOrdersStatus(OrdersStatus.CANCEL);

        //주문 취소 시 상품 재고 수량 + , 판매 개수 -
        for (OrdersProduct ordersProduct : orders.getOrdersProducts()) {
            Product product = ordersProduct.getProduct();

            product.minusSellCount(ordersProduct.getCount());
            product.addStock(ordersProduct.getCount());
        }

        //회원 마일리지 감소
        Member member = orders.getMember();
        member.minusMileage(credit.getSavedMileage());
    }

    @Transactional(readOnly = true)
    public PaymentInfoDto getPaymentInfo(Long orderId) {
        // 주문 정보 가져오기, 없다면 throw
        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_VALID_ERROR));

        Member member = memberAuthorizationUtil.getMember();

        // 현재 사용자와 주문자가 다른 경우
        if (orders.getMember().getId() != member.getId()) {
            log.info(">>> 현재 사용자와 주문자가 다른 경우 <<<");

            throw new CustomException(CustomExceptionCode.NOT_VALID_AUTH_ERROR);
        }

        PaymentInfoDto paymentInfoDto = new PaymentInfoDto();

        paymentInfoDto.setProductName("order_" + orderId);
        paymentInfoDto.setAmount(orders.getTotalPrice());
        paymentInfoDto.setName(orders.getMember().getUsername());
        paymentInfoDto.setPhone(orders.getPhone());
        paymentInfoDto.setZipCode(orders.getAddress().getZipCode());
        paymentInfoDto.setEmail(orders.getMember().getEmail());
        paymentInfoDto.setAddress(orders.getAddress().getCity() + " " + orders.getAddress().getStreet());

        return paymentInfoDto;
    }
}