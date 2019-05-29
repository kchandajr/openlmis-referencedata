-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

CREATE TABLE administrative_messages (
    id uuid NOT NULL,
    title character varying(255),
    message text NOT NULL,
    startdate timestamp with time zone,
    createddate timestamp with time zone NOT NULL,
    expirydate timestamp with time zone,
    active boolean NOT NULL,
    authorid uuid NOT NULL
);

ALTER TABLE ONLY administrative_messages
  ADD CONSTRAINT administrative_messages_pkey PRIMARY KEY (id);

ALTER TABLE administrative_messages
  ADD CONSTRAINT administrative_messages_fkey FOREIGN KEY (authorid)
  REFERENCES referencedata.users(id);

CREATE INDEX ON administrative_messages (active, authorid);