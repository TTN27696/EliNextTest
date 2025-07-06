package com.example.elinexttest.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.elinexttest.domain.entities.ImageEntities

class ImageDiffCallback : DiffUtil.ItemCallback<ImageEntities>() {
    override fun areItemsTheSame(oldItem: ImageEntities, newItem: ImageEntities): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ImageEntities, newItem: ImageEntities): Boolean {
        return oldItem == newItem
    }
}