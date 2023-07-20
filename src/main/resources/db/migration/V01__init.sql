CREATE TABLE IF NOT EXISTS client_setting (
    id          SERIAL PRIMARY KEY,
    api_key     VARCHAR(50) NOT NULL,
    rpm integer NOT NULL
);
