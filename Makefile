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

mock_aws_kill:
	localstack stop

mock_aws_log:
	localstack logs -f

mock_aws_status:
	localstack status services

mock_aws_s3_create_bucket:
	awslocal s3api create-bucket --bucket crate-bucket

mock_aws_s3_upload_example:
	echo "Hello, World!" > hello_world.txt
	awslocal s3 cp hello_world.txt s3://crate-bucket/  
	rm hello_world.txt
