SHELL := /bin/bash

crate_db_compile:
	cd cratedb; ./gradlew compileJava

crate_db_run:
	cd cratedb; ./gradlew app:run

crate_ui_install:
	cd crate-admin; source bootstrap.sh

crate_ui_run:
	cd crate-admin; npm run develop
