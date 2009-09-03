\set ON_ERROR_STOP

SET SESSION AUTHORIZATION 'tms';

UPDATE system_attribute SET value = '3.97.0' WHERE name = 'database_version';

INSERT INTO event.event_description VALUES (20, 'Incident CLEARED');
INSERT INTO event.event_description VALUES (21, 'Incident CRASH');
INSERT INTO event.event_description VALUES (22, 'Incident STALL');
INSERT INTO event.event_description VALUES (23, 'Incident BLOCKAGE');
INSERT INTO event.event_description VALUES (24, 'Incident CONSTRUCTION');
INSERT INTO event.event_description VALUES (29, 'Incident IMPACT');

CREATE TABLE event.incident (
	event_id INTEGER PRIMARY KEY DEFAULT nextval('event.event_id_seq'),
	name VARCHAR(16) NOT NULL UNIQUE,
	event_date timestamp WITH time zone NOT NULL,
	event_desc_id INTEGER NOT NULL
		REFERENCES event.event_description(event_desc_id),
	road VARCHAR(20) NOT NULL,
	dir SMALLINT NOT NULL REFERENCES iris.direction(id),
	easting INTEGER NOT NULL,
	northing INTEGER NOT NULL,
	impact VARCHAR(20) NOT NULL,
	cleared BOOLEAN NOT NULL
);
