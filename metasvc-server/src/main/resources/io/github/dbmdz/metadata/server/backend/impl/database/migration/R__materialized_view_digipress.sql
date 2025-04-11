DROP MATERIALIZED VIEW IF EXISTS digipress;

CREATE MATERIALIZED VIEW digipress AS
SELECT
	mf.uuid mf_uuid,
	mf.created mf_created,
	mf.last_modified mf_lastModified,
	mf.description mf_description,
	mf.identifiable_objecttype mf_identifiableObjectType,
	mf.identifiable_type mf_type,
	mf.label mf_label,
	mf.preview_hints mf_previewImageRenderingHints,
	mf.custom_attrs mf_customAttributes,
	mf.navdate mf_navDate,
	mf.refid mf_refId,
	mf.notes mf_notes,
	mf.expressiontypes mf_expressionTypes,
	mf.language mf_language,
	mf.manifestationtype mf_manifestationType,
	mf.manufacturingtype mf_manufacturingType,
	mf.mediatypes mf_mediaTypes,
	mf.titles mf_titles,
	-- work
	w.uuid wo_uuid,
	get_identifiers (w.uuid) wo_identifiers,
	w.label wo_label,
	w.titles wo_titles,
	mms.title parent_title,
	mms.sortKey parent_sortKey,
	parent.uuid parent_uuid,
	parent.label parent_label,
	parent.titles parent_titles,
	parent.manifestationtype parent_manifestationType,
	parent.refid parent_refId,
	parent.notes parent_notes,
	parent.created parent_created,
	parent.last_modified parent_lastModified,
	parent.identifiable_objecttype parent_identifiableObjectType,
	get_identifiers (parent.uuid) parent_identifiers,
	-- parent's work
	parentwork.uuid parentwork_uuid,
	get_identifiers (parentwork.uuid) parentwork_identifiers,
	parentwork.label parentwork_label,
	parentwork.titles parentwork_titles,
	rel.predicate rel_predicate,
	rel.sortindex rel_sortindex,
	rel.additional_predicates rel_additionalPredicates,
	max(rel.sortindex) OVER (
		PARTITION BY
			mf.uuid
	) relation_max_sortindex,
	get_identifiers (e.uuid) e_identifiers,
	-- entity's name, if any
	ename.name entity_name,
	ename.name_locales_original_scripts entity_nameLocalesOfOriginalScripts,
	e.uuid e_uuid,
	e.created e_created,
	e.last_modified e_lastModified,
	e.description e_description,
	e.identifiable_objecttype e_identifiableObjectType,
	e.identifiable_type e_type,
	e.label e_label,
	e.preview_hints e_previewImageRenderingHints,
	e.custom_attrs e_customAttributes,
	e.navdate e_navDate,
	e.refid e_refId,
	e.notes e_notes,
	mf.publication_info mf_publicationInfo,
	mf.production_info mf_productionInfo,
	mf.distribution_info mf_distributionInfo,
	-- publisher
	ag.uuid ag_uuid,
	ag.created ag_created,
	ag.last_modified ag_lastModified,
	ag.description ag_description,
	ag.identifiable_objecttype ag_identifiableObjectType,
	ag.identifiable_type ag_type,
	ag.label ag_label,
	ag.preview_hints ag_previewImageRenderingHints,
	ag.custom_attrs ag_customAttributes,
	ag.navdate ag_navDate,
	ag.refid ag_refId,
	ag.notes ag_notes,
	ag.name ag_name,
	ag.name_locales_original_scripts ag_nameLocalesOfOriginalScripts,
	h.uuid hs_uuid,
	h.created hs_created,
	h.last_modified hs_lastModified,
	h.description hs_description,
	h.identifiable_objecttype hs_identifiableObjectType,
	h.identifiable_type hs_type,
	h.label hs_label,
	h.preview_hints hs_previewImageRenderingHints,
	h.custom_attrs hs_customAttributes,
	h.navdate hs_navDate,
	h.refid hs_refId,
	h.notes hs_notes,
	h.name hs_name,
	h.name_locales_original_scripts hs_nameLocalesOfOriginalScripts,
	h.geolocation_type hs_geoLocationType,
	h.settlement_type hs_humanSettlementType,
	get_identifiers (ag.uuid) ag_identifiers,
	get_identifiers (h.uuid) hs_identifiers,
	id.uuid id_uuid,
	id.created id_created,
	id.last_modified id_lastModified,
	id.identifiable id_identifiable,
	id.namespace id_namespace,
	id.identifier id_id,
	file.uuid pi_uuid,
	file.label pi_label,
	file.filename pi_filename,
	file.mimetype pi_mimeType,
	file.uri pi_uri,
	file.http_base_url pi_httpBaseUrl,
	ua.uuid ua_uuid,
	ua.created ua_created,
	ua.last_modified ua_lastModified,
	ua.last_published ua_lastPublished,
	ua.primary ua_primary,
	ua.slug ua_slug,
	ua.target_language ua_targetLanguage,
	ua.target_identifiable_objecttype uaidf_identifiableObjectType,
	ua.target_identifiable_type uaidf_identifiableType,
	ua.target_uuid uaidf_uuid,
	uawebs.uuid uawebs_uuid,
	uawebs.label uawebs_label,
	uawebs.url uawebs_url
FROM
	(
		SELECT
			mf.*
		FROM
			manifestations AS mf
			LEFT JOIN identifiers id ON id.identifiable = mf.uuid
		WHERE
			manifestationtype IN ('NEWSPAPER', 'JOURNAL')
			AND (id.namespace = 'mdz-manifestation-periodical')
	) AS mf
	LEFT JOIN (
		manifestation_manifestations mms
		INNER JOIN manifestations parent ON parent.uuid = mms.subject_uuid
	) ON mms.object_uuid = mf.uuid
	LEFT JOIN works parentwork ON parentwork.uuid = parent.work
	LEFT JOIN (
		rel_entity_entities rel
		INNER JOIN entities e ON rel.subject_uuid = e.uuid
	) ON rel.object_uuid = mf.uuid
	-- select the entity's name, if any
	LEFT JOIN named_entities ename ON ename.uuid = e.uuid
	LEFT JOIN works w ON w.uuid = mf.work
	LEFT JOIN agents ag ON ag.uuid = ANY (mf.publishing_info_agent_uuids)
	LEFT JOIN humansettlements h ON h.uuid = ANY (mf.publishing_info_locations_uuids)
	LEFT JOIN identifiers AS id ON mf.uuid = id.identifiable
	LEFT JOIN fileresources_image AS file ON mf.previewfileresource = file.uuid
	LEFT JOIN url_aliases AS ua ON mf.uuid = ua.target_uuid
	LEFT JOIN websites AS uawebs ON uawebs.uuid = ua.website_uuid;

