CREATE TABLE IF NOT EXISTS stat(
    id          BIGINT          GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    app         VARCHAR(50)     NOT NULL,
    uri         VARCHAR(50)     NOT NULL,
    ip          VARCHAR(15)     NOT NULL,
    timestamp   TIMESTAMP       WITHOUT TIME ZONE NOT NULL
);