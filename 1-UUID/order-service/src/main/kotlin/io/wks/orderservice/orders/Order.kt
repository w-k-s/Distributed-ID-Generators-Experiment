package io.wks.orderservice.orders

import java.time.Clock
import java.time.OffsetDateTime
import java.util.UUID

data class Order(

    val uuid: UUID,
    /**
     * Name of the customer
     */
    val customer: String,
    /**
     * Name of the meal being ordered e.g. Fish & Chips
     */
    val meal: String,
    /**
     * The serverId that processed the order
     */
    val createdBy: String,
    /**
     * The timestamp when the order was created
     */
    val createdAt: OffsetDateTime = OffsetDateTime.now(Clock.systemUTC())
)