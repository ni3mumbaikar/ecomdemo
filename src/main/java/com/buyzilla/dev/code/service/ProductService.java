package com.buyzilla.dev.code.service;

import com.buyzilla.dev.code.exceptions.ProductAlreadyExistException;
import com.buyzilla.dev.code.exceptions.ProductNotFoundException;
import com.buyzilla.dev.code.respository.ProductRepository;
import com.buyzilla.dev.code.vo.ProductVo;
import com.buyzilla.dev.code.exceptions.SupplierNotFoundException;
import com.buyzilla.dev.code.respository.SuppliersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.util.List;

@Service //Create Injectable singleton object of service
public class ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    SuppliersRepository suppliersRepository;
    @Autowired
    PlatformTransactionManager platformTransactionManager;
    TransactionTemplate transactionTemplate;

    @PostConstruct
    void initTransactionManagement() {
        transactionTemplate = new TransactionTemplate(platformTransactionManager);
    }

    public List<com.buyzilla.dev.code.entity.Product> getProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    //TODO : Study propagation
    public void saveProducts(List<ProductVo> productVos) throws ProductAlreadyExistException, SupplierNotFoundException {
//        for (ProductVo product : productVos) {
//            if (productRepository.findById(product.getProductID()).isPresent()) {
//                throw new ProductAlreadyExistException(product.getProductID());
//            }
//            productRepository.save(getEntity(product));
//        }
        // enhanced control using programmatic transaction management.
        TransactionCallback<Void> transactionCallback = new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                for (ProductVo product : productVos) {
                    if (productRepository.findById(product.getProductID()).isPresent()) {
                        throw new ProductAlreadyExistException(product.getProductID());
                    }
                    productRepository.save(getEntity(product));
                }
                return null;
            }

        };
        transactionTemplate.execute(transactionCallback);

    }

    public com.buyzilla.dev.code.entity.Product getProductByPid(Integer pid) throws ProductNotFoundException {
        return productRepository.findById(pid).orElseThrow(() -> new ProductNotFoundException(pid));
    }

    public void updateProducts(List<ProductVo> productVos) throws ProductNotFoundException, SupplierNotFoundException {
        for (ProductVo p : productVos) {
            if (productRepository.findById(p.getProductID()).isEmpty()) {
                throw new ProductNotFoundException(p.getProductID());
            }
            productRepository.save(getEntity(p));
        }
    }

    public com.buyzilla.dev.code.entity.Product getEntity(ProductVo productVo) throws SupplierNotFoundException {
        return com.buyzilla.dev.code.entity.Product.builder()
                .productName(productVo.getProductName())
                .productImage(productVo.getProductImage())
                .productID(productVo.getProductID())
                .price(productVo.getPrice())
                .supplier(suppliersRepository.findById(productVo.getSupplierID()).orElseThrow(() -> new SupplierNotFoundException(productVo.getSupplierID())))
                .unit(productVo.getUnit()).build();
        //TODO : jsoncopy one liner
    }

    /*public vo.ProductVo getVo(ProductVo product){
        return vo.ProductVo.builder()
                .productID(product.getProductID())
                .productName(product.getProductName())
                .price(product.getPrice())
                .unit(product.getUnit()).build();
    }*/

}
