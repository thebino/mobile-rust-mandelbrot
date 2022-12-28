package pro.stuermer.mandelbrot.ui

import android.graphics.Bitmap

data class HomeScreenState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val bitmap: Bitmap? = null
)
