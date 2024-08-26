package ru.romanow.migration.config

import org.h2.Driver
import org.springframework.boot.autoconfigure.batch.BatchDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.support.JdbcTransactionManager
import javax.sql.DataSource

@Configuration
class DatabaseConfiguration {

    @BatchDataSource
    @Bean(name = ["batchDataSource"])
    fun batchDataSource(): DataSource = DataSourceBuilder.create()
        .url("jdbc:h2:mem:test")
        .username("SA")
        .driverClassName(Driver::class.java.canonicalName)
        .build()

    @Bean
    fun transactionManager() = JdbcTransactionManager(batchDataSource())

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.source")
    @ConditionalOnMissingBean(name = ["sourceDataSource"])
    fun sourceDataSource(): DataSource = DataSourceBuilder.create().build()

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.target")
    @ConditionalOnMissingBean(name = ["targetDataSource"])
    fun targetDataSource(): DataSource = DataSourceBuilder.create().build()
}
