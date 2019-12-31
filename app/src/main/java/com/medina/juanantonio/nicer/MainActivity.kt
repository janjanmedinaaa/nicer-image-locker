package com.medina.juanantonio.nicer

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.kroegerama.imgpicker.BottomSheetImagePicker
import com.medina.juanantonio.nicer.adapters.ImageGalleryAdapter
import com.medina.juanantonio.nicer.common.Constants.DIRECTORY.HIDDEN_DIRECTORY
import com.medina.juanantonio.nicer.common.Constants.INTENTS.IMAGE
import com.medina.juanantonio.nicer.databinding.ActivityMainBinding
import com.medina.juanantonio.nicer.managers.FileEncryptionManager
import com.medina.juanantonio.nicer.managers.FileManager
import com.medina.juanantonio.nicer.managers.SharedPrefsManager
import java.io.File

class MainActivity : AppCompatActivity(), BottomSheetImagePicker.OnImagesSelectedListener,
    ImageGalleryAdapter.ImageGalleryItemListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageGalleryAdapter: ImageGalleryAdapter
    private lateinit var sharedPrefsManager: SharedPrefsManager

    private lateinit var fileManager: FileManager
    private val fileEncryptionManager = FileEncryptionManager()

    private var startActivity = false

    companion object {
        const val REQUEST_TAG = "multi"
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(FLAG_SECURE, FLAG_SECURE)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        setSupportActionBar(binding.toolbar)
        setTitle(R.string.nicer_image_locker)

        fileManager = FileManager(this, fileEncryptionManager)
        fileManager.hiddenDirectory = fileManager.makeHiddenFolder(HIDDEN_DIRECTORY)

        sharedPrefsManager = SharedPrefsManager(this, fileEncryptionManager)

        fileEncryptionManager.secretKey = sharedPrefsManager.getSecretKey()

        setupImageGallery()
    }

    private fun setupImageGallery() {
        fileManager.hiddenDirectory?.let { directory ->
            imageGalleryAdapter = ImageGalleryAdapter(
                this,
                fileEncryptionManager
            )

            getNewImages(directory)

            binding.imageRecyclerview.run {
                adapter = imageGalleryAdapter
                layoutManager = GridLayoutManager(this@MainActivity, 4)
            }
        }
    }

    private fun getNewImages(directory: String) {
        imageGalleryAdapter.setImages(
            fileManager.getFiles(directory).map {
                File(it).toUri()
            }
        )
    }

    private fun openImagePicker() {
        BottomSheetImagePicker.Builder(application.packageName)
            .multiSelect()
            .multiSelectTitles(
                R.plurals.pick_multi,
                R.plurals.pick_multi_more,
                R.string.pick_multi_limit
            )
            .peekHeight(R.dimen.peekHeight)
            .columnSize(R.dimen.columnSize)
            .requestTag(REQUEST_TAG)
            .show(supportFragmentManager)
    }

    private fun hideImages(uris: List<Uri>) {
        fileManager.makeHiddenFolder(HIDDEN_DIRECTORY)?.let {
            uris.forEachIndexed { index, uri ->
                fileManager.hideFile(uri, it, index)
            }
            getNewImages(it)
        }
    }

    override fun onResume() {
        super.onResume()
        startActivity = false
    }

    override fun onPause() {
        super.onPause()
        if (!startActivity) onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_camera -> {
                openImagePicker()
                true
            }
            R.id.action_invisible -> {
                onBackPressed()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onImagesSelected(uris: List<Uri>, tag: String?) {
        if (uris.isNotEmpty()) hideImages(uris)
    }

    override fun onImageClicked(path: String, position: Int) {
        intent = Intent(this, ImageViewerActivity::class.java)
        intent.putExtra(IMAGE, path)
        startActivity = true
        startActivity(intent)
    }

    override fun onImageLongClicked(path: String, position: Int) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_title)
            .setMessage(R.string.delete_message)
            .setPositiveButton(android.R.string.yes) { _, _ ->
                File(path).delete()
                imageGalleryAdapter.removeImage(position)
            }
            .setNegativeButton(android.R.string.no, null)
            .show()
    }
}