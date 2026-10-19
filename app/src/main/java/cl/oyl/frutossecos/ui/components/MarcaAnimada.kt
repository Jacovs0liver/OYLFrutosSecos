package cl.oyl.frutossecos.ui.components

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import cl.oyl.frutossecos.R

@Composable
fun MarcaAnimada(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val imagen = remember { ImageView(context).apply { setImageResource(R.drawable.ic_oyl); contentDescription = "OYL Frutos Secos" } }
    val animacion = remember(imagen) {
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(imagen, View.SCALE_X, 1f, 1.33f, 1f),
                ObjectAnimator.ofFloat(imagen, View.SCALE_Y, 1f, 1.33f, 1f)
            )
            duration = 700
        }
    }
    DisposableEffect(imagen) {
        val iniciar = Runnable { animacion.start() }
        imagen.setOnClickListener { animacion.start() }
        imagen.post(iniciar)
        onDispose { imagen.removeCallbacks(iniciar); imagen.setOnClickListener(null); animacion.cancel() }
    }
    AndroidView(factory = { imagen }, modifier = modifier)
}
