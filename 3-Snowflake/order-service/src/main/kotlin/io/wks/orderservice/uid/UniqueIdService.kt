package io.wks.orderservice.uid

import io.wks.snowflake.Snowflake
import org.springframework.stereotype.Service

interface UniqueIdService {
    fun nextId(): Long
}

@Service
class SnowflakeIdService(
    private val snowflake: Snowflake
) : UniqueIdService {

    override fun nextId() = snowflake.nextId()
}