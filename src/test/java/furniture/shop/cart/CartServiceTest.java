package furniture.shop.cart;

import furniture.shop.cart.dto.CartDto;
import furniture.shop.cart.dto.CartProductAddDto;
import furniture.shop.cart.dto.CartProductEditDto;
import furniture.shop.configure.exception.CustomException;
import furniture.shop.configure.exception.CustomExceptionCode;
import furniture.shop.global.MemberAuthorizationUtil;
import furniture.shop.global.embed.Address;
import furniture.shop.member.Member;
import furniture.shop.product.Product;
import furniture.shop.product.ProductRepository;
import furniture.shop.product.constant.ProductCategory;
import furniture.shop.product.constant.ProductStatus;
import furniture.shop.product.embed.ProductSize;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CartServiceTest {

    @InjectMocks
    CartService cartService;

    @Mock
    ProductRepository productRepository;

    @Mock
    CartRepository cartRepository;

    @Mock
    CartProductRepository cartProductRepository;

    @Mock
    MemberAuthorizationUtil memberAuthorizationUtil;

    Product product;
    Member member;
    Cart cart;
    CartProduct cartProduct;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(0L)
                .address(new Address("12345", "서울시 강남구 강남대로 114", "테스트 빌딩 5층"))
                .email("test@test.com")
                .password("123456")
                .phone("01012345678")
                .username("테스터")
                .build();

        product = Product.builder()
                .id(0L)
                .productName("테스트 상품")
                .productCode("test-1234")
                .productCategory(ProductCategory.CHAIR)
                .productStatus(ProductStatus.SELLING)
                .size(new ProductSize(50.7, 102.5, 100.3))
                .price(100)
                .stock(10)
                .description("테스트 상품입니다.")
                .build();

        cart = Cart.createCart(member);
    }

    @Test
    @DisplayName("장바구니 상품 추가 테스트")
    void 장바구니_상품_추가_테스트() {
        CartProductAddDto cartProductAddDto = new CartProductAddDto();
        cartProductAddDto.setProductId(0L);
        cartProductAddDto.setCount(5);

        when(memberAuthorizationUtil.getMember()).thenReturn(member);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartRepository.findByMemberId(member.getId())).thenReturn(cart);

        cartService.addCart(cartProductAddDto);

        assertEquals(1, cart.getCartProductList().size());
        assertEquals(5, cart.getCartProductList().get(0).getCount());
        assertEquals(500, cart.getTotalPrice());
    }

    @Test
    @DisplayName("장바구니 상품 추가 실패 테스트")
    void 장바구니_상품_추가_실패_테스트() {
        CartProductAddDto cartProductAddDto = new CartProductAddDto();
        cartProductAddDto.setProductId(0L);
        cartProductAddDto.setCount(5);

        when(memberAuthorizationUtil.getMember()).thenReturn(member);
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> cartService.addCart(cartProductAddDto));
    }

    @Test
    @DisplayName("장바구니 조회 성공 테스트")
    void 장바구니_조회_성공_테스트() {
        when(memberAuthorizationUtil.getMember()).thenReturn(member);
        when(cartRepository.findByMemberId(member.getId())).thenReturn(cart);

        CartDto cartDto = cartService.getCart();

        assertNotNull(cartDto);
    }

    @Test
    @DisplayName("장바구니 조회 실패 테스트")
    void 장바구니_조회_실패_테스트() {
        when(memberAuthorizationUtil.getMember()).thenReturn(member);
        when(cartRepository.findByMemberId(member.getId())).thenReturn(null);

        CartDto cartDto = cartService.getCart();

        assertNotNull(cartDto);
        assertEquals(0, cartDto.getCartProductDtoList().size());
    }

    @Test
    @DisplayName("장바구니 수정 테스트 - 삭제")
    void 장바구니_수정_삭제_테스트() {
        cartProduct = CartProduct.createCartProduct(cart, product, 10);

        CartProductEditDto dto = new CartProductEditDto();
        dto.setProductId(0L);
        dto.setCount(0);

        when(memberAuthorizationUtil.getMember()).thenReturn(member);
        when(cartRepository.findByMemberId(member.getId())).thenReturn(cart);
        when(cartProductRepository.findByCartIdAndProductId(any(), any())).thenReturn(cartProduct);

        cartService.editCartProduct(dto);

        assertEquals(0, cart.getCartProductList().size());
    }

    @Test
    @DisplayName("장바구니 수정 테스트 - 수량 변경")
    void 장바구니_수정_수량_테스트() {
        cartProduct = CartProduct.createCartProduct(cart, product, 10);

        CartProductEditDto dto = new CartProductEditDto();
        dto.setProductId(0L);
        dto.setCount(5);

        when(memberAuthorizationUtil.getMember()).thenReturn(member);
        when(cartRepository.findByMemberId(member.getId())).thenReturn(cart);
        when(cartProductRepository.findByCartIdAndProductId(any(), any())).thenReturn(cartProduct);

        cartService.editCartProduct(dto);

        assertEquals(1, cart.getCartProductList().size());
        assertEquals(5, cart.getCartProductList().get(0).getCount());
    }

    @Test
    @DisplayName("수정 실패 테스트 - 장바구니 없음")
    void 장바구니_수정_실패_테스트() {
        CartProductEditDto dto = new CartProductEditDto();
        dto.setProductId(0L);
        dto.setCount(5);

        when(memberAuthorizationUtil.getMember()).thenReturn(member);
        when(cartRepository.findByMemberId(member.getId())).thenReturn(null);

        assertThrows(CustomException.class, () -> cartService.editCartProduct(dto));
    }

    @Test
    @DisplayName("수정 실패 테스트2 - 장바구니 상품 없음")
    void 장바구니_수정_실패_테스트2() {
        CartProductEditDto dto = new CartProductEditDto();
        dto.setProductId(0L);
        dto.setCount(5);

        when(memberAuthorizationUtil.getMember()).thenReturn(member);
        when(cartRepository.findByMemberId(member.getId())).thenReturn(null);

        assertThrows(CustomException.class, () -> cartService.editCartProduct(dto));
    }

}