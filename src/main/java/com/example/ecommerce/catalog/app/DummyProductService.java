package com.example.ecommerce.catalog.app;

import com.example.ecommerce.catalog.domain.DummyProduct;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DummyProductService {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static class ApiResponse {
        public DummyProduct[] products;
        public int total;
        public int skip;
        public int limit;
    }

    public List<DummyProduct> getAllProducts() throws IOException, InterruptedException {
        int limit = 100;
        int skip = 0;
        int total = Integer.MAX_VALUE;

        List<DummyProduct> allProducts = new ArrayList<>();

        while (skip < total) {
            String url = "https://dummyjson.com/products?limit=" + limit + "&skip=" + skip;

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed API call: " + response.statusCode());
            }

            ApiResponse apiResponse = mapper.readValue(response.body(), ApiResponse.class);

            allProducts.addAll(Arrays.asList(apiResponse.products));

            total = apiResponse.total;
            skip += limit;
        }

        return allProducts;
    }

}
