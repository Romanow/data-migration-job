package ru.romanow.migration

import org.slf4j.LoggerFactory
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import ru.romanow.migration.properties.BatchProcessProperties
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SpringBootApplication
@EnableConfigurationProperties(BatchProcessProperties::class)
class DataMigrationApplication {
    private val logger = LoggerFactory.getLogger(DataMigrationApplication::class.java)

    @Bean
    fun runner(jobLauncher: JobLauncher, migration: Job, properties: BatchProcessProperties): ApplicationRunner {
        return ApplicationRunner {
            for (table in properties.tables) {
                val source = table.source
                val target = table.target
                val params = JobParametersBuilder()
                    .addString("key", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()))
                    .addString("keyColumnName", table.keyColumnName)
                    .addString("sourceTable", source.schema + "." + source.table)
                    .addString("targetTable", target.schema + "." + target.table)
                    .toJobParameters()

                val execution = jobLauncher.run(migration, params)
                if (execution.status == BatchStatus.COMPLETED) {
                    logger.info(
                        "Migration process from '{}' to '{}' completed successfully (duration: {})",
                        "${source.schema}.${source.table}",
                        "${target.schema}.${target.table}",
                        Duration.between(execution.endTime?.toLocalTime(), execution.startTime?.toLocalTime())
                    )
                } else {
                    logger.error(
                        "Migration process from '{}' to '{}' failed with status {}",
                        "${source.schema}.${source.table}", "${target.schema}.${target.table}", execution.status
                    )
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<DataMigrationApplication>(*args)
}
