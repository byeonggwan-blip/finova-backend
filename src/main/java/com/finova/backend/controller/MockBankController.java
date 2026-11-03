package com.finova.backend.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.InputStream;

@RestController
@RequestMapping("/api/mock-bank")
public class MockBankController {

    private final JsonMapper jsonMapper;

    public MockBankController(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @GetMapping("/transactions")
    public ResponseEntity<JsonNode> getTransactions() throws Exception {

        ClassPathResource resource =
                new ClassPathResource("mock-bank/transactions.json");

        try (InputStream inputStream = resource.getInputStream()) {
            JsonNode data = jsonMapper.readTree(inputStream);
            return ResponseEntity.ok(data);
        }
    }
}