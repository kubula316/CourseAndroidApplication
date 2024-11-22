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
import java.lang.reflect.Modifier


@Composable
fun VideoPlayer(videoUrl: String) {
    val context = LocalContext.current

    // Tworzymy instancję ExoPlayer
    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build()
    }

    // Używamy AndroidView, aby zintegrować ExoPlayer z Compose
    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
                useController = true // Kontrolki odtwarzania (play, pause, itd.)
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    800
                )
            }
        }
    )

    // Zwalnianie zasobów, gdy widok nie jest już wyświetlany
    DisposableEffect(videoUrl) {
        if (videoUrl.isNotEmpty()) {
            val mediaItem = MediaItem.fromUri(videoUrl)
            exoPlayer.setMediaItem(mediaItem) // Ustawienie nowego MediaItem
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }

        onDispose {
            exoPlayer.stop()
            exoPlayer.release()
        }
    }
}