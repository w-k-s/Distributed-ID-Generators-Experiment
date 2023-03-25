docker-deploy: build-images
	docker compose up -d --build

build-images: build-project build-snowflake-service-image build-order-service-image

build-project: build-snowflake-api build-snowflake-service build-order-service

build-snowflake-api: snowflake-api
	cd snowflake-api && mvn -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn clean install && cd ..

build-snowflake-service: snowflake-service
	cd snowflake-service && mvn -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn clean package -DskipTests && cd ..

build-order-service: order-service
	cd order-service && mvn -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn clean package -DskipTests  && cd ..

build-snowflake-service-image:
	cd snowflake-service && docker build -t wkas/snowflake-service . && cd ..

build-order-service-image:
	cd order-service && docker build -t wkas/order-service . && cd ..

docker-destroy:
	docker compose kill
	docker container prune -f 
