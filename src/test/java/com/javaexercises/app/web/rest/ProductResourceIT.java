package com.javaexercises.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.javaexercises.app.IntegrationTest;
import com.javaexercises.app.domain.Product;
import com.javaexercises.app.repository.ProductRepository;
import com.javaexercises.app.service.criteria.ProductCriteria;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProductResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_NOTE = 1;
    private static final Integer UPDATED_NOTE = 2;
    private static final Integer SMALLER_NOTE = 1 - 1;

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final Float DEFAULT_WEIGHT = 1F;
    private static final Float UPDATED_WEIGHT = 2F;
    private static final Float SMALLER_WEIGHT = 1F - 1F;

    private static final Float DEFAULT_TOTAL_WEIGHT = 1F;
    private static final Float UPDATED_TOTAL_WEIGHT = 2F;
    private static final Float SMALLER_TOTAL_WEIGHT = 1F - 1F;

    private static final Boolean DEFAULT_IS_VERIFIED = false;
    private static final Boolean UPDATED_IS_VERIFIED = true;

    private static final String ENTITY_API_URL = "/api/products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductMockMvc;

    private Product product;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createEntity(EntityManager em) {
        Product product = new Product()
            .name(DEFAULT_NAME)
            .note(DEFAULT_NOTE)
            .content(DEFAULT_CONTENT)
            .quantity(DEFAULT_QUANTITY)
            .weight(DEFAULT_WEIGHT)
            .totalWeight(DEFAULT_TOTAL_WEIGHT)
            .isVerified(DEFAULT_IS_VERIFIED);
        return product;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createUpdatedEntity(EntityManager em) {
        Product product = new Product()
            .name(UPDATED_NAME)
            .note(UPDATED_NOTE)
            .content(UPDATED_CONTENT)
            .quantity(UPDATED_QUANTITY)
            .weight(UPDATED_WEIGHT)
            .totalWeight(UPDATED_TOTAL_WEIGHT)
            .isVerified(UPDATED_IS_VERIFIED);
        return product;
    }

    @BeforeEach
    public void initTest() {
        product = createEntity(em);
    }

    @Test
    @Transactional
    void createProduct() throws Exception {
        int databaseSizeBeforeCreate = productRepository.findAll().size();
        // Create the Product
        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isCreated());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate + 1);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProduct.getNote()).isEqualTo(DEFAULT_NOTE);
        assertThat(testProduct.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testProduct.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testProduct.getWeight()).isEqualTo(DEFAULT_WEIGHT);
        assertThat(testProduct.getTotalWeight()).isEqualTo(DEFAULT_TOTAL_WEIGHT);
        assertThat(testProduct.getIsVerified()).isEqualTo(DEFAULT_IS_VERIFIED);
    }

    @Test
    @Transactional
    void createProductWithExistingId() throws Exception {
        // Create the Product with an existing ID
        product.setId(1L);

        int databaseSizeBeforeCreate = productRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setName(null);

        // Create the Product, which fails.

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProducts() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].weight").value(hasItem(DEFAULT_WEIGHT.doubleValue())))
            .andExpect(jsonPath("$.[*].totalWeight").value(hasItem(DEFAULT_TOTAL_WEIGHT.doubleValue())))
            .andExpect(jsonPath("$.[*].isVerified").value(hasItem(DEFAULT_IS_VERIFIED.booleanValue())));
    }

    @Test
    @Transactional
    void getProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get the product
        restProductMockMvc
            .perform(get(ENTITY_API_URL_ID, product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(product.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.weight").value(DEFAULT_WEIGHT.doubleValue()))
            .andExpect(jsonPath("$.totalWeight").value(DEFAULT_TOTAL_WEIGHT.doubleValue()))
            .andExpect(jsonPath("$.isVerified").value(DEFAULT_IS_VERIFIED.booleanValue()));
    }

    @Test
    @Transactional
    void getProductsByIdFiltering() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        Long id = product.getId();

        defaultProductShouldBeFound("id.equals=" + id);
        defaultProductShouldNotBeFound("id.notEquals=" + id);

        defaultProductShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProductShouldNotBeFound("id.greaterThan=" + id);

        defaultProductShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProductShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name equals to DEFAULT_NAME
        defaultProductShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the productList where name equals to UPDATED_NAME
        defaultProductShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name in DEFAULT_NAME or UPDATED_NAME
        defaultProductShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the productList where name equals to UPDATED_NAME
        defaultProductShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name is not null
        defaultProductShouldBeFound("name.specified=true");

        // Get all the productList where name is null
        defaultProductShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByNameContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name contains DEFAULT_NAME
        defaultProductShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the productList where name contains UPDATED_NAME
        defaultProductShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name does not contain DEFAULT_NAME
        defaultProductShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the productList where name does not contain UPDATED_NAME
        defaultProductShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where note equals to DEFAULT_NOTE
        defaultProductShouldBeFound("note.equals=" + DEFAULT_NOTE);

        // Get all the productList where note equals to UPDATED_NOTE
        defaultProductShouldNotBeFound("note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllProductsByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where note in DEFAULT_NOTE or UPDATED_NOTE
        defaultProductShouldBeFound("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE);

        // Get all the productList where note equals to UPDATED_NOTE
        defaultProductShouldNotBeFound("note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllProductsByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where note is not null
        defaultProductShouldBeFound("note.specified=true");

        // Get all the productList where note is null
        defaultProductShouldNotBeFound("note.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByNoteIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where note is greater than or equal to DEFAULT_NOTE
        defaultProductShouldBeFound("note.greaterThanOrEqual=" + DEFAULT_NOTE);

        // Get all the productList where note is greater than or equal to UPDATED_NOTE
        defaultProductShouldNotBeFound("note.greaterThanOrEqual=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllProductsByNoteIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where note is less than or equal to DEFAULT_NOTE
        defaultProductShouldBeFound("note.lessThanOrEqual=" + DEFAULT_NOTE);

        // Get all the productList where note is less than or equal to SMALLER_NOTE
        defaultProductShouldNotBeFound("note.lessThanOrEqual=" + SMALLER_NOTE);
    }

    @Test
    @Transactional
    void getAllProductsByNoteIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where note is less than DEFAULT_NOTE
        defaultProductShouldNotBeFound("note.lessThan=" + DEFAULT_NOTE);

        // Get all the productList where note is less than UPDATED_NOTE
        defaultProductShouldBeFound("note.lessThan=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllProductsByNoteIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where note is greater than DEFAULT_NOTE
        defaultProductShouldNotBeFound("note.greaterThan=" + DEFAULT_NOTE);

        // Get all the productList where note is greater than SMALLER_NOTE
        defaultProductShouldBeFound("note.greaterThan=" + SMALLER_NOTE);
    }

    @Test
    @Transactional
    void getAllProductsByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where content equals to DEFAULT_CONTENT
        defaultProductShouldBeFound("content.equals=" + DEFAULT_CONTENT);

        // Get all the productList where content equals to UPDATED_CONTENT
        defaultProductShouldNotBeFound("content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllProductsByContentIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where content in DEFAULT_CONTENT or UPDATED_CONTENT
        defaultProductShouldBeFound("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT);

        // Get all the productList where content equals to UPDATED_CONTENT
        defaultProductShouldNotBeFound("content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllProductsByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where content is not null
        defaultProductShouldBeFound("content.specified=true");

        // Get all the productList where content is null
        defaultProductShouldNotBeFound("content.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByContentContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where content contains DEFAULT_CONTENT
        defaultProductShouldBeFound("content.contains=" + DEFAULT_CONTENT);

        // Get all the productList where content contains UPDATED_CONTENT
        defaultProductShouldNotBeFound("content.contains=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllProductsByContentNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where content does not contain DEFAULT_CONTENT
        defaultProductShouldNotBeFound("content.doesNotContain=" + DEFAULT_CONTENT);

        // Get all the productList where content does not contain UPDATED_CONTENT
        defaultProductShouldBeFound("content.doesNotContain=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where quantity equals to DEFAULT_QUANTITY
        defaultProductShouldBeFound("quantity.equals=" + DEFAULT_QUANTITY);

        // Get all the productList where quantity equals to UPDATED_QUANTITY
        defaultProductShouldNotBeFound("quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where quantity in DEFAULT_QUANTITY or UPDATED_QUANTITY
        defaultProductShouldBeFound("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY);

        // Get all the productList where quantity equals to UPDATED_QUANTITY
        defaultProductShouldNotBeFound("quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where quantity is not null
        defaultProductShouldBeFound("quantity.specified=true");

        // Get all the productList where quantity is null
        defaultProductShouldNotBeFound("quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where quantity is greater than or equal to DEFAULT_QUANTITY
        defaultProductShouldBeFound("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the productList where quantity is greater than or equal to UPDATED_QUANTITY
        defaultProductShouldNotBeFound("quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where quantity is less than or equal to DEFAULT_QUANTITY
        defaultProductShouldBeFound("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the productList where quantity is less than or equal to SMALLER_QUANTITY
        defaultProductShouldNotBeFound("quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where quantity is less than DEFAULT_QUANTITY
        defaultProductShouldNotBeFound("quantity.lessThan=" + DEFAULT_QUANTITY);

        // Get all the productList where quantity is less than UPDATED_QUANTITY
        defaultProductShouldBeFound("quantity.lessThan=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where quantity is greater than DEFAULT_QUANTITY
        defaultProductShouldNotBeFound("quantity.greaterThan=" + DEFAULT_QUANTITY);

        // Get all the productList where quantity is greater than SMALLER_QUANTITY
        defaultProductShouldBeFound("quantity.greaterThan=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByWeightIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where weight equals to DEFAULT_WEIGHT
        defaultProductShouldBeFound("weight.equals=" + DEFAULT_WEIGHT);

        // Get all the productList where weight equals to UPDATED_WEIGHT
        defaultProductShouldNotBeFound("weight.equals=" + UPDATED_WEIGHT);
    }

    @Test
    @Transactional
    void getAllProductsByWeightIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where weight in DEFAULT_WEIGHT or UPDATED_WEIGHT
        defaultProductShouldBeFound("weight.in=" + DEFAULT_WEIGHT + "," + UPDATED_WEIGHT);

        // Get all the productList where weight equals to UPDATED_WEIGHT
        defaultProductShouldNotBeFound("weight.in=" + UPDATED_WEIGHT);
    }

    @Test
    @Transactional
    void getAllProductsByWeightIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where weight is not null
        defaultProductShouldBeFound("weight.specified=true");

        // Get all the productList where weight is null
        defaultProductShouldNotBeFound("weight.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByWeightIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where weight is greater than or equal to DEFAULT_WEIGHT
        defaultProductShouldBeFound("weight.greaterThanOrEqual=" + DEFAULT_WEIGHT);

        // Get all the productList where weight is greater than or equal to UPDATED_WEIGHT
        defaultProductShouldNotBeFound("weight.greaterThanOrEqual=" + UPDATED_WEIGHT);
    }

    @Test
    @Transactional
    void getAllProductsByWeightIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where weight is less than or equal to DEFAULT_WEIGHT
        defaultProductShouldBeFound("weight.lessThanOrEqual=" + DEFAULT_WEIGHT);

        // Get all the productList where weight is less than or equal to SMALLER_WEIGHT
        defaultProductShouldNotBeFound("weight.lessThanOrEqual=" + SMALLER_WEIGHT);
    }

    @Test
    @Transactional
    void getAllProductsByWeightIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where weight is less than DEFAULT_WEIGHT
        defaultProductShouldNotBeFound("weight.lessThan=" + DEFAULT_WEIGHT);

        // Get all the productList where weight is less than UPDATED_WEIGHT
        defaultProductShouldBeFound("weight.lessThan=" + UPDATED_WEIGHT);
    }

    @Test
    @Transactional
    void getAllProductsByWeightIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where weight is greater than DEFAULT_WEIGHT
        defaultProductShouldNotBeFound("weight.greaterThan=" + DEFAULT_WEIGHT);

        // Get all the productList where weight is greater than SMALLER_WEIGHT
        defaultProductShouldBeFound("weight.greaterThan=" + SMALLER_WEIGHT);
    }

    @Test
    @Transactional
    void getAllProductsByTotalWeightIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where totalWeight equals to DEFAULT_TOTAL_WEIGHT
        defaultProductShouldBeFound("totalWeight.equals=" + DEFAULT_TOTAL_WEIGHT);

        // Get all the productList where totalWeight equals to UPDATED_TOTAL_WEIGHT
        defaultProductShouldNotBeFound("totalWeight.equals=" + UPDATED_TOTAL_WEIGHT);
    }

    @Test
    @Transactional
    void getAllProductsByTotalWeightIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where totalWeight in DEFAULT_TOTAL_WEIGHT or UPDATED_TOTAL_WEIGHT
        defaultProductShouldBeFound("totalWeight.in=" + DEFAULT_TOTAL_WEIGHT + "," + UPDATED_TOTAL_WEIGHT);

        // Get all the productList where totalWeight equals to UPDATED_TOTAL_WEIGHT
        defaultProductShouldNotBeFound("totalWeight.in=" + UPDATED_TOTAL_WEIGHT);
    }

    @Test
    @Transactional
    void getAllProductsByTotalWeightIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where totalWeight is not null
        defaultProductShouldBeFound("totalWeight.specified=true");

        // Get all the productList where totalWeight is null
        defaultProductShouldNotBeFound("totalWeight.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByTotalWeightIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where totalWeight is greater than or equal to DEFAULT_TOTAL_WEIGHT
        defaultProductShouldBeFound("totalWeight.greaterThanOrEqual=" + DEFAULT_TOTAL_WEIGHT);

        // Get all the productList where totalWeight is greater than or equal to UPDATED_TOTAL_WEIGHT
        defaultProductShouldNotBeFound("totalWeight.greaterThanOrEqual=" + UPDATED_TOTAL_WEIGHT);
    }

    @Test
    @Transactional
    void getAllProductsByTotalWeightIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where totalWeight is less than or equal to DEFAULT_TOTAL_WEIGHT
        defaultProductShouldBeFound("totalWeight.lessThanOrEqual=" + DEFAULT_TOTAL_WEIGHT);

        // Get all the productList where totalWeight is less than or equal to SMALLER_TOTAL_WEIGHT
        defaultProductShouldNotBeFound("totalWeight.lessThanOrEqual=" + SMALLER_TOTAL_WEIGHT);
    }

    @Test
    @Transactional
    void getAllProductsByTotalWeightIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where totalWeight is less than DEFAULT_TOTAL_WEIGHT
        defaultProductShouldNotBeFound("totalWeight.lessThan=" + DEFAULT_TOTAL_WEIGHT);

        // Get all the productList where totalWeight is less than UPDATED_TOTAL_WEIGHT
        defaultProductShouldBeFound("totalWeight.lessThan=" + UPDATED_TOTAL_WEIGHT);
    }

    @Test
    @Transactional
    void getAllProductsByTotalWeightIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where totalWeight is greater than DEFAULT_TOTAL_WEIGHT
        defaultProductShouldNotBeFound("totalWeight.greaterThan=" + DEFAULT_TOTAL_WEIGHT);

        // Get all the productList where totalWeight is greater than SMALLER_TOTAL_WEIGHT
        defaultProductShouldBeFound("totalWeight.greaterThan=" + SMALLER_TOTAL_WEIGHT);
    }

    @Test
    @Transactional
    void getAllProductsByIsVerifiedIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where isVerified equals to DEFAULT_IS_VERIFIED
        defaultProductShouldBeFound("isVerified.equals=" + DEFAULT_IS_VERIFIED);

        // Get all the productList where isVerified equals to UPDATED_IS_VERIFIED
        defaultProductShouldNotBeFound("isVerified.equals=" + UPDATED_IS_VERIFIED);
    }

    @Test
    @Transactional
    void getAllProductsByIsVerifiedIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where isVerified in DEFAULT_IS_VERIFIED or UPDATED_IS_VERIFIED
        defaultProductShouldBeFound("isVerified.in=" + DEFAULT_IS_VERIFIED + "," + UPDATED_IS_VERIFIED);

        // Get all the productList where isVerified equals to UPDATED_IS_VERIFIED
        defaultProductShouldNotBeFound("isVerified.in=" + UPDATED_IS_VERIFIED);
    }

    @Test
    @Transactional
    void getAllProductsByIsVerifiedIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where isVerified is not null
        defaultProductShouldBeFound("isVerified.specified=true");

        // Get all the productList where isVerified is null
        defaultProductShouldNotBeFound("isVerified.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductShouldBeFound(String filter) throws Exception {
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].weight").value(hasItem(DEFAULT_WEIGHT.doubleValue())))
            .andExpect(jsonPath("$.[*].totalWeight").value(hasItem(DEFAULT_TOTAL_WEIGHT.doubleValue())))
            .andExpect(jsonPath("$.[*].isVerified").value(hasItem(DEFAULT_IS_VERIFIED.booleanValue())));

        // Check, that the count call also returns 1
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductShouldNotBeFound(String filter) throws Exception {
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProduct() throws Exception {
        // Get the product
        restProductMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product
        Product updatedProduct = productRepository.findById(product.getId()).get();
        // Disconnect from session so that the updates on updatedProduct are not directly saved in db
        em.detach(updatedProduct);
        updatedProduct
            .name(UPDATED_NAME)
            .note(UPDATED_NOTE)
            .content(UPDATED_CONTENT)
            .quantity(UPDATED_QUANTITY)
            .weight(UPDATED_WEIGHT)
            .totalWeight(UPDATED_TOTAL_WEIGHT)
            .isVerified(UPDATED_IS_VERIFIED);

        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProduct.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProduct))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProduct.getNote()).isEqualTo(UPDATED_NOTE);
        assertThat(testProduct.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testProduct.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testProduct.getWeight()).isEqualTo(UPDATED_WEIGHT);
        assertThat(testProduct.getTotalWeight()).isEqualTo(UPDATED_TOTAL_WEIGHT);
        assertThat(testProduct.getIsVerified()).isEqualTo(UPDATED_IS_VERIFIED);
    }

    @Test
    @Transactional
    void putNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, product.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(product))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(product))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductWithPatch() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setId(product.getId());

        partialUpdatedProduct.weight(UPDATED_WEIGHT).totalWeight(UPDATED_TOTAL_WEIGHT);

        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProduct))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProduct.getNote()).isEqualTo(DEFAULT_NOTE);
        assertThat(testProduct.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testProduct.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testProduct.getWeight()).isEqualTo(UPDATED_WEIGHT);
        assertThat(testProduct.getTotalWeight()).isEqualTo(UPDATED_TOTAL_WEIGHT);
        assertThat(testProduct.getIsVerified()).isEqualTo(DEFAULT_IS_VERIFIED);
    }

    @Test
    @Transactional
    void fullUpdateProductWithPatch() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setId(product.getId());

        partialUpdatedProduct
            .name(UPDATED_NAME)
            .note(UPDATED_NOTE)
            .content(UPDATED_CONTENT)
            .quantity(UPDATED_QUANTITY)
            .weight(UPDATED_WEIGHT)
            .totalWeight(UPDATED_TOTAL_WEIGHT)
            .isVerified(UPDATED_IS_VERIFIED);

        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProduct))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProduct.getNote()).isEqualTo(UPDATED_NOTE);
        assertThat(testProduct.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testProduct.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testProduct.getWeight()).isEqualTo(UPDATED_WEIGHT);
        assertThat(testProduct.getTotalWeight()).isEqualTo(UPDATED_TOTAL_WEIGHT);
        assertThat(testProduct.getIsVerified()).isEqualTo(UPDATED_IS_VERIFIED);
    }

    @Test
    @Transactional
    void patchNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, product.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(product))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(product))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeDelete = productRepository.findAll().size();

        // Delete the product
        restProductMockMvc
            .perform(delete(ENTITY_API_URL_ID, product.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
