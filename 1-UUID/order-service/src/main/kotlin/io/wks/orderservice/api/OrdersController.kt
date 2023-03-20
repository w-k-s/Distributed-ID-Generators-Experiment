package io.wks.orderservice.orders

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime
import java.util.*

data class OrderRequest(
    val customer: String,
    val meal: String
)

data class OrderResponse(
    val uuid: UUID,
    val customer: String,
    val meal: String,
    val createdBy: String,
    val createdAt: OffsetDateTime,
) {
    companion object {
        fun of(order: Order) = OrderResponse(
            uuid = order.uuid,
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