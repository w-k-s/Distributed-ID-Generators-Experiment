package io.wks.snowflakeservice.snowflake

import io.grpc.stub.StreamObserver
import io.wks.snowflake.api.SnowflakeRequest
import io.wks.snowflake.api.SnowflakeResponse
import io.wks.snowflake.api.SnowflakeServiceGrpc.SnowflakeServiceImplBase
import net.devh.boot.grpc.server.service.GrpcService

@GrpcService
class SnowflakeService(private val idWorker: IdWorker) : SnowflakeServiceImplBase() {

    override fun newId(request: SnowflakeRequest, responseObserver: StreamObserver<SnowflakeResponse>) {
        val id = idWorker.nextId()
        responseObserver.onNext(SnowflakeResponse.newBuilder().setId(id).build())
        responseObserver.onCompleted()
    }
}