package furniture.shop.order;

import furniture.shop.cart.Cart;
import furniture.shop.cart.CartRepository;
import furniture.shop.configure.exception.CustomException;
import furniture.shop.configure.exception.CustomExceptionCode;
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
    private final OrdersProductRepository ordersProductRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final OrdersQueryRepository ordersQueryRepository;

    @Transactional
    public OrderResponseDto createSingleOrder(OrderSingleRequestDto ordersSingleDto) {
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
        OrdersProduct ordersProduct = OrdersProduct.createOrdersProduct(orders, product, ordersSingleDto.getCount());

        ordersRepository.save(orders);

        //상품 주문 시 회원의 정보들로 구성 후 주문 상황 보여주기
        OrderProductResponseDto orderProductResponseDto = OrdersProductEntityToDTO(ordersProduct);

        List<OrderProductResponseDto> orderProductResponseDtoList = new ArrayList<>();
        orderProductResponseDtoList.add(orderProductResponseDto);

        return ordersEntityToDto(member, orders, orderProductResponseDtoList);
    }

    @Transactional
    public OrderResponseDto createCartOrder() {
        Member member = memberAuthorizationUtil.getMember();

        Cart cart = cartRepository.findByMemberId(member.getId());

        if (cart == null || cart.getCartProductList().isEmpty()) {
            throw new CustomException(CustomExceptionCode.NOT_VALID_ERROR);
        }

        Orders orders = Orders.createOrders(member);
        List<OrdersProduct> ordersProducts = new ArrayList<>();

        for (int i = 0; i < cart.getCartProductList().size(); i++) {
            Product product = productRepository.findById(cart.getCartProductList().get(i).getId())
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
        }

        ordersRepository.save(orders);

        List<OrderProductResponseDto> orderProductResponseDtoList = new ArrayList<>();

        for (OrdersProduct ordersProduct : ordersProducts) {
            OrderProductResponseDto orderProductResponseDto = OrdersProductEntityToDTO(ordersProduct);

            orderProductResponseDtoList.add(orderProductResponseDto);
        }

        return ordersEntityToDto(member, orders, orderProductResponseDtoList);
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
            throw new CustomException(CustomExceptionCode.NOT_VALID_ERROR);
        }

        // 주문 진행중인 상태가 아니면 진행할 수 없음
        if (orders.getOrdersStatus() != OrdersStatus.READY) {
            throw new CustomException(CustomExceptionCode.NOT_VALID_ERROR);
        }
    }

    @Transactional
    public void changeOrderStatus(Long orderId) {
        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_VALID_ERROR));

        orders.updateOrdersStatus(OrdersStatus.READY_CREDIT);
    }

    private OrderResponseDto ordersEntityToDto(Member member, Orders orders, List<OrderProductResponseDto> orderProductResponseDtoList) {
        OrderResponseDto orderResponseDto = new OrderResponseDto();

        orderResponseDto.setOrderId(orders.getId());
        orderResponseDto.setOrderItemDtoList(orderProductResponseDtoList);
        orderResponseDto.setCity(member.getAddress().getCity());
        orderResponseDto.setStreet(member.getAddress().getStreet());
        orderResponseDto.setZipCode(member.getAddress().getZipCode());
        orderResponseDto.setReceiver(member.getUsername());
        orderResponseDto.setPhone(member.getPhone());

        return orderResponseDto;
    }

    private OrderProductResponseDto OrdersProductEntityToDTO(OrdersProduct ordersProduct) {
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
