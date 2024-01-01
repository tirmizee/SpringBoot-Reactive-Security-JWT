CREATE SCHEMA schema_sit;

CREATE TABLE schema_sit.users (
	id serial PRIMARY KEY,
	username VARCHAR(50) UNIQUE NOT NULL,
	password VARCHAR(255) NOT NULL,
	email VARCHAR (255) UNIQUE NOT NULL,
    enabled BOOLEAN NOT NULL,
	created_date TIMESTAMP NOT NULL,
    last_login TIMESTAMP
);