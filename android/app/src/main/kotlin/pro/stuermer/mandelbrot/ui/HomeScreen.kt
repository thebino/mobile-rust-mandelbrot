package pro.stuermer.mandelbrot.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.koin.androidx.compose.getViewModel

@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = getViewModel()

    HomeContent(
        state = viewModel.uiState.collectAsState().value,
        handleEvent = viewModel::handleEvent,
    )
}

/**
 * stateless
 */
@Composable
fun HomeContent(
    state: HomeScreenState,
    handleEvent: (event: HomeScreenEvent) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val configuration = LocalConfiguration.current
        val screenWidthPx = with(density) {
            configuration.screenWidthDp.dp.roundToPx()
        }
        val screenHeightPx = with(density) {
            configuration.screenHeightDp.dp.roundToPx()
        }

        LaunchedEffect(Unit) {
            handleEvent(
                HomeScreenEvent.GenerateMandelbrot(
                    width = screenWidthPx,
                    height = screenHeightPx
                )
            )
        }

        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            model = state.bitmap,
            contentDescription = null
        )

        AnimatedVisibility(visible = state.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(modifier = Modifier)
            }
        }
    }
}
