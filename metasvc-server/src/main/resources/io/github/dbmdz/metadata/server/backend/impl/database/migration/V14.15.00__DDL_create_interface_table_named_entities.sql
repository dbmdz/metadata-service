create table if not exists named_entities (
	uuid UUID primary key,
	name jsonb,
	name_locales_original_scripts varchar[] collate "ucs_basic"
);

alter table if exists agents inherit named_entities;
alter table if exists events inherit named_entities;
alter table if exists geolocations inherit named_entities;

