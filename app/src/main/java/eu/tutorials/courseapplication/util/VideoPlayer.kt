package eu.tutorials.courseapplication.util

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import eu.tutorials.courseapplication.MainViewModel
import java.lang.reflect.Modifier


@Composable
fun VideoPlayer(viewModel: MainViewModel) {
    val context = LocalContext.current

    val exoPlayer = viewModel.exoPlayer

    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
                useController = true
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    600
                )
            }
        }
    )
}