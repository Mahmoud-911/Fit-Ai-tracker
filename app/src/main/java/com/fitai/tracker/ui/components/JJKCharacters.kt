package com.fitai.tracker.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

// ──────────────────────────────────────────────
// GOJO SATORU  — The Strongest (∞ purple aura)
// ──────────────────────────────────────────────
@Composable
fun GojoCharacter(modifier: Modifier = Modifier, animated: Boolean = true) {
    val infiniteTransition = rememberInfiniteTransition(label = "gojo")
    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "aura"
    )
    val rotateDeg by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "rotate"
    )
    val eyeGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "eye"
    )

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val scale = size.width / 200f

        drawGojoAura(cx, cy, scale, auraAlpha, rotateDeg, animated)
        drawGojoBody(cx, cy, scale)
        drawGojoFace(cx, cy, scale, eyeGlow, animated)
        drawGojoHair(cx, cy, scale)
    }
}

private fun DrawScope.drawGojoAura(cx: Float, cy: Float, s: Float, alpha: Float, deg: Float, animated: Boolean) {
    val purple = Color(0xFF9B5DE5)
    val blue = Color(0xFF00BBF9)

    repeat(5) { i ->
        val r = (55 + i * 12) * s
        val offset = if (animated) deg + i * 72f else i * 72f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(purple.copy(alpha = alpha * 0.3f), Color.Transparent),
                center = Offset(cx, cy), radius = r
            ),
            radius = r, center = Offset(cx, cy)
        )
    }

    for (i in 0..7) {
        val angle = Math.toRadians((deg + i * 45.0))
        val r = 62 * s
        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(blue.copy(alpha = alpha * 0.6f), Color.Transparent)
            ),
            start = Offset(cx, cy),
            end = Offset(cx + cos(angle).toFloat() * r, cy + sin(angle).toFloat() * r),
            strokeWidth = 2f * s,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawGojoBody(cx: Float, cy: Float, s: Float) {
    val bodyColor = Color(0xFF1A1A2E)
    val darkBlue = Color(0xFF0D0D1F)

    drawRoundRect(
        color = darkBlue,
        topLeft = Offset(cx - 28 * s, cy + 55 * s),
        size = Size(56 * s, 60 * s),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12 * s)
    )
    drawRoundRect(
        color = Color(0xFF222244),
        topLeft = Offset(cx - 22 * s, cy + 52 * s),
        size = Size(44 * s, 10 * s),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(5 * s)
    )

    val leftShoulder = Path().apply {
        moveTo(cx - 28 * s, cy + 62 * s)
        cubicTo(cx - 50 * s, cy + 55 * s, cx - 55 * s, cy + 80 * s, cx - 45 * s, cy + 100 * s)
        lineTo(cx - 28 * s, cy + 95 * s)
        close()
    }
    drawPath(leftShoulder, darkBlue)

    val rightShoulder = Path().apply {
        moveTo(cx + 28 * s, cy + 62 * s)
        cubicTo(cx + 50 * s, cy + 55 * s, cx + 55 * s, cy + 80 * s, cx + 45 * s, cy + 100 * s)
        lineTo(cx + 28 * s, cy + 95 * s)
        close()
    }
    drawPath(rightShoulder, darkBlue)

    drawLine(Color(0xFF9B5DE5).copy(alpha = 0.7f), Offset(cx - 20 * s, cy + 56 * s), Offset(cx + 20 * s, cy + 56 * s), 2f * s)
}

