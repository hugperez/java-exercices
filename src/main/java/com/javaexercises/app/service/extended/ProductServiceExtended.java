package com.javaexercises.app.service.extended;

import com.javaexercises.app.domain.Product;
import com.javaexercises.app.repository.ProductRepository;
import com.javaexercises.app.repository.extended.ProductRepositoryExtended;
import com.javaexercises.app.service.ProductService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceExtended extends ProductService {

    private final ProductRepositoryExtended productRepositoryExtended;

    public ProductServiceExtended(ProductRepository productRepository, ProductRepositoryExtended productRepositoryExtended) {
        super(productRepository);
        this.productRepositoryExtended = productRepositoryExtended;
    }

    public List<Product> findAll(boolean isVerified, Integer minNote) {
        List<Product> allProducts = productRepositoryExtended.findAll();

        // Should perform java filters for
        // isVerified
        // if minNote != null then product.note >= minNote
        // the size of allProducts should be maximum 20

        return allProducts;
    }

    public List<Product> findAllJPQL(boolean isVerified, Integer minNote) {
        // should perform the same actions as findAll() but using JPQL in productRepositoryExtended
        // + order by note
        return new ArrayList<>();
    }

    public void deleteProduct(Long id) {
        // should retrieve the product in productRepositoryExtended, and check for isVerified == false before delete, else throw an error
    }

    public Product save(Product product) {
        // should perform actions before save
        // totalWeight = quantity*weight
        // isVerified = quantity && weight & content && note > 5
        return productRepositoryExtended.save(product);
    }

    public String fibonacciSequence(Integer n) {
        String fibonacciSequence = "";
        // should return fibonacci series for n in format "0,1,1,..."
        // Fo = 0, F1 = 1,  Fn = Fn-1 + Fn-2 for n>=2

        return fibonacciSequence;
    }
}
