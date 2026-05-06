package com.fitai.tracker.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin

// ─────────────────────────────────────────────────────────────
//  CHIBI ANIME-STICKER CHARACTERS — SOCCER ATHLETES
//
//  Inspired by sticker-style anime chibis: oversized round head,
//  small body with a numbered soccer jersey + shorts + tiny shoes,
//  detailed multi-strand hair, sparkly anime eyes, and a soft
//  white halo behind the figure (the "sticker" border).
//
//  The whole roster shares a single soccer-team identity —
//  blue jersey + white trim — so they read as the FitAI club.
//
//  Coordinate system: each character draws into a 200×200 logical
//  canvas. `s = size.width / 200f` scales everything to whatever
//  size the caller passes in.
// ─────────────────────────────────────────────────────────────

private object Palette {
    val Skin = Color(0xFFFFE0C5)
    val SkinShade = Color(0xFFEFC09F)
    val EyeWhite = Color(0xFFFFFFFF)
    val MouthDark = Color(0xFF5A2E1E)
    val Tongue = Color(0xFFFF7BA0)
    val Blush = Color(0x8AFF8FA8)
    val Outline = Color(0xFF0A0E1F)
    val OutlineSoft = Color(0x66131C36)

    // Team kit
    val JerseyBlue = Color(0xFF1E63E0)
    val JerseyBlueDark = Color(0xFF12459C)
    val JerseyTrim = Color(0xFFFFFFFF)
    val ShortsBlue = Color(0xFF0E2D6E)
    val ShoeBlack = Color(0xFF0B1224)
    val ShoeWhite = Color(0xFFFFFFFF)
}

// ─── Halo / shadow / sparkle helpers ────────────────────────

/** White rounded "sticker" halo behind the character. */
private fun DrawScope.drawStickerHalo(cx: Float, cy: Float, s: Float, alpha: Float = 1f) {
    drawRoundRect(
        color = Color.White.copy(alpha = 0.10f * alpha),
        topLeft = Offset(cx - 80 * s, cy - 92 * s),
        size = Size(160 * s, 188 * s),
        cornerRadius = CornerRadius(40 * s, 40 * s)
    )
}

/** Soft drop shadow that sits just below the character's feet. */
private fun DrawScope.drawGroundShadow(cx: Float, cy: Float, s: Float) {
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x88000000), Color.Transparent),
            center = Offset(cx, cy + 84 * s),
            radius = 36 * s
        ),
        topLeft = Offset(cx - 36 * s, cy + 76 * s),
        size = Size(72 * s, 16 * s)
    )
}

/** Sparkle stars (4-pointed) around the figure. */
private fun DrawScope.drawSparkles(cx: Float, cy: Float, s: Float, color: Color, alpha: Float) {
    fun fourStar(px: Float, py: Float, r: Float) {
        val path = Path().apply {
            moveTo(px, py - r)
            lineTo(px + r * 0.30f, py - r * 0.30f)
            lineTo(px + r, py)
            lineTo(px + r * 0.30f, py + r * 0.30f)
            lineTo(px, py + r)
            lineTo(px - r * 0.30f, py + r * 0.30f)
            lineTo(px - r, py)
            lineTo(px - r * 0.30f, py - r * 0.30f)
            close()
        }
        drawPath(path, color.copy(alpha = alpha))
    }
    fourStar(cx - 70 * s, cy - 60 * s, 5 * s)
    fourStar(cx + 76 * s, cy - 30 * s, 4 * s)
    fourStar(cx + 66 * s, cy + 26 * s, 3 * s)
    fourStar(cx - 78 * s, cy + 6 * s, 3.5f * s)
}

/** Soccer ball sitting on top of a head. */
private fun DrawScope.drawSoccerBall(cx: Float, cy: Float, s: Float, scale: Float = 1f) {
    val r = 16 * s * scale
    drawCircle(Color.White, r, Offset(cx, cy))
    drawCircle(Palette.Outline, r, Offset(cx, cy), style = Stroke(1.5f * s))

    // black pentagon top, plus simple seams
    val center = Path().apply {
        val r2 = r * 0.45f
        for (i in 0..4) {
            val a = Math.toRadians((i * 72.0 - 90.0))
            val px = cx + cos(a).toFloat() * r2
            val py = cy + sin(a).toFloat() * r2
            if (i == 0) moveTo(px, py) else lineTo(px, py)
        }
        close()
    }
    drawPath(center, Palette.Outline)

    for (i in 0..4) {
        val a = Math.toRadians((i * 72.0 - 90.0))
        val r2 = r * 0.45f
        val r3 = r * 0.92f
        drawLine(
            color = Palette.Outline,
            start = Offset(cx + cos(a).toFloat() * r2, cy + sin(a).toFloat() * r2),
            end = Offset(cx + cos(a).toFloat() * r3, cy + sin(a).toFloat() * r3),
            strokeWidth = 1.4f * s, cap = StrokeCap.Round
        )
    }
}