private fun DrawScope.drawGojoFace(cx: Float, cy: Float, s: Float, eyeGlow: Float, animated: Boolean) {
    val skinColor = Color(0xFFFFDDB4)
    val headRadius = 32f * s

    drawCircle(color = skinColor, radius = headRadius, center = Offset(cx, cy + 20 * s))

    val blindfoldPath = Path().apply {
        moveTo(cx - 34 * s, cy + 14 * s)
        cubicTo(cx - 20 * s, cy + 10 * s, cx + 20 * s, cy + 10 * s, cx + 34 * s, cy + 14 * s)
        cubicTo(cx + 34 * s, cy + 22 * s, cx + 20 * s, cy + 26 * s, cx - 20 * s, cy + 26 * s)
        close()
    }
    drawPath(blindfoldPath, Color(0xFF0A0A1A))
    drawPath(blindfoldPath, Color(0xFF9B5DE5).copy(alpha = 0.3f), style = Stroke(1.5f * s))

    drawCircle(
        color = Color(0xFF00BBF9).copy(alpha = eyeGlow * 0.4f),
        radius = 6f * s, center = Offset(cx - 12 * s, cy + 18 * s)
    )
    drawCircle(
        color = Color(0xFF00BBF9).copy(alpha = eyeGlow * 0.4f),
        radius = 6f * s, center = Offset(cx + 12 * s, cy + 18 * s)
    )

    val smilePath = Path().apply {
        moveTo(cx - 10 * s, cy + 34 * s)
        cubicTo(cx - 5 * s, cy + 38 * s, cx + 5 * s, cy + 38 * s, cx + 10 * s, cy + 34 * s)
    }
    drawPath(smilePath, Color(0xFF8B6347), style = Stroke(2f * s, cap = StrokeCap.Round))
}

private fun DrawScope.drawGojoHair(cx: Float, cy: Float, s: Float) {
    val hairColor = Color(0xFFF8F8FF)
    val shadowColor = Color(0xFFD0D0E8)

    val hairPath = Path().apply {
        moveTo(cx - 30 * s, cy + 5 * s)
        cubicTo(cx - 38 * s, cy - 8 * s, cx - 35 * s, cy - 25 * s, cx - 20 * s, cy - 30 * s)
        cubicTo(cx - 5 * s, cy - 38 * s, cx + 5 * s, cy - 38 * s, cx + 20 * s, cy - 30 * s)
        cubicTo(cx + 35 * s, cy - 25 * s, cx + 38 * s, cy - 8 * s, cx + 30 * s, cy + 5 * s)
        cubicTo(cx + 25 * s, cy - 2 * s, cx + 15 * s, cy - 5 * s, cx, cy - 3 * s)
        cubicTo(cx - 15 * s, cy - 5 * s, cx - 25 * s, cy - 2 * s, cx - 30 * s, cy + 5 * s)
        close()
    }
    drawPath(hairPath, hairColor)

    val spikeL = Path().apply {
        moveTo(cx - 30 * s, cy - 5 * s)
        cubicTo(cx - 45 * s, cy - 20 * s, cx - 40 * s, cy - 35 * s, cx - 28 * s, cy - 28 * s)
        close()
    }
    drawPath(spikeL, hairColor)

    val spikeR = Path().apply {
        moveTo(cx + 30 * s, cy - 5 * s)
        cubicTo(cx + 45 * s, cy - 20 * s, cx + 40 * s, cy - 35 * s, cx + 28 * s, cy - 28 * s)
        close()
    }
    drawPath(spikeR, hairColor)

    drawPath(hairPath, shadowColor, style = Stroke(1.5f * s))
}

// ──────────────────────────────────────────────
// YUJI ITADORI  — Divergent Fist (pink hair, scar)
// ──────────────────────────────────────────────
@Composable
fun YujiCharacter(modifier: Modifier = Modifier, animated: Boolean = true) {
    val infiniteTransition = rememberInfiniteTransition(label = "yuji")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.97f, targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    val energyAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(700, easing = LinearEasing), RepeatMode.Reverse),
        label = "energy"
    )

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val scale = size.width / 200f

        scale(if (animated) pulseScale else 1f, pivot = Offset(cx, cy + 20 * scale)) {
            drawYujiAura(cx, cy, scale, energyAlpha, animated)
            drawYujiBody(cx, cy, scale)
            drawYujiFace(cx, cy, scale)
            drawYujiHair(cx, cy, scale)
        }
    }
}

