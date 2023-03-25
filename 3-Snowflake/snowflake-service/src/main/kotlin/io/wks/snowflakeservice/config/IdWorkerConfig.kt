package io.wks.snowflakeservice.config

import io.wks.snowflakeservice.snowflake.IdWorker
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class IdWorkerConfig {

    @Bean
    fun idWorker(
        @Value("\${app.worker.id}")
        workerId: Long,
        @Value("\${app.datacenter.id}")
        dataCenterId: Long,
    ): IdWorker = IdWorker(workerId, dataCenterId)
}