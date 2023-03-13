package io.wks.orderservice.uid

import io.wks.idrangeapi.IdRangeAllocationServiceGrpc.IdRangeAllocationServiceBlockingStub
import io.wks.idrangeapi.IdRangeRequest
import net.devh.boot.grpc.client.inject.GrpcClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class NextValueGenerationException(sequenceName: String, cause: Throwable? = null) :
    RuntimeException("Failed to generate next value from sequence '$sequenceName'", cause)

class IdSequenceNotFoundException() : RuntimeException("No id sequence found")

@Service
class UniqueIdService(
    @GrpcClient("idRangeAllocationService")
    private val idRangeAllocationServiceBlockingStub: IdRangeAllocationServiceBlockingStub,
    private val uniqueIdSequenceRepository: UniqueIdSequenceRepository,
    private val jdbcTemplate: JdbcTemplate,
    @Value("\${server.id}")
    private val serverId: String,
    @Value("\${application.ids.fetch.size:1000000}")
    private val fetchIdRangeSize: Long,
    @Value("\${application.ids.prefetch.after:500000}")
    private val prefetchMinValueOffset: Long,
) {

    private val logger = LoggerFactory.getLogger(UniqueIdService::class.java)
    private val deleteLock = Object()

    private val fetchingIdRange = AtomicBoolean(false)

    private val async = Executors.newSingleThreadExecutor()

    // In-memory cache of the next id ranges to use.
    private val cache = ConcurrentLinkedQueue<UniqueIdSequence>();

    init {
        prepareCache()
    }

    private fun prepareCache() {
        when (val sequence = uniqueIdSequenceRepository.findFirstByDepletedIsFalse()) {
            null -> requestNewUniqueIdRange() //If no sequence is found, then make an API call to fetch more sequences synchronously. TODO: Retry
            else -> cache.add(sequence)
        }
    }

    fun nextId(): Long {
        // Get the sequence name from the cache
        val currentIdSequence = cache.peek()
            ?: throw IdSequenceNotFoundException()

        // Query nextval from sequence
        val nextId = try {
            jdbcTemplate.queryForObject("""SELECT nextval('${currentIdSequence.name}')""", Long::class.java)
                ?: throw NextValueGenerationException(currentIdSequence.name)
        } catch (e: DataAccessException) {
            logger.error("Failed to execute nextval on sqequence '${currentIdSequence.name}'", e)
            markSequenceAsDepleted(currentIdSequence)
            return nextId()
        }

        if (nextId >= currentIdSequence.prefetchValue && cache.size == 1) {
            requestNewUniqueIdRangeAsync()
        } else if (nextId == currentIdSequence.maxValue) {
            markSequenceAsDepleted(currentIdSequence)
        }

        return nextId
    }

    private fun markSequenceAsDepleted(sequence: UniqueIdSequence) {
        // Let's say there are 10 concurrent requests. When we call nextval:
        // - Thraad 1 gets id = max - 1,
        // - Thread 2 gets id = max.
        // - Threads 3-10 cause an exception (because db sequence has already reached max)
        //
        // Now:
        // - Threads 2 - 10 will enter the synchronized method `markSequenceAsDepleted`.
        // - One of the threads will update the cache and the db, while the other 8 threads wait.
        // - This means that whenever we reach the end of a sequence, there is a bit of a bottleneck.
        // TODO: Can this implementation be improved? There is probably a much smarter, simpler way to do all of this.
        synchronized(deleteLock) {
            cache.removeIf { it.name == sequence.name }
            uniqueIdSequenceRepository.markSequenceAsDepleted(sequence.name)
        }
    }

    private fun requestNewUniqueIdRangeAsync() {
        if (fetchingIdRange.compareAndSet(false, true)) {
            async.execute {
                try {
                    requestNewUniqueIdRange()
                } finally {
                    fetchingIdRange.set(false)
                }
            }
        }
    }

    private fun requestNewUniqueIdRange() {
        // Make a call to id service to get an id range
        val idRangeResponse = idRangeAllocationServiceBlockingStub.requestIdRange(
            IdRangeRequest.newBuilder()
                .setServerId(serverId)
                .setSize(fetchIdRangeSize)
                .build()
        )

        val newUniqueIdSequence = UniqueIdSequence(
            name = "order_service.seq_unique_ids_" + System.currentTimeMillis(),
            minValue = idRangeResponse.minValue,
            maxValue = idRangeResponse.maxValue,
            prefetchValue = idRangeResponse.minValue + prefetchMinValueOffset,
            isDepleted = false
        )

        // Add the sequence to the cache.
        cache.add(newUniqueIdSequence)

        // Save the details of the sequence to the db.
        jdbcTemplate.execute("""CREATE SEQUENCE '${newUniqueIdSequence.name}' MINVALUE ${newUniqueIdSequence.minValue} MAXVALUE ${newUniqueIdSequence.maxValue}""")
    }

    // TODO: Configure scheduled task
    private fun deleteDepletedSequences() {
        uniqueIdSequenceRepository.findByDepletedIsTrue()
            .forEach { jdbcTemplate.execute("""DROP SEQUENCE IF EXISTS '${it.name}'""") }
    }
}