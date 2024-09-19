package com.example.clinic.util;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DbUtil {

    private final JdbcOperations jdbcOperations;

    public DbUtil(DataSource dataSource) {
        jdbcOperations = new JdbcTemplate(dataSource);
    }

    public List<Map<String, Object>> doSelect(String sqlSelect) {
        return jdbcOperations.queryForList(sqlSelect);
    }

    public Long getPersonIdByEmail(String email, String type) {
        return jdbcOperations.queryForObject("select id from person where email = ? and type = ?",
                Long.class, email, type);
    }
}