// ─── Body parts ─────────────────────────────────────────────

private fun DrawScope.drawHead(cx: Float, cy: Float, s: Float) {
    // soft cheek shadow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x33000000), Color.Transparent),
            center = Offset(cx, cy + 16 * s),
            radius = 38 * s
        ),
        radius = 38 * s,
        center = Offset(cx, cy + 16 * s)
    )
    // head fill + outline
    drawCircle(Palette.Skin, 36f * s, Offset(cx, cy))
    drawCircle(Palette.Outline, 36f * s, Offset(cx, cy), style = Stroke(1.2f * s))
    // chin shading
    drawCircle(Palette.SkinShade.copy(alpha = 0.45f), 6f * s, Offset(cx - 22 * s, cy + 12 * s))
    drawCircle(Palette.SkinShade.copy(alpha = 0.45f), 6f * s, Offset(cx + 22 * s, cy + 12 * s))
}

/** Standard rounded anime eye with two highlights. */
private fun DrawScope.drawAnimeEye(
    cx: Float,
    cy: Float,
    s: Float,
    iris: Color,
    pupilDark: Color = Color(0xFF050B16)
) {
    val w = 9f * s
    val h = 12f * s
    // eye outline (slightly squared anime shape)
    drawOval(Palette.Outline, Offset(cx - w, cy - h), Size(w * 2, h * 2))
    // sclera
    drawOval(Palette.EyeWhite, Offset(cx - w + 1f * s, cy - h + 1.4f * s), Size((w - 1f * s) * 2, (h - 1.4f * s) * 2))
    // iris gradient
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(iris, iris.copy(alpha = 0.65f), pupilDark),
            center = Offset(cx - 1f * s, cy - 1f * s),
            radius = w * 1.1f
        ),
        radius = w * 0.9f,
        center = Offset(cx, cy)
    )
    // pupil
    drawCircle(pupilDark, w * 0.45f, Offset(cx, cy + 0.6f * s))
    // big highlight
    drawCircle(Color.White, w * 0.36f, Offset(cx + 2.6f * s, cy - 3 * s))
    // small bottom highlight
    drawCircle(Color.White.copy(alpha = 0.85f), w * 0.18f, Offset(cx - 3 * s, cy + 4 * s))
}

/** Excited 4-point star eye with a small starburst inside. */
private fun DrawScope.drawStarEye(cx: Float, cy: Float, s: Float, iris: Color) {
    val w = 9f * s
    val h = 12f * s
    drawOval(Palette.Outline, Offset(cx - w, cy - h), Size(w * 2, h * 2))
    drawOval(Palette.EyeWhite, Offset(cx - w + 1f * s, cy - h + 1.4f * s), Size((w - 1f * s) * 2, (h - 1.4f * s) * 2))
    drawCircle(iris, w * 0.85f, Offset(cx, cy))

    // 4-point star
    val r = w * 0.7f
    val starPath = Path().apply {
        moveTo(cx, cy - r)
        lineTo(cx + r * 0.32f, cy - r * 0.32f)
        lineTo(cx + r, cy)
        lineTo(cx + r * 0.32f, cy + r * 0.32f)
        lineTo(cx, cy + r)
        lineTo(cx - r * 0.32f, cy + r * 0.32f)
        lineTo(cx - r, cy)
        lineTo(cx - r * 0.32f, cy - r * 0.32f)
        close()
    }
    drawPath(starPath, Color.White)
    // small inner highlight
    drawCircle(Color.White, w * 0.18f, Offset(cx + 2.5f * s, cy - 2.5f * s))
}

private fun DrawScope.drawSmile(cx: Float, cy: Float, s: Float, width: Float = 9f) {
    val path = Path().apply {
        moveTo(cx - width * s, cy)
        cubicTo(cx - width * 0.4f * s, cy + 4f * s, cx + width * 0.4f * s, cy + 4f * s, cx + width * s, cy)
    }
    drawPath(path, Palette.MouthDark, style = Stroke(2.2f * s, cap = StrokeCap.Round))
}

private fun DrawScope.drawOpenMouth(cx: Float, cy: Float, s: Float) {
    val mouthPath = Path().apply {
        moveTo(cx - 8 * s, cy - 1 * s)
        cubicTo(cx - 6 * s, cy + 8 * s, cx + 6 * s, cy + 8 * s, cx + 8 * s, cy - 1 * s)
        cubicTo(cx + 6 * s, cy - 3 * s, cx - 6 * s, cy - 3 * s, cx - 8 * s, cy - 1 * s)
        close()
    }
    drawPath(mouthPath, Palette.MouthDark)
    drawPath(mouthPath, Palette.Outline, style = Stroke(1.4f * s))
    // tongue
    drawOval(
        Palette.Tongue,
        topLeft = Offset(cx - 5 * s, cy + 1 * s),
        size = Size(10 * s, 5 * s)
    )
}

