package com.medina.juanantonio.nicer.managers

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.medina.juanantonio.nicer.common.Constants.SharedPreferences.SECRET_KEY
import javax.crypto.SecretKey

@SuppressLint("CommitPrefEdits")
class SharedPrefsManager(
    context: Context,
    private val fileEncryptionManager: FileEncryptionManager? = null
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
        val encodedKey = secretKey.toEncodedString()
        editor.putString(SECRET_KEY, encodedKey).apply()
        return encodedKey
    }

    fun getSecretKey(): SecretKey {
        val key = sharedPrefs.getString(SECRET_KEY, null)

        if (key == null) {
            val secretKey = fileEncryptionManager?.generateSecretKey()
            saveSecretKey(secretKey!!)
            return secretKey
        }

        return key.toSecretKey()
    }
}