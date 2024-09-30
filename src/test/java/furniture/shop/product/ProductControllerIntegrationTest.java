package furniture.shop.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import furniture.shop.global.WithMockCustomMember;
import furniture.shop.member.constant.MemberRole;
import furniture.shop.product.constant.ProductCategory;
import furniture.shop.product.constant.ProductStatus;
import furniture.shop.product.dto.ProductRegisterDto;
import furniture.shop.product.dto.ProductUpdateDto;
import furniture.shop.product.embed.ProductSize;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("상품 목록 조회 성공 테스트")
    void 상품_목록_조회_성공_테스트() throws Exception {
        Product product1 = Product.builder()
                .productCode("code-1111")
                .productName("product1111")
                .productCategory(ProductCategory.CHAIR)
                .stock(100)
                .price(100000)
                .size(new ProductSize(10.5, 10.2, 10.4))
                .description("테스트1111 상품입니다.")
                .build();

        Product product2 = Product.builder()
                .productCode("code-2222")
                .productName("product2222")
                .productCategory(ProductCategory.BED)
                .stock(5)
                .price(2460000)
                .size(new ProductSize(150.3, 220.7, 30.6))
                .description("테스트2222 상품입니다.")
                .build();

        productRepository.save(product1);
        productRepository.save(product2);

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .param("productName", "product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content[0].productName").value("product1111"))
                .andExpect(jsonPath("$.result.content[1].productName").value("product2222"))
                .andDo(print());
    }

    @Test
    @DisplayName("상품 목록 조회 결과 없음 테스트")
    void 상품_목록_조회_결과_없음_테스트() throws Exception {
        Product product1 = Product.builder()
                .productCode("code-1111")
                .productName("product1111")
                .productCategory(ProductCategory.CHAIR)
                .stock(100)
                .price(100000)
                .size(new ProductSize(10.5, 10.2, 10.4))
                .description("테스트1111 상품입니다.")
                .build();

        Product product2 = Product.builder()
                .productCode("code-2222")
                .productName("product2222")
                .productCategory(ProductCategory.BED)
                .stock(5)
                .price(2460000)
                .size(new ProductSize(150.3, 220.7, 30.6))
                .description("테스트2222 상품입니다.")
                .build();

        productRepository.save(product1);
        productRepository.save(product2);

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .param("productStatus", "STOP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.totalElements").value(0))
                .andDo(print());
    }

    @Test
    @DisplayName("상품 등록 성공 테스트")
    @WithMockCustomMember(email = "test@test.com", role = MemberRole.ADMIN)
    void 상품_등록_성공_테스트() throws Exception {
        ProductRegisterDto productRegisterDto = new ProductRegisterDto();

        productRegisterDto.setProductCode("chair-1111");
        productRegisterDto.setProductName("의자1111");
        productRegisterDto.setStock(35);
        productRegisterDto.setPrice(120000);
        productRegisterDto.setProductCategory(ProductCategory.CHAIR);
        productRegisterDto.setProductStatus(ProductStatus.SELLING);
        productRegisterDto.setDescription("의자1111 입니다.");
        productRegisterDto.setWidth(50.7);
        productRegisterDto.setLength(100.4);
        productRegisterDto.setHeight(73.56);

        mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(productRegisterDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("상품을 등록했습니다."))
                .andDo(print());

        List<Product> products = productRepository.findAll();

        assertEquals(1, products.size());
        assertEquals(productRegisterDto.getProductCode(), products.get(0).getProductCode());
    }

    @Test
    @DisplayName("상품 등록 실패 테스트 - 권한 없음1")
    void 상품_등록_실패_테스트_권한없음1() throws Exception {
        ProductRegisterDto productRegisterDto = new ProductRegisterDto();

        productRegisterDto.setProductCode("chair-1111");
        productRegisterDto.setProductName("의자1111");
        productRegisterDto.setStock(35);
        productRegisterDto.setPrice(120000);
        productRegisterDto.setProductCategory(ProductCategory.CHAIR);
        productRegisterDto.setProductStatus(ProductStatus.SELLING);
        productRegisterDto.setDescription("의자1111 입니다.");
        productRegisterDto.setWidth(50.7);
        productRegisterDto.setLength(100.4);
        productRegisterDto.setHeight(73.56);

        mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productRegisterDto)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 등록 실패 테스트 - 권한 없음2")
    @WithMockCustomMember(email = "test@test.com", role = MemberRole.MEMBER)
    void 상품_등록_실패_테스트_권한없음2() throws Exception {
        ProductRegisterDto productRegisterDto = new ProductRegisterDto();

        productRegisterDto.setProductCode("chair-1111");
        productRegisterDto.setProductName("의자1111");
        productRegisterDto.setStock(35);
        productRegisterDto.setPrice(120000);
        productRegisterDto.setProductCategory(ProductCategory.CHAIR);
        productRegisterDto.setProductStatus(ProductStatus.SELLING);
        productRegisterDto.setDescription("의자1111 입니다.");
        productRegisterDto.setWidth(50.7);
        productRegisterDto.setLength(100.4);
        productRegisterDto.setHeight(73.56);

        mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productRegisterDto)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 등록 실패 테스트 - 중복")
    @WithMockCustomMember(email = "test@test.com", role = MemberRole.ADMIN)
    void 상품_등록_실패_테스트_중복() throws Exception {
        ProductRegisterDto productRegisterDto = new ProductRegisterDto();

        productRegisterDto.setProductCode("chair-1111");
        productRegisterDto.setProductName("의자1111");
        productRegisterDto.setStock(35);
        productRegisterDto.setPrice(120000);
        productRegisterDto.setProductCategory(ProductCategory.CHAIR);
        productRegisterDto.setProductStatus(ProductStatus.SELLING);
        productRegisterDto.setDescription("의자1111 입니다.");
        productRegisterDto.setWidth(50.7);
        productRegisterDto.setLength(100.4);
        productRegisterDto.setHeight(73.56);

        mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productRegisterDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("상품을 등록했습니다."))
                .andDo(print());

        mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productRegisterDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 등록된 상품코드입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("상품 등록 실패 테스트 - 입력 오류")
    @WithMockCustomMember(email = "test@test.com", role = MemberRole.ADMIN)
    void 상품_등록_실패_테스트_입력_오류() throws Exception {
        ProductRegisterDto productRegisterDto = new ProductRegisterDto();

        productRegisterDto.setProductCode("chair-1111");
        productRegisterDto.setProductName("의자1111");
        productRegisterDto.setStock(35);
        productRegisterDto.setDescription("의자1111 입니다.");

        mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productRegisterDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("상품 상세 조회 성공 테스트")
    @WithMockCustomMember
    void 상품_상세_조회_성공_테스트() throws Exception {
        Product product = Product.builder()
                .productCode("code-1111")
                .productName("product1111")
                .productCategory(ProductCategory.CHAIR)
                .stock(100)
                .price(100000)
                .size(new ProductSize(10.5, 10.2, 10.4))
                .description("테스트1111 상품입니다.")
                .build();

        Long productId = productRepository.save(product).getId();

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/product/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("상품을 조회했습니다."))
                .andExpect(jsonPath("$.result.productName").value(product.getProductName()))
                .andExpect(jsonPath("$.result.productCode").value(product.getProductCode()))
                .andDo(print());
    }

    @Test
    @DisplayName("상품 상세 조회 실패 테스트")
    @WithMockCustomMember
    void 상품_상세_조회_실패_테스트() throws Exception {
        mockMvc.perform(get("/product/{id}", 100L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("상품 수정 성공 테스트")
    @WithMockCustomMember(role = MemberRole.ADMIN)
    void 상품_수정_성공_테스트() throws Exception {
        Product product = Product.builder()
                .productCode("chair-1111")
                .productName("의자-1111")
                .productCategory(ProductCategory.CHAIR)
                .stock(35)
                .price(120000)
                .size(new ProductSize(50.7, 100.4, 73.56))
                .description("의자1111 입니다.")
                .productStatus(ProductStatus.SELLING)
                .build();

        Long productId = productRepository.save(product).getId();

        entityManager.flush();
        entityManager.clear();

        ProductUpdateDto productUpdateDto = new ProductUpdateDto();

        productUpdateDto.setDescription("판매 중단된 상품 입니다.");
        productUpdateDto.setProductStatus(ProductStatus.STOP);
        productUpdateDto.setStock(2500);
        productUpdateDto.setPrice(1000000);

        mockMvc.perform(patch("/product/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(productUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("상품을 수정했습니다."))
                .andExpect(jsonPath("$.result.productStatus").value(productUpdateDto.getProductStatus().name()))
                .andExpect(jsonPath("$.result.stock").value(productUpdateDto.getStock()))
                .andExpect(jsonPath("$.result.price").value(productUpdateDto.getPrice()))
                .andExpect(jsonPath("$.result.description").value(productUpdateDto.getDescription()))
                .andExpect(jsonPath("$.result.productCode").value(product.getProductCode()))
                .andDo(print());
    }

    @Test
    @DisplayName("상품 수정 실패 테스트 - 권한 없음1")
    void 상품_수정_실패_테스트_권한없음1() throws Exception {
        Product product = Product.builder()
                .productCode("chair-1111")
                .productName("의자-1111")
                .productCategory(ProductCategory.CHAIR)
                .stock(35)
                .price(120000)
                .size(new ProductSize(50.7, 100.4, 73.56))
                .description("의자1111 입니다.")
                .productStatus(ProductStatus.SELLING)
                .build();

        Long productId = productRepository.save(product).getId();

        entityManager.flush();
        entityManager.clear();

        ProductUpdateDto productUpdateDto = new ProductUpdateDto();

        productUpdateDto.setDescription("판매 중단된 상품 입니다.");
        productUpdateDto.setProductStatus(ProductStatus.STOP);
        productUpdateDto.setStock(2500);
        productUpdateDto.setPrice(1000000);

        mockMvc.perform(patch("/product/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productUpdateDto)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 수정 실패 테스트 - 권한 없음2")
    @WithMockCustomMember
    void 상품_수정_실패_테스트_권한없음2() throws Exception {
        Product product = Product.builder()
                .productCode("chair-1111")
                .productName("의자-1111")
                .productCategory(ProductCategory.CHAIR)
                .stock(35)
                .price(120000)
                .size(new ProductSize(50.7, 100.4, 73.56))
                .description("의자1111 입니다.")
                .productStatus(ProductStatus.SELLING)
                .build();

        Long productId = productRepository.save(product).getId();

        entityManager.flush();
        entityManager.clear();

        ProductUpdateDto productUpdateDto = new ProductUpdateDto();

        productUpdateDto.setDescription("판매 중단된 상품 입니다.");
        productUpdateDto.setProductStatus(ProductStatus.STOP);
        productUpdateDto.setStock(2500);
        productUpdateDto.setPrice(1000000);

        mockMvc.perform(patch("/product/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productUpdateDto)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 수정 실패 테스트 - 상품 없음")
    @WithMockCustomMember(role = MemberRole.ADMIN)
    void 상품_수정_실패_테스트_상품없음() throws Exception {
        ProductUpdateDto productUpdateDto = new ProductUpdateDto();

        productUpdateDto.setDescription("판매 중단된 상품 입니다.");
        productUpdateDto.setProductStatus(ProductStatus.STOP);
        productUpdateDto.setStock(2500);
        productUpdateDto.setPrice(1000000);

        mockMvc.perform(patch("/product/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productUpdateDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
