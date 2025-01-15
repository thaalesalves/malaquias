--liquibase formatted sql
--changeset moirai:1736283065_create_table_discord_user
--preconditions onFail:HALT, onError:HALT

CREATE TABLE discord_user (
    id VARCHAR(100) PRIMARY KEY,
    discord_id VARCHAR(100) NOT NULL UNIQUE,
    version INT DEFAULT 0 NOT NULL,
    creator_discord_id VARCHAR(100) NOT NULL,
    creation_date TIMESTAMP WITH TIME ZONE,
    last_update_date TIMESTAMP WITH TIME ZONE
);

--rollback DROP TABLE discord_user CASCADE;
