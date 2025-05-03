package juniojsv.engine.example.android

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Preview
@Composable
fun VirtualJoystick(
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    knobSize: Dp = 60.dp,
    onMove: (Offset) -> Unit = {}
) {
    var knobOffset by remember { mutableStateOf(Offset.Zero) }

    val sizePx = with(LocalDensity.current) { size.toPx() }
    val knobPx = with(LocalDensity.current) { knobSize.toPx() }
    val radius = sizePx / 2f - knobPx / 2f

    Box(
        modifier = modifier
            .size(size)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        val raw = knobOffset + dragAmount
                        val dist = raw.getDistance()

                        knobOffset = if (dist <= radius) {
                            raw
                        } else {
                            val angle = atan2(raw.y, raw.x)
                            Offset(
                                x = cos(angle) * radius,
                                y = sin(angle) * radius
                            )
                        }

                        val normX = knobOffset.x / radius
                        val normY = knobOffset.y / radius
                        onMove(Offset(normX, -1 * normY))
                    },
                    onDragEnd = {
                        knobOffset = Offset.Zero
                        onMove(Offset.Zero)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Base
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray.copy(alpha = 0.3f), CircleShape)
        )

        // Knob
        Box(
            modifier = Modifier
                .size(knobSize)
                .offset {
                    IntOffset(
                        knobOffset.x.roundToInt(),
                        knobOffset.y.roundToInt()
                    )
                }
                .background(Color.White, CircleShape)
        )
    }
}
