package com.example.elinexttest.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elinexttest.domain.entities.ImageEntities
import com.example.elinexttest.domain.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _images = MutableLiveData<List<ImageEntities>>()
    val images: LiveData<List<ImageEntities>> = _images

    fun reloadAll() {
        viewModelScope.launch {
            _images.value = imageRepository.getImages(140)
        }
    }

    fun addImage() {
        val current = _images.value ?: emptyList()
        val lastId = current.lastOrNull()?.id ?: 0
        val newImage = ImageEntities(
            id = lastId + 1,
            url = "http://loremflickr.com/200/200/?random=${UUID.randomUUID()}"
        )
        _images.value = current + newImage
    }
}