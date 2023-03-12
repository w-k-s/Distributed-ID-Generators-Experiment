package io.wks.idrangeservice.idrange

import io.grpc.stub.StreamObserver
import io.wks.idrangeapi.IdRangeAllocationServiceGrpc
import io.wks.idrangeapi.IdRangeRequest
import io.wks.idrangeapi.IdRangeResponse
import net.devh.boot.grpc.server.service.GrpcService

class IdRangeAllocationException : Exception("Failed to allocate an id range for unknown reasons")

@GrpcService
class IdRangeService(private val idRangeDao: IdRangeDao) :
    IdRangeAllocationServiceGrpc.IdRangeAllocationServiceImplBase() {

    override fun requestIdRange(request: IdRangeRequest, responseObserver: StreamObserver<IdRangeResponse>) {
        try {
            when (val range = idRangeDao.assignIdRangeForServer(
                request.serverId,
                request.size
            )) {
                null -> responseObserver.onError(IdRangeAllocationException())
                else -> responseObserver.onNext(
                    IdRangeResponse.newBuilder()
                        .setServerId(range.serverId)
                        .setMinValue(range.minValue)
                        .setMaxValue(range.maxValue)
                        .build()
                )
            }
            responseObserver.onCompleted()
        } catch (e: Exception) {
            e.printStackTrace()
            responseObserver.onError(e)
        }
    }
}