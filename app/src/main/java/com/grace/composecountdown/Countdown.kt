package com.grace.composecountdown

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toOffset
import com.grace.composecountdown.ui.theme.bgColorCenter
import com.grace.composecountdown.ui.theme.bgColorEdge
import com.grace.composecountdown.ui.theme.darkRed
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class WheelPosition(val start: Offset, val current: Offset) {
    val delta: Offset get() = current - start
    val theta: Float get() = delta.theta
}

val Offset.theta: Float get() = (atan2(y.toDouble(), x.toDouble()) * 180.0 / PI).toFloat()

operator fun WheelPosition?.plus(rhs: Offset): WheelPosition =
    this?.copy(current = current + rhs) ?: WheelPosition(rhs, rhs)

@Composable
fun Countdown() {

    var origin by remember { mutableStateOf(Offset.Zero) }
    var position by remember { mutableStateOf<Offset?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(listOf(bgColorCenter, bgColorEdge))),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            position = offset - origin
                        },
                        onDragEnd = {
                            position = null
                        },
                        onDragCancel = {
                            position = null
                        },
                        onDrag = { change, amount ->
                            position = position?.let { it + amount } ?: amount
                            change.consumeAllChanges()
                        }
                    )
                }
                .onSizeChanged { origin = it.center.toOffset() }
                .fillMaxWidth()
                //.background(Color.Blue, shape = CircleShape)
                .aspectRatio(1f),
            /*.drawBehind {
                drawCircle(Color.White, center = center, radius = 20f)
            }*/
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "01:29",
                style = TextStyle(
                    Color.White,
                    fontSize = 48.sp,
                    textAlign = TextAlign.Center
                )
            )
            val theta = position?.theta ?: 0f
            for (i in 0 until 40) {
                val angle = i * 9
                TickMark(
                    angle = angle,
                    on = angle < theta
                )
            }
        }
    }
}

const val EndRadiusFraction = 0.75f
const val StartRadiusFraction = 0.5f
const val TickWidth = 9f


@Composable
fun TickMark(
    angle: Int,
    on: Boolean
) {
    Box(
        Modifier
            .fillMaxSize()
            .drawBehind {
                val theta = angle * PI.toFloat() / 180f
                val startRadius = size.width / 2 * StartRadiusFraction
                val endRadius = size.width / 2 * EndRadiusFraction
                val startPos = Offset(
                    cos(theta) * startRadius,
                    sin(theta) * startRadius
                )
                val endPos = Offset(
                    cos(theta) * endRadius,
                    sin(theta) * endRadius
                )
                drawLine(
                    if (on) darkRed else Color.White.copy(alpha = 0.1f),
                    center + startPos,
                    center + endPos,
                    TickWidth,
                    StrokeCap.Round
                )
            }
    )
}