package io.wks.orderservice.uid

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("order_service.unique_id_sequences")
data class UniqueIdSequence(
    @Id
    val name: String,
    val minValue: Long,
    val maxValue: Long,
    val prefetchValue: Long,
    val isDepleted: Boolean
)