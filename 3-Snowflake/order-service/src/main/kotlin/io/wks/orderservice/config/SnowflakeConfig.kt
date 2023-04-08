package io.wks.orderservice.config

import com.netflix.discovery.EurekaClient
import io.wks.snowflake.Snowflake
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SnowflakeConfig(private val eurekaClient: EurekaClient) {

    @Bean
    fun snowflake() =
        Snowflake(
            nodeId = Snowflake.withNodeId(
                eurekaClient.applicationInfoManager.info.instanceId.hashCode().toLong(),
                mask = true
            )
        )
}
