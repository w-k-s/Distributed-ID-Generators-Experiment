package io.wks.orderservice.uid

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UniqueIdSequenceRepository : CrudRepository<UniqueIdSequence, String> {
    fun findFirstByDepletedIsFalse(): UniqueIdSequence?
}