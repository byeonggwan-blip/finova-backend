package com.finova.backend.mapper;

import com.finova.backend.dto.MonthlyStatisticsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;
import com.finova.backend.dto.CategoryStatisticsDto;

import java.util.List;

@Mapper
public interface StatisticsMapper {

    @Select("""
        SELECT
            DATE_FORMAT(t.transaction_date, '%Y-%m') AS month,
            COALESCE(SUM(CASE
                WHEN t.transaction_type = 'INCOME'
                THEN t.amount ELSE 0 END), 0) AS total_income,
            COALESCE(SUM(CASE
                WHEN t.transaction_type = 'EXPENSE'
                THEN t.amount ELSE 0 END), 0) AS total_expense,
            COALESCE(SUM(CASE
                WHEN t.transaction_type = 'INCOME'
                THEN t.amount ELSE -t.amount END), 0) AS net_amount
        FROM transactions t
        JOIN users u ON t.user_id = u.user_id
        WHERE u.username = #{username}
        GROUP BY DATE_FORMAT(t.transaction_date, '%Y-%m')
        ORDER BY month DESC
        """)
    @Results({
            @Result(property = "month", column = "month"),
            @Result(property = "totalIncome", column = "total_income"),
            @Result(property = "totalExpense", column = "total_expense"),
            @Result(property = "netAmount", column = "net_amount")
    })
    List<MonthlyStatisticsDto> findMonthlyStatistics(
            @Param("username") String username
    );
    @Select("""
    SELECT
        t.category,
        SUM(t.amount) AS total_amount,
        COUNT(*) AS transaction_count
    FROM transactions t
    JOIN users u ON t.user_id = u.user_id
    WHERE u.username = #{username}
      AND t.transaction_type = 'EXPENSE'
      AND t.transaction_date >= #{startDate}
      AND t.transaction_date < DATE_ADD(#{startDate}, INTERVAL 1 MONTH)
    GROUP BY t.category
    ORDER BY total_amount DESC
    """)
    @Results({
            @Result(property = "category", column = "category"),
            @Result(property = "totalAmount", column = "total_amount"),
            @Result(property = "transactionCount", column = "transaction_count")
    })
    List<CategoryStatisticsDto> findCategoryStatistics(
            @Param("username") String username,
            @Param("startDate") String startDate
    );
}