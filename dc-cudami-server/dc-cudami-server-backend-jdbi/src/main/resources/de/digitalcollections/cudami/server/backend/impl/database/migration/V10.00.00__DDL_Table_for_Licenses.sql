CREATE TABLE IF NOT EXISTS licenses (
  uuid UUID NOT NULL PRIMARY KEY,
  acronym VARCHAR,
  label JSONB,
  url VARCHAR NOT NULL UNIQUE,

  created TIMESTAMP NOT NULL,
  last_modified TIMESTAMP NOT NULL
);