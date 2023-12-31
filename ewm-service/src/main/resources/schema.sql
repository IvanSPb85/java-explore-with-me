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
    description VARCHAR(7000) NOT NULL,
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id BIGINT NOT NULL,
    lat REAL NOT NULL,
    lon REAL NOT NULL,
    is_paid BOOLEAN,
    participant_limit BIGINT,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    is_request_moderation BOOLEAN,
    state VARCHAR NOT NULL,
    title VARCHAR(120) NOT NULL,
    views BIGINT,
    CONSTRAINT pk_event_id PRIMARY KEY (id),
    CONSTRAINT fk_initiator_id_users FOREIGN KEY (initiator_id) REFERENCES users(id),
    CONSTRAINT fk_category_id_categories FOREIGN KEY (category_id) REFERENCES categories(id)
  );

  CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE,
    event_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    status VARCHAR NOT NULL,
    CONSTRAINT pk_request_id PRIMARY KEY (id),
    CONSTRAINT fk_event_id_events FOREIGN KEY (event_id) REFERENCES events(id),
    CONSTRAINT fk_requester_id_users FOREIGN KEY (requester_id) REFERENCES users(id),
    CONSTRAINT uq_request UNIQUE(event_id, requester_id)
  );

  CREATE TABLE IF NOT EXISTS compilations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    is_pinned BOOLEAN,
    title VARCHAR(50) NOT NULL,
    CONSTRAINT pk_compilation_id PRIMARY KEY (id),
    CONSTRAINT uq_compilation_title UNIQUE (title)
  );

  CREATE TABLE IF NOT EXISTS compilation_event (
    compilation_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    CONSTRAINT pk_compilation_event PRIMARY KEY (event_id, compilation_id),
    CONSTRAINT compilation_event_fk1 FOREIGN KEY (compilation_id) REFERENCES compilations(id),
    CONSTRAINT compilation_event_fk2 FOREIGN KEY (event_id) REFERENCES events(id)
  );

  CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id BIGINT NOT NULL,
    commentator_id BIGINT NOT NULL,
    text VARCHAR(10000),
    posted TIMESTAMP WITHOUT TIME ZONE,
    updated TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_comment_id PRIMARY KEY (id),
    CONSTRAINT fk_event_id_comment FOREIGN KEY (event_id) REFERENCES events(id),
    CONSTRAINT fk_commentator_id FOREIGN KEY (commentator_id) REFERENCES users(id)
  );