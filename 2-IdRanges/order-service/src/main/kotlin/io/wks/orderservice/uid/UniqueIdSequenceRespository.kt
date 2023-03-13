package io.wks.orderservice.uid

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UniqueIdSequenceRepository : CrudRepository<UniqueIdSequence, String> {
    fun findFirstByDepletedIsFalse(): UniqueIdSequence?

    fun findByDepletedIsTrue(): List<UniqueIdSequence>

    @Modifying
    @Query("UPDATE UniqueIdSequence SET isDepleted = true WHERE name = :sequenceName")
    fun markSequenceAsDepleted(@Param("sequenceName") sequenceName: String)
}