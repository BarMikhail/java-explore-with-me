DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS locations CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS comp_events CASCADE;
DROP TABLE IF EXISTS requests CASCADE;


CREATE TABLE IF NOT EXISTS users(
    id      BIGINT          GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name    VARCHAR(250)    NOT NULL,
    email   VARCHAR(250)    NOT NULL    UNIQUE
);

CREATE TABLE IF NOT EXISTS locations(
   id       BIGINT      GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
   lat      FLOAT       NOT NULL,
   lon      FLOAT       NOT NULL
);

CREATE TABLE IF NOT EXISTS categories(
    id      BIGINT          GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name    VARCHAR(100)    NOT NULL    UNIQUE
);

CREATE TABLE IF NOT EXISTS events(
    id                      BIGINT              GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation              VARCHAR(2000)       NOT NULL,
    title                   VARCHAR(120)        NOT NULL,
    description             VARCHAR(7000)       NOT NULL,
    confirmed_requests      BIGINT              NOT NULL,
    paid                    BOOLEAN             NOT NULL,
    category_id             BIGINT              REFERENCES categories(id) ON DELETE CASCADE,
    initiator_id            BIGINT              REFERENCES users(id) ON DELETE CASCADE,
    location_id             BIGINT              REFERENCES locations(id) ON DELETE CASCADE,
    participant_limit       BIGINT              DEFAULT 0,
    event_date              TIMESTAMP           WITHOUT TIME ZONE   NOT NULL,
    created_on              TIMESTAMP           WITHOUT TIME ZONE   NOT NULL,
    published_on            TIMESTAMP           WITHOUT TIME ZONE   NOT NULL,
    request_moderation      BOOLEAN             DEFAULT TRUE,
    state                   VARCHAR(20),
    views                   BIGINT
);

CREATE TABLE IF NOT EXISTS compilations(
        id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        title       VARCHAR(50)     NOT NULL,
        pinned      BOOLEAN         NOT NULL
);

CREATE TABLE IF NOT EXISTS comp_events(
    compilation_id  BIGINT NOT NULL,
    event_id        BIGINT NOT NULL,
    CONSTRAINT compilation_event_pk PRIMARY KEY(compilation_id,event_id),
    FOREIGN KEY (compilation_id) REFERENCES compilations (id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requests(
    id              BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    event_id        BIGINT REFERENCES events (id) ON DELETE CASCADE,
    requester_id    BIGINT REFERENCES users (id) ON DELETE CASCADE,
    status          VARCHAR(50)
);