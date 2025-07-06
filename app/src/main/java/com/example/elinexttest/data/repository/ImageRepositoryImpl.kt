package com.example.elinexttest.data.repository

import com.example.elinexttest.domain.entities.ImageEntities
import com.example.elinexttest.domain.repository.ImageRepository
import com.example.elinexttest.utils.getImageURL
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor() : ImageRepository {
    override suspend fun getImages(count: Int): List<ImageEntities>
    = List(count) {
        ImageEntities(
            id = it,
            url = getImageURL()
        )
    }
}