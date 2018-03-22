package com.packtpub.yummy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    public void addUser(UserDetails userDetails) {
        jdbcTemplate.update("INSERT INTO users(username, password)" +
                        " VALUES (:username,:password)",
                new MapSqlParameterSource("username", userDetails.getUsername())
                        .addValue("password", userDetails.getPassword()));
        jdbcTemplate.batchUpdate("INSERT INTO user_roles(username, role) VALUES (:username, :role)",
                (SqlParameterSource[]) userDetails.getAuthorities().stream()
                        .map(a -> new MapSqlParameterSource("username", userDetails.getUsername())
                                .addValue("role", a.getAuthority()))
                        .toArray(size -> new SqlParameterSource[size]));
    }

}
