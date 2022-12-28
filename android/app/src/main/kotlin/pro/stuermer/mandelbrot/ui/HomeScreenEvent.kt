package pro.stuermer.mandelbrot.ui

sealed interface HomeScreenEvent {
    class GenerateMandelbrot(val width: Int, val height: Int) : HomeScreenEvent
}
