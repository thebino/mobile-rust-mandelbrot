package pro.stuermer.mandelbrot

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pro.stuermer.mandelbrot.ui.HomeViewModel

val appModule = module {
    // ui
    viewModel {
        HomeViewModel()
    }
}
