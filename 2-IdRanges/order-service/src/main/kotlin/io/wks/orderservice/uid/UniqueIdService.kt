package io.wks.orderservice.uid

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentLinkedQueue

@Component
class UniqueIdService(private val uniqueIdSequenceRepository: UniqueIdSequenceRepository) {

    // In-memory cache of the next id ranges to use.
    private val cache = ConcurrentLinkedQueue<UniqueIdSequence>();

    init {
        prepareCache()
    }

    private fun prepareCache() {
        when (val sequence = uniqueIdSequenceRepository.findFirstByDepletedIsFalse()) {
            null -> TODO("If no sequence is found, then make an API call to fetch more sequences synchronously")
            else -> cache.add(sequence)
        }
    }


    fun nextId(): Long {
        // TODO: Get the sequence name from the cache

        // TODO: Query nextval from sequence

        // TODO: If nextval >= prefetch value & cache size == 1, fetch more ids asynchronously

        /** TODO:
         * If nextval >= max value,
         * -- if the cache size > 1, pop the sequence from cache
         * -- else, fetch more ids synchronously
         */
        return 0
    }

    private fun requestNewUniqueIdRange() {
        // TODO: Make a call to id service to get an id range

        // TODO: Create a sequence in the db.

        // TODO: Add the sequence to the cache.

        // TODO: Save the details of the sequence to the db.

        // TODO: Delete completed sequences in the db.
    }
}