package furniture.shop.cart;

import furniture.shop.cart.dto.CartDto;
import furniture.shop.cart.dto.CartProductAddDto;
import furniture.shop.cart.dto.CartProductDto;
import furniture.shop.cart.dto.CartProductEditDto;
import furniture.shop.configure.exception.CustomException;
import furniture.shop.configure.exception.CustomExceptionCode;
import furniture.shop.global.MemberAuthorizationUtil;
import furniture.shop.member.Member;
import furniture.shop.product.Product;
import furniture.shop.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final ProductRepository productRepository;
    private final CartProductRepository cartProductRepository;
    private final CartRepository cartRepository;
    private final MemberAuthorizationUtil memberAuthorizationUtil;

    @Transactional
    public void addCart(CartProductAddDto dto) {
        Member member = memberAuthorizationUtil.getMember();

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_VALID_ERROR));

        Cart cart = cartRepository.findByMemberId(member.getId());

        //기존 장바구니가 없다면 장바구니 새로 만들기
        if (cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        CartProduct savedCartProduct = cartProductRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        //장바구니에 기존 상품이 있다면 count ++
        if (savedCartProduct != null) {
            savedCartProduct.addCount(dto.getCount());
        } else {
            CartProduct cartProduct = CartProduct.createCartProduct(cart, product, dto.getCount());
            cartProductRepository.save(cartProduct);
        }
    }

    @Transactional(readOnly = true)
    public CartDto getCart() {
        Member member = memberAuthorizationUtil.getMember();

        CartDto cartDto = new CartDto();

        Cart cart = cartRepository.findByMemberId(member.getId());

        if (cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        } else {
            List<CartProductDto> cartProductDtoList = new ArrayList<>();

            List<CartProduct> cartProductList = cart.getCartProductList();

            for (CartProduct cartProduct : cartProductList) {
                CartProductDto cartProductDto = new CartProductDto();

                cartProductDto.setProductCode(cartProduct.getProduct().getProductCode());
                cartProductDto.setProductName(cartProduct.getProduct().getProductName());
                cartProductDto.setCount(cartProduct.getCount());
                cartProductDto.setPrice(cartProduct.getCount() * cartProduct.getProduct().getPrice());

                cartProductDtoList.add(cartProductDto);
            }

            cartDto.setCartProductDtoList(cartProductDtoList);
            cartDto.setTotalPrice(cart.getTotalPrice());
        }

        return cartDto;
    }

    @Transactional
    public void editCartProduct(CartProductEditDto editDto) {
        Member member = memberAuthorizationUtil.getMember();

        Cart cart = cartRepository.findByMemberId(member.getId());

        if (cart == null) {
            throw new CustomException(CustomExceptionCode.NOT_VALID_ERROR);
        }

        CartProduct cartProduct = cartProductRepository.findByCartIdAndProductId(cart.getId(), editDto.getProductId());

        if (cartProduct == null) {
            throw new CustomException(CustomExceptionCode.NOT_VALID_ERROR);
        }

        if (editDto.getCount() == 0) {
            cart.getCartProductList().remove(cartProduct);
        } else {
            cartProduct.editCount(editDto.getCount());
        }
    }

}
