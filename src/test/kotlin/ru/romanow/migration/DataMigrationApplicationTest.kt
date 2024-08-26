package ru.romanow.migration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import ru.romanow.migration.config.DatabaseTestConfiguration
import ru.romanow.migration.properties.BatchProcessProperties
import javax.sql.DataSource

@ActiveProfiles("test")
@SpringBootTest
@Import(DatabaseTestConfiguration::class)
internal class DataMigrationApplicationTest {

    @Autowired
    @Qualifier("targetDataSource")
    private lateinit var dataSource: DataSource

    @Autowired
    private lateinit var properties: BatchProcessProperties

    @Test
    fun test() {
        val jdbcTemplate = JdbcTemplate(dataSource)
        val target = properties.tables.first().target
        val targetTableName = "${target.schema}.${target.table}"
        val count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM $targetTableName", Long::class.java)!!
        assertThat(count).isEqualTo(10000)
    }
}