private fun DrawScope.drawBlush(cx: Float, cy: Float, s: Float) {
    drawOval(
        Palette.Blush,
        topLeft = Offset(cx - 28 * s, cy + 4 * s),
        size = Size(12 * s, 6 * s)
    )
    drawOval(
        Palette.Blush,
        topLeft = Offset(cx + 16 * s, cy + 4 * s),
        size = Size(12 * s, 6 * s)
    )
}

/** Soccer kit: jersey with number, shorts, legs, and tiny shoes. */
private fun DrawScope.drawTeamKit(
    cx: Float,
    cy: Float,
    s: Float,
    jerseyNumber: String,
    jerseyAccent: Color = Palette.JerseyBlueDark
) {
    val jerseyTop = cy + 28 * s
    val jerseyBottom = cy + 64 * s
    val shortsBottom = cy + 80 * s

    // jersey body — slightly trapezoidal
    val jersey = Path().apply {
        moveTo(cx - 26 * s, jerseyTop)
        cubicTo(cx - 30 * s, jerseyTop + 6 * s, cx - 32 * s, jerseyBottom - 8 * s, cx - 28 * s, jerseyBottom)
        lineTo(cx + 28 * s, jerseyBottom)
        cubicTo(cx + 32 * s, jerseyBottom - 8 * s, cx + 30 * s, jerseyTop + 6 * s, cx + 26 * s, jerseyTop)
        // V-neck
        cubicTo(cx + 18 * s, jerseyTop + 2 * s, cx + 8 * s, jerseyTop + 6 * s, cx, jerseyTop + 12 * s)
        cubicTo(cx - 8 * s, jerseyTop + 6 * s, cx - 18 * s, jerseyTop + 2 * s, cx - 26 * s, jerseyTop)
        close()
    }
    drawPath(jersey, Palette.JerseyBlue)
    drawPath(jersey, Palette.Outline, style = Stroke(1.4f * s))

    // shoulder shading
    val shade = Path().apply {
        moveTo(cx - 26 * s, jerseyTop + 2 * s)
        cubicTo(cx - 30 * s, jerseyTop + 8 * s, cx - 32 * s, jerseyBottom - 8 * s, cx - 28 * s, jerseyBottom - 4 * s)
        lineTo(cx - 22 * s, jerseyBottom - 6 * s)
        cubicTo(cx - 24 * s, jerseyBottom - 14 * s, cx - 24 * s, jerseyTop + 12 * s, cx - 20 * s, jerseyTop + 6 * s)
        close()
    }
    drawPath(shade, jerseyAccent.copy(alpha = 0.55f))

    // V-neck trim
    val neckTrim = Path().apply {
        moveTo(cx - 14 * s, jerseyTop + 2 * s)
        cubicTo(cx - 8 * s, jerseyTop + 5 * s, cx + 8 * s, jerseyTop + 5 * s, cx + 14 * s, jerseyTop + 2 * s)
    }
    drawPath(neckTrim, Palette.JerseyTrim, style = Stroke(2.2f * s, cap = StrokeCap.Round))

    // sleeve trim — small bands at the shoulders
    drawLine(
        Palette.JerseyTrim,
        Offset(cx - 28 * s, jerseyTop + 8 * s),
        Offset(cx - 18 * s, jerseyTop + 6 * s),
        strokeWidth = 2 * s, cap = StrokeCap.Round
    )
    drawLine(
        Palette.JerseyTrim,
        Offset(cx + 18 * s, jerseyTop + 6 * s),
        Offset(cx + 28 * s, jerseyTop + 8 * s),
        strokeWidth = 2 * s, cap = StrokeCap.Round
    )

    // jersey number
    val numCenterY = (jerseyTop + jerseyBottom) / 2 + 2 * s
    drawNumber(jerseyNumber, cx, numCenterY, s, Palette.JerseyTrim)

    // shorts
    val shorts = Path().apply {
        moveTo(cx - 28 * s, jerseyBottom)
        lineTo(cx + 28 * s, jerseyBottom)
        cubicTo(cx + 30 * s, shortsBottom - 4 * s, cx + 16 * s, shortsBottom, cx + 8 * s, shortsBottom)
        lineTo(cx + 4 * s, jerseyBottom + 4 * s)
        lineTo(cx - 4 * s, jerseyBottom + 4 * s)
        lineTo(cx - 8 * s, shortsBottom)
        cubicTo(cx - 16 * s, shortsBottom, cx - 30 * s, shortsBottom - 4 * s, cx - 28 * s, jerseyBottom)
        close()
    }
    drawPath(shorts, Palette.ShortsBlue)
    drawPath(shorts, Palette.Outline, style = Stroke(1.4f * s))

    // legs (just tiny calves between shorts and shoes)
    drawRoundRect(
        Palette.Skin,
        topLeft = Offset(cx - 16 * s, shortsBottom),
        size = Size(8 * s, 6 * s),
        cornerRadius = CornerRadius(2 * s, 2 * s)
    )
    drawRoundRect(
        Palette.Skin,
        topLeft = Offset(cx + 8 * s, shortsBottom),
        size = Size(8 * s, 6 * s),
        cornerRadius = CornerRadius(2 * s, 2 * s)
    )

    // shoes
    val shoeY = shortsBottom + 5 * s
    drawShoe(cx - 12 * s, shoeY, s, mirror = false)
    drawShoe(cx + 12 * s, shoeY, s, mirror = true)

    // arms hanging at sides
    drawArm(cx - 30 * s, jerseyTop + 4 * s, s, mirror = false)
    drawArm(cx + 30 * s, jerseyTop + 4 * s, s, mirror = true)
}

