-- idenfifiers
ALTER TABLE identifiers
ALTER COLUMN created SET DEFAULT CURRENT_TIMESTAMP,
ALTER COLUMN last_modified SET DEFAULT CURRENT_TIMESTAMP;

-- identifiertypes
ALTER TABLE identifiertypes
ALTER COLUMN created SET DEFAULT CURRENT_TIMESTAMP,
ALTER COLUMN last_modified SET DEFAULT CURRENT_TIMESTAMP;

-- rendering_templates
ALTER TABLE rendering_templates
ALTER COLUMN created SET DEFAULT CURRENT_TIMESTAMP,
ALTER COLUMN last_modified SET DEFAULT CURRENT_TIMESTAMP;

-- users
ALTER TABLE users
ALTER COLUMN created SET DEFAULT CURRENT_TIMESTAMP,
ALTER COLUMN last_modified SET DEFAULT CURRENT_TIMESTAMP;
