CREATE TABLE failure_message
(
    id           BIGSERIAL PRIMARY KEY,
    topic        VARCHAR(255) NOT NULL,
    error_key    UUID         NOT NULL,
    error_data   TEXT         NOT NULL,
    partition    INT          NOT NULL,
    offset_value BIGINT       NOT NULL,
    exception    TEXT         NOT NULL,
    status       VARCHAR(255) NOT NULL
);

CREATE INDEX idx_failure_message_status ON failure_message (status);
