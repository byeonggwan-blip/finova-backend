package com.finova.backend.service;

import com.finova.backend.dto.MonthlyStatisticsDto;
import com.finova.backend.dto.CategoryStatisticsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiAnalysisService {

    private final StatisticsService statisticsService;
    private final GeminiService geminiService;
    private final JsonMapper jsonMapper;

    public String analyzeSpending(String username, String month)
            throws Exception {

        // 1. 로그인 사용자의 월별 통계 조회
        List<MonthlyStatisticsDto> monthlyStatistics =
                statisticsService.getMonthlyStatistics(username);

        // 2. 요청한 월의 카테고리별 통계 조회
        List<CategoryStatisticsDto> categoryStatistics =
                statisticsService.getCategoryStatistics(username, month);

        // 3. 요청한 월의 통계 추출
        MonthlyStatisticsDto targetMonth = monthlyStatistics.stream()
                .filter(item -> month.equals(item.getMonth()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "해당 월의 거래내역이 없습니다."
                        )
                );

        // 4. DTO를 실제 숫자와 항목이 포함된 JSON으로 변환
        String statisticsJson = jsonMapper.writeValueAsString(
                Map.of(
                        "monthlyStatistics", targetMonth,
                        "categoryStatistics", categoryStatistics
                )
        );

        // 5. Gemini 분석 프롬프트
        String prompt = """
                당신은 금융 소비 분석 전문가입니다.

                다음 JSON은 사용자의 가상 금융 거래 집계 데이터입니다.

                %s

                아래 항목을 한국어로 분석하세요.

                1. 이번 달 수입과 지출 요약
                2. 주요 지출 카테고리 분석
                3. 소비 패턴에서 주의할 점
                4. 실천 가능한 절약 방안 3가지

                조건:
                - JSON에 포함된 실제 숫자와 카테고리만 사용할 것
                - 데이터가 부족하면 추측하지 말 것
                - 수입과 지출 금액을 정확하게 표시할 것
                - 금융상품이나 투자 추천은 하지 말 것
                - 간결하고 이해하기 쉽게 설명할 것
                """.formatted(statisticsJson);

        // 6. Gemini 분석 요청
        return geminiService.generateAnalysis(prompt);
    }
}