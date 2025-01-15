--liquibase formatted sql
--changeset moirai:create_sample_table
--preconditions onFail:HALT, onError:HALT

CREATE TABLE sample_table (
    a_column VARCHAR(255)
);

/* liquibase rollback
DROP TABLE sample_table
*/
