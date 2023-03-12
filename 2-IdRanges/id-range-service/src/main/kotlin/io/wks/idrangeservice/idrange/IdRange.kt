package io.wks.idrangeservice.idrange

data class IdRange(
    val serverId: String,
    val minValue: Long,
    val maxValue: Long
)