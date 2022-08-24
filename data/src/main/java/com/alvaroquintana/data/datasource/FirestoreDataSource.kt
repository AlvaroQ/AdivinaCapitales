package com.alvaroquintana.data.datasource

import arrow.core.Either
import com.alvaroquintana.data.repository.RepositoryException
import com.alvaroquintana.domain.Integrity
import com.alvaroquintana.domain.User

interface FirestoreDataSource {
    suspend fun addRecord(user: User): Either<RepositoryException, User>
    suspend fun addPayload(payload: Integrity): Either<RepositoryException, Integrity>
    suspend fun getRanking(): MutableList<User>
    suspend fun getWorldRecords(limit: Long): String
}