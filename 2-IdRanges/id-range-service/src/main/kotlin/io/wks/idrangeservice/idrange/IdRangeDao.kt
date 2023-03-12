package io.wks.idrangeservice.idrange

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Repository

@Repository
class IdRangeDao(@Autowired private val jdbcTemplate: JdbcTemplate) {

    // TODO: Clean this up.
    fun assignIdRangeForServer(serverId: String, size: Long): IdRange? {
        return jdbcTemplate.query(
            """WITH last_max_value AS (SELECT COALESCE(MAX(max_value), 0) AS last_max_value FROM id_range_allocator.id_range_allocation)
                INSERT INTO id_range_allocator.id_range_allocation (server_id, min_value, max_value)
                SELECT ?, last_max_value + 1, last_max_value + ?
                FROM last_max_value 
                RETURNING *""",
            ResultSetExtractor {
                it.next()
                IdRange(
                    it.getString("server_id"),
                    it.getLong("min_value"),
                    it.getLong("max_value")
                )
            },
            serverId,
            size
        )
    }
}