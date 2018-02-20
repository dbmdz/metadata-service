CREATE TABLE IF NOT EXISTS websites (
  id SERIAL PRIMARY KEY NOT NULL,
  uuid UUID NOT NULL UNIQUE,

  url VARCHAR NOT NULL UNIQUE,
  registration_date DATE,
  rootPages JSONB
);

