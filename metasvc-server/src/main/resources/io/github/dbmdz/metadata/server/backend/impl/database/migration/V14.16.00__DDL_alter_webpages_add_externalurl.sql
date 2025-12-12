alter table if exists webpages
add column if not exists external_url varchar collate ucs_basic,
add column if not exists show_as_internal_url boolean default false;