private fun DrawScope.drawYujiAura(cx: Float, cy: Float, s: Float, alpha: Float, animated: Boolean) {
    val pink = Color(0xFFF15BB5)
    val red = Color(0xFFEF233C)

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(red.copy(alpha = alpha * 0.25f), Color.Transparent),
            center = Offset(cx, cy + 20 * s), radius = 75 * s
        ),
        radius = 75 * s, center = Offset(cx, cy + 20 * s)
    )

    for (i in 0..5) {
        val angle = Math.toRadians((i * 60.0 + if (animated) System.currentTimeMillis() * 0.02 % 360 else 0.0))
        val r = 65 * s
        drawLine(
            color = pink.copy(alpha = alpha * 0.5f),
            start = Offset(cx, cy + 20 * s),
            end = Offset(cx + cos(angle).toFloat() * r, cy + 20 * s + sin(angle).toFloat() * r),
            strokeWidth = 3f * s, cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawYujiBody(cx: Float, cy: Float, s: Float) {
    val shirtColor = Color(0xFF2A2A4A)
    val collarColor = Color(0xFF3A3A5A)

    drawRoundRect(
        color = shirtColor,
        topLeft = Offset(cx - 30 * s, cy + 55 * s),
        size = Size(60 * s, 55 * s),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10 * s)
    )

    drawRoundRect(
        color = Color(0xFFF15BB5).copy(alpha = 0.3f),
        topLeft = Offset(cx - 8 * s, cy + 55 * s),
        size = Size(16 * s, 30 * s),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4 * s)
    )

    val leftArm = Path().apply {
        moveTo(cx - 30 * s, cy + 62 * s)
        cubicTo(cx - 55 * s, cy + 58 * s, cx - 60 * s, cy + 85 * s, cx - 50 * s, cy + 100 * s)
        lineTo(cx - 32 * s, cy + 95 * s)
        close()
    }
    drawPath(leftArm, shirtColor)

    val rightArm = Path().apply {
        moveTo(cx + 30 * s, cy + 62 * s)
        cubicTo(cx + 55 * s, cy + 58 * s, cx + 60 * s, cy + 85 * s, cx + 50 * s, cy + 100 * s)
        lineTo(cx + 32 * s, cy + 95 * s)
        close()
    }
    drawPath(rightArm, shirtColor)

    drawCircle(Color(0xFFFFDDB4), 12 * s, Offset(cx - 52 * s, cy + 98 * s))
    drawCircle(Color(0xFFFFDDB4), 12 * s, Offset(cx + 52 * s, cy + 98 * s))

    val pinkMark1 = Path().apply {
        moveTo(cx - 52 * s, cy + 93 * s)
        lineTo(cx - 58 * s, cy + 95 * s)
        lineTo(cx - 55 * s, cy + 100 * s)
        lineTo(cx - 48 * s, cy + 100 * s)
        close()
    }
    drawPath(pinkMark1, Color(0xFFEF233C).copy(alpha = 0.8f))
}

private fun DrawScope.drawYujiFace(cx: Float, cy: Float, s: Float) {
    val skinColor = Color(0xFFFFDDB4)
    val eyeColor = Color(0xFF3A2010)

    drawCircle(color = skinColor, radius = 30f * s, center = Offset(cx, cy + 22 * s))

    drawCircle(Color.White, 7f * s, Offset(cx - 11 * s, cy + 18 * s))
    drawCircle(Color.White, 7f * s, Offset(cx + 11 * s, cy + 18 * s))

    drawCircle(eyeColor, 5f * s, Offset(cx - 11 * s, cy + 18 * s))
    drawCircle(eyeColor, 5f * s, Offset(cx + 11 * s, cy + 18 * s))

    drawCircle(Color.Black, 3f * s, Offset(cx - 11 * s, cy + 18 * s))
    drawCircle(Color.Black, 3f * s, Offset(cx + 11 * s, cy + 18 * s))

    drawCircle(Color.White, 1.5f * s, Offset(cx - 9 * s, cy + 16 * s))
    drawCircle(Color.White, 1.5f * s, Offset(cx + 13 * s, cy + 16 * s))

    val leftBrow = Path().apply {
        moveTo(cx - 17 * s, cy + 10 * s)
        cubicTo(cx - 13 * s, cy + 8 * s, cx - 7 * s, cy + 9 * s, cx - 5 * s, cy + 11 * s)
    }
    drawPath(leftBrow, Color(0xFF3A3020), style = Stroke(2.5f * s, cap = StrokeCap.Round))

    val rightBrow = Path().apply {
        moveTo(cx + 5 * s, cy + 11 * s)
        cubicTo(cx + 7 * s, cy + 9 * s, cx + 13 * s, cy + 8 * s, cx + 17 * s, cy + 10 * s)
    }
    drawPath(rightBrow, Color(0xFF3A3020), style = Stroke(2.5f * s, cap = StrokeCap.Round))

    val smilePath = Path().apply {
        moveTo(cx - 12 * s, cy + 30 * s)
        cubicTo(cx - 6 * s, cy + 36 * s, cx + 6 * s, cy + 36 * s, cx + 12 * s, cy + 30 * s)
    }
    drawPath(smilePath, Color(0xFF8B4513), style = Stroke(2.5f * s, cap = StrokeCap.Round))

    drawOval(
        color = Color(0xFFFF8FA0).copy(alpha = 0.6f),
        topLeft = Offset(cx - 26 * s, cy + 22 * s), size = Size(12 * s, 8 * s)
    )
    drawOval(
        color = Color(0xFFFF8FA0).copy(alpha = 0.6f),
        topLeft = Offset(cx + 14 * s, cy + 22 * s), size = Size(12 * s, 8 * s)
    )

    drawLine(
        color = Color(0xFF8B0000).copy(alpha = 0.6f),
        start = Offset(cx + 4 * s, cy + 19 * s), end = Offset(cx + 10 * s, cy + 23 * s),
        strokeWidth = 1.5f * s, cap = StrokeCap.Round
    )
    drawLine(
        color = Color(0xFF8B0000).copy(alpha = 0.6f),
        start = Offset(cx + 7 * s, cy + 17 * s), end = Offset(cx + 13 * s, cy + 21 * s),
        strokeWidth = 1.5f * s, cap = StrokeCap.Round
    )
}

