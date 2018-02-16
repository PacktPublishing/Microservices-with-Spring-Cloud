package com.packtpub.yummy.service;

import com.packtpub.yummy.model.Bookmark;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

class BookmarkRowMapper implements RowMapper<Bookmark> {
    @Override
    public Bookmark mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Bookmark(
                rs.getObject("uuid", UUID.class),
                rs.getString("description"),
                rs.getString("url"),
                rs.getInt("version"),
                rs.getTimestamp("createdon").toLocalDateTime(),
                rs.getTimestamp("updatedon").toLocalDateTime()
        );
    }
}
