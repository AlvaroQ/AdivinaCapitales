package com.alvaroquintana.data.repository

import com.alvaroquintana.data.datasource.FirestoreDataSource
import com.alvaroquintana.domain.Integrity

class IntegrityRepository(private val firestoreDataSource: FirestoreDataSource) {

    suspend fun addPayload(payload: Integrity) = firestoreDataSource.addPayload(payload)
}
