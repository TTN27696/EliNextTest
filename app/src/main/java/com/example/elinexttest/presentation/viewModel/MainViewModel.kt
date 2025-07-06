package com.example.elinexttest.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elinexttest.domain.entities.ImageEntities
import com.example.elinexttest.domain.repository.ImageRepository
import com.example.elinexttest.presentation.GalleryActionState
import com.example.elinexttest.utils.getImageURL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MainViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _images = MutableLiveData<List<ImageEntities>>()
    val images: LiveData<List<ImageEntities>> = _images

    private val _stateGallery = MutableLiveData<GalleryActionState>()
    val stateGallery: LiveData<GalleryActionState> = _stateGallery

    fun reloadAll() {
        viewModelScope.launch {
            _images.value = imageRepository.getImages(140)
            delay(500.milliseconds)
            _stateGallery.value = GalleryActionState.RELOAD
        }
    }

    fun addImage() {
        viewModelScope.launch {
            val current = _images.value ?: emptyList()
            val lastId = current.lastOrNull()?.id ?: 0
            val newImage = ImageEntities(
                id = lastId + 1,
                url = getImageURL()
            )
            _images.value = current + newImage
            delay(500.milliseconds)
            _stateGallery.value = GalleryActionState.ADD_NEW
        }
    }
}