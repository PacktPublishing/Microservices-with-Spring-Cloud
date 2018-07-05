package com.packtpub.yummy.ratings;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
@AllArgsConstructor
public class RatingRepository {
    private NamedParameterJdbcTemplate jdbcTemplate;


    public void persist(String type, String entityId, BigDecimal rating) {
        jdbcTemplate.update(" insert into ratings (type, entity_id, rating)" +
                        " values (:type, :entity_id, :rating)",
                new MapSqlParameterSource()
                        .addValue("type", type)
                        .addValue("entity_id", entityId)
                        .addValue("rating", rating));
    }

    public RatingResult calculateRating(String type, String entityId) {
        try {
            return jdbcTemplate.queryForObject("select  type, entity_id as entityId," +
                            " avg(rating) as rating, count(*) as ratingCount" +
                            " from ratings" +
                            " where type=:type and entity_id=:entity_id" +
                            " group by type, entity_id",
                    new MapSqlParameterSource()
                            .addValue("type", type)
                            .addValue("entity_id", entityId),
                    new BeanPropertyRowMapper<>(RatingResult.class));
        } catch (EmptyResultDataAccessException ex) {
            return new RatingResult(type, entityId, BigDecimal.ZERO, 0);
        }
    }

    public void remove(String type, String entityId) {
        jdbcTemplate.update("delete from ratings" +
                        " where type=:type and entity_id=:entity_id",
                new MapSqlParameterSource()
                        .addValue("type", type)
                        .addValue("entity_id", entityId));
    }
}
