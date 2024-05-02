create index if not exists idx_agents_uuid_name on agents (uuid)
	include (name, name_locales_original_scripts);
create index if not exists idx_corporatebodies_uuid_name on corporatebodies (uuid)
	include (name, name_locales_original_scripts);
create index if not exists idx_persons_uuid_name on persons (uuid)
	include (name, name_locales_original_scripts);
create index if not exists idx_geolocations_uuid_name on geolocations (uuid)
	include (name, name_locales_original_scripts);
create index if not exists idx_humansettlements_uuid_name on humansettlements (uuid)
	include (name, name_locales_original_scripts);

