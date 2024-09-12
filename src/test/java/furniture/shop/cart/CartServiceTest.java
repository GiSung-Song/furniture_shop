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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

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

    @Test
    @DisplayName("장바구니 상품 추가 테스트")
    void 장바구니_상품_추가_테스트() {
        setUp();

        CartProductAddDto cartProductAddDto = new CartProductAddDto();
        cartProductAddDto.setProductId(0L);
        cartProductAddDto.setCount(5);

        cartService.addCart(cartProductAddDto);

        verify(cartRepository, times(1)).save(any());
        verify(cartProductRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("장바구니 상품 추가 테스트2")
    void 장바구니_상품_추가_테스트2() {
        setUp();
        setUpCart();

        CartProductAddDto cartProductAddDto = new CartProductAddDto();
        cartProductAddDto.setProductId(0L);
        cartProductAddDto.setCount(5);

        cartService.addCart(cartProductAddDto);

        Cart cart = cartRepository.findByMemberId(any());

        verify(cartProductRepository, times(1)).save(any());
        Assertions.assertEquals(cart.getTotalPrice(), 500);
    }

    @Test
    @DisplayName("장바구니 상품 추가 테스트3")
    void 장바구니_상품_추가_테스트3() {
        setUp();
        setUpCart();
        setUpCartProduct();

        CartProductAddDto cartProductAddDto = new CartProductAddDto();
        cartProductAddDto.setProductId(0L);
        cartProductAddDto.setCount(5);

        cartService.addCart(cartProductAddDto);

        Assertions.assertEquals(cart.getTotalPrice(), 1000);
    }

    @Test
    @DisplayName("장바구니 추가 실패 테스트1")
    void 장바구니_추가_실패_테스트1() {
        setUpMember();

        CartProductAddDto cartProductAddDto = new CartProductAddDto();
        cartProductAddDto.setProductId(0L);
        cartProductAddDto.setCount(5);

        given(memberAuthorizationUtil.getMember()).willThrow(new CustomException(CustomExceptionCode.NOT_VALID_ERROR));

        Assertions.assertThrows(CustomException.class, () -> cartService.addCart(cartProductAddDto));
    }

    @Test
    @DisplayName("장바구니 추가 실패 테스트2")
    void 장바구니_추가_실패_테스트2() {
        setUp();

        CartProductAddDto cartProductAddDto = new CartProductAddDto();
        cartProductAddDto.setProductId(0L);
        cartProductAddDto.setCount(5);

        given(productRepository.findById(any())).willThrow(new CustomException(CustomExceptionCode.NOT_VALID_ERROR));

        Assertions.assertThrows(CustomException.class, () -> cartService.addCart(cartProductAddDto));
    }

    @Test
    @DisplayName("장바구니 조회 테스트")
    void 장바구니_조회_테스트() {
        member = Member.builder()
                .address(new Address("12345", "서울시 강남구 강남대로 114", "테스트 빌딩 5층"))
                .email("test@test.com")
                .password("123456")
                .phone("01012345678")
                .username("테스터")
                .build();

        setUpCart();

        product = Product.builder()
                .productName("테스트 상품")
                .productCode("test-1234")
                .productCategory(ProductCategory.CHAIR)
                .productStatus(ProductStatus.SELLING)
                .size(new ProductSize(50.7, 102.5, 100.3))
                .price(100)
                .stock(10)
                .description("테스트 상품입니다.")
                .build();

        cartProduct = CartProduct.createCartProduct(cart, product, 5);

        given(memberAuthorizationUtil.getMember()).willReturn(member);

        CartDto cartDto = cartService.getCart();

        Assertions.assertEquals(cartDto.getTotalPrice(), 500);
        Assertions.assertEquals(cartDto.getCartProductDtoList().size(), 1);
        Assertions.assertEquals(cartDto.getCartProductDtoList().get(0).getCount(), 5);
    }

    @Test
    @DisplayName("장바구니 수정 테스트")
    void 장바구니_수정_테스트() {
        product = Product.builder()
                .productName("테스트 상품")
                .productCode("test-1234")
                .productCategory(ProductCategory.CHAIR)
                .productStatus(ProductStatus.SELLING)
                .size(new ProductSize(50.7, 102.5, 100.3))
                .price(100)
                .stock(10)
                .description("테스트 상품입니다.")
                .build();

        setUpMember();
        setUpCart();
        setUpCartProduct();

        CartProductEditDto dto = new CartProductEditDto();
        dto.setProductId(0L);
        dto.setCount(1);

        cartService.editCartProduct(dto);

        CartProduct find = cartProductRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        Assertions.assertEquals(find.getCount(), 1);
    }

    @Test
    @DisplayName("장바구니 수정 테스트2")
    void 장바구니_수정_테스트2() {
        product = Product.builder()
                .productName("테스트 상품")
                .productCode("test-1234")
                .productCategory(ProductCategory.CHAIR)
                .productStatus(ProductStatus.SELLING)
                .size(new ProductSize(50.7, 102.5, 100.3))
                .price(100)
                .stock(10)
                .description("테스트 상품입니다.")
                .build();

        setUpMember();
        setUpCart();
        setUpCartProduct();

        CartProductEditDto dto = new CartProductEditDto();
        dto.setProductId(0L);
        dto.setCount(0);

        cartService.editCartProduct(dto);

        verify(cartProductRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("수정 실패 테스트")
    void 장바구니_수정_실패_테스트() {
        setUpMember();

        CartProductEditDto dto = new CartProductEditDto();
        dto.setProductId(0L);
        dto.setCount(5);

        when(memberAuthorizationUtil.getMember()).thenThrow(new CustomException(CustomExceptionCode.NOT_VALID_ERROR));

        Assertions.assertThrows(CustomException.class, () -> cartService.editCartProduct(dto));
    }

    @Test
    @DisplayName("수정 실패 테스트2")
    void 장바구니_수정_실패_테스트2() {
        setUpMember();

        CartProductEditDto dto = new CartProductEditDto();
        dto.setProductId(0L);
        dto.setCount(5);

        when(cartRepository.findByMemberId(member.getId())).thenReturn(null);

        Assertions.assertThrows(CustomException.class, () -> cartService.editCartProduct(dto));
    }

    @Test
    @DisplayName("수정 실패 테스트3")
    void 장바구니_수정_실패_테스트3() {
        setUpMember();
        setUpCart();

        CartProductEditDto dto = new CartProductEditDto();
        dto.setProductId(0L);
        dto.setCount(5);

        when(cartProductRepository.findByCartIdAndProductId(any(), any())).thenReturn(null);

        Assertions.assertThrows(CustomException.class, () -> cartService.editCartProduct(dto));
    }

    void setUpCartProduct() {
        cartProduct = CartProduct.createCartProduct(cart, product, 5);

        given(cartProductRepository.findByCartIdAndProductId(any(), any())).willReturn(cartProduct);
    }

    void setUpCart() {
        cart = Cart.createCart(member);

        given(cartRepository.findByMemberId(any())).willReturn(cart);
    }

    void setUpMember() {
        member = Member.builder()
                .address(new Address("12345", "서울시 강남구 강남대로 114", "테스트 빌딩 5층"))
                .email("test@test.com")
                .password("123456")
                .phone("01012345678")
                .username("테스터")
                .build();

        given(memberAuthorizationUtil.getMember()).willReturn(member);
    }

    void setUp() {
        member = Member.builder()
                .address(new Address("12345", "서울시 강남구 강남대로 114", "테스트 빌딩 5층"))
                .email("test@test.com")
                .password("123456")
                .phone("01012345678")
                .username("테스터")
                .build();

        given(memberAuthorizationUtil.getMember()).willReturn(member);

        product = Product.builder()
                .productName("테스트 상품")
                .productCode("test-1234")
                .productCategory(ProductCategory.CHAIR)
                .productStatus(ProductStatus.SELLING)
                .size(new ProductSize(50.7, 102.5, 100.3))
                .price(100)
                .stock(10)
                .description("테스트 상품입니다.")
                .build();

        when(productRepository.findById(any())).thenReturn(Optional.ofNullable(product));
    }
}