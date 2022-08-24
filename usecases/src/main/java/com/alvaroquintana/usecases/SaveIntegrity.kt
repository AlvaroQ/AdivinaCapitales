package com.alvaroquintana.usecases

import arrow.core.Either
import com.alvaroquintana.data.repository.IntegrityRepository
import com.alvaroquintana.data.repository.RepositoryException
import com.alvaroquintana.domain.Integrity

class SaveIntegrity(private val integrityRepository: IntegrityRepository) {

    suspend fun invoke(payload: Integrity): Either<RepositoryException, Integrity> = integrityRepository.addPayload(payload)

}
