package pro.stuermer.mandelbrot.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pro.stuermer.mandelbrot.MandelbrotJNI
import timber.log.Timber


class HomeViewModel : ViewModel() {
    val uiState = MutableStateFlow(HomeScreenState())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Simple echo to showcase the JNI communication with rust code
            val echo: String = MandelbrotJNI.echo("This is a test")
            Timber.w("echo = $echo")
        }
    }

    fun handleEvent(event: HomeScreenEvent) {
        when (event) {
            is HomeScreenEvent.GenerateMandelbrot -> {
                uiState.update { it.copy(isLoading = true, bitmap = null) }
                viewModelScope.launch(Dispatchers.IO) {
                    val bitmap = generateBitmap(event.width, event.height)

                    uiState.update { it.copy(isLoading = false, bitmap = bitmap) }
                }
            }
        }
    }


    /**
     * Generate a bitmap based on the render output from rust
     */
    private fun generateBitmap(width: Int, height: Int): Bitmap {
        val bytes: ByteArray = MandelbrotJNI.render("${width}x${height}")
        Timber.d("bytes=(${bytes.size})")

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val rgbData = decodeGreyscale(bytes, width, height)
        bitmap.setPixels(
            rgbData, 0, width, 0, 0, width, height
        )

        Timber.i("Bitmap: ${bitmap.width} x ${bitmap.height}")

        return bitmap
    }
}

/**
 * Convert a byte array from YUV to greyscale to integer by shifting bits
 */
private fun decodeGreyscale(bytes: ByteArray, width: Int, height: Int): IntArray {
    val size = width * height
    val array = IntArray(size)
    for (x in 0 until size) {
        val intensity = bytes[x].toInt() and 0xFF
        array[x] = -0x1000000 or (intensity shl 16) or (intensity shl 8) or intensity
    }
    return array
}
