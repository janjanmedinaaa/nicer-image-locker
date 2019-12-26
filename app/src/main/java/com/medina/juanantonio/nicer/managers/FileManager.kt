package com.medina.juanantonio.nicer.managers

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import com.medina.juanantonio.nicer.common.Constants.DATE.FILE_NAME_FORMAT
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class FileManager(
    private val context: Context,
    private val encryptionManager: EncryptionManager
) {
    var hiddenDirectory: String? = null

    fun makeHiddenFolder(folderName: String): String? {
        val dirName = "${context.applicationInfo.dataDir}/.$folderName"
        val directory = File(dirName)

        if (directory.exists()) return dirName
        return if (directory.mkdir()) dirName else null
    }

    fun hideFile(currentUri: Uri, hiddenFolder: String, key: Int) {
        val currentFile = currentUri.toFile()
        val newFileName = renameEncryptedFile(
            currentFile.absolutePath,
            key
        )
        val newFileLocation = "${hiddenFolder}/$newFileName"

        encryptionManager.encryptFile(currentFile, newFileLocation)
    }

    fun getFiles(directoryUri: String): ArrayList<String> {
        val directory = File(directoryUri)
        val imageList = arrayListOf<String>()

        if (directory.exists() && directory.isDirectory) {
            directory.listFiles { dir, name ->
                imageList.add("$dir/$name")
            }
        }

        return imageList
    }

    private fun renameEncryptedFile(fileName: String, key: Int): String {
        val sdf = SimpleDateFormat(FILE_NAME_FORMAT, Locale.getDefault())
        val fileType = fileName.split(".").last()

        return "${sdf.format(Date())}_$key.$fileType"
    }
}