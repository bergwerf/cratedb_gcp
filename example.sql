CREATE TABLE person (
  first_name varchar(255),
  last_name varchar(255),
  notes text
);

INSERT INTO person (first_name, last_name, notes)
VALUES ('Herman', 'Bergwerf', 'Author');

--CREATE REPOSITORY aws TYPE s3 WITH (
--  endpoint='http://localhost:4566',
--  protocol='http',
--  bucket='crate-bucket',
--  base_path='my_base_path',
--  access_key='my_access_key',
--  secret_key='my_secret_key',
--  compress=true
--);

CREATE REPOSITORY gcloud TYPE gcs WITH (
  bucket='bucket',
  base_path='repository',
  compress=true
);

CREATE SNAPSHOT gcloud.snapshot;
