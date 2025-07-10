package com.example.elinexttest.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewTreeObserver
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elinexttest.R
import com.example.elinexttest.databinding.ActivityMainBinding
import com.example.elinexttest.presentation.GalleryActionState
import com.example.elinexttest.presentation.adapter.GridSpacingItemDecoration
import com.example.elinexttest.presentation.adapter.ImageGalleryAdapter
import com.example.elinexttest.presentation.viewModel.MainViewModel
import com.example.elinexttest.utils.dpToPx
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val adapter = ImageGalleryAdapter()
    private var currentPage = 0
    private val itemsPerPage = 70
    private var scrolledEnough = false
    private lateinit var gestureDetector: GestureDetector

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
        setupSwipeGesture()

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
                    val itemCount = adapter.itemCount
                    val remain = itemCount % itemsPerPage
                    val divider = itemCount / itemsPerPage
                    val maxPage = if (remain == 0) divider - 1 else divider
                    if (currentPage < maxPage) {
                        currentPage = maxPage
                    }
                }
                GalleryActionState.RELOAD -> {
                    binding.rvGallery.smoothScrollToPosition(0)
                    currentPage = 0
                }

                else -> {}
            }

        }
    }

    private fun setupButtons() {
        binding.buttonReloadAll.setOnClickListener { viewModel.reloadAll() }
        binding.imageAdd.setOnClickListener { viewModel.addImage() }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSwipeGesture() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            private val SWIPE_THRESHOLD = 100

            override fun onDown(e: MotionEvent): Boolean {
                scrolledEnough = false
                return true
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                if (e1 == null) return false
                val diffX = e2.x - e1.x

                if (!scrolledEnough && abs(diffX) > SWIPE_THRESHOLD) {
                    scrolledEnough = true
                    if (diffX < 0) {
                        goToNextPage()
                    } else {
                        goToPreviousPage()
                    }
                    return true
                }
                return false
            }
        })

        binding.rvGallery.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun goToNextPage() {
        val itemCount = adapter.itemCount
        val remain = itemCount % itemsPerPage
        val divider = itemCount / itemsPerPage
        val maxPage = if (remain == 0) divider - 1 else divider

        if (currentPage < maxPage) {
            currentPage++
            val targetPos = currentPage * itemsPerPage * 2 - 1
            binding.rvGallery.smoothScrollToPosition(targetPos)
        }
    }

    private fun goToPreviousPage() {
        if (currentPage > 0) {
            currentPage--
            val targetPos = currentPage * itemsPerPage
            binding.rvGallery.smoothScrollToPosition(targetPos)
        }
    }
}