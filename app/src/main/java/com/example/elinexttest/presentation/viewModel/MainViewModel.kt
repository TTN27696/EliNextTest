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

@HiltViewModel
class MainViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {

    private var realLastId: Int = 0
    private var realImagesSize : Int = 0

    private val _images = MutableLiveData<List<ImageEntities>>()
    val images: LiveData<List<ImageEntities>> = _images

    private val _stateGallery = MutableLiveData<GalleryActionState>()
    val stateGallery: LiveData<GalleryActionState> = _stateGallery

    fun reloadAll() {
        viewModelScope.launch {
            _images.value = imageRepository.getImages(140)
            realImagesSize = _images.value?.size ?: 0
            delay(500.milliseconds)
            _stateGallery.value = GalleryActionState.RELOAD
        }
    }

    fun addImage() {
        viewModelScope.launch {
            val current = _images.value?.toMutableList() ?: mutableListOf()
            if (realImagesSize % 70 == 0) {
                realLastId = current.lastOrNull()?.id ?: 0
                val newImage = ImageEntities(
                    id = realLastId + 1,
                    url = getImageURL()
                )
                val emptyImages = List(69) {
                    ImageEntities(
                        id = realLastId + it + 2,
                        url = ""
                    )
                }
                _images.value = current + newImage + emptyImages
                realImagesSize++
                realLastId++
                delay(500.milliseconds)
                _stateGallery.value = GalleryActionState.ADD_NEW
            } else {
                realImagesSize++
                realLastId++
                val oldItem = current.getOrNull(realLastId)
                val updateItem = oldItem?.copy(url = getImageURL())
                updateItem?.let {
                    current[realLastId] = it
                }
                _images.value = current.toList()
                delay(500.milliseconds)
                _stateGallery.value = GalleryActionState.ADD_NEW
            }
        }
    }
}