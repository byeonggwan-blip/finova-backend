package com.finova.backend.service;

import com.finova.backend.dto.TransactionDto;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatementPdfService {

    private static final float LEFT = 45;
    private static final float LINE_HEIGHT = 19;
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public byte[] generate(
            String username,
            List<TransactionDto> transactions) throws IOException {

        try (PDDocument document = new PDDocument();
             InputStream fontStream = getClass()
                     .getResourceAsStream("/fonts/NanumGothic-Regular.ttf")) {

            if (fontStream == null) {
                throw new IOException("PDF 한글 폰트를 찾을 수 없습니다.");
            }

            PDType0Font font = PDType0Font.load(document, fontStream);

            PDPageContentStream stream = null;

            try {
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                stream = new PDPageContentStream(document, page);

                float y = 780;

                write(stream, font, 18, LEFT, y, "FINOVA 거래명세서");
                y -= 40;

                write(stream, font, 10, LEFT, y,
                        "사용자: " + safe(username));
                y -= LINE_HEIGHT;

                write(stream, font, 10, LEFT, y,
                        "발급일시: " + LocalDateTime.now(java.time.ZoneId.of("Asia/Seoul")).format(DATE_FORMAT));
                y -= LINE_HEIGHT;

                write(stream, font, 10, LEFT, y,
                        "거래 건수: " + transactions.size() + "건");
                y -= 35;

                if (transactions.isEmpty()) {
                    write(stream, font, 11, LEFT, y,
                            "조회된 거래내역이 없습니다.");
                }

                for (TransactionDto tx : transactions) {
                    if (y < 80) {
                        stream.close();

                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        stream = new PDPageContentStream(document, page);

                        y = 780;
                        write(stream, font, 13, LEFT, y,
                                "FINOVA 거래명세서 (계속)");
                        y -= 35;
                    }

                    String date = tx.getTransactionDate() == null
                            ? "-"
                            : tx.getTransactionDate().format(DATE_FORMAT);

                    String amount = tx.getAmount() == null
                            ? "-"
                            : tx.getAmount().toPlainString() + " 원";

                    write(stream, font, 10, LEFT, y,
                            date + " | "
                                    + safe(tx.getTransactionType())
                                    + " | " + amount);
                    y -= LINE_HEIGHT;

                    write(stream, font, 9, LEFT + 10, y,
                            "분류: " + safe(tx.getCategory())
                                    + " / 내용: "
                                    + shorten(safe(tx.getDescription()), 35));
                    y -= 30;
                }

            } finally {
                if (stream != null) {
                    stream.close();
                }
            }

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            document.save(output);
            return output.toByteArray();
        }
    }

    private void write(
            PDPageContentStream stream,
            PDType0Font font,
            int size,
            float x,
            float y,
            String text) throws IOException {

        stream.beginText();
        stream.setFont(font, size);
        stream.newLineAtOffset(x, y);
        stream.showText(text);
        stream.endText();
    }

    private String safe(String value) {
        if (value == null) {
            return "-";
        }
        return value.replaceAll("[\\r\\n\\t]", " ");
    }

    private String shorten(String value, int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength) + "...";
    }
}
