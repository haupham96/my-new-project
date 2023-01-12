
package com.example.bt.app.exception_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : HauPV
 * class xử lý Exception cho Controller
 */
@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    //    Xử lý Exception cho vi phạm các ràng buộc dữ liệu trong database
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public String handleSQLIntegrityConstraintViolationException(Model model,
                                                                 SQLIntegrityConstraintViolationException ex) {
        log.info("class - ControllerExceptionHandler");
        log.info("method : handleSQLIntegrityConstraintViolationException()");
        model.addAttribute("error", ex.getLocalizedMessage());
        log.info("Kết thúc method : handleSQLIntegrityConstraintViolationException()");
        return "error-page";
    }

    //    Xử lý Exception cho các trường hợp còn lại
    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        log.info("class - ControllerExceptionHandler");
        log.info("method : handleException()");
        model.addAttribute("error", ex.getMessage());
        log.error("error : {}", ex.getMessage());
        log.info("kết thúc method : handleException()");
        return "error-page";
    }
}
