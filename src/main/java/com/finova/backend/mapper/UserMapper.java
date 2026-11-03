package com.finova.backend.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.finova.backend.dto.UserAccount;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;

@Mapper
public interface UserMapper {

    @Select("""
        SELECT COUNT(*)
        FROM users
        WHERE username = #{username}
        """)
    int countByUsername(@Param("username") String username);

    @Insert("""
        INSERT INTO users (username, password_hash, name)
        VALUES (#{username}, #{passwordHash}, #{name})
        """)
    int insertUser(
            @Param("username") String username,
            @Param("passwordHash") String passwordHash,
            @Param("name") String name
    );
    @Select("""
    SELECT username, password_hash
    FROM users
    WHERE username = #{username}
    """)
    @Results({
            @Result(property = "username", column = "username"),
            @Result(property = "passwordHash", column = "password_hash")
    })
    UserAccount findByUsername(@Param("username") String username);
}