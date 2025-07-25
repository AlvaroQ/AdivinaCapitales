package com.alvaroquintana.adivinacapitales.datasource

import  arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.alvaroquintana.adivinacapitales.utils.Constants.COLLECTION_INTEGRITY
import com.alvaroquintana.adivinacapitales.utils.Constants.COLLECTION_RANKING
import com.alvaroquintana.data.datasource.FirestoreDataSource
import com.alvaroquintana.data.repository.RepositoryException
import com.alvaroquintana.domain.Integrity
import com.alvaroquintana.domain.User
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine

@ExperimentalCoroutinesApi
class FirestoreDataSourceImpl(private val database: FirebaseFirestore) : FirestoreDataSource {

    override suspend fun addRecord(user: User): Either<RepositoryException, User> {
        return suspendCancellableCoroutine { continuation ->
            database.collection(COLLECTION_RANKING)
                .add(user)
                .addOnSuccessListener {
                    continuation.resume(user.right()){}
                }
                .addOnFailureListener {
                    continuation.resume(RepositoryException.NoConnectionException.left()){}
                    FirebaseCrashlytics.getInstance().recordException(Throwable(it.cause))
                }
        }
    }

    override suspend fun getRanking(): MutableList<User> {
        return suspendCancellableCoroutine { continuation ->
            val ref = database
                .collection(COLLECTION_RANKING)
                .orderBy("score", Query.Direction.DESCENDING)
                .limit(20)

            ref.get()
                .addOnSuccessListener {
                    continuation.resume(it.toObjects<User>().toMutableList()){}
                }
                .addOnFailureListener {
                    continuation.resume(mutableListOf()){}
                    FirebaseCrashlytics.getInstance().recordException(Throwable(it.cause))
                }
        }
    }

    override suspend fun getWorldRecords(limit: Long): String {
        return suspendCancellableCoroutine { continuation ->
            val ref = database
                .collection(COLLECTION_RANKING)
                .orderBy("score", Query.Direction.DESCENDING)
                .limit(limit)

            ref.get()
                .addOnSuccessListener {
                    continuation.resume(it.toObjects<User>().lastOrNull()?.score?.toString() ?: "") {}
                }
                .addOnFailureListener {
                    continuation.resume(""){}
                    FirebaseCrashlytics.getInstance().recordException(Throwable(it.cause))
                }
        }
    }

    override suspend fun addPayload(payload: Integrity): Either<RepositoryException, Integrity> {
        return suspendCancellableCoroutine { continuation ->
            database.collection(COLLECTION_INTEGRITY)
                .add(payload)
                .addOnSuccessListener {
                    continuation.resume(payload.right()) {}
                }
                .addOnFailureListener {
                    continuation.resume(RepositoryException.NoConnectionException.left()) {}
                    FirebaseCrashlytics.getInstance().recordException(Throwable(it.cause))
                }
        }
    }
}