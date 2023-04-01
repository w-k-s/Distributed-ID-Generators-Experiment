package io.wks.snowflakeservice.snowflake


class InvalidSystemClock(override val message: String) : RuntimeException(message)

class IdWorker(
    private val workerId: Long,
    private val datacenterId: Long,
    private var sequence: Long = 0L
) {
    companion object {
        private const val EPOCH: Long = 1288834974657L // Thu Nov 04 2010 01:42:54

        private const val NUM_WORKER_ID_BITS = 5
        private const val MAX_WORKED_ID = -1L xor (-1L shl NUM_WORKER_ID_BITS)

        private const val NUM_DATA_CENTER_BITS: Int = 5
        private const val MAX_DATA_CENTER_ID = -1L xor (-1L shl NUM_DATA_CENTER_BITS)

        private const val NUM_SEQUENCE_ID_BITS = 12
        const val SEQUENCE_MASK = -1L xor (-1L shl NUM_SEQUENCE_ID_BITS)

        private const val SHL_WORKER_ID = NUM_SEQUENCE_ID_BITS
        private const val SHL_DATACENTER_ID = NUM_WORKER_ID_BITS + NUM_WORKER_ID_BITS
        private const val SHL_TIMESTAMP = NUM_SEQUENCE_ID_BITS + NUM_WORKER_ID_BITS + NUM_DATA_CENTER_BITS
    }

    private var lastTimestamp: Long = -1L

    init {
        require(workerId in 1 until MAX_WORKED_ID) {
            """worker Id can't be greater than $MAX_DATA_CENTER_ID or less than 0: $workerId"""
        }
        require(datacenterId in 1 until MAX_DATA_CENTER_ID) {
            """datacenter Id can't be greater than $MAX_DATA_CENTER_ID or less than 0: $datacenterId"""
        }
    }

    @Synchronized
    fun nextId(): Long {
        var timestamp = System.currentTimeMillis()

        if (lastTimestamp > timestamp) {
            throw InvalidSystemClock("Clock moved backwards.  Refusing to generate id for ${lastTimestamp - timestamp} milliseconds")
        }

        if (lastTimestamp == timestamp) {
            // If more than 1 id is requested per millisecond, then increment the sequence number.
            // Ensure, though, that the sequence is no longer than 12 bits.
            sequence = (sequence + 1) or SEQUENCE_MASK
            if (sequence == 0L) {
                // If after masking, the sequence is 0, then use a different timestamp
                timestamp = nextTimestampAfter(lastTimestamp)
            }
        } else {
            // If this is the first id generated at the given millisecond since epoch, use sequence = 0.
            sequence = 0
        }

        lastTimestamp = timestamp

        return timestamp - EPOCH shl SHL_TIMESTAMP or
                (datacenterId shl SHL_DATACENTER_ID) or
                (workerId shl SHL_WORKER_ID) or
                sequence
    }

    private fun nextTimestampAfter(lastTimestamp: Long): Long {
        var timestamp = System.currentTimeMillis()
        if (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis()
        }
        return timestamp
    }
}