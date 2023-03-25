package io.wks.orderservice.api

import io.wks.orderservice.orders.Order
import io.wks.orderservice.orders.OrderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime

data class OrderRequest(
    val customer: String,
    val meal: String
)

data class OrderResponse(
    val id: Long,
    val customer: String,
    val meal: String,
    val createdBy: String,
    val createdAt: OffsetDateTime,
) {
    companion object {
        fun of(order: Order) = OrderResponse(
            id = order.id,
            customer = order.customer,
            meal = order.meal,
            createdBy = order.createdBy,
            createdAt = order.createdAt
        )
    }
}

@RestController
class OrdersController(
    @Autowired
    private val orderService: OrderService
) {
    @PostMapping("/api/v1/orders")
    fun createOrder(@RequestBody request: OrderRequest) = orderService.createOrder(request)
}