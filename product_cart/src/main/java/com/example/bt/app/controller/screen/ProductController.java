
package com.example.bt.app.controller.screen;

import com.example.bt.app.dto.ProductDTO;
import com.example.bt.app.exception.*;
import com.example.bt.app.service.product.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

/**
 * @author : HauPV
 * Controller cho chức năng xử lý sản phẩm
 */
@Slf4j
@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    //    Controller điều hướng đến trang danh sách sản phẩm
    @GetMapping("")
    public String listProduct(Model model) {
        log.info(this.getClass().getSimpleName());
        log.info("mapping : GET /product ");
        log.info("method : listProduct()");
        List<ProductDTO> products = this.iProductService.findAll();
        model.addAttribute("products", products);

        log.info("Kết thúc method : listProduct()");
        return "/product/list";
    }

    //    Controller điều hướng đến form tạo mới sản phẩm - chỉ dành cho Admin
    @GetMapping("/create")
    public String createPage(Model model) {
        log.info(this.getClass().getSimpleName());
        log.info("mapping : GET /product/create ");
        log.info("method : createPage()");
        ProductDTO productDTO = new ProductDTO();
        model.addAttribute("productDTO", productDTO);
        log.info("Kết thúc method : createPage()");
        return "/product/create";
    }

    //    Controller xử lý thêm mới sản phẩm -  chỉ dành cho Admin
    @PostMapping("/create")
    public String createProduct(Model model,
                                @Validated @ModelAttribute ProductDTO productDTO,
                                BindingResult bindingResult) {
        log.info(this.getClass().getSimpleName());
        log.info("mapping : POST /product/create ");
        log.info("method : createProduct()");
        productDTO.setIProductService(iProductService);
        productDTO.validate(productDTO, bindingResult);
        if (bindingResult.hasFieldErrors()) {
            log.info("Khối if : bindingResult.hasFieldErrors() ");
//            Nếu có lỗi validate -> trả về message lỗi cho view
            model.addAttribute("productDTO", productDTO);
            log.info("Kết thúc khối if : bindingResult.hasFieldErrors() ");
            return "/product/create";
        }
        this.iProductService.handleCreate(productDTO, model);

        log.info("Kết thúc method : createProduct()");
        return "/product/create";
    }

    //    Controller dành cho chức năng xem thông tin chi tiết của sản phẩm
    @GetMapping("/detail/{id}")
    public String productDetail(@PathVariable Integer id, Model model) throws Exception {
        log.info(this.getClass().getSimpleName());
        log.info("mapping : GET /product/detail/{}", id);
        log.info("method : productDetail()");
        ProductDTO productDTO = iProductService.findById(id);
        if (productDTO == null) {
            log.info("Khối if : productDTO == null ");
//            Nếu không tìm thấy sản phẩm -> trả về message lỗi cho view
            model.addAttribute("error", "Không tìm thấy sản phẩm !");
            log.info("Kết thúc khối if : productDTO == null ");
            return "/product/detail";
        }
        model.addAttribute("productDTO", productDTO);
        log.info("kết thúc method : productDetail()");
        return "/product/detail";
    }

    //  Xoá sản phẩm : Role - Admin
    @GetMapping("/delete/{productId}")
    public String deleteProduct(@PathVariable int productId, RedirectAttributes redirectAttributes) throws ProductNotFoundException, EmptyCollectionException {
        this.iProductService.handleDelete(productId, redirectAttributes);
        return "redirect:/product";
    }

    //  Điều hướng đến trang Edit sản phẩm : Role Admin
    @GetMapping(value = "/edit/{productId}", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String showEditProductPage(@PathVariable int productId, Model model)
            throws ProductNotFoundException, IOException {
        ProductDTO productDTO = this.iProductService.getDataForEditPage(productId);
        model.addAttribute("productDTO", productDTO);
        return "/product/edit";
    }

    //  Xử lý edit sản phẩm - role Admin
    @PostMapping(value = "/edit/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String editProduct(@PathVariable int productId,
                              Model model,
                              RedirectAttributes redirectAttributes,
                              @Validated @ModelAttribute ProductDTO productDTO,
                              BindingResult bindingResult)
            throws ProductNotFoundException, ConflictException, IOException,
            EmptyFileException, EmptyCollectionException, NullValueException {
        log.info(this.getClass().getSimpleName());
        log.info("POST /edit/{}", productId);
        log.info("method : editProduct");
//      set bean của IProductService để check trùng tên trong db
        productDTO.setIProductService(iProductService);
        productDTO.validate(productDTO, bindingResult);
        if (bindingResult.hasFieldErrors()) {
//          Nếu có lỗi validate -> trả lỗi về trang view
            log.info("khối if bindingResult.hasFieldErrors()");
            model.addAttribute(productDTO);
            log.info("kết thúc khối if bindingResult.hasFieldErrors()");
            return "/product/edit";
        }
        this.iProductService.handleEdit(productId, productDTO);
        redirectAttributes.addFlashAttribute("message", "Chỉnh sửa thành công .");
        log.info("kết thúc method : editProduct");
        return "redirect:/product";
    }
}