private fun DrawScope.drawYujiHair(cx: Float, cy: Float, s: Float) {
    val pink = Color(0xFFFF6FA0)
    val darkPink = Color(0xFFCC3366)

    val mainHair = Path().apply {
        moveTo(cx - 28 * s, cy + 8 * s)
        cubicTo(cx - 36 * s, cy - 5 * s, cx - 32 * s, cy - 22 * s, cx - 18 * s, cy - 26 * s)
        cubicTo(cx - 5 * s, cy - 33 * s, cx + 5 * s, cy - 33 * s, cx + 18 * s, cy - 26 * s)
        cubicTo(cx + 32 * s, cy - 22 * s, cx + 36 * s, cy - 5 * s, cx + 28 * s, cy + 8 * s)
        cubicTo(cx + 22 * s, cy + 2 * s, cx + 12 * s, cy - 2 * s, cx, cy)
        cubicTo(cx - 12 * s, cy - 2 * s, cx - 22 * s, cy + 2 * s, cx - 28 * s, cy + 8 * s)
        close()
    }
    drawPath(mainHair, pink)

    val topSpike = Path().apply {
        moveTo(cx - 8 * s, cy - 30 * s)
        cubicTo(cx - 2 * s, cy - 50 * s, cx + 2 * s, cy - 50 * s, cx + 8 * s, cy - 30 * s)
        close()
    }
    drawPath(topSpike, pink)

    val spikeL = Path().apply {
        moveTo(cx - 28 * s, cy)
        cubicTo(cx - 42 * s, cy - 15 * s, cx - 38 * s, cy - 30 * s, cx - 24 * s, cy - 24 * s)
        close()
    }
    drawPath(spikeL, pink)

    val spikeR = Path().apply {
        moveTo(cx + 28 * s, cy)
        cubicTo(cx + 42 * s, cy - 15 * s, cx + 38 * s, cy - 30 * s, cx + 24 * s, cy - 24 * s)
        close()
    }
    drawPath(spikeR, pink)

    drawPath(mainHair, darkPink, style = Stroke(1.5f * s))
}

// ──────────────────────────────────────────────
// MEGUMI FUSHIGURO — Ten Shadows (dark, stoic)
// ──────────────────────────────────────────────
@Composable
fun MegumiCharacter(modifier: Modifier = Modifier, animated: Boolean = true) {
    val infiniteTransition = rememberInfiniteTransition(label = "megumi")
    val shadowPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "shadow"
    )

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val s = size.width / 200f

        drawMegumiShadows(cx, cy, s, shadowPulse, animated)
        drawMegumiBody(cx, cy, s)
        drawMegumiFace(cx, cy, s)
        drawMegumiHair(cx, cy, s)
    }
}

