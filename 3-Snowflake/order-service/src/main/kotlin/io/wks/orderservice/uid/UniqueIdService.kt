package io.wks.orderservice.uid

import io.wks.snowflake.api.SnowflakeRequest
import io.wks.snowflake.api.SnowflakeServiceGrpc
import net.devh.boot.grpc.client.inject.GrpcClient
import org.springframework.stereotype.Service

interface UniqueIdService {
    fun nextId(): Long
}

@Service
class SnowflakeIdService(
    @GrpcClient("snowflake-service")
    private val snowflakeRpc: SnowflakeServiceGrpc.SnowflakeServiceBlockingStub,
) : UniqueIdService {

    override fun nextId() = snowflakeRpc.newId(SnowflakeRequest.getDefaultInstance()).id
}