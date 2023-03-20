package io.wks.orderservice.orders

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class OrderRepository(private val jdbcTemplate: JdbcTemplate) {

    fun save(order: Order) {
        jdbcTemplate.update(
            """
            INSERT INTO order_service.orders (uuid, customer, meal, created_by, created_at)
            VALUES 
            (?,?,?,?,?)
            """,
            order.uuid,
            order.customer,
            order.meal,
            order.createdBy,
            order.createdAt
        )
    }
}