private fun DrawScope.drawArm(px: Float, py: Float, s: Float, mirror: Boolean) {
    val side = if (mirror) -1f else 1f
    val arm = Path().apply {
        moveTo(px, py)
        cubicTo(
            px - 8 * s * side, py + 10 * s,
            px - 6 * s * side, py + 26 * s,
            px - 2 * s * side, py + 30 * s
        )
        lineTo(px + 4 * s * side, py + 28 * s)
        cubicTo(
            px + 2 * s * side, py + 18 * s,
            px + 6 * s * side, py + 6 * s,
            px + 4 * s * side, py
        )
        close()
    }
    drawPath(arm, Palette.JerseyBlue)
    drawPath(arm, Palette.Outline, style = Stroke(1.2f * s))

    // hand (skin circle)
    drawCircle(Palette.Skin, 4.6f * s, Offset(px - 2 * s * side, py + 32 * s))
    drawCircle(Palette.Outline, 4.6f * s, Offset(px - 2 * s * side, py + 32 * s), style = Stroke(1.0f * s))
}

private fun DrawScope.drawShoe(cx: Float, cy: Float, s: Float, mirror: Boolean) {
    val side = if (mirror) -1f else 1f
    val shoe = Path().apply {
        moveTo(cx - 10 * s, cy)
        cubicTo(cx - 12 * s, cy + 6 * s, cx + 14 * s * side, cy + 8 * s, cx + 14 * s * side, cy + 4 * s)
        cubicTo(cx + 14 * s * side, cy + 1 * s, cx + 8 * s * side, cy - 1 * s, cx, cy - 1 * s)
        lineTo(cx - 10 * s, cy)
        close()
    }
    drawPath(shoe, Palette.ShoeBlack)
    drawPath(shoe, Palette.Outline, style = Stroke(1.2f * s))
    // sole
    drawLine(
        Palette.ShoeWhite,
        start = Offset(cx - 10 * s, cy + 5 * s),
        end = Offset(cx + 13 * s * side, cy + 6 * s),
        strokeWidth = 1.6f * s, cap = StrokeCap.Round
    )
}

/** Tiny stylised number rendered as filled paths so we don't need a font. */
private fun DrawScope.drawNumber(num: String, cx: Float, cy: Float, s: Float, color: Color) {
    val w = 10f * s
    val h = 14f * s
    val stroke = 2.6f * s

    // digits centred horizontally as a group
    val digits = num.toCharArray()
    val totalWidth = digits.size * w + (digits.size - 1) * 2 * s
    var x = cx - totalWidth / 2 + w / 2
    digits.forEach { d ->
        drawDigit(d, x, cy, w, h, stroke, color)
        x += w + 2 * s
    }
}

