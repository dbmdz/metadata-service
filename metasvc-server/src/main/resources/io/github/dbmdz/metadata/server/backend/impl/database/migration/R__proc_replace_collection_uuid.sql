CREATE OR REPLACE PROCEDURE replace_collection_uuid(original uuid, new_uuid uuid)
LANGUAGE plpgsql
AS $$
declare
	urlaliases url_aliases[] := '{}';
	urlalias url_aliases%rowtype;

	ccs collection_collections[] := '{}';
	cc collection_collections%rowtype;

	cds collection_digitalobjects[] := '{}';
	cd collection_digitalobjects%rowtype;
begin
	if original is null or new_uuid is null then
		raise exception 'Both parameters (original, new_uuid) must not be null!';
	end if;

	-- store & remove url aliases
	for urlalias in
	  delete from url_aliases
	  where target_uuid = original
	  returning *
	loop
		-- change target uuid
		urlalias.target_uuid := new_uuid;
		urlaliases := urlaliases || urlalias;
	end loop;

	-- store & remove collection_collections entries
	for cc in
	  delete from collection_collections
	  where child_collection_uuid = original
	  returning *
	loop
		-- change child collection's uuid
		cc.child_collection_uuid := new_uuid;
		ccs := ccs || cc;
	end loop;

	-- store & remove collection_digitalobjects entries
	for cd in
	  delete from collection_digitalobjects
	  where collection_uuid = original
	  returning *
	loop
		cd.collection_uuid := new_uuid;
		cds := cds || cd;
	end loop;

	update collections set uuid = new_uuid where uuid = original;
	if not found then
		raise info 'Table collections was not updated, % not found.', original;
	end if;

	-- reinsert formerly removed data sets
	if cardinality(urlaliases) = 0 then
		raise info 'There were no url aliases to update, target % not found.', original;
	else
		foreach urlalias in array urlaliases loop
			insert into url_aliases values (urlalias.*);
		end loop;
	end if;

	if cardinality(ccs) = 0 then
		raise info 'Table collection_collections was not updated, child UUID % not found.', original;
	else
		foreach cc in array ccs loop
			insert into collection_collections values (cc.*);
		end loop;
	end if;

	if cardinality(cds) = 0 then
		raise info 'Table collection_digitalobjects was not updated, collection UUID % not found.',
		  original;
	else
		foreach cd in array cds loop
			insert into collection_digitalobjects values (cd.*);
		end loop;
	end if;
end;
$$;

