package com.finova.backend.controller;

import com.finova.backend.service.AiAnalysisService;
import com.finova.backend.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiService geminiService;
    private final AiAnalysisService aiAnalysisService;

    // Gemini 연결 테스트
    @PostMapping("/test")
    public String testGemini() throws Exception {

        String prompt = """
                너는 금융 소비 분석 AI야.
                다음 가상 데이터를 한 문장으로 분석해.

                월 수입: 3,200,000원
                월 지출: 800,000원

                한국어로 간결하게 답변해.
                """;

        return geminiService.generateAnalysis(prompt);
    }

    // 실제 MySQL 금융 통계 기반 AI 분석
    @PostMapping("/analyze")
    public String analyzeSpending(
            Authentication authentication,
            @RequestParam String month) throws Exception {

        String username = authentication.getName();

        return aiAnalysisService.analyzeSpending(username, month);
    }
}