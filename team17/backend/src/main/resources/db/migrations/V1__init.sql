CREATE SEQUENCE IF NOT EXISTS device_group_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS device_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS totp_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS user_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE device
(
    id              BIGINT                      NOT NULL,
    device_group_id BIGINT                      NOT NULL,
    identifier      VARCHAR(17)                 NOT NULL,
    name            VARCHAR(255),
    token           VARCHAR(255)                NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_device PRIMARY KEY (id)
);

CREATE TABLE device_group
(
    id         BIGINT                      NOT NULL,
    name       VARCHAR(255)                NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_device_group PRIMARY KEY (id)
);

CREATE TABLE device_group_user
(
    role            INTEGER                     NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id         BIGINT                      NOT NULL,
    device_group_id BIGINT                      NOT NULL,
    CONSTRAINT pk_device_group_user PRIMARY KEY (user_id, device_group_id)
);

CREATE TABLE totp_authenticator
(
    id         BIGINT                      NOT NULL,
    user_id    BIGINT                      NOT NULL,
    secret     VARCHAR(255)                NOT NULL,
    verified   BOOLEAN                     NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_totp_authenticator PRIMARY KEY (id)
);

CREATE TABLE "user"
(
    id                 BIGINT                      NOT NULL,
    username           VARCHAR(255)                NOT NULL,
    password           VARCHAR(255)                NOT NULL,
    email              VARCHAR(255)                NOT NULL,
    verified           BOOLEAN                     NOT NULL,
    verification_token VARCHAR(255),
    role               INTEGER                     NOT NULL,
    updated_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

ALTER TABLE device
    ADD CONSTRAINT uc_device_identifier UNIQUE (identifier);

ALTER TABLE "user"
    ADD CONSTRAINT uc_user_email UNIQUE (email);

ALTER TABLE "user"
    ADD CONSTRAINT uc_user_username UNIQUE (username);

ALTER TABLE device_group_user
    ADD CONSTRAINT FK_DEVICE_GROUP_USER_ON_DEVICE_GROUP FOREIGN KEY (device_group_id) REFERENCES device_group (id);

ALTER TABLE device_group_user
    ADD CONSTRAINT FK_DEVICE_GROUP_USER_ON_USER FOREIGN KEY (user_id) REFERENCES "user" (id);

ALTER TABLE device
    ADD CONSTRAINT FK_DEVICE_ON_DEVICE_GROUP FOREIGN KEY (device_group_id) REFERENCES device_group (id);

ALTER TABLE totp_authenticator
    ADD CONSTRAINT FK_TOTP_AUTHENTICATOR_ON_USER FOREIGN KEY (user_id) REFERENCES "user" (id);