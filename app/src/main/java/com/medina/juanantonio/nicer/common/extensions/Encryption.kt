package com.medina.juanantonio.nicer.common.extensions

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

fun String.md5Encrypt(): String? {
    return try {
        val digest = MessageDigest.getInstance("MD5")
        digest.update(toByteArray())
        val messageDigest = digest.digest()
        val hexString = StringBuffer()

        for (i in messageDigest.indices) hexString.append(
            Integer.toHexString(0xFF and messageDigest[i].toInt())
        )

        hexString.toString()
    } catch (e: NoSuchAlgorithmException) {
        ""
    }
}