package com.packtpub.yummy.service;

import com.packtpub.yummy.model.MyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    public MyUser findUserByUsername(String username) {
        try {
            MapSqlParameterSource source = new MapSqlParameterSource("username", username);
            MyUser queryResult = jdbcTemplate.queryForObject("SELECT username, password, enabled FROM users WHERE username=:username",
                    source,
                    (rs, rownum) -> MyUser.builder().username(rs.getString("username"))
                            .password(rs.getString("password"))
                            .enabled(rs.getBoolean("enabled"))
                            .build());
            queryResult.setAuthorities(
                    jdbcTemplate.query("SELECT role FROM user_roles WHERE username=:username",
                            source,
                            (rs, rownum) -> new SimpleGrantedAuthority(rs.getString("role")))

            );
            return queryResult;
        }catch (IncorrectResultSizeDataAccessException ex){
            throw new UsernameNotFoundException("Could not find user", ex);
        }
    }
}
