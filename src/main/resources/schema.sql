CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(200)                            NOT NULL,
    email VARCHAR(400)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    id              BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name            VARCHAR(200)                            NOT NULL,
    description     VARCHAR(400)                            NOT NULL,
    is_available    BOOLEAN,
    owner_id        BIGINT,
    item_request_id BIGINT,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_user_id FOREIGN KEY (owner_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    status     VARCHAR(200)                            NOT NULL,
    booker_id  BIGINT                                  NOT NULL,
    item_id    BIGINT                                  NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT fk_booker_id FOREIGN KEY (booker_id) REFERENCES users (id),
    CONSTRAINT fk_item_id_to_bookings FOREIGN KEY (item_id) REFERENCES items (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text      VARCHAR(200)                            NOT NULL,
    item_id   BIGINT                                  NOT NULL,
    author_id BIGINT                                  NOT NULL,
    created   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_author_id FOREIGN KEY (author_id) REFERENCES users (id),
    CONSTRAINT fk_item_id_to_comments FOREIGN KEY (item_id) REFERENCES items (id)
);

CREATE TABLE IF NOT EXISTS item_requests
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(200)                            NOT NULL,
    owner_id    BIGINT                                  NOT NULL,
    created     TIMESTAMP WITHOUT TIME ZONE,
    item_id     BIGINT,
    CONSTRAINT pk_item_requests PRIMARY KEY (id),
    CONSTRAINT fk_item_id_to_item FOREIGN KEY (item_id) REFERENCES items (id)
);
