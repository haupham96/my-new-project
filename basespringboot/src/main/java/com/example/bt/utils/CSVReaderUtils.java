package com.example.bt.utils;

import com.example.bt.app.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author : HauPV
 * lớp util để đọc file csv
 */
@Slf4j
public class CSVReaderUtils {

    private CSVReaderUtils() {
    }

    //    Đọc file csv sang kiểu List<Product>
    public static List<Product> readProductsFromFileCSV(InputStream inputStream) throws IOException {
        log.info("class - CSVReaderUtils");
        log.info("method - readProductsFromFileCSV()");

        List<Product> list = new ArrayList<>();
//        Khai báo encoding là SHIFT_JIS
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                Charset.forName("SHIFT_JIS"));
        BufferedReader br = new BufferedReader(inputStreamReader);
        String line;
        while ((line = br.readLine()) != null) {
            try {
                log.info("khối try");
                String[] stringArr = line.split(",");
                Product product = new Product(
                        stringArr[0],
                        Long.parseLong(stringArr[1]),
                        stringArr[2] != null ? stringArr[2] : null);
                list.add(product);
                log.info("kết thúc khối try");
            } catch (Exception ex) {
                log.info("khối catch");
                log.error("error : {}", ex.getMessage());
                log.info("kết thúc khối catch -> Exception");
            }
        }

        log.info("kết thúc method - readProductsFromFileCSV()");
        return list;
    }

}