private fun DrawScope.drawMegumiShadows(cx: Float, cy: Float, s: Float, alpha: Float, animated: Boolean) {
    val shadowBlue = Color(0xFF4CC9F0)
    val darkBlue = Color(0xFF0D1B2A)

    repeat(3) { i ->
        val r = (45 + i * 15) * s
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(shadowBlue.copy(alpha = alpha * 0.2f), Color.Transparent),
                center = Offset(cx, cy + 20 * s), radius = r
            ),
            radius = r, center = Offset(cx, cy + 20 * s)
        )
    }

    val shikigamiPoints = listOf(-40f to 80f, 40f to 80f, -50f to 50f, 50f to 50f)
    shikigamiPoints.forEach { (dx, dy) ->
        drawCircle(
            color = shadowBlue.copy(alpha = alpha),
            radius = 5 * s,
            center = Offset(cx + dx * s, cy + dy * s)
        )
    }
}

private fun DrawScope.drawMegumiBody(cx: Float, cy: Float, s: Float) {
    val uniformColor = Color(0xFF1A2035)
    val collarColor = Color(0xFF0D1525)
    val buttonColor = Color(0xFF2A3A5A)

    drawRoundRect(
        color = uniformColor,
        topLeft = Offset(cx - 28 * s, cy + 55 * s),
        size = Size(56 * s, 58 * s),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10 * s)
    )

    val collarPath = Path().apply {
        moveTo(cx - 12 * s, cy + 55 * s)
        lineTo(cx, cy + 68 * s)
        lineTo(cx + 12 * s, cy + 55 * s)
    }
    drawPath(collarPath, collarColor)
    drawPath(collarPath, Color(0xFF4CC9F0).copy(alpha = 0.4f), style = Stroke(1.5f * s))

    for (i in 0..3) {
        drawCircle(buttonColor, 2 * s, Offset(cx, cy + (62 + i * 10) * s))
    }

    val leftArm = Path().apply {
        moveTo(cx - 28 * s, cy + 60 * s)
        cubicTo(cx - 48 * s, cy + 55 * s, cx - 52 * s, cy + 80 * s, cx - 44 * s, cy + 98 * s)
        lineTo(cx - 28 * s, cy + 93 * s)
        close()
    }
    drawPath(leftArm, uniformColor)

    val rightArm = Path().apply {
        moveTo(cx + 28 * s, cy + 60 * s)
        cubicTo(cx + 48 * s, cy + 55 * s, cx + 52 * s, cy + 80 * s, cx + 44 * s, cy + 98 * s)
        lineTo(cx + 28 * s, cy + 93 * s)
        close()
    }
    drawPath(rightArm, uniformColor)
}

private fun DrawScope.drawMegumiFace(cx: Float, cy: Float, s: Float) {
    val skinColor = Color(0xFFFFDDB4)

    drawCircle(color = skinColor, radius = 30f * s, center = Offset(cx, cy + 22 * s))

    drawCircle(Color.White, 7f * s, Offset(cx - 11 * s, cy + 18 * s))
    drawCircle(Color.White, 7f * s, Offset(cx + 11 * s, cy + 18 * s))

    drawCircle(Color(0xFF1A2040), 6f * s, Offset(cx - 11 * s, cy + 18 * s))
    drawCircle(Color(0xFF1A2040), 6f * s, Offset(cx + 11 * s, cy + 18 * s))

    drawCircle(Color(0xFF4CC9F0), 3f * s, Offset(cx - 11 * s, cy + 18 * s))
    drawCircle(Color(0xFF4CC9F0), 3f * s, Offset(cx + 11 * s, cy + 18 * s))
    drawCircle(Color.Black, 2f * s, Offset(cx - 11 * s, cy + 18 * s))
    drawCircle(Color.Black, 2f * s, Offset(cx + 11 * s, cy + 18 * s))
    drawCircle(Color.White, 1f * s, Offset(cx - 9.5f * s, cy + 16.5f * s))
    drawCircle(Color.White, 1f * s, Offset(cx + 12.5f * s, cy + 16.5f * s))

    val leftBrow = Path().apply {
        moveTo(cx - 18 * s, cy + 9 * s)
        lineTo(cx - 5 * s, cy + 10 * s)
    }
    drawPath(leftBrow, Color(0xFF1A1A2A), style = Stroke(3f * s, cap = StrokeCap.Round))

    val rightBrow = Path().apply {
        moveTo(cx + 5 * s, cy + 10 * s)
        lineTo(cx + 18 * s, cy + 9 * s)
    }
    drawPath(rightBrow, Color(0xFF1A1A2A), style = Stroke(3f * s, cap = StrokeCap.Round))

    drawLine(
        color = Color(0xFF8B6347),
        start = Offset(cx - 8 * s, cy + 32 * s),
        end = Offset(cx + 8 * s, cy + 32 * s),
        strokeWidth = 2f * s, cap = StrokeCap.Round
    )
}

