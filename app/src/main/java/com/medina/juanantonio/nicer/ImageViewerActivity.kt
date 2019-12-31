package com.medina.juanantonio.nicer

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.medina.juanantonio.nicer.common.Constants.INTENTS.IMAGE
import com.medina.juanantonio.nicer.databinding.ActivityImageViewerBinding
import com.medina.juanantonio.nicer.managers.FileEncryptionManager
import com.medina.juanantonio.nicer.managers.SharedPrefsManager
import java.io.File

class ImageViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageViewerBinding
    private lateinit var sharedPrefsManager: SharedPrefsManager
    private val fileEncryptionManager = FileEncryptionManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(FLAG_SECURE, FLAG_SECURE)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_image_viewer
        )

        sharedPrefsManager = SharedPrefsManager(this, fileEncryptionManager)
        fileEncryptionManager.secretKey = sharedPrefsManager.getSecretKey()

        val imagePath = intent.getStringExtra(IMAGE) ?: ""
        val encryptedImageFile = File(imagePath).toUri()

        fileEncryptionManager.decryptFile(encryptedImageFile.toFile()).let {
            binding.photoViewer.setImageBitmap(
                BitmapFactory.decodeByteArray(it, 0, it.size)
            )
        }

        binding.imageBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onRestart() {
        super.onRestart()
        intent = Intent(this, LockScreenActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
