  CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL,
    CONSTRAINT pk_user_id PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
  );

  CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT pk_category_id PRIMARY KEY (id),
    CONSTRAINT uq_category_name UNIQUE (name)
  );

  CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation VARCHAR(2000) NOT NULL,
    category_id BIGINT NOT NULL,
    confirmed_requests BIGINT,
    created_on TIMESTAMP WITHOUT TIME ZONE,
    description VARCHAR(7000),
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id BIGINT NOT NULL,
    lat REAL NOT NULL,
    lon REAL NOT NULL,
    is_paid BOOLEAN DEFAULT FALSE NOT NULL,
    participant_limit BIGINT DEFAULT 0,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    is_request_moderation BOOLEAN DEFAULT TRUE,
    state VARCHAR NOT NULL,
    title VARCHAR(120) NOT NULL,
    views BIGINT,
    CONSTRAINT pk_event_id PRIMARY KEY (id),
    CONSTRAINT fk_initiator_id_users FOREIGN KEY (initiator_id) REFERENCES users(id),
    CONSTRAINT fk_category_id_categories FOREIGN KEY (category_id) REFERENCES categories(id)
  );