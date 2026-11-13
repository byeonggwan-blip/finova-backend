package com.finova.backend.service;

import com.finova.backend.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatementServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private TransactionService transactionService;

    @Mock
    private StatementPdfService statementPdfService;

    @Mock
    private StatementS3Service statementS3Service;

    @InjectMocks
    private StatementService statementService;

    @Test
    void authenticatedUserDownloadsOwnStatement() {

        String statementId = UUID.randomUUID().toString();
        byte[] expectedPdf = "%PDF-test".getBytes();

        when(userMapper.findUserIdByUsername("user01"))
                .thenReturn(1L);

        String expectedKey =
                "statements/1/" + statementId + ".pdf";

        when(statementS3Service.downloadPdf(expectedKey))
                .thenReturn(expectedPdf);

        byte[] result = statementService.downloadStatement(
                "user01", statementId);

        assertArrayEquals(expectedPdf, result);
        verify(statementS3Service).downloadPdf(expectedKey);
    }

    @Test
    void pathTraversalIsRejected() {

        when(userMapper.findUserIdByUsername("user01"))
                .thenReturn(1L);

        assertThrows(IllegalArgumentException.class,
                () -> statementService.downloadStatement(
                        "user01", "../2/private.pdf"));

        verifyNoInteractions(statementS3Service);
    }

    @Test
    void userCannotSelectAnotherUsersFolder() {

        String statementId = UUID.randomUUID().toString();

        when(userMapper.findUserIdByUsername("user02"))
                .thenReturn(2L);

        statementService.downloadStatement(
                "user02", statementId);

        verify(statementS3Service).downloadPdf(
                "statements/2/" + statementId + ".pdf");

        verify(statementS3Service, never()).downloadPdf(
                "statements/1/" + statementId + ".pdf");
    }
}
