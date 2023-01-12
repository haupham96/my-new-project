
package com.example.bt.app.dto;

import com.example.bt.app.entity.Image;
import com.example.bt.app.entity.Product;
import com.example.bt.app.entity.ProductImage;
import com.example.bt.app.service.product.IProductService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author : HauPV
 * class validate cho Product
 */
@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO implements Validator, Serializable {

    private int id;
    @NotBlank(message = "Vui lòng khôg để trống.")
    @Pattern(regexp = "(^$)|(^[a-zA-Z0-9\\s]+$)", message = "Không được chứa kí tự dặc biệt .")
    private String name;

    @Min(value = 1000000, message = "số tiền hiện đang dưới 1,000,000 vnđ")
    private long price;

    @NotBlank(message = "Không được để trống .")
    private String description;
    private transient MultipartFile mainImage;
    private transient MultipartFile[] detailImages;
    private transient IProductService iProductService;

    private List<Image> images;

    //    Method override mặc định , không đụng đến
    @Override
    public boolean supports(Class<?> clazz) {
        log.info("class - {}" + this.getClass().getSimpleName());
        log.info("method : supports()");
        return false;
    }

    //    Validated dữ liệu người dùng nhập
    @Override
    public void validate(Object target, Errors errors) {
        log.info("class - {}" + this.getClass().getSimpleName());
        log.info("method : validate()");
        ProductDTO productDTO = (ProductDTO) target;
        Product productFindByName = this.iProductService.findByName(productDTO.getName());
        Product productFindByID = this.iProductService.findEntityById(productDTO.getId());

//      Validate trùng tên sản phẩm
        if (productFindByName != null && productFindByName.equals(productFindByID)) {
//          Trường hợp tìm theo tên và id đều giống nhau -> Trường hợp Edit nhưng ko sửa tên
            log.info("Khối if productFindByName != null && productFindByName.equals(productFindByID)");
            if (!productDTO.getName().equals(productFindByName.getName())) {
//          Trường hợp Edit nhưng tên đã bị trùng trong db
                log.info("khối if !productDTO.getName().equals(productFindByName.getName())");
                errors.rejectValue("name", "", "Tên đã tồn tại .");
                log.info("kết thúc khối if !productDTO.getName().equals(productFindByName.getName()) ");
            }
            log.info("kết thúc khối if productFindByName != null && productFindByName.equals(productFindByID)");
        } else if (productFindByName != null && productFindByID == null) {
//          Nếu tìm thấy sp theo tên nhưng tìm theo id chưa có trong db -> chức năng thêm mới và bị trùng tên sp
            log.info("Khối else - if productFindByName != null && productFindByName.equals(productFindByID)");
            errors.rejectValue("name", "", "Tên đã tồn tại .");
            log.info("kết thúc khối else - if productFindByName != null && productFindByName.equals(productFindByID)");
        } else if (productFindByName != null) {
//          Trường hợp edit và đã bị trùng tên sp
            errors.rejectValue("name", "", "Tên đã tồn tại .");
        }
        log.info("Kết thúc method : validate()");
    }

    //    Chuyển đổi từ kiều ProductDTO sang kiểu Product
    public static Product mapToEntity(ProductDTO productDTO) throws IOException {
        log.info("class - ProductDTO");
        log.info("method : mapToEntity()");
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        log.info("Kết thúc method : mapToEntity()");
        return product;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ProductDTO)) return false;
        ProductDTO productDTO = (ProductDTO) obj;
        return getId() == productDTO.getId();
    }
}
