CREATE TABLE Location (
    id integer NOT NULL,
    code VARCHAR NOT NULL,
    type VARCHAR NOT NULL,
    longitude double precision,
    latitude double precision,
    parent_id integer,
    CONSTRAINT pk_location PRIMARY KEY (id),
    CONSTRAINT location_translation FOREIGN KEY (parent_id)
        REFERENCES Location (id)
);

CREATE INDEX fki_parent
    ON Location (parent_id);

CREATE TABLE Translation (
    id integer NOT NULL,
    location integer NOT NULL,
    language VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    description VARCHAR NOT NULL,
    CONSTRAINT pk_translation PRIMARY KEY (id),
    CONSTRAINT fk_translation_location FOREIGN KEY (location)
      REFERENCES Location (id)
);

CREATE INDEX fki_translation_location
    ON Translation (location);