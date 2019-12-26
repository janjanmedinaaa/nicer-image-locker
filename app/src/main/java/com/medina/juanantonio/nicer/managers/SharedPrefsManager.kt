package com.medina.juanantonio.nicer.managers

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.medina.juanantonio.nicer.common.Constants.ENCRYPTION.ALGORITHM
import com.medina.juanantonio.nicer.common.Constants.SharedPreferences.SECRET_KEY
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@SuppressLint("CommitPrefEdits")
class SharedPrefsManager(
    context: Context,
    private val encryptionManager: EncryptionManager? = null
) {
    private var sharedPrefs: SharedPreferences
    private var editor: SharedPreferences.Editor

    init {
        sharedPrefs = context.getSharedPreferences(
            context.applicationInfo.packageName,
            0
        )
        editor = sharedPrefs.edit()
    }

    private fun saveSecretKey(secretKey: SecretKey): String {
        val encodedKey = Base64.encodeToString(secretKey.encoded, Base64.NO_WRAP)
        editor.putString(SECRET_KEY, encodedKey).apply()
        return encodedKey
    }

    fun getSecretKey(): SecretKey {
        val key = sharedPrefs.getString(SECRET_KEY, null)

        if (key == null) {
            val secretKey = encryptionManager?.generateSecretKey()
            saveSecretKey(secretKey!!)
            return secretKey
        }

        val decodedKey = Base64.decode(key, Base64.NO_WRAP)
        return SecretKeySpec(
            decodedKey,
            0,
            decodedKey.size,
            ALGORITHM
        )
    }
}