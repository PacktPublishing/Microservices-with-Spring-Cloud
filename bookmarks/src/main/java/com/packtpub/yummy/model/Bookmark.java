package com.packtpub.yummy.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Bookmark {
    @Length(max=255)
    @NotEmpty
    private String description;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    @Length(max=255)
    @NotEmpty
    @URL
    private String url;
    private UUID uuid;
    private int version;

    @SuppressWarnings("unused")
    public Bookmark() {

    }

    public Bookmark(String description, String url) {
        this.url = url;
        uuid = null;
        this.description = description;
    }

    @JsonCreator
    public Bookmark(@JsonProperty("uuid") UUID uuid,
                    @JsonProperty("description") String description,
                    @JsonProperty("url") String url,
                    @JsonProperty("version") int version,
                    @JsonProperty("createdOn") LocalDateTime createdOn,
                    @JsonProperty("updatedOn") LocalDateTime updatedOn) {
        this.description = description;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.url = url;
        this.uuid = uuid;
        this.version = version;
    }
    public Bookmark withUuid(UUID uuid) {
        return new Bookmark(uuid, description, url, version, createdOn, updatedOn);
    }

    public Bookmark withUrl(String newUrl) {
        return new Bookmark(uuid, description, newUrl, version, createdOn, updatedOn);
    }
}