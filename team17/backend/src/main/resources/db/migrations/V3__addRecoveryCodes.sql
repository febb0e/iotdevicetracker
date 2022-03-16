CREATE SEQUENCE IF NOT EXISTS recovery_code_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE recovery_code
(
    id      BIGINT      NOT NULL,
    user_id BIGINT      NOT NULL,
    code    VARCHAR(19) NOT NULL,
    CONSTRAINT pk_recovery_code PRIMARY KEY (id)
);

ALTER TABLE recovery_code
    ADD CONSTRAINT FK_RECOVERY_CODE_ON_USER FOREIGN KEY (user_id) REFERENCES "user" (id);