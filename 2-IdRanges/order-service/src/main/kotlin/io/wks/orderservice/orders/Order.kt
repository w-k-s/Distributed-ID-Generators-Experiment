package io.wks.orderservice.orders

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Clock
import java.time.OffsetDateTime


@Table("order_service.orders")
data class Order(
    @Id
    val id: Long,
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