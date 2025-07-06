package com.example.elinexttest.domain.repository

import com.example.elinexttest.domain.entities.ImageEntities

interface ImageRepository {
    suspend fun getImages(count: Int): List<ImageEntities>
}