private fun DrawScope.drawDigit(d: Char, cx: Float, cy: Float, w: Float, h: Float, stroke: Float, color: Color) {
    val left = cx - w / 2
    val right = cx + w / 2
    val top = cy - h / 2
    val bottom = cy + h / 2
    val mid = cy
    fun seg(a: Offset, b: Offset) = drawLine(color, a, b, strokeWidth = stroke, cap = StrokeCap.Round)
    when (d) {
        '0' -> {
            drawOval(color, Offset(left, top), Size(w, h), style = Stroke(stroke))
        }
        '1' -> seg(Offset(cx, top), Offset(cx, bottom))
        '2' -> {
            seg(Offset(left, top), Offset(right, top))
            seg(Offset(right, top), Offset(right, mid))
            seg(Offset(right, mid), Offset(left, mid))
            seg(Offset(left, mid), Offset(left, bottom))
            seg(Offset(left, bottom), Offset(right, bottom))
        }
        '3' -> {
            seg(Offset(left, top), Offset(right, top))
            seg(Offset(right, top), Offset(right, bottom))
            seg(Offset(left, mid), Offset(right, mid))
            seg(Offset(left, bottom), Offset(right, bottom))
        }
        '4' -> {
            seg(Offset(left, top), Offset(left, mid))
            seg(Offset(left, mid), Offset(right, mid))
            seg(Offset(right, top), Offset(right, bottom))
        }
        '5' -> {
            seg(Offset(left, top), Offset(right, top))
            seg(Offset(left, top), Offset(left, mid))
            seg(Offset(left, mid), Offset(right, mid))
            seg(Offset(right, mid), Offset(right, bottom))
            seg(Offset(left, bottom), Offset(right, bottom))
        }
        '6' -> {
            seg(Offset(right, top), Offset(left, top))
            seg(Offset(left, top), Offset(left, bottom))
            seg(Offset(left, bottom), Offset(right, bottom))
            seg(Offset(right, bottom), Offset(right, mid))
            seg(Offset(right, mid), Offset(left, mid))
        }
        '7' -> {
            seg(Offset(left, top), Offset(right, top))
            seg(Offset(right, top), Offset(left, bottom))
        }
        '8' -> {
            drawOval(color, Offset(left, top), Size(w, h), style = Stroke(stroke))
            seg(Offset(left, mid), Offset(right, mid))
        }
        '9' -> {
            seg(Offset(right, mid), Offset(left, mid))
            seg(Offset(left, mid), Offset(left, top))
            seg(Offset(left, top), Offset(right, top))
            seg(Offset(right, top), Offset(right, bottom))
            seg(Offset(left, bottom), Offset(right, bottom))
        }
        else -> Unit
    }
}

