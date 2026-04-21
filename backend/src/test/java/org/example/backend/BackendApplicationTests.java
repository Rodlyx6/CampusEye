package org.example.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootTest
class BackendApplicationTests {

    @Autowired
    private JdbcClient jdbcClient;

    @Test
    void contextLoads() {
        // 测试mysql 连接
        String message =jdbcClient.sql("SELECT 'MySQL connection successful' AS message")
                .query(String.class)
                .single();
        System.out.println( message);
    }

}
