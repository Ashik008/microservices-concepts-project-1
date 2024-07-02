package com.project1.product_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project1.product_service.dto.ProductRequest;
import com.project1.product_service.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    /***
     * Steps that will execute after starting integration test:
     * 1. MongoDB image of specified version will be downloaded
     * 2. MongoDB container will be started
     * 3. replicaset url of MongoDB will be fetched and feed it to the spring.data.mongodb.uri property
     * 4. DynamicPropertyRegistry will dynamically load the url and set in the application testing context
     */

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.3");

    @Autowired
    private MockMvc mockMvc; // this is to mock our web server without actually spinning one

    @Autowired
    private ObjectMapper objectMapper; // converts pojo object to json and vice-versa

    @Autowired
    private ProductRepository productRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void testShouldCreateProduct() throws Exception {
		ProductRequest productRequest = getProductRequest("AshikPhone 99", "AshikPhone 99",
				BigDecimal.valueOf(2200));
        String productRequestString = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andExpect(status().isCreated());
        Assertions.assertEquals(1, productRepository.findAll().size());
    }

    @Test
    void testGetAllProducts() throws Exception {
		ProductRequest productRequest = getProductRequest("iPhone 13", "iPhone 13",
				BigDecimal.valueOf(1999));
        String productRequestString = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andExpect(status().isCreated());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/product"))
                .andExpect(status().isOk());
        Assertions.assertEquals(2, productRepository.findAll().size());
    }

    private ProductRequest getProductRequest(String name, String description, BigDecimal price) {
        return ProductRequest.builder()
                .name("AshikPhone 99")
                .description("AshikPhone 99")
                .price(BigDecimal.valueOf(2200))
                .build();
    }
}
