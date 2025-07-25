package com.alvaroquintana.adivinacapitales.datasource

import com.alvaroquintana.adivinacapitales.BuildConfig
import com.alvaroquintana.adivinacapitales.utils.Constants.PATH_REFERENCE_COUNTRIES
import com.alvaroquintana.adivinacapitales.utils.Constants.PATH_REFERENCE_APPS
import com.alvaroquintana.adivinacapitales.utils.Constants.TOTAL_ITEM_EACH_LOAD
import com.alvaroquintana.data.datasource.DataBaseSource
import com.alvaroquintana.domain.Country
import com.alvaroquintana.adivinacapitales.utils.log
import com.alvaroquintana.domain.App
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import com.google.firebase.database.ktx.getValue
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.coroutines.resume

class DataBaseSourceImpl : DataBaseSource {

    override suspend fun getCountryById(id: Int): Country {
        return suspendCancellableCoroutine { continuation ->
            FirebaseDatabase.getInstance().getReference(PATH_REFERENCE_COUNTRIES + id)
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        continuation.resume(dataSnapshot.getValue(Country::class.java) as Country){}
                    }

                    override fun onCancelled(error: DatabaseError) {
                        log("getCountryById FAILED", "Failed to read value.", error.toException())
                        continuation.resume(Country()){}
                        FirebaseCrashlytics.getInstance().recordException(Throwable(error.toException()))
                    }
                })
        }
    }

    override suspend fun getCountryList(currentPage: Int): MutableList<Country> {
        return suspendCancellableCoroutine { continuation ->
            FirebaseDatabase.getInstance().getReference(PATH_REFERENCE_COUNTRIES)
                .orderByKey()
                .startAt((currentPage * TOTAL_ITEM_EACH_LOAD).toString())
                .limitToFirst(TOTAL_ITEM_EACH_LOAD)
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val countryList = mutableListOf<Country>()
                        if(dataSnapshot.hasChildren()) {
                            for(snapshot in dataSnapshot.children) {
                                countryList.add(snapshot.getValue(Country::class.java)!!)
                            }
                        }
                        continuation.resume(countryList) {}
                    }

                    override fun onCancelled(error: DatabaseError) {
                        log("DataBaseBaseSourceImpl", "Failed to read value.", error.toException())
                        continuation.resume(mutableListOf()){}
                        FirebaseCrashlytics.getInstance().recordException(Throwable(error.toException()))
                    }
                })
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAppsRecommended(): MutableList<App> =
        suspendCancellableCoroutine { continuation ->
            val ref = FirebaseDatabase.getInstance().getReference(PATH_REFERENCE_APPS)

            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    try {
                        val appList = mutableListOf<App>()
                        if (dataSnapshot.hasChildren()) {
                            for (snapshot in dataSnapshot.children) {
                                val app = snapshot.getValue(App::class.java)
                                if (app != null) {
                                    appList.add(app)
                                }
                            }
                        }
                        continuation.resume(appList)
                    } catch (e: Exception) {
                        continuation.resumeWith(Result.failure(e))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    log("DataBaseBaseSourceImpl", "Failed to read value.", error.toException())
                    FirebaseCrashlytics.getInstance().recordException(error.toException())
                    continuation.resume(mutableListOf()) {}
                }
            }

            ref.addListenerForSingleValueEvent(listener)

            continuation.invokeOnCancellation {
                ref.removeEventListener(listener)
            }
        }
}