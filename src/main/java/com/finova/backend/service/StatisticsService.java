package com.finova.backend.service;

import com.finova.backend.dto.MonthlyStatisticsDto;
import com.finova.backend.mapper.StatisticsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.finova.backend.dto.CategoryStatisticsDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StatisticsMapper statisticsMapper;

    @Transactional(readOnly = true)
    public List<MonthlyStatisticsDto> getMonthlyStatistics(String username) {
        return statisticsMapper.findMonthlyStatistics(username);
    }
    @Transactional(readOnly = true)
    public List<CategoryStatisticsDto> getCategoryStatistics(
            String username, String month) {

        // YYYY-MM 형식 검증 및 실제 날짜 확인
        if (month == null || !month.matches("\\d{4}-(0[1-9]|1[0-2])")) {
            throw new IllegalArgumentException("월 형식은 YYYY-MM이어야 합니다.");
        }

        String startDate = java.time.YearMonth.parse(month)
                .atDay(1)
                .toString();

        return statisticsMapper.findCategoryStatistics(username, startDate);
    }
}