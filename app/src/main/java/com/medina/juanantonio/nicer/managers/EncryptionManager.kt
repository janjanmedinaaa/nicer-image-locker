package com.medina.juanantonio.nicer.managers

import android.annotation.SuppressLint
import android.util.Base64
import com.medina.juanantonio.nicer.managers.EncryptionManager.ENCRYPTION.ALGORITHM
import com.medina.juanantonio.nicer.managers.EncryptionManager.ENCRYPTION.PROVIDER
import com.medina.juanantonio.nicer.managers.EncryptionManager.ENCRYPTION.TRANSFORMATION
import java.io.File
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class EncryptionManager {
    lateinit var secretKey: SecretKey

    object ENCRYPTION {
        const val ALGORITHM = "AES"
        const val TRANSFORMATION = "AES"
        const val PROVIDER = "BC"
    }

    @Throws(Exception::class)
    fun generateSecretKey(): SecretKey? {
        val secureRandom = SecureRandom()
        val keyGenerator = KeyGenerator.getInstance(ALGORITHM)
        keyGenerator?.init(128, secureRandom)
        return keyGenerator?.generateKey()
    }

    @Throws(Exception::class)
    private fun readFile(file: File): ByteArray {
        val fileContents = file.readBytes()
        val inputBuffer = BufferedInputStream(FileInputStream(file))

        inputBuffer.read(fileContents)
        inputBuffer.close()

        return fileContents
    }

    @Throws(Exception::class)
    private fun saveFile(fileData: ByteArray, path: String) {
        val file = File(path)
        val bos = BufferedOutputStream(FileOutputStream(file, false))
        bos.write(fileData)
        bos.flush()
        bos.close()
    }

    @Throws(Exception::class)
    @SuppressLint("GetInstance")
    private fun encrypt(fileData: ByteArray): ByteArray {
        val data = secretKey.encoded
        val secretKeySpec = SecretKeySpec(data, 0, data.size, ALGORITHM)
        val cipher = Cipher.getInstance(TRANSFORMATION, PROVIDER)
        cipher.init(ENCRYPT_MODE, secretKeySpec, IvParameterSpec(ByteArray(cipher.blockSize)))

        return cipher.doFinal(fileData)
    }

    @Throws(Exception::class)
    @SuppressLint("GetInstance")
    private fun decrypt(fileData: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION, PROVIDER)
        cipher.init(DECRYPT_MODE, secretKey, IvParameterSpec(ByteArray(cipher.blockSize)))

        return cipher.doFinal(fileData)
    }

    @Throws(Exception::class)
    fun encryptFile(currentFile: File, destinationPath: String) {
        val encryptedData = encrypt(readFile(currentFile))

        saveFile(encryptedData, destinationPath)
    }

    @Throws(Exception::class)
    fun decryptFile(file: File): ByteArray {
        return decrypt(readFile(file))
    }
}

fun SecretKey.toEncodedString(): String = Base64.encodeToString(encoded, Base64.NO_WRAP)

fun String?.toSecretKey(): SecretKey {
    val decodedKey = Base64.decode(this, Base64.NO_WRAP)
    return SecretKeySpec(
        decodedKey,
        0,
        decodedKey.size,
        ALGORITHM
    )
}