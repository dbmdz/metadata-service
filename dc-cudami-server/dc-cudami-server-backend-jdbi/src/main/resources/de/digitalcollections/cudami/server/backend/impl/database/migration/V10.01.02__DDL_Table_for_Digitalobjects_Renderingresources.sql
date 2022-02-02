CREATE TABLE IF NOT EXISTS digitalobject_renderingresources (
  digitalobject_uuid UUID NOT NULL,
  linkeddata_fileresource_uuid UUID NOT NULL UNIQUE,
  sortIndex SMALLINT,

  PRIMARY KEY (digitalobject_uuid, linkeddata_fileresource_uuid),
  FOREIGN KEY (digitalobject_uuid) REFERENCES digitalobjects(uuid),
  FOREIGN KEY (linkeddata_fileresource_uuid) REFERENCES fileresources_linkeddata(uuid)
);