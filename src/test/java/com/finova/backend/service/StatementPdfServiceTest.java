package com.finova.backend.service;

import com.finova.backend.dto.TransactionDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatementPdfServiceTest {

    @Test
    void generateKoreanStatementPdf() throws Exception {

        TransactionDto tx = new TransactionDto();
        tx.setTransactionType("출금");
        tx.setCategory("식비");
        tx.setDescription("점심 식사");
        tx.setAmount(new BigDecimal("12500"));
        tx.setTransactionDate(
                LocalDateTime.of(2026, 11, 12, 12, 30));

        StatementPdfService service = new StatementPdfService();

        byte[] pdf = service.generate(
                "finova-test-user",
                List.of(tx));

        assertTrue(pdf.length > 1000);
        assertEquals("%PDF-",
                new String(pdf, 0, 5,
                        java.nio.charset.StandardCharsets.US_ASCII));

        Path output = Path.of("target/finova-statement-test.pdf");
        Files.createDirectories(output.getParent());
        Files.write(output, pdf);

        System.out.println("PDF CREATED: " + output);
        System.out.println("PDF SIZE: " + pdf.length + " bytes");
    }
}
