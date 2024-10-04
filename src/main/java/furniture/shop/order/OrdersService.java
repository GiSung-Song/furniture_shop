package furniture.shop.order;

import furniture.shop.cart.Cart;
import furniture.shop.cart.CartRepository;
import furniture.shop.configure.exception.CustomException;
import furniture.shop.configure.exception.CustomExceptionCode;
import furniture.shop.credit.Credit;
import furniture.shop.credit.CreditRepository;
import furniture.shop.global.MemberAuthorizationUtil;
import furniture.shop.member.Member;
import furniture.shop.order.contsant.OrdersStatus;
import furniture.shop.order.dto.OrderProductResponseDto;
import furniture.shop.order.dto.OrderResponseDto;
import furniture.shop.order.dto.OrderSingleRequestDto;
import furniture.shop.order.dto.OrdersListResponseDto;
import furniture.shop.product.Product;
import furniture.shop.product.ProductRepository;
import furniture.shop.product.constant.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrdersService {

    private final MemberAuthorizationUtil memberAuthorizationUtil;
    private final OrdersRepository ordersRepository;
    private final ProductRepository productRepository;
    private final OrdersProductRepository ordersProductRepository;
    private final CartRepository cartRepository;
    private final OrdersQueryRepository ordersQueryRepository;
    private final CreditRepository creditRepository;

    @Transactional
    public void createSingleOrder(OrderSingleRequestDto ordersSingleDto) {
        Member member = memberAuthorizationUtil.getMember();

        Product product = productRepository.findById(ordersSingleDto.getProductId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_VALID_ERROR));

        //상품이 판매중이 아니면 throw
        if (product.getProductStatus() != ProductStatus.SELLING) {
            throw new CustomException(CustomExceptionCode.NOT_SELLING_PRODUCT_EXCEPTION);
        }

        //재고보다 주문 수량이 크면 throw
        if (product.getStock() < ordersSingleDto.getCount()) {
            throw new CustomException(CustomExceptionCode.NOT_ENOUGH_PRODUCT_EXCEPTION);
        }

        Orders orders = Orders.createOrders(member);
        ordersRepository.save(orders);

        OrdersProduct ordersProduct = OrdersProduct.createOrdersProduct(orders, product, ordersSingleDto.getCount());

        //총 가격
        orders.editTotalPrice(ordersProduct.getTotalPrice());
    }

    @Transactional
    public void createCartOrder() {
        Member member = memberAuthorizationUtil.getMember();

        Cart cart = cartRepository.findByMemberId(member.getId());

        if (cart == null || cart.getCartProductList().isEmpty()) {
            throw new CustomException(CustomExceptionCode.NOT_VALID_ERROR);
        }

        Orders orders = Orders.createOrders(member);
        ordersRepository.save(orders);

        List<OrdersProduct> ordersProducts = new ArrayList<>();
        int sumPrice = 0;

        for (int i = 0; i < cart.getCartProductList().size(); i++) {
            Product product = productRepository.findById(cart.getCartProductList().get(i).getProduct().getId())
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_VALID_ERROR));

            //상품이 판매중이 아니면 throw
            if (product.getProductStatus() != ProductStatus.SELLING) {
                throw new CustomException(CustomExceptionCode.NOT_SELLING_PRODUCT_EXCEPTION);
            }

            //재고보다 주문 수량이 크면 throw
            if (product.getStock() < cart.getCartProductList().get(i).getCount()) {
                throw new CustomException(CustomExceptionCode.NOT_ENOUGH_PRODUCT_EXCEPTION);
            }

            OrdersProduct ordersProduct = OrdersProduct.createOrdersProduct(orders, product, cart.getCartProductList().get(i).getCount());

            ordersProducts.add(ordersProduct);

            sumPrice += ordersProduct.getTotalPrice();
        }

        orders.editTotalPrice(sumPrice);

        //장바구니 비우기
        cart.resetCart();
    }

    @Transactional
    public Page<OrdersListResponseDto> getOrdersList(Pageable pageable) {
        Member member = memberAuthorizationUtil.getMember();

        Page<OrdersListResponseDto> orderList = ordersQueryRepository.getOrderList(member, pageable);

        return orderList;
    }

    @Transactional(readOnly = true)
    public void isRightOrder(Long orderId) {
        Member member = memberAuthorizationUtil.getMember();

        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_VALID_ERROR));

        // 로그인한 사용자와 주문한 사용자가 다른 경우
        if (member.getId() != orders.getMember().getId()) {
            throw new CustomException(CustomExceptionCode.NOT_VALID_AUTH_ERROR);
        }

        // 주문 진행중인 상태가 아니면 진행할 수 없음
        if (orders.getOrdersStatus() != OrdersStatus.READY) {
            throw new CustomException(CustomExceptionCode.NOT_VALID_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderDetail(Long orderId) {
        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_VALID_ERROR));

        List<OrderProductResponseDto> orderProductResponseDtoList = new ArrayList<>();

        for (OrdersProduct ordersProduct : orders.getOrdersProducts()) {
            OrderProductResponseDto orderProductResponseDto = ordersProductEntityToDTO(ordersProduct);

            orderProductResponseDtoList.add(orderProductResponseDto);
        }

        OrderResponseDto orderResponseDto = ordersEntityToDto(orders, orderProductResponseDtoList);

        // 결제 완료 상태 혹은 결제 취소된 경우
        if (orders.getOrdersStatus() != OrdersStatus.READY) {
            Credit credit = creditRepository.findByOrdersId(orderId);

            if (credit == null) {
                throw new CustomException(CustomExceptionCode.NOT_VALID_ERROR);
            }

            orderResponseDto.setOrdersStatus(orders.getOrdersStatus());
            orderResponseDto.setAmount(credit.getAmount());
            orderResponseDto.setMileage(credit.getSavedMileage());
            orderResponseDto.setPayMethod(credit.getPayMethod());
            orderResponseDto.setImpUID(credit.getImpUID());

            if (orders.getOrdersStatus() == OrdersStatus.FINISH) {
                orderResponseDto.setPaidAt(credit.getPaidAt());
            } else if (orders.getOrdersStatus() == OrdersStatus.CANCEL) {
                orderResponseDto.setPayCancelledAt(credit.getCancelledAt());
            }
        }

        return orderResponseDto;
    }

    private OrderResponseDto ordersEntityToDto(Orders orders, List<OrderProductResponseDto> orderProductResponseDtoList) {
        OrderResponseDto orderResponseDto = new OrderResponseDto();

        orderResponseDto.setOrderId(orders.getId());
        orderResponseDto.setOrderProductList(orderProductResponseDtoList);
        orderResponseDto.setCity(orders.getAddress().getCity());
        orderResponseDto.setStreet(orders.getAddress().getStreet());
        orderResponseDto.setZipCode(orders.getAddress().getZipCode());
        orderResponseDto.setReceiver(orders.getReceiver());
        orderResponseDto.setPhone(orders.getPhone());

        return orderResponseDto;
    }

    private OrderProductResponseDto ordersProductEntityToDTO(OrdersProduct ordersProduct) {
        OrderProductResponseDto orderProductResponseDto = new OrderProductResponseDto();

        orderProductResponseDto.setOrderProductId(ordersProduct.getId());
        orderProductResponseDto.setProductName(ordersProduct.getProduct().getProductName());
        orderProductResponseDto.setProductCode(ordersProduct.getProduct().getProductCode());
        orderProductResponseDto.setProductId(ordersProduct.getProduct().getId());
        orderProductResponseDto.setPrice(ordersProduct.getProduct().getPrice());
        orderProductResponseDto.setCount(ordersProduct.getCount());
        orderProductResponseDto.setTotalPrice(ordersProduct.getTotalPrice());

        return orderProductResponseDto;
    }
}
