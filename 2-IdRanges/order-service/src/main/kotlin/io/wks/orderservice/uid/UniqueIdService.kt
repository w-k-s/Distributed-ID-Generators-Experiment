package io.wks.orderservice.uid

import io.wks.idrangeapi.IdRangeAllocationServiceGrpc.IdRangeAllocationServiceBlockingStub
import io.wks.idrangeapi.IdRangeRequest
import net.devh.boot.grpc.client.inject.GrpcClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.lang.UnsupportedOperationException
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

class NextValueGenerationException(sequenceName: String, cause: Throwable? = null) :
    RuntimeException("Failed to generate next value from sequence '$sequenceName'", cause)

@Service
class UniqueIdService(
    @GrpcClient("idRangeAllocationService")
    private val idRangeAllocationServiceBlockingStub: IdRangeAllocationServiceBlockingStub,
    private val jdbcTemplate: JdbcTemplate,
    @Value("\${server.id}")
    private val serverId: String,
    @Value("\${application.ids.fetch.size:1000000}")
    private val fetchIdRangeSize: Long,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(UniqueIdService::class.java)
        private val SEQUENCE_A = "order_service.seq_unique_id_a"
        private val SEQUENCE_B = "order_service.seq_unique_id_b"
    }

    private val async = Executors.newSingleThreadExecutor()

    private val currentSequence = AtomicReference<String>(SEQUENCE_A)
    private val isOtherSequenceReady = AtomicBoolean(false)
    private val fetchingIdRange = AtomicBoolean(false)

    init {
        assignIdRangeToSequence(currentSequence.get())
    }

    private fun assignIdRangeToSequence(sequenceName: String) {
        LOGGER.info("Requesting id range for sequence '$sequenceName'. ServerId: '${serverId}', Size: '${fetchIdRangeSize}'")
        // Make a call to id service to get an id range
        val idRangeResponse = idRangeAllocationServiceBlockingStub.requestIdRange(
            IdRangeRequest.newBuilder()
                .setServerId(serverId)
                .setSize(fetchIdRangeSize)
                .build()
        )

        // Update the range of the given sequence
        LOGGER.info("Assigning id range to sequence '$sequenceName'. Min: '${idRangeResponse.minValue}', Max: '${idRangeResponse.maxValue}'")
        jdbcTemplate.execute("""ALTER SEQUENCE '$sequenceName' RESTART WITH ${idRangeResponse.minValue} MAXVALUE ${idRangeResponse.maxValue}""")
    }

    fun nextId(): Long {
        // Get the sequence name from the cache
        val currentIdSequence = currentSequence.get()

        // Query nextval from sequence
        val nextId = try {
            jdbcTemplate.queryForObject("""SELECT nextval('$currentIdSequence')""", Long::class.java)
                ?: throw NextValueGenerationException(currentIdSequence)
        } catch (e: DataAccessException) {
            LOGGER.warn("Failed to execute nextval on sqequence '$currentIdSequence'", e)
            currentSequence.compareAndSet(currentIdSequence, otherSequenceOf(currentIdSequence))
            isOtherSequenceReady.compareAndSet(true, false)
            return nextId()
        }

        if (!isOtherSequenceReady.get() && fetchingIdRange.compareAndSet(false, true)) {
            async.execute {
                try {
                    assignIdRangeToSequence(otherSequenceOf(currentIdSequence))
                    isOtherSequenceReady.set(true)
                } finally {
                    fetchingIdRange.set(false)
                }
            }
        }

        return nextId
    }

    private fun otherSequenceOf(sequenceName: String) = when (sequenceName) {
        SEQUENCE_A -> SEQUENCE_B
        SEQUENCE_B -> SEQUENCE_A
        else -> throw UnsupportedOperationException("sequenceName expected to be either '$SEQUENCE_A' or '$SEQUENCE_B'. Unexpected sequence name: '$sequenceName'")
    }
}