// ─────────────────────────────────────────────────────────────
//  GOJO — silver-white hair, calm, soccer ball balanced on top
// ─────────────────────────────────────────────────────────────
@Composable
fun GojoCharacter(modifier: Modifier = Modifier, animated: Boolean = true) {
    val infinite = rememberInfiniteTransition(label = "gojo")
    val sway by infinite.animateFloat(
        initialValue = -1.6f, targetValue = 1.6f,
        animationSpec = infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "sway"
    )
    val sparkle by infinite.animateFloat(
        initialValue = 0.55f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1300, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "sparkle"
    )

    Canvas(modifier = modifier) {
        val s = size.width / 200f
        val cx = size.width / 2f
        val cyHead = size.height / 2f - 24 * s

        drawStickerHalo(cx, cyHead + 30 * s, s)
        drawSparkles(cx, cyHead + 20 * s, s, Color(0xFFB6D6FF), sparkle)
        drawGroundShadow(cx, cyHead + 30 * s, s)

        rotate(if (animated) sway else 0f, pivot = Offset(cx, cyHead + 70 * s)) {
            drawTeamKit(cx, cyHead, s, jerseyNumber = "1", jerseyAccent = Palette.JerseyBlueDark)
            drawHead(cx, cyHead, s)

            // eyes — small, slightly sleepy
            val eyeY = cyHead + 2 * s
            drawAnimeEye(cx - 13 * s, eyeY, s, iris = Color(0xFF5A6B89))
            drawAnimeEye(cx + 13 * s, eyeY, s, iris = Color(0xFF5A6B89))

            // mouth — tiny smirk
            drawSmile(cx, cyHead + 18 * s, s, width = 6f)

            // hair — fluffy silver-white with multiple tufts
            val hairColor = Color(0xFFEFF3FA)
            val hairShade = Color(0xFFB7C2D6)

            val mainHair = Path().apply {
                moveTo(cx - 36 * s, cyHead + 0 * s)
                cubicTo(cx - 50 * s, cyHead - 22 * s, cx - 30 * s, cyHead - 50 * s, cx - 6 * s, cyHead - 46 * s)
                cubicTo(cx - 4 * s, cyHead - 56 * s, cx + 4 * s, cyHead - 56 * s, cx + 6 * s, cyHead - 46 * s)
                cubicTo(cx + 30 * s, cyHead - 50 * s, cx + 50 * s, cyHead - 22 * s, cx + 36 * s, cyHead + 0 * s)
                cubicTo(cx + 30 * s, cyHead - 14 * s, cx + 18 * s, cyHead - 18 * s, cx + 6 * s, cyHead - 14 * s)
                lineTo(cx + 4 * s, cyHead - 6 * s)
                cubicTo(cx + 2 * s, cyHead - 12 * s, cx - 2 * s, cyHead - 12 * s, cx - 4 * s, cyHead - 6 * s)
                lineTo(cx - 6 * s, cyHead - 14 * s)
                cubicTo(cx - 18 * s, cyHead - 18 * s, cx - 30 * s, cyHead - 14 * s, cx - 36 * s, cyHead + 0 * s)
                close()
            }
            drawPath(mainHair, hairColor)
            drawPath(mainHair, Palette.Outline, style = Stroke(1.4f * s))

            // hair strands across the forehead
            listOf(
                -16f to -8f,
                -6f to -12f,
                4f to -12f,
                14f to -8f
            ).forEach { (dx, dy) ->
                val tuft = Path().apply {
                    moveTo(cx + dx * s - 3 * s, cyHead + dy * s)
                    quadraticTo(cx + dx * s, cyHead + (dy - 8f) * s, cx + dx * s + 3 * s, cyHead + dy * s)
                    quadraticTo(cx + dx * s, cyHead + (dy + 2f) * s, cx + dx * s - 3 * s, cyHead + dy * s)
                }
                drawPath(tuft, hairColor)
                drawPath(tuft, hairShade.copy(alpha = 0.7f), style = Stroke(0.8f * s))
            }

            // side bangs falling down
            val sideL = Path().apply {
                moveTo(cx - 36 * s, cyHead - 2 * s)
                cubicTo(cx - 44 * s, cyHead + 4 * s, cx - 42 * s, cyHead + 18 * s, cx - 30 * s, cyHead + 22 * s)
                lineTo(cx - 28 * s, cyHead + 16 * s)
                cubicTo(cx - 30 * s, cyHead + 10 * s, cx - 30 * s, cyHead + 4 * s, cx - 30 * s, cyHead + 0 * s)
                close()
            }
            drawPath(sideL, hairColor)
            drawPath(sideL, Palette.Outline, style = Stroke(1.0f * s))

            val sideR = Path().apply {
                moveTo(cx + 36 * s, cyHead - 2 * s)
                cubicTo(cx + 44 * s, cyHead + 4 * s, cx + 42 * s, cyHead + 18 * s, cx + 30 * s, cyHead + 22 * s)
                lineTo(cx + 28 * s, cyHead + 16 * s)
                cubicTo(cx + 30 * s, cyHead + 10 * s, cx + 30 * s, cyHead + 4 * s, cx + 30 * s, cyHead + 0 * s)
                close()
            }
            drawPath(sideR, hairColor)
            drawPath(sideR, Palette.Outline, style = Stroke(1.0f * s))

            // soccer ball balanced just on top of the hair
            drawSoccerBall(cx, cyHead - 50 * s, s, scale = 0.9f)
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  YUJI — black spiky hair, sparkle eyes, jersey #8
// ─────────────────────────────────────────────────────────────
@Composable
fun YujiCharacter(modifier: Modifier = Modifier, animated: Boolean = true) {
    val infinite = rememberInfiniteTransition(label = "yuji")
    val bounce by infinite.animateFloat(
        initialValue = -2.5f, targetValue = 2.5f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bounce"
    )
    val sparkle by infinite.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700, easing = LinearEasing), RepeatMode.Reverse),
        label = "sparkle"
    )

    Canvas(modifier = modifier) {
        val s = size.width / 200f
        val cx = size.width / 2f
        val cyHead = size.height / 2f - 24 * s + (if (animated) bounce else 0f) * s

        drawStickerHalo(cx, cyHead + 30 * s, s)
        drawSparkles(cx, cyHead + 20 * s, s, Color(0xFFFFE066), sparkle)
        drawGroundShadow(cx, cyHead + 30 * s, s)

        drawTeamKit(cx, cyHead, s, jerseyNumber = "8", jerseyAccent = Palette.JerseyBlueDark)
        drawHead(cx, cyHead, s)

        // sparkle / star eyes — excited
        drawStarEye(cx - 13 * s, cyHead + 2 * s, s, iris = Color(0xFF1E63E0))
        drawStarEye(cx + 13 * s, cyHead + 2 * s, s, iris = Color(0xFF1E63E0))

        // wide open laughing mouth + tongue
        drawOpenMouth(cx, cyHead + 18 * s, s)
        drawBlush(cx, cyHead, s)

        // black spiky hair
        val hairColor = Color(0xFF12131C)
        val hairShine = Color(0xFF394256)

        val mainHair = Path().apply {
            moveTo(cx - 36 * s, cyHead + 0 * s)
            cubicTo(cx - 50 * s, cyHead - 22 * s, cx - 30 * s, cyHead - 52 * s, cx - 4 * s, cyHead - 50 * s)
            cubicTo(cx + 6 * s, cyHead - 56 * s, cx + 22 * s, cyHead - 50 * s, cx + 30 * s, cyHead - 38 * s)
            cubicTo(cx + 50 * s, cyHead - 30 * s, cx + 52 * s, cyHead - 6 * s, cx + 36 * s, cyHead + 0 * s)
            cubicTo(cx + 30 * s, cyHead - 14 * s, cx + 16 * s, cyHead - 18 * s, cx, cyHead - 14 * s)
            cubicTo(cx - 16 * s, cyHead - 18 * s, cx - 30 * s, cyHead - 14 * s, cx - 36 * s, cyHead + 0 * s)
            close()
        }
        drawPath(mainHair, hairColor)

        // top spikes
        listOf(-18f, -8f, 4f, 16f).forEach { dx ->
            val spike = Path().apply {
                moveTo(cx + (dx - 6) * s, cyHead - 36 * s)
                lineTo(cx + dx * s, cyHead - 56 * s)
                lineTo(cx + (dx + 6) * s, cyHead - 36 * s)
                close()
            }
            drawPath(spike, hairColor)
        }
        // hair shine highlight
        val shineRect = Path().apply {
            moveTo(cx - 20 * s, cyHead - 36 * s)
            cubicTo(cx - 8 * s, cyHead - 44 * s, cx + 8 * s, cyHead - 44 * s, cx + 20 * s, cyHead - 36 * s)
            cubicTo(cx + 14 * s, cyHead - 38 * s, cx - 14 * s, cyHead - 38 * s, cx - 20 * s, cyHead - 36 * s)
            close()
        }
        drawPath(shineRect, hairShine)

        // hair outline
        drawPath(mainHair, Palette.Outline, style = Stroke(1.4f * s))
    }
}

