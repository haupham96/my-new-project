package com.example.bt.app.service.product;

import com.example.bt.app.dto.ProductDTO;
import com.example.bt.app.entity.Product;
import com.example.bt.app.exception.*;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

/**
 * @author : HauPV
 * service cho product
 */
public interface IProductService {
    void handleCreate(ProductDTO productDTO, Model model);

    Product findByName(String name);

    List<ProductDTO> findAll();

    ProductDTO findById(Integer id) throws ProductNotFoundException, IOException;

    Product findEntityById(Integer productId);

    void addProductsFromCSV(MultipartFile csv) throws Exception;

    void handleDelete(int productId, RedirectAttributes redirectAttributes) throws ProductNotFoundException, EmptyCollectionException;

    ProductDTO getDataForEditPage(int productId) throws ProductNotFoundException, IOException;

    void handleEdit(int productId, ProductDTO productDTO) throws ProductNotFoundException, ConflictException, IOException, EmptyFileException, EmptyCollectionException, NullValueException;
}
