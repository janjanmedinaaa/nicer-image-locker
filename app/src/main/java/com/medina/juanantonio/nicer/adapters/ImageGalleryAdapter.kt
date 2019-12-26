package com.medina.juanantonio.nicer.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toFile
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.medina.juanantonio.nicer.R
import com.medina.juanantonio.nicer.databinding.ImageGalleryItemLayoutBinding
import com.medina.juanantonio.nicer.managers.EncryptionManager
import com.medina.juanantonio.nicer.models.ImageGalleryItem

class ImageGalleryAdapter(
    private val imageGalleryItemListener: ImageGalleryItemListener,
    private val encryptionManager: EncryptionManager
) : RecyclerView.Adapter<ImageGalleryAdapter.ViewHolder>() {
    private val imageGalleryList = arrayListOf<ImageGalleryItem>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ImageGalleryItemLayoutBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.image_gallery_item_layout,
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return imageGalleryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imageGalleryList[position], position)
    }

    fun setImages(uriList: List<Uri>) {
        imageGalleryList.clear()
        uriList.forEach {
            imageGalleryList.add(ImageGalleryItem(it.toFile()))
        }
        notifyDataSetChanged()
    }

    fun removeImage(position: Int) {
        imageGalleryList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemChanged(position)
    }

    inner class ViewHolder(
        private val binding: ImageGalleryItemLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ImageGalleryItem, position: Int) {
            binding.imageItem.run {
                setOnClickListener {
                    imageGalleryItemListener.onImageClicked(item.file.path, position)
                }

                setOnLongClickListener {
                    imageGalleryItemListener.onImageLongClicked(item.file.path, position)
                    return@setOnLongClickListener true
                }

                Glide.with(binding.root.context)
                    .load(encryptionManager.decryptFile(item.file))
                    .into(this)
            }
        }
    }

    interface ImageGalleryItemListener {
        fun onImageClicked(path: String, position: Int)
        fun onImageLongClicked(path: String, position: Int)
    }
}