package io.wks.idrangeservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource


@Configuration
class DataSourcConfiguration {

    @Bean
    fun dataSource(
        @Value("\${spring.datasource.driver-class-name}")
        driverClassName: String,
        @Value("\${spring.datasource.url}")
        jdbcUrl: String,
        @Value("\${spring.datasource.username}")
        username: String,
        @Value("\${spring.datasource.password}")
        password: String,
    ): DataSource? {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName(driverClassName)
        dataSource.url = jdbcUrl
        dataSource.username = username
        dataSource.password = password
        return dataSource
    }
}
