package pro.stuermer.mandelbrot

/**
 * Cache and re-use IDs by using a static class initializer
 *
 * @see https://developer.android.com/training/articles/perf-jni#jclass,-jmethodid,-and-jfieldid
 */
class MandelbrotJNI {

    companion object {
        init {
            System.loadLibrary("mandelbrot")
        }

        @JvmStatic
        external fun echo(string: String): String

        @JvmStatic
        external fun render(string: String): ByteArray
    }
}
