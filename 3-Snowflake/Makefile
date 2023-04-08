docker-deploy: build-images
	docker compose up -d --build

build-images: build-project build-order-service-image build-service-registry-image

build-project: build-snowflake build-order-service build-service-registry

build-service-registry: service-registry
	cd service-registry && mvn -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn clean package -DskipTests  && cd ..

build-snowflake: snowflake
	cd snowflake && mvn -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn clean install && cd ..

build-order-service: order-service
	cd order-service && mvn -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn clean package -DskipTests  && cd ..

build-order-service-image:
	cd order-service && docker build -t wkas/order-service . && cd ..

build-service-registry-image:
	cd service-registry && docker build -t wkas/service-registry . && cd ..

docker-destroy:
	docker compose kill
	docker container prune -f 
