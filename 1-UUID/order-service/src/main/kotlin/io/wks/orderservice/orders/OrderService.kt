package io.wks.orderservice.orders

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrderService(
    @Autowired
    private val orderRepository: OrderRepository,
    @Value("\${server.id}")
    private val serverId: String,
) {
    fun createOrder(request: OrderRequest): OrderResponse {
        return with(
            Order(
                uuid = UUID.randomUUID(),
                customer = request.customer,
                meal = request.meal,
                createdBy = "Server $serverId",
            )
        ) {
            orderRepository.save(this)
            OrderResponse.of(this)
        }
    }
}