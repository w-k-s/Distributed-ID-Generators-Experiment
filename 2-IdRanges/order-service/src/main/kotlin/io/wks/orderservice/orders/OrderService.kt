package io.wks.orderservice.orders

import io.wks.orderservice.uid.UniqueIdService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class OrderService(
    @Autowired
    private val orderRepository: OrderRepository,
    @Autowired
    private val uniqueIdService: UniqueIdService,
    @Value("\${server.id}")
    private val serverId: String,
) {
    fun createOrder(request: OrderRequest): OrderResponse {
        return with(
            Order(
                id = uniqueIdService.nextId(),
                customer = request.customer,
                meal = request.meal,
                createdBy = serverId,
            )
        ) {
            orderRepository.save(this)
            OrderResponse.of(this)
        }
    }
}