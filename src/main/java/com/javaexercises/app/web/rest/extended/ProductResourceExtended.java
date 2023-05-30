package com.javaexercises.app.web.rest.extended;

import com.javaexercises.app.domain.Product;
import com.javaexercises.app.repository.ProductRepository;
import com.javaexercises.app.service.ProductQueryService;
import com.javaexercises.app.service.extended.ProductServiceExtended;
import com.javaexercises.app.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for managing {@link Product}.
 */
@RestController
@RequestMapping("/api/v1/extended")
public class ProductResourceExtended {

    private final Logger log = LoggerFactory.getLogger(ProductResourceExtended.class);

    private static final String ENTITY_NAME = "product";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductServiceExtended productServiceExtended;

    private final ProductRepository productRepository;

    private final ProductQueryService productQueryService;

    public ProductResourceExtended(
        ProductServiceExtended productServiceExtended,
        ProductRepository productRepository,
        ProductQueryService productQueryService
    ) {
        this.productServiceExtended = productServiceExtended;
        this.productRepository = productRepository;
        this.productQueryService = productQueryService;
    }

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) throws URISyntaxException {
        log.debug("REST request to save Product : {}", product);
        if (product.getId() != null) {
            throw new BadRequestAlertException("A new product cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Product result = productServiceExtended.save(product);
        return ResponseEntity
            .created(new URI("/api/products/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Product product
    ) throws URISyntaxException {
        log.debug("REST request to update Product : {}, {}", id, product);
        if (product.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, product.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Product result = productServiceExtended.update(product);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, product.getId().toString()))
            .body(result);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts(boolean isVerified, Integer minNote) {
        log.debug("REST request to get Products with is verified, minNote: {} {}", isVerified, minNote);
        List<Product> entityList = productServiceExtended.findAll(isVerified, minNote);
        return ResponseEntity.ok().body(entityList);
    }

    @GetMapping("/products-jpql")
    public ResponseEntity<List<Product>> getAllProductsWithJPQL(boolean isVerified, Integer minNote) {
        log.debug("REST request to get Products JPQL with is verified, minNote: {} {}", isVerified, minNote);
        List<Product> entityList = productServiceExtended.findAllJPQL(isVerified, minNote);
        return ResponseEntity.ok().body(entityList);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.debug("REST request to delete Product : {}", id);
        productServiceExtended.deleteProduct(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
