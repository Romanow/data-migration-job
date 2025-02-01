package ru.romanow.migration.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "batch-processing")
data class BatchProcessProperties(
    private var chunkSize: Int = 5000,
    var tables: List<Tables> = listOf()
)

data class Tables(
    val keyColumnName: String,
    val source: Table,
    val target: Table,
)

data class Table(
    val table: String,
    val schema: String
)
