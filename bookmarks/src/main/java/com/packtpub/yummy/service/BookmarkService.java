package com.packtpub.yummy.service;

import com.packtpub.yummy.config.AmqpConfig;
import com.packtpub.yummy.model.Bookmark;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@Service
@Transactional
@RefreshScope
public class BookmarkService {

    @Autowired
    AmqpConfig.EventSource source;

    @Autowired
    JdbcTemplate jdbcTemplate;

    public UUID addBookmark(Bookmark bookmark) {
        UUID uuid = UUID.randomUUID();
        jdbcTemplate.update("INSERT INTO bookmark (url, uuid, version, description)" +
                " VALUES (?,?,1,?)", bookmark.getUrl(), uuid.toString(), bookmark.getDescription());
        System.out.println("http://localhost:8080/bookmark/" + uuid);
        return uuid;
    }

    public Bookmark find(UUID id) {
        return jdbcTemplate.queryForObject("SELECT * FROM bookmark WHERE uuid=?",
                new BookmarkRowMapper(), id.toString());

    }

    public Iterable<Bookmark> findAll() {
        return jdbcTemplate.query("SELECT * FROM bookmark",
                new BookmarkRowMapper());
    }

    public Bookmark update(Bookmark bookmark) {
        find(bookmark.getUuid());
        int update = jdbcTemplate.update(
                "UPDATE bookmark SET url=?, description=?, " +
                        " updatedon=current_timestamp(), version=version+1 " +
                        " WHERE uuid=? AND version =?",
                bookmark.getUrl(), bookmark.getDescription(), bookmark.getUuid().toString(), bookmark.getVersion()
        );
        if (update != 1) throw new OptimisticLockingFailureException("Stale update detected for " + bookmark.getUuid());
        return find(bookmark.getUuid());
    }

    public void delete(UUID id) {
        if (jdbcTemplate.update("DELETE FROM bookmark WHERE uuid=?", id.toString()) != 1)
            throw new NotModifiedDataAccessException("Bookmark already gone");
        source.bookmarkDeletions().send(MessageBuilder
                .withPayload(
                        new DeletionEvent("bookmark", id.toString()))
                .build());
    }

    @ResponseStatus(HttpStatus.NOT_MODIFIED)
    public static class NotModifiedDataAccessException extends DataAccessException {

        public NotModifiedDataAccessException(String msg) {
            super(msg);
        }
    }

    @Data
    @AllArgsConstructor
    public static class DeletionEvent {
        String type;
        String entityId;
    }
}
