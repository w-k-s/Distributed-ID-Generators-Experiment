package io.wks.snowflakeservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SnowflakeServiceApplication

fun main(args: Array<String>) {
    runApplication<SnowflakeServiceApplication>(*args)
}
