package io.wks.snowflake

import java.net.NetworkInterface
import java.security.SecureRandom


class InvalidSystemClock(override val message: String) : RuntimeException(message)

class Snowflake(
    private val nodeId: Long = createNodeId()
) {
    companion object {
        private const val EPOCH: Long = 1288834974657L // Thu Nov 04 2010 01:42:54

        private const val NUM_NODE_ID_BITS = 10
        private const val MAX_NODE_ID = -1L xor (-1L shl NUM_NODE_ID_BITS)

        private const val NUM_SEQUENCE_ID_BITS = 12
        const val SEQUENCE_MASK = -1L xor (-1L shl NUM_SEQUENCE_ID_BITS)

        private const val SHL_NODE_ID = NUM_SEQUENCE_ID_BITS
        private const val SHL_TIMESTAMP = NUM_SEQUENCE_ID_BITS + NUM_NODE_ID_BITS

        private fun createNodeId(): Long {
            var nodeId: Long = try {
                val sb = StringBuilder()
                val networkInterfaces = NetworkInterface.getNetworkInterfaces()
                while (networkInterfaces.hasMoreElements()) {
                    val networkInterface = networkInterfaces.nextElement()
                    val mac = networkInterface.hardwareAddress
                    if (mac != null) {
                        for (macPort in mac) {
                            sb.append(String.format("%02X", macPort))
                        }
                    }
                }
                sb.toString().hashCode().toLong()
            } catch (ex: Exception) {
                SecureRandom().nextLong()
            }
            return nodeId and MAX_NODE_ID
        }

        fun withNodeId(nodeId: Long, mask: Boolean) = if (mask) {
            nodeId and MAX_NODE_ID
        } else nodeId
    }

    @Volatile
    private var sequence: Long = 0L

    @Volatile
    private var lastTimestamp: Long = -1L

    init {
        require(nodeId in 1 until MAX_NODE_ID) {
            """node Id can't be greater than $MAX_NODE_ID or less than 0"""
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
            sequence = (sequence + 1) and SEQUENCE_MASK
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
                (nodeId shl SHL_NODE_ID) or
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