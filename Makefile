SHELL := /bin/bash

crate_db_compile:
	cd cratedb; ./gradlew compileJava

crate_db_run:
	cd cratedb; ./gradlew app:run

crate_ui_install:
	cd crate-admin; source bootstrap.sh

crate_ui_run:
	cd crate-admin; npm run develop

mock_aws_install:
	pip install localstack awscli awscli-local

mock_aws_run:
	localstack start -d

mock_aws_status:
	localstack status services