private fun DrawScope.drawMegumiHair(cx: Float, cy: Float, s: Float) {
    val darkHair = Color(0xFF1A2040)
    val blueHighlight = Color(0xFF2A3560)

    val mainHair = Path().apply {
        moveTo(cx - 30 * s, cy + 6 * s)
        cubicTo(cx - 38 * s, cy - 8 * s, cx - 34 * s, cy - 24 * s, cx - 18 * s, cy - 28 * s)
        cubicTo(cx - 5 * s, cy - 35 * s, cx + 5 * s, cy - 35 * s, cx + 18 * s, cy - 28 * s)
        cubicTo(cx + 34 * s, cy - 24 * s, cx + 38 * s, cy - 8 * s, cx + 30 * s, cy + 6 * s)
        close()
    }
    drawPath(mainHair, darkHair)

    val swooshPath = Path().apply {
        moveTo(cx - 30 * s, cy + 6 * s)
        cubicTo(cx - 20 * s, cy - 5 * s, cx + 5 * s, cy - 5 * s, cx + 25 * s, cy - 15 * s)
        cubicTo(cx + 30 * s, cy - 18 * s, cx + 32 * s, cy - 10 * s, cx + 28 * s, cy - 5 * s)
    }
    drawPath(swooshPath, blueHighlight, style = Stroke(4f * s, cap = StrokeCap.Round))

    val sideL = Path().apply {
        moveTo(cx - 30 * s, cy + 6 * s)
        cubicTo(cx - 40 * s, cy + 15 * s, cx - 38 * s, cy + 30 * s, cx - 32 * s, cy + 38 * s)
        lineTo(cx - 28 * s, cy + 35 * s)
        cubicTo(cx - 30 * s, cy + 22 * s, cx - 28 * s, cy + 10 * s, cx - 28 * s, cy + 8 * s)
        close()
    }
    drawPath(sideL, darkHair)

    val sideR = Path().apply {
        moveTo(cx + 30 * s, cy + 6 * s)
        cubicTo(cx + 40 * s, cy + 15 * s, cx + 38 * s, cy + 30 * s, cx + 32 * s, cy + 38 * s)
        lineTo(cx + 28 * s, cy + 35 * s)
        cubicTo(cx + 30 * s, cy + 22 * s, cx + 28 * s, cy + 10 * s, cx + 28 * s, cy + 8 * s)
        close()
    }
    drawPath(sideR, darkHair)

    drawPath(mainHair, Color(0xFF2A3560), style = Stroke(1.5f * s))
}

// ──────────────────────────────────────────────
// NOBARA KUGISAKI — Straw Doll (nail & hammer)
// ──────────────────────────────────────────────
@Composable
fun NobaraCharacter(modifier: Modifier = Modifier, animated: Boolean = true) {
    val infiniteTransition = rememberInfiniteTransition(label = "nobara")
    val hammerSwing by infiniteTransition.animateFloat(
        initialValue = -15f, targetValue = 15f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "hammer"
    )
    val fireAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "fire"
    )

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val s = size.width / 200f

        drawNobaraAura(cx, cy, s, fireAlpha, animated)
        drawNobaraBody(cx, cy, s)
        drawNobaraHammer(cx, cy, s, hammerSwing, animated)
        drawNobaraFace(cx, cy, s)
        drawNobaraHair(cx, cy, s)
    }
}

