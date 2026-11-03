package com.finova.backend.mapper;

import com.finova.backend.dto.TransactionDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;
import com.finova.backend.dto.MockBankTransactionDto;
import org.apache.ibatis.annotations.Insert;

import java.util.List;

@Mapper
public interface TransactionMapper {

    @Select("""
        SELECT
            t.transaction_id,
            t.user_id,
            t.transaction_type,
            t.category,
            t.amount,
            t.description,
            t.transaction_date
        FROM transactions t
        JOIN users u ON t.user_id = u.user_id
        WHERE u.username = #{username}
        ORDER BY t.transaction_date DESC
        """)
    @Results({
            @Result(property = "transactionId", column = "transaction_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "transactionType", column = "transaction_type"),
            @Result(property = "category", column = "category"),
            @Result(property = "amount", column = "amount"),
            @Result(property = "description", column = "description"),
            @Result(property = "transactionDate", column = "transaction_date")
    })
    List<TransactionDto> findByUsername(
            @Param("username") String username
    );
    @Insert("""
    INSERT INTO transactions (
        user_id,
        transaction_type,
        category,
        amount,
        description,
        transaction_date,
        bank_code,
        external_transaction_id
    )
    SELECT
        u.user_id,
        #{transaction.transactionType},
        #{transaction.category},
        #{transaction.amount},
        #{transaction.description},
        #{transaction.transactionDate},
        #{bankCode},
        #{transaction.externalTransactionId}
    FROM users u
    WHERE u.username = #{username}
      AND NOT EXISTS (
          SELECT 1
          FROM transactions t
          WHERE t.bank_code = #{bankCode}
            AND t.external_transaction_id =
                #{transaction.externalTransactionId}
      )
    """)
    int insertBankTransaction(
            @Param("username") String username,
            @Param("bankCode") String bankCode,
            @Param("transaction") MockBankTransactionDto transaction
    );
    @Select("""
    SELECT COUNT(*)
    FROM bank_accounts b
    JOIN users u ON b.user_id = u.user_id
    WHERE u.username = #{username}
      AND b.account_id = #{accountId}
      AND b.bank_code = #{bankCode}
    """)
    int countOwnedBankAccount(
            @Param("username") String username,
            @Param("accountId") String accountId,
            @Param("bankCode") String bankCode
    );
}