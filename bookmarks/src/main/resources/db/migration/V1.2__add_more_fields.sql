
ALTER TABLE bookmark ADD description VARCHAR(255) NULL;
ALTER TABLE bookmark ADD createdon TIMESTAMP DEFAULT current_timestamp() NULL;
ALTER TABLE bookmark ADD updatedon TIMESTAMP DEFAULT current_timestamp() NULL;