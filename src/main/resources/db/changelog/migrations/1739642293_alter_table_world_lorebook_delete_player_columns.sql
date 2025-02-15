--liquibase formatted sql
--changeset moirai:1739642293_alter_table_world_lorebook_delete_player_columns
--preconditions onFail:HALT, onError:HALT

ALTER TABLE world_lorebook
DROP COLUMN player_discord_id,
DROP COLUMN is_player_character;

/* liquibase rollback
ALTER TABLE world_lorebook
 ADD COLUMN player_discord_id   VARCHAR(100),
 ADD COLUMN is_player_character BOOLEAN DEFAULT FALSE NOT NULL;
*/
