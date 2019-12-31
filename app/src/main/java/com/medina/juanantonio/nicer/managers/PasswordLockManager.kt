package com.medina.juanantonio.nicer.managers

import com.medina.juanantonio.nicer.common.extensions.md5Encrypt

class PasswordLockManager(private val sharedPrefsManager: SharedPrefsManager) {

    fun checkStatus(): PasswordLockStatus {
        val pattern = sharedPrefsManager.getPatternLock()
        return if (pattern == null) {
            PasswordLockStatus.UNSECURED
        } else {
            PasswordLockStatus.SECURED
        }
    }

    fun validatePatternLock(pattern: String): Boolean {
        return sharedPrefsManager.getPatternLock() == pattern.md5Encrypt()
    }

    fun savePatternLock(pattern: String) {
        sharedPrefsManager.setPatternLock(pattern)
    }
}

enum class PasswordLockStatus {
    SECURED,
    UNSECURED
}