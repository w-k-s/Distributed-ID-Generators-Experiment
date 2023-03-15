build-project: build-id-range-api build-id-range-service build-order-service

build-id-range-api: id-range-api
	cd id-range-api && mvn -B clean install && cd ..

build-id-range-service: id-range-service
	cd id-range-service && mvn -B clean package -DskipTests && cd ..


build-order-service: order-service
	cd order-service && mvn -B clean package -DskipTests  && cd ..

build-images: build-project build-id-range-service-image build-order-service-image

build-id-range-service-image:
	cd id-range-service && docker build -t wkas/id-range-service . && cd ..

build-order-service-image:
	cd order-service && docker build -t wkas/order-service . && cd ..

docker-deploy: build-images
	docker compose up -d
