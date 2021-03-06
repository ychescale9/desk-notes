package io.github.reactivecircus.desknotes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.material.AmbientContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.withSaveLayer
import androidx.compose.ui.unit.dp

@Composable
fun NightModeSwitch(
    isOn: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    IconSwitch(
        checked = isOn,
        onCheckedChange = onChange,
        modifier = modifier,
        colors = IconSwitchConstants.defaultColors(
            checkedThumbColor = CheckedThumbColor,
            checkedTrackColor = MaterialTheme.colors.background,
            checkedTrackAlpha = 1.0f,
            checkedBorderColor = CheckedThumbColor,
            checkedBorderAlpha = CheckedBorderAlpha,
            uncheckedThumbColor = MaterialTheme.colors.onBackground,
            uncheckedTrackColor = MaterialTheme.colors.surface,
            uncheckedTrackAlpha = 1.0f,
            uncheckedBorderColor = MaterialTheme.colors.onBackground,
            uncheckedBorderAlpha = UncheckedBorderAlpha,
        ),
    ) {
        MoonIcon(
            modifier = Modifier.size(MoonIconSize),
            tint = if (isOn) MaterialTheme.colors.onSurface else UncheckedIconColor,
        )
    }
}

private val CheckedThumbColor = Color(0xFF6E40C9)
private val UncheckedIconColor = Color(0xFFFFDF5D)

private val MoonIconSize = 16.dp
private const val CheckedBorderAlpha = 0.5f
private const val UncheckedBorderAlpha = 0.1f

@Composable
private fun MoonIcon(
    modifier: Modifier = Modifier,
    tint: Color = AmbientContentColor.current,
) {
    Canvas(
        modifier = modifier.aspectRatio(1f)
    ) {
        val sizePx = size.width

        drawContext.transform.rotate(-45f)
        drawContext.canvas.withSaveLayer(
            bounds = drawContext.size.toRect(),
            paint = Paint()
        ) {
            drawCircle(
                color = tint,
                radius = sizePx * 0.5f,
            )

            drawCircle(
                color = Color.Black,
                radius = sizePx * 0.4f,
                center = Offset(
                    x = size.width * 0.5f,
                    y = size.height * 0.20f,
                ),
                blendMode = BlendMode.DstOut
            )
        }
    }
}
