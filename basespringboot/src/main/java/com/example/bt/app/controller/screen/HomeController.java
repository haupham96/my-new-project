
package com.example.bt.app.controller.screen;

import com.example.bt.app.dto.login.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author : HauPV
 * Controller cho chức năng login
 */
@Slf4j
@Controller
@RequestMapping("")
public class HomeController {

    //    Controller điều hướng mặc định đến trang danh sách sản phẩm
    @GetMapping("/")
    public String redirectToProductPage() {
        log.info(this.getClass().getName());
        log.info("mapping : GET / ");
        log.info("method : redirectToProductPage()");
        return "redirect:/product";
    }

    //  Controller điều hướng đến trang nhập thông tin username , password
    @GetMapping("/login")
    public String loginPage(Model model) {
        log.info(this.getClass().getName());
        log.info("mapping : GET /login ");
        log.info("method : loginPage()");
        LoginRequest loginRequest = new LoginRequest();
        model.addAttribute("loginRequest", loginRequest);

        log.info("Kết thúc method : loginPage()");
        return "login";
    }

    //    Controller test chức năng phân quyền cho Admin
    @GetMapping("/admin")
    public String adminPage() {
        log.info(this.getClass().getName());
        log.info("mapping : GET /admin ");
        log.info("method : adminPage()");
        return "admin";
    }

    //    Controller test chức năng phân quyền cho mọi user đã được xác thực
    @GetMapping("/user")
    public String userPage() {
        log.info(this.getClass().getName());
        log.info("mapping : GET /user ");
        log.info("method : userPage()");
        return "user";
    }

    //    Controller điều hướng đến trang thông báo lỗi 403 -> ko có quyền truy cập
    @GetMapping("/403")
    public String forbidden() {
        log.info(this.getClass().getName());
        log.info("mapping : GET /403 ");
        log.info("method : forbidden()");
        return "403";
    }

}
