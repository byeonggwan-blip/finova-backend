package com.finova.backend.controller;

import com.finova.backend.dto.MonthlyStatisticsDto;
import com.finova.backend.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.finova.backend.dto.CategoryStatisticsDto;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyStatisticsDto>> getMonthlyStatistics(
            Authentication authentication) {

        String username = authentication.getName();

        List<MonthlyStatisticsDto> statistics =
                statisticsService.getMonthlyStatistics(username);

        return ResponseEntity.ok(statistics);
    }
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryStatisticsDto>> getCategoryStatistics(
            Authentication authentication,
            @RequestParam String month) {

        String username = authentication.getName();

        List<CategoryStatisticsDto> statistics =
                statisticsService.getCategoryStatistics(username, month);

        return ResponseEntity.ok(statistics);
    }
}