package com.example.elinexttest.presentation.ui

import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.elinexttest.R
import com.example.elinexttest.databinding.ActivityMainBinding
import com.example.elinexttest.presentation.GalleryActionState
import com.example.elinexttest.presentation.adapter.GridSpacingItemDecoration
import com.example.elinexttest.presentation.adapter.ImageGalleryAdapter
import com.example.elinexttest.presentation.viewModel.MainViewModel
import com.example.elinexttest.utils.dpToPx
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val adapter = ImageGalleryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupObservers()
        setupButtons()

        viewModel.reloadAll()
    }

    private fun setupRecyclerView() {
        with(binding) {
            rvGallery.layoutManager =
                GridLayoutManager(this@MainActivity, 10, RecyclerView.HORIZONTAL, false)

            rvGallery.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    rvGallery.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val height = rvGallery.height
                    adapter.setAvailableHeight(height)
                }
            })


            binding.rvGallery.adapter = adapter
            PagerSnapHelper().attachToRecyclerView(binding.rvGallery)

            val spacingPx = 2.dpToPx(this@MainActivity)
            binding.rvGallery.addItemDecoration(GridSpacingItemDecoration(7, spacingPx))
        }

    }

    private fun setupObservers() {
        viewModel.images.observe(this) { newList ->
            adapter.submitList(newList.toList())
        }

        viewModel.stateGallery.observe(this) { state ->
            when (state) {
                GalleryActionState.ADD_NEW -> {
                    binding.rvGallery.smoothScrollToPosition(adapter.itemCount - 1)
                }
                GalleryActionState.RELOAD -> {
                    binding.rvGallery.smoothScrollToPosition(0)
                }

                else -> {}
            }

        }
    }

    private fun setupButtons() {
        binding.buttonReloadAll.setOnClickListener { viewModel.reloadAll() }
        binding.imageAdd.setOnClickListener { viewModel.addImage() }
    }
}