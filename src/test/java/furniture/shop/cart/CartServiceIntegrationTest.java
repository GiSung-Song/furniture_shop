package furniture.shop.cart;

import furniture.shop.cart.dto.CartDto;
import furniture.shop.cart.dto.CartProductAddDto;
import furniture.shop.cart.dto.CartProductEditDto;
import furniture.shop.configure.exception.CustomException;
import furniture.shop.configure.exception.CustomExceptionCode;
import furniture.shop.global.MemberAuthorizationUtil;
import furniture.shop.global.WithMockCustomMember;
import furniture.shop.global.embed.Address;
import furniture.shop.member.Member;
import furniture.shop.member.MemberRepository;
import furniture.shop.member.constant.MemberGender;
import furniture.shop.product.Product;
import furniture.shop.product.ProductRepository;
import furniture.shop.product.constant.ProductCategory;
import furniture.shop.product.embed.ProductSize;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class CartServiceIntegrationTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartProductRepository cartProductRepository;

    @Autowired
    private MemberAuthorizationUtil memberAuthorizationUtil;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    Product product1;
    Product product2;

    @BeforeEach
    void setUp() {
        Member member = Member.builder()
                .username("테스터")
                .email("test@test.com")
                .password("password")
                .phone("01012341234")
                .gender(MemberGender.MALE)
                .address(new Address("11232", "서울시 서울구 서울로", "11 서울아파트 11동 111호"))
                .build();

        //member 미리 저장 하여 @WithMockCustomMember 에서 findByEmail != null 을 하기 위함.
        memberRepository.save(member);

        product1 = Product.builder()
                .productCode("code-1111")
                .productName("product1111")
                .productCategory(ProductCategory.CHAIR)
                .stock(100)
                .price(1)
                .size(new ProductSize(10.5, 10.2, 10.4))
                .description("테스트1111 상품입니다.")
                .build();

        product2 = Product.builder()
                .productCode("code-2222")
                .productName("product2222")
                .productCategory(ProductCategory.BED)
                .stock(5)
                .price(3)
                .size(new ProductSize(150.3, 220.7, 30.6))
                .description("테스트2222 상품입니다.")
                .build();

        productRepository.save(product1);
        productRepository.save(product2);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("장바구니 추가 성공 테스트")
    @WithMockCustomMember(email = "test@test.com")
    void 장바구니_추가_성공_테스트() {
        CartProductAddDto cartProductAddDto = new CartProductAddDto();

        cartProductAddDto.setProductId(product1.getId());
        cartProductAddDto.setCount(20);

        cartService.addCart(cartProductAddDto);

        Cart cart = cartRepository.findByMemberId(memberAuthorizationUtil.getMember().getId());

        assertNotNull(cart);

        CartProduct cartProduct = cartProductRepository.findByCartIdAndProductId(cart.getId(), 1L);

        assertNotNull(cartProduct);
        assertEquals(20, cartProduct.getCount());
    }

    @Test
    @DisplayName("장바구니 추가 실패 테스트")
    @WithMockCustomMember(email = "test@test.com")
    void 장바구니_추가_실패_테스트() {
        CartProductAddDto cartProductAddDto = new CartProductAddDto();

        cartProductAddDto.setProductId(3214321L); //존재하지 않는 상품
        cartProductAddDto.setCount(20);

        CustomException customException = assertThrows(CustomException.class, () -> cartService.addCart(cartProductAddDto));
        assertEquals(CustomExceptionCode.NOT_VALID_ERROR, customException.getCode());
    }

    @Test
    @DisplayName("장바구니 조회 성공 테스트")
    @WithMockCustomMember(email = "test@test.com")
    void 장바구니_조회_성공_테스트() {
        CartProductAddDto cartProductAddDto = new CartProductAddDto();

        cartProductAddDto.setProductId(product1.getId());
        cartProductAddDto.setCount(1);

        cartService.addCart(cartProductAddDto);

        cartProductAddDto.setProductId(product2.getId());
        cartProductAddDto.setCount(1);

        cartService.addCart(cartProductAddDto);

        CartDto cart = cartService.getCart();

        assertNotNull(cart);
        assertEquals(2, cart.getCartProductDtoList().size());
        assertEquals(4, cart.getTotalPrice());
        assertEquals(1, cart.getCartProductDtoList().get(0).getCount());
    }

    @Test
    @DisplayName("빈 장바구니 조회 테스트")
    @WithMockCustomMember(email = "test@test.com")
    void 빈_장바구니_조회_테스트() {
        CartDto cart = cartService.getCart();

        assertNotNull(cart);
        assertEquals(0, cart.getCartProductDtoList().size());
    }

    @Test
    @DisplayName("장바구니 수정 성공 테스트1")
    @WithMockCustomMember(email = "test@test.com")
    void 장바구니_수정_성공_테스트1() {
        CartProductAddDto cartProductAddDto = new CartProductAddDto();

        cartProductAddDto.setProductId(product1.getId());
        cartProductAddDto.setCount(10);

        cartService.addCart(cartProductAddDto);

        cartProductAddDto.setProductId(product2.getId());
        cartProductAddDto.setCount(5);

        cartService.addCart(cartProductAddDto);

        CartProductEditDto cartProductEditDto = new CartProductEditDto();

        cartProductEditDto.setProductId(product1.getId());
        cartProductEditDto.setCount(5);

        cartService.editCartProduct(cartProductEditDto);

        Cart cart = cartRepository.findByMemberId(memberAuthorizationUtil.getMember().getId());
        CartProduct cartProduct = cartProductRepository.findByCartIdAndProductId(cart.getId(), cartProductEditDto.getProductId());

        assertNotNull(cartProduct);
        assertEquals(cartProductEditDto.getCount(), cartProduct.getCount());
    }

    @Test
    @DisplayName("장바구니 수정 성공 테스트2")
    @WithMockCustomMember(email = "test@test.com")
    void 장바구니_수정_성공_테스트2() {
        CartProductAddDto cartProductAddDto = new CartProductAddDto();

        cartProductAddDto.setProductId(product2.getId());
        cartProductAddDto.setCount(5);

        cartService.addCart(cartProductAddDto);

        CartProductEditDto cartProductEditDto = new CartProductEditDto();

        cartProductEditDto.setProductId(product2.getId());
        cartProductEditDto.setCount(0);

        cartService.editCartProduct(cartProductEditDto);

        Cart cart = cartRepository.findByMemberId(memberAuthorizationUtil.getMember().getId());
        CartProduct cartProduct = cartProductRepository.findByCartIdAndProductId(cart.getId(), cartProductEditDto.getProductId());

        assertNull(cartProduct);
    }

    @Test
    @DisplayName("장바구니 수정 실패 테스트")
    @WithMockCustomMember(email = "test@test.com")
    void 장바구니_수정_실패_테스트() {
        CartProductEditDto cartProductEditDto = new CartProductEditDto();

        cartProductEditDto.setProductId(523L);
        cartProductEditDto.setCount(5);

        CustomException customException = assertThrows(CustomException.class, () -> cartService.editCartProduct(cartProductEditDto));

        assertEquals(CustomExceptionCode.NOT_VALID_ERROR, customException.getCode());
    }
}
