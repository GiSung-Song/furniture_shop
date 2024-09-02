package furniture.shop.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import furniture.shop.configure.exception.CustomException;
import furniture.shop.configure.exception.CustomExceptionCode;
import furniture.shop.product.constant.ProductCategory;
import furniture.shop.product.constant.ProductStatus;
import furniture.shop.product.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.ArrayList;
import java.util.List;

import static org.awaitility.Awaitility.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = ProductController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {OncePerRequestFilter.class}))
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductService productService;

    void setUp() {
        List<ProductListDto> productListDtoList = new ArrayList<>();

        for(int i = 0; i <= 10; i++) {
            ProductListDto productListDto = new ProductListDto(Long.valueOf(i), "name" + i, ProductStatus.SELLING, ProductCategory.CHAIR);

            productListDtoList.add(productListDto);
        }

        ProductSearchCondition productSearchCondition = new ProductSearchCondition();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<ProductListDto> pageListDto = new PageImpl<>(productListDtoList, pageable, 11);

        when(productService.getProductList(any(), any())).thenReturn(pageListDto);
    }

    @Test
    @DisplayName("조회 테스트")
    void 조회_테스트() throws Exception {
        setUp();

        mockMvc.perform(get("/product?productName=name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 등록 테스트")
    void 상품_등록_테스트() throws Exception {
        ProductRegisterDto dto = getRegisterDto();

        when(productService.registerProduct(dto)).thenReturn(1L);

        mockMvc.perform(post("/product")
                .content(new ObjectMapper().writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 등록 실패 테스트")
    void 상품_등록_실패_테스트() throws Exception {
        ProductRegisterDto dto = getRegisterDto();

        when(productService.registerProduct(any())).thenThrow(new CustomException(CustomExceptionCode.CODE_DUPLICATE_EXCEPTION));

        mockMvc.perform(post("/product")
                .content(new ObjectMapper().writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 등록 실패 테스트2")
    void 상품_등록_실패_테스트2() throws Exception {
        ProductRegisterDto registerDto = getRegisterDto();

        registerDto.setProductCategory("CATEGORY");
        registerDto.setProductStatus("STATUS");

        mockMvc.perform(post("/product")
                        .content(new ObjectMapper().writeValueAsString(registerDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 조회 테스트")
    void 상품_조회_테스트() throws Exception {
        ProductDetailDto productDetailDto = getProductDetailDto();

        when(productService.getProductDetail(any())).thenReturn(productDetailDto);

        mockMvc.perform(get("/product/{id}", 0L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 조회 실패 테스트")
    void 상품_조회_실패_테스트() throws Exception {
        when(productService.getProductDetail(any())).thenThrow(new CustomException(CustomExceptionCode.NOT_VALID_ERROR));

        mockMvc.perform(get("/product/{id}", 0L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 수정 테스트")
    void 상품_수정_테스트() throws Exception {
        ProductUpdateDto productUpdateDto = new ProductUpdateDto();
        productUpdateDto.setProductStatus("STOP");

        mockMvc.perform(patch("/product/{id}", 0L)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(productUpdateDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 수정 실패 테스트")
    void 상품_수정_실패_테스트() throws Exception {
        ProductUpdateDto productUpdateDto = new ProductUpdateDto();
        productUpdateDto.setProductStatus("STOP");

        when(productService.updateProduct(any(), any())).thenThrow(new CustomException(CustomExceptionCode.NOT_VALID_ERROR));

        mockMvc.perform(patch("/product/{id}", 0L)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(productUpdateDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    ProductDetailDto getProductDetailDto() {
        ProductDetailDto productDetailDto = new ProductDetailDto();

        productDetailDto.setProductCode("test-1234");
        productDetailDto.setProductName("테스트 상품");
        productDetailDto.setProductStatus(ProductStatus.SELLING);
        productDetailDto.setHeight(101.5);
        productDetailDto.setLength(100.2);
        productDetailDto.setWidth(18.6);
        productDetailDto.setCategory(ProductCategory.CHAIR);
        productDetailDto.setDescription("테스트 설명입니다.");
        productDetailDto.setPrice(100003500);
        productDetailDto.setStock(5);
        productDetailDto.setSellingCount(Long.valueOf(100));

        return productDetailDto;
    }

    ProductRegisterDto getRegisterDto() {
        ProductRegisterDto dto = new ProductRegisterDto();

        dto.setProductCode("test-1234");
        dto.setProductName("테스트 상품");
        dto.setProductStatus("SELLING");
        dto.setProductCategory("CHAIR");
        dto.setLength(102.5);
        dto.setHeight(100.3);
        dto.setWidth(50.7);
        dto.setPrice(102030);
        dto.setStock(10);
        dto.setDescription("테스트 상품입니다.");

        return dto;
    }
}