// ─────────────────────────────────────────────────────────────
//  MEGUMI — navy hair, jersey #5, calm focused expression
// ─────────────────────────────────────────────────────────────
@Composable
fun MegumiCharacter(modifier: Modifier = Modifier, animated: Boolean = true) {
    val infinite = rememberInfiniteTransition(label = "megumi")
    val sway by infinite.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "sway"
    )
    val sparkle by infinite.animateFloat(
        initialValue = 0.4f, targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "sparkle"
    )

    Canvas(modifier = modifier) {
        val s = size.width / 200f
        val cx = size.width / 2f
        val cyHead = size.height / 2f - 24 * s

        drawStickerHalo(cx, cyHead + 30 * s, s)
        drawSparkles(cx, cyHead + 20 * s, s, Color(0xFF8DC0FF), sparkle)
        drawGroundShadow(cx, cyHead + 30 * s, s)

        rotate(if (animated) sway else 0f, pivot = Offset(cx, cyHead + 70 * s)) {
            drawTeamKit(cx, cyHead, s, jerseyNumber = "5", jerseyAccent = Palette.JerseyBlueDark)
            drawHead(cx, cyHead, s)

            // narrow focused eyes
            drawAnimeEye(cx - 13 * s, cyHead + 2 * s, s, iris = Color(0xFF1E63E0))
            drawAnimeEye(cx + 13 * s, cyHead + 2 * s, s, iris = Color(0xFF1E63E0))
            // serious mouth
            drawLine(
                color = Palette.MouthDark,
                start = Offset(cx - 5 * s, cyHead + 18 * s),
                end = Offset(cx + 5 * s, cyHead + 18 * s),
                strokeWidth = 2.4f * s, cap = StrokeCap.Round
            )

            // dark navy hair — choppy spiky shape
            val hair = Color(0xFF0E1A33)
            val hairShine = Color(0xFF1F3870)
            val mainHair = Path().apply {
                moveTo(cx - 36 * s, cyHead + 0 * s)
                cubicTo(cx - 52 * s, cyHead - 18 * s, cx - 36 * s, cyHead - 50 * s, cx - 6 * s, cyHead - 46 * s)
                cubicTo(cx + 8 * s, cyHead - 56 * s, cx + 28 * s, cyHead - 52 * s, cx + 32 * s, cyHead - 36 * s)
                cubicTo(cx + 50 * s, cyHead - 28 * s, cx + 50 * s, cyHead - 6 * s, cx + 36 * s, cyHead + 0 * s)
                cubicTo(cx + 30 * s, cyHead - 14 * s, cx + 16 * s, cyHead - 18 * s, cx, cyHead - 14 * s)
                cubicTo(cx - 16 * s, cyHead - 18 * s, cx - 30 * s, cyHead - 14 * s, cx - 36 * s, cyHead + 0 * s)
                close()
            }
            drawPath(mainHair, hair)

            // a few top spikes + shine
            listOf(-22f, -6f, 12f).forEach { dx ->
                val spike = Path().apply {
                    moveTo(cx + (dx - 4) * s, cyHead - 38 * s)
                    lineTo(cx + dx * s, cyHead - 54 * s)
                    lineTo(cx + (dx + 4) * s, cyHead - 38 * s)
                    close()
                }
                drawPath(spike, hair)
            }
            val shine = Path().apply {
                moveTo(cx - 16 * s, cyHead - 36 * s)
                cubicTo(cx - 6 * s, cyHead - 42 * s, cx + 6 * s, cyHead - 42 * s, cx + 16 * s, cyHead - 36 * s)
                cubicTo(cx + 12 * s, cyHead - 38 * s, cx - 12 * s, cyHead - 38 * s, cx - 16 * s, cyHead - 36 * s)
                close()
            }
            drawPath(shine, hairShine)

            drawPath(mainHair, Palette.Outline, style = Stroke(1.4f * s))
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  NOBARA — auburn-blue hair side ponytail, jersey #11, smirk
// ─────────────────────────────────────────────────────────────
@Composable
fun NobaraCharacter(modifier: Modifier = Modifier, animated: Boolean = true) {
    val infinite = rememberInfiniteTransition(label = "nobara")
    val swing by infinite.animateFloat(
        initialValue = -8f, targetValue = 10f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "swing"
    )
    val sparkle by infinite.animateFloat(
        initialValue = 0.55f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1100, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "sparkle"
    )

    Canvas(modifier = modifier) {
        val s = size.width / 200f
        val cx = size.width / 2f
        val cyHead = size.height / 2f - 24 * s

        drawStickerHalo(cx, cyHead + 30 * s, s)
        drawSparkles(cx, cyHead + 20 * s, s, Color(0xFF8DE2EE), sparkle)
        drawGroundShadow(cx, cyHead + 30 * s, s)

        drawTeamKit(cx, cyHead, s, jerseyNumber = "11", jerseyAccent = Palette.JerseyBlueDark)
        drawHead(cx, cyHead, s)

        // confident eyes with cyan iris
        drawAnimeEye(cx - 13 * s, cyHead + 2 * s, s, iris = Color(0xFF40CBE0))
        drawAnimeEye(cx + 13 * s, cyHead + 2 * s, s, iris = Color(0xFF40CBE0))

        // smirk
        val smirk = Path().apply {
            moveTo(cx - 8 * s, cyHead + 18 * s)
            cubicTo(cx - 2 * s, cyHead + 24 * s, cx + 6 * s, cyHead + 22 * s, cx + 9 * s, cyHead + 14 * s)
        }
        drawPath(smirk, Palette.MouthDark, style = Stroke(2.2f * s, cap = StrokeCap.Round))
        drawBlush(cx, cyHead, s)

        // hair — short bob with a side tail
        val hair = Color(0xFF8CCCE6)
        val hairShade = Color(0xFF40A2C8)

        val mainHair = Path().apply {
            moveTo(cx - 36 * s, cyHead + 0 * s)
            cubicTo(cx - 50 * s, cyHead - 22 * s, cx - 30 * s, cyHead - 52 * s, cx - 4 * s, cyHead - 50 * s)
            cubicTo(cx + 8 * s, cyHead - 54 * s, cx + 22 * s, cyHead - 52 * s, cx + 30 * s, cyHead - 40 * s)
            cubicTo(cx + 50 * s, cyHead - 32 * s, cx + 52 * s, cyHead - 8 * s, cx + 36 * s, cyHead + 0 * s)
            cubicTo(cx + 30 * s, cyHead - 14 * s, cx + 16 * s, cyHead - 18 * s, cx, cyHead - 14 * s)
            cubicTo(cx - 16 * s, cyHead - 18 * s, cx - 30 * s, cyHead - 14 * s, cx - 36 * s, cyHead + 0 * s)
            close()
        }
        drawPath(mainHair, hair)
        drawPath(mainHair, hairShade.copy(alpha = 0.45f), style = Stroke(1.0f * s))
        drawPath(mainHair, Palette.Outline, style = Stroke(1.4f * s))

        // side tail tuft
        rotate(if (animated) swing else 6f, pivot = Offset(cx + 30 * s, cyHead - 12 * s)) {
            val tail = Path().apply {
                moveTo(cx + 30 * s, cyHead - 14 * s)
                cubicTo(cx + 52 * s, cyHead - 4 * s, cx + 56 * s, cyHead + 18 * s, cx + 42 * s, cyHead + 28 * s)
                cubicTo(cx + 38 * s, cyHead + 16 * s, cx + 36 * s, cyHead + 4 * s, cx + 30 * s, cyHead - 4 * s)
                close()
            }
            drawPath(tail, hair)
            drawPath(tail, Palette.Outline, style = Stroke(1.2f * s))
        }

        // hairclip
        drawRoundRect(
            color = Palette.JerseyTrim,
            topLeft = Offset(cx - 26 * s, cyHead - 22 * s),
            size = Size(10 * s, 4 * s),
            cornerRadius = CornerRadius(2 * s, 2 * s)
        )
    }
}