private fun DrawScope.drawNobaraAura(cx: Float, cy: Float, s: Float, alpha: Float, animated: Boolean) {
    val orange = Color(0xFFFF9F1C)

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(orange.copy(alpha = alpha * 0.25f), Color.Transparent),
            center = Offset(cx, cy + 20 * s), radius = 70 * s
        ),
        radius = 70 * s, center = Offset(cx, cy + 20 * s)
    )

    for (i in 0..5) {
        val angle = Math.toRadians((i * 60.0))
        val r = 60 * s
        drawLine(
            color = orange.copy(alpha = alpha * 0.4f),
            start = Offset(cx + cos(angle).toFloat() * 30 * s, cy + 20 * s + sin(angle).toFloat() * 30 * s),
            end = Offset(cx + cos(angle).toFloat() * r, cy + 20 * s + sin(angle).toFloat() * r),
            strokeWidth = 2.5f * s, cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawNobaraBody(cx: Float, cy: Float, s: Float) {
    val uniformColor = Color(0xFF2A2040)
    val trimColor = Color(0xFFB5651D)

    drawRoundRect(
        color = uniformColor,
        topLeft = Offset(cx - 26 * s, cy + 55 * s),
        size = Size(52 * s, 55 * s),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10 * s)
    )

    drawLine(trimColor.copy(alpha = 0.6f), Offset(cx - 26 * s, cy + 60 * s), Offset(cx + 26 * s, cy + 60 * s), 3 * s)
    drawLine(trimColor.copy(alpha = 0.6f), Offset(cx, cy + 60 * s), Offset(cx, cy + 110 * s), 2 * s)

    val leftArm = Path().apply {
        moveTo(cx - 26 * s, cy + 62 * s)
        cubicTo(cx - 46 * s, cy + 58 * s, cx - 50 * s, cy + 82 * s, cx - 42 * s, cy + 98 * s)
        lineTo(cx - 26 * s, cy + 93 * s)
        close()
    }
    drawPath(leftArm, uniformColor)

    val rightArm = Path().apply {
        moveTo(cx + 26 * s, cy + 62 * s)
        cubicTo(cx + 46 * s, cy + 58 * s, cx + 50 * s, cy + 82 * s, cx + 42 * s, cy + 98 * s)
        lineTo(cx + 26 * s, cy + 93 * s)
        close()
    }
    drawPath(rightArm, uniformColor)

    drawCircle(Color(0xFFFFDDB4), 11 * s, Offset(cx - 43 * s, cy + 97 * s))
    drawCircle(Color(0xFFFFDDB4), 11 * s, Offset(cx + 43 * s, cy + 97 * s))
}

private fun DrawScope.drawNobaraHammer(cx: Float, cy: Float, s: Float, swing: Float, animated: Boolean) {
    val hammerAngle = if (animated) swing else 10f
    val hammerCx = cx + 50 * s
    val hammerCy = cy + 70 * s

    rotate(hammerAngle, pivot = Offset(cx + 42 * s, cy + 97 * s)) {
        drawRoundRect(
            color = Color(0xFF8B6347),
            topLeft = Offset(hammerCx - 5 * s, hammerCy - 30 * s),
            size = Size(10 * s, 40 * s),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3 * s)
        )

        drawRoundRect(
            color = Color(0xFF555555),
            topLeft = Offset(hammerCx - 14 * s, hammerCy - 42 * s),
            size = Size(28 * s, 18 * s),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4 * s)
        )

        drawLine(
            color = Color(0xFFFF9F1C).copy(alpha = 0.7f),
            start = Offset(hammerCx, hammerCy - 44 * s),
            end = Offset(hammerCx, hammerCy - 60 * s),
            strokeWidth = 3 * s, cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawNobaraFace(cx: Float, cy: Float, s: Float) {
    val skinColor = Color(0xFFFFDDB4)

    drawCircle(color = skinColor, radius = 29f * s, center = Offset(cx, cy + 22 * s))

    drawCircle(Color.White, 6.5f * s, Offset(cx - 10 * s, cy + 19 * s))
    drawCircle(Color.White, 6.5f * s, Offset(cx + 10 * s, cy + 19 * s))

    drawCircle(Color(0xFF5A3010), 5f * s, Offset(cx - 10 * s, cy + 19 * s))
    drawCircle(Color(0xFF5A3010), 5f * s, Offset(cx + 10 * s, cy + 19 * s))
    drawCircle(Color.Black, 3f * s, Offset(cx - 10 * s, cy + 19 * s))
    drawCircle(Color.Black, 3f * s, Offset(cx + 10 * s, cy + 19 * s))
    drawCircle(Color.White, 1.5f * s, Offset(cx - 8.5f * s, cy + 17.5f * s))
    drawCircle(Color.White, 1.5f * s, Offset(cx + 11.5f * s, cy + 17.5f * s))

    val leftBrow = Path().apply {
        moveTo(cx - 17 * s, cy + 11 * s)
        cubicTo(cx - 12 * s, cy + 9 * s, cx - 6 * s, cy + 9 * s, cx - 4 * s, cy + 12 * s)
    }
    drawPath(leftBrow, Color(0xFF5A3010), style = Stroke(2.5f * s, cap = StrokeCap.Round))

    val rightBrow = Path().apply {
        moveTo(cx + 4 * s, cy + 12 * s)
        cubicTo(cx + 6 * s, cy + 9 * s, cx + 12 * s, cy + 9 * s, cx + 17 * s, cy + 11 * s)
    }
    drawPath(rightBrow, Color(0xFF5A3010), style = Stroke(2.5f * s, cap = StrokeCap.Round))

    val smilePath = Path().apply {
        moveTo(cx - 11 * s, cy + 30 * s)
        cubicTo(cx - 5 * s, cy + 36 * s, cx + 5 * s, cy + 36 * s, cx + 11 * s, cy + 30 * s)
    }
    drawPath(smilePath, Color(0xFF8B4513), style = Stroke(2.5f * s, cap = StrokeCap.Round))

    drawOval(color = Color(0xFFFF8FA0).copy(alpha = 0.5f), topLeft = Offset(cx - 25 * s, cy + 23 * s), size = Size(12 * s, 7 * s))
    drawOval(color = Color(0xFFFF8FA0).copy(alpha = 0.5f), topLeft = Offset(cx + 13 * s, cy + 23 * s), size = Size(12 * s, 7 * s))
}

private fun DrawScope.drawNobaraHair(cx: Float, cy: Float, s: Float) {
    val auburn = Color(0xFFB5651D)
    val darkAuburn = Color(0xFF8B4513)

    val mainHair = Path().apply {
        moveTo(cx - 28 * s, cy + 8 * s)
        cubicTo(cx - 36 * s, cy - 5 * s, cx - 32 * s, cy - 22 * s, cx - 16 * s, cy - 26 * s)
        cubicTo(cx - 5 * s, cy - 33 * s, cx + 5 * s, cy - 33 * s, cx + 16 * s, cy - 26 * s)
        cubicTo(cx + 32 * s, cy - 22 * s, cx + 36 * s, cy - 5 * s, cx + 28 * s, cy + 8 * s)
        close()
    }
    drawPath(mainHair, auburn)

    val bunPath = Path().apply {
        moveTo(cx - 15 * s, cy - 24 * s)
        cubicTo(cx - 10 * s, cy - 50 * s, cx + 10 * s, cy - 50 * s, cx + 15 * s, cy - 24 * s)
        close()
    }
    drawPath(bunPath, auburn)
    drawCircle(auburn, 16 * s, Offset(cx, cy - 42 * s))
    drawCircle(darkAuburn, 16 * s, Offset(cx, cy - 42 * s), style = Stroke(2f * s))

    val sideL = Path().apply {
        moveTo(cx - 28 * s, cy + 8 * s)
        cubicTo(cx - 38 * s, cy + 20 * s, cx - 36 * s, cy + 35 * s, cx - 30 * s, cy + 42 * s)
        lineTo(cx - 26 * s, cy + 38 * s)
        cubicTo(cx - 28 * s, cy + 25 * s, cx - 26 * s, cy + 12 * s, cx - 26 * s, cy + 8 * s)
        close()
    }
    drawPath(sideL, auburn)

    val sideR = Path().apply {
        moveTo(cx + 28 * s, cy + 8 * s)
        cubicTo(cx + 38 * s, cy + 20 * s, cx + 36 * s, cy + 35 * s, cx + 30 * s, cy + 42 * s)
        lineTo(cx + 26 * s, cy + 38 * s)
        cubicTo(cx + 28 * s, cy + 25 * s, cx + 26 * s, cy + 12 * s, cx + 26 * s, cy + 8 * s)
        close()
    }
    drawPath(sideR, auburn)

    drawPath(mainHair, darkAuburn, style = Stroke(1.5f * s))
}
