package furniture.shop.product;

import furniture.shop.configure.exception.CustomException;
import furniture.shop.configure.exception.CustomExceptionCode;
import furniture.shop.product.constant.ProductCategory;
import furniture.shop.product.constant.ProductStatus;
import furniture.shop.product.dto.*;
import furniture.shop.product.embed.ProductSize;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductQueryRepository productQueryRepository;

    @Transactional(readOnly = true)
    public Page<ProductListDto> getProductList(ProductSearchCondition productSearchCondition, Pageable pageable) {
        return productQueryRepository.searchProductPage(productSearchCondition, pageable);
    }

    @Transactional
    public Long registerProduct(ProductRegisterDto dto) {
        Product findProduct = productRepository.findByProductCode(dto.getProductCode());

        if (findProduct != null) {
            throw new CustomException(CustomExceptionCode.CODE_DUPLICATE_EXCEPTION);
        }

        Product product = Product.builder()
                .productCode(dto.getProductCode())
                .productName(dto.getProductName())
                .productCategory(dto.getProductCategory())
                .productStatus(dto.getProductStatus())
                .stock(dto.getStock())
                .price(dto.getPrice())
                .size(new ProductSize(dto.getWidth(), dto.getLength(), dto.getHeight()))
                .description(dto.getDescription())
                .build();

        return productRepository.save(product).getId();
    }

    @Transactional(readOnly = true)
    public ProductDetailDto getProductDetail(Long productId) {
        if (!productRepository.findById(productId).isPresent()) {
            throw new CustomException(CustomExceptionCode.NOT_VALID_ERROR);
        }

        Product product = productRepository.findById(productId).get();

        return detailEntityToDto(product);
    }

    @Transactional
    public ProductDetailDto updateProduct(Long productId, ProductUpdateDto updateDto) {
        if (!productRepository.findById(productId).isPresent()) {
            throw new CustomException(CustomExceptionCode.NOT_VALID_ERROR);
        }

        Product product = productRepository.findById(productId).get();

        if (isValidEnumType(updateDto.getProductStatus())) {
            product.updateProductStatus(updateDto.getProductStatus());
        }

        if (StringUtils.hasText(updateDto.getDescription())) {
            product.updateDescription(updateDto.getDescription());
        }

        if (updateDto.getPrice() != 0) {
            product.updatePrice(updateDto.getPrice());
        }

        if (updateDto.getStock() != 0) {
            product.updateStock(updateDto.getStock());
        }

        if (updateDto.getLength() != 0) {
            if (updateDto.getWidth() != 0) {
                if (updateDto.getHeight() != 0) {
                    product.updateSize(new ProductSize(updateDto.getWidth(), updateDto.getLength(), updateDto.getHeight()));
                }
            }
        }

        return detailEntityToDto(product);
    }

    private boolean isValidEnumType(Enum<?> enumValue) {

        if (enumValue == null) {
            return false;
        }

        if (!StringUtils.hasText(enumValue.name())) {
            return false;
        }

        for (Enum<?> value : enumValue.getDeclaringClass().getEnumConstants()) {
            if (enumValue.equals(value)) {
                return true;
            }
        }

        return false;
    }

    private ProductDetailDto detailEntityToDto(Product product) {
        ProductDetailDto productDetailDto = new ProductDetailDto();

        productDetailDto.setProductCode(product.getProductCode());
        productDetailDto.setProductName(product.getProductName());
        productDetailDto.setProductStatus(product.getProductStatus());
        productDetailDto.setHeight(product.getSize().getHeight());
        productDetailDto.setLength(product.getSize().getLength());
        productDetailDto.setWidth(product.getSize().getWidth());
        productDetailDto.setCategory(product.getProductCategory());
        productDetailDto.setDescription(product.getDescription());
        productDetailDto.setPrice(product.getPrice());
        productDetailDto.setStock(product.getStock());
        productDetailDto.setSellingCount(product.getSellingCount());

        return productDetailDto;
    }

}
