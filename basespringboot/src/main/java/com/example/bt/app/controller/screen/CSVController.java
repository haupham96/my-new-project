
package com.example.bt.app.controller.screen;

import com.example.bt.app.service.product.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author : HauPV
 * Controller cho chức năng upload CSV
 */
@Slf4j
@Controller
@RequestMapping("/csv")
public class CSVController {

    /**
     * ROLE : AMIN
     * */

    @Autowired
    private IProductService iProductService;

    @GetMapping(value = "/upload")
    public String uploadPage() {
        return "/csv/upload-page";
    }

    //    Controller nhận 1 file CSV và tiến hành thêm đồng loạt tất cả sp trong file vào database
    @PostMapping("/upload")
    public String uploadCSV(@RequestParam MultipartFile csv, Model model) throws Exception {
        log.info("class - CSVController");
        log.info("mapping : POST /upload ");
        log.info("method : uploadCSV()");
        if (csv.isEmpty()) {
            log.info("Khối if : file csv empty");
//            Nếu không có file sẽ gửi message lỗi về cho view
            model.addAttribute("error", "File is Empty");
            log.info("Kết thúc khối if : file csv empty");
            return "/csv/upload-page";
        }
        iProductService.addProductsFromCSV(csv);

        log.info("Kết thúc method : uploadCSV()");
        return "redirect:/product";
    }

}
