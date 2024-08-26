package ru.romanow.migration.config

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.database.JdbcBatchItemWriter
import org.springframework.batch.item.database.JdbcPagingItemReader
import org.springframework.batch.item.database.Order
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.jdbc.core.ColumnMapRowMapper
import org.springframework.transaction.PlatformTransactionManager
import ru.romanow.migration.service.DynamicJdbcBatchItemWriter
import javax.sql.DataSource


@Configuration
class BatchProcessConfiguration(
    @Qualifier("sourceDataSource") private val sourceDataSource: DataSource,
    @Qualifier("targetDataSource") private val targetDataSource: DataSource,
) {

    @Bean
    fun taskExecutor() = SimpleAsyncTaskExecutor()

    @Bean
    fun migration(jobRepository: JobRepository, transactionManager: PlatformTransactionManager): Job {
        return JobBuilder(MIGRATION_NAME, jobRepository)
            .start(migrate(jobRepository, transactionManager))
            .build()
    }

    @Bean
    fun migrate(jobRepository: JobRepository, transactionManager: PlatformTransactionManager): Step {
        return StepBuilder(MIGRATION_NAME, jobRepository)
            .chunk<Map<String, Any>, Map<String, Any>>(CHUNK_SIZE, transactionManager)
            .reader(sourceReader(null))
            .writer(targetWriter(null))
            .build()
    }

    @Bean
    @StepScope
    fun sourceReader(
        @Value("#{jobParameters['sourceTable']}") sourceTableName: String?
    ): JdbcPagingItemReader<Map<String, Any>> {
        val provider = PostgresPagingQueryProvider()
        provider.setSelectClause("SELECT *")
        provider.setFromClause("FROM $sourceTableName")
        provider.sortKeys = java.util.Map.of("id", Order.ASCENDING)
        return JdbcPagingItemReaderBuilder<Map<String, Any>>()
            .dataSource(sourceDataSource)
            .queryProvider(provider)
            .saveState(false)
            .pageSize(CHUNK_SIZE)
            .rowMapper(ColumnMapRowMapper())
            .build()
    }

    @Bean
    @StepScope
    fun targetWriter(
        @Value("#{jobParameters['targetTable']}") targetTableName: String?
    ): JdbcBatchItemWriter<Map<String, Any>> {
        return DynamicJdbcBatchItemWriter(targetTableName, targetDataSource)
    }

    companion object {
        private const val MIGRATION_NAME = "migrate"
        private const val CHUNK_SIZE = 500
    }
}
