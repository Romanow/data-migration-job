package ru.romanow.migration.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "batch-processing")
data class BatchProcessProperties(
    var tables: List<Tables> = listOf()
)

data class Tables(
    val source: Table,
    val target: Table,
)

data class Table(
    val schema: String,
    val table: String
)
