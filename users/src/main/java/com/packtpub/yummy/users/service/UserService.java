package com.packtpub.yummy.users.service;

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
        jdbcTemplate.update("INSERT INTO users(username, password,enabled)" +
                        " VALUES (lower(:username),:password,1)",
                new MapSqlParameterSource("username", userDetails.getUsername())
                        .addValue("password", passwordEncoder.encode(userDetails.getPassword())));
        jdbcTemplate.batchUpdate("INSERT INTO authorities(username, authority) " +
                        "VALUES (lower(:username), :role)",
                (SqlParameterSource[]) userDetails.getAuthorities().stream()
                        .map(a -> new MapSqlParameterSource("username", userDetails.getUsername())
                                .addValue("role", a.getAuthority()))
                        .toArray(size -> new SqlParameterSource[size]));
    }

    public boolean hasUser(String username){
        return  !jdbcTemplate.query("select 1 from users where username=lower(:username)",
                new MapSqlParameterSource("username",username)
                ,(rs, rowNum) -> rs.getInt(1)
                ).isEmpty();
    }
}
