package com.example.elinexttest.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.elinexttest.R
import com.example.elinexttest.databinding.ItemGalleryBinding
import com.example.elinexttest.domain.entities.ImageEntities
import com.example.elinexttest.utils.dpToPx

class ImageGalleryAdapter :
    ListAdapter<ImageEntities, ImageGalleryAdapter.ImageViewHolder>(ImageDiffCallback()) {

    private var availableHeight: Int = 0

    fun setAvailableHeight(height: Int) {
        availableHeight = height
        notifyDataSetChanged()
    }

    inner class ImageViewHolder(private val binding: ItemGalleryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val progressDrawable: CircularProgressDrawable =
            CircularProgressDrawable(binding.root.context).apply {
                strokeWidth = 5f
                centerRadius = 30f
                start()
            }

        fun bind(item: ImageEntities) {
            with(binding) {
                val screenWidth = itemView.resources.displayMetrics.widthPixels

                val columns = 7
                val rows = 10

                val spacingPx = 2.dpToPx(itemView.context)
                val totalSpacingX = spacingPx * (columns + 1)
                val totalSpacingY = spacingPx * (rows + 1)

                val itemWidth = (screenWidth - totalSpacingX) / columns
                val itemHeight = (availableHeight - totalSpacingY) / rows

                itemView.layoutParams = itemView.layoutParams.apply {
                    width = itemWidth
                    height = itemHeight
                }

                Glide.with(itemImageGallery)
                    .load(item.url)
                    .placeholder(progressDrawable)
                    .transform(CenterCrop(), RoundedCorners(7.dpToPx(root.context)))
                    .error(R.drawable.ic_launcher_background)
                    .into(itemImageGallery)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemGalleryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}