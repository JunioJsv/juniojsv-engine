package juniojsv.engine.example.android

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GamepadButton(size: Dp, label: String, onClick: (label: String) -> Unit = {}) {
    val backgroundColor = Color.Gray.copy(alpha = 0.3f)
    val foregroundColor = Color.White
    Box(
        modifier = Modifier
            .size(size)
            .background(backgroundColor, CircleShape)
            .clip(CircleShape)
            .clickable {
                onClick(label)
            }
    ) {
        Text(
            label,
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                color = foregroundColor,
                fontSize = (size / 3).value.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun GameVirtualGamepadPreview() {
    val context = LocalContext.current
    val fakeGame = object : Game(context) {}
    GameVirtualGamepad(game = fakeGame)
}

@Composable
fun GameVirtualGamepad(
    modifier: Modifier = Modifier,
    game: Game
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomStart
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()

                            val change = event.changes.first()
                            val pointerId = change.id.value
                            val position = change.position
                            val action = when {
                                change.changedToDown() -> MotionEvent.ACTION_DOWN
                                change.changedToUp() -> MotionEvent.ACTION_UP
                                change.positionChanged() -> MotionEvent.ACTION_MOVE
                                else -> -1
                            }

                            if (action != -1) {
                                game.onTouchEvent(
                                    pointerId,
                                    position.x,
                                    position.y,
                                    action
                                )
                            }
                        }
                    }
                }
        )
        Box(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            val buttonSize = 56.dp

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                GamepadButton(buttonSize, "X") { game.onButtonEvent(it) }
                Row {
                    GamepadButton(buttonSize, "Y") { game.onButtonEvent(it) }
                    Spacer(modifier = Modifier.size(buttonSize))
                    GamepadButton(buttonSize, "A") { game.onButtonEvent(it) }
                }
                GamepadButton(buttonSize, "B") { game.onButtonEvent(it) }
            }
        }
        VirtualJoystick(
            modifier = Modifier.offset(24.dp, (-24).dp),
            onMove = { offset ->
                game.onJoystickEvent(offset.x, offset.y)
            }
        )
    }
}