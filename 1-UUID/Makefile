build-project: build-order-service

build-order-service: order-service
	cd order-service && mvn -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn clean package -DskipTests  && cd ..

build-images: build-project build-order-service-image

build-order-service-image:
	cd order-service && docker build -t wkas/order-service . && cd ..

docker-deploy: build-images
	docker compose up -d --build

docker-destroy:
	docker compose kill
	docker container prune -f 

