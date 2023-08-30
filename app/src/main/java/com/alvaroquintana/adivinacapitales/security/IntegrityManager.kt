package com.alvaroquintana.adivinacapitales.security

import android.content.Context
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.alvaroquintana.adivinacapitales.ui.select.SelectViewModel
import com.alvaroquintana.usecases.SaveIntegrity
import com.google.android.gms.tasks.Task
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import com.google.android.play.core.integrity.IntegrityTokenResponse
import kotlinx.coroutines.launch
import org.jose4j.jwe.JsonWebEncryption
import org.jose4j.jwx.JsonWebStructure
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


class IntegrityManager(private val context: Context, private val viewModel: SelectViewModel) {

    fun integrityToken() {
        var payload: String

        // Receive the nonce from the secure server.
        val nonce: String = Base64.encodeToString("this_is_my_nonce".toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)

        // Create an instance of a manager.
        val integrityManager = IntegrityManagerFactory.create(context)

        // Request the integrity token by providing a nonce.
        val myIntegrityTokenResponse : Task<IntegrityTokenResponse> =
            integrityManager.requestIntegrityToken(
                IntegrityTokenRequest.builder()
                    .setNonce(nonce)
                    .build())

        myIntegrityTokenResponse.addOnFailureListener {
            // Integrity API error (-8); The calling app is making too many requests to the API and hence is throttled.
            Log.d("IntegrityTokenResponse-Failure", it.message ?: "Error play integrity")
        }

        val base64OfEncodedDecryptionKey = "U3/4J/W2XiP2lHjfmiUsOUYwTyPWQorBHUCIG0AyuXQ="
        val base64OfEncodedVerificationKey = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEI/ByC51nTJMBwsBKQZmZb33viFqtg2xunOGtRahq7IukfLSGrmO3LCz4t4UDRslTs1DcZzMsMRHzsyzOAFdA9A=="

        myIntegrityTokenResponse.addOnSuccessListener {
            try {
                val token: String = myIntegrityTokenResponse.result.token()
                Log.d("token", token)

                val decryptionKeyBytes: ByteArray = Base64.decode(base64OfEncodedDecryptionKey, Base64.DEFAULT)

                val decryptionKey: SecretKey = SecretKeySpec(
                    decryptionKeyBytes,
                    0,
                    decryptionKeyBytes.size,
                    "AES")

                val encodedVerificationKey: ByteArray = Base64.decode(base64OfEncodedVerificationKey, Base64.DEFAULT)
                val verificationKey: PublicKey = KeyFactory.getInstance("EC").generatePublic(X509EncodedKeySpec(encodedVerificationKey))
                val jwe: JsonWebEncryption = JsonWebStructure.fromCompactSerialization(token) as JsonWebEncryption
                jwe.key = decryptionKey

                val compactJws: String = jwe.payload
                val jws: JsonWebStructure? = JsonWebStructure.fromCompactSerialization(compactJws)
                jws!!.key = verificationKey
                payload = jws.payload
                checkAppIntegrity(payload)
                Log.d("GOOGLE PLAY VEREDICT", payload)


                // save result on Cloud Firestore -> logs-integrity
                viewModel.savePayloadIntegrity(payload)
            } catch (e: Error) {
                // LICENSE error
                payload = e.message.toString()
                Log.d("LICENSE error", e.printStackTrace().toString())
            } catch (e: IOException) {
                // LICENSE error
                payload = e.message.toString()
                Log.d("LICENSE error", e.printStackTrace().toString())
            } catch (e: Exception) {
                // LICENSE error
                payload = e.message.toString()
                Log.d("LICENSE error", e.printStackTrace().toString())
            }
        }
    }

    private fun checkAppIntegrity(payload: String) {
        try {
            // "appRecognitionVerdict" = "PLAY_RECOGNIZED"
            val appIntegrity = JSONObject(payload).getJSONObject("appIntegrity")
            val appRecognitionVerdict = appIntegrity.getString("appRecognitionVerdict")
            if (appRecognitionVerdict.equals("PLAY_RECOGNIZED", ignoreCase = true)) {
                Log.d("checkAppIntegrity", "appRecognitionVerdict = PLAY_RECOGNIZED")
            }

            // "appLicensingVerdict": "LICENSED"
            val accountDetails = JSONObject(payload).getJSONObject("accountDetails")
            val appLicensingVerdict = accountDetails.getString("appLicensingVerdict")
            if (appLicensingVerdict.equals("LICENSED", ignoreCase = true)) {
                Log.d("checkAppIntegrity","appLicensingVerdict = LICENSED")
            }
        } catch (exception: JSONException) {
            Log.d("SelectFragment", exception.message.toString())
        }
    }
}