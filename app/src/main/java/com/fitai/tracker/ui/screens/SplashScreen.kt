package com.fitai.tracker.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitai.tracker.ui.components.GojoCharacter
import com.fitai.tracker.ui.components.MegumiCharacter
import com.fitai.tracker.ui.components.NobaraCharacter
import com.fitai.tracker.ui.components.YujiCharacter
import com.fitai.tracker.ui.theme.GradientEnd
import com.fitai.tracker.ui.theme.GradientMid
import com.fitai.tracker.ui.theme.GradientStart
import com.fitai.tracker.ui.theme.IosBlue
import com.fitai.tracker.ui.theme.TextPrimary
import com.fitai.tracker.ui.theme.TextSecondary
import com.fitai.tracker.ui.theme.TextTertiary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val titleAlpha = remember { Animatable(0f) }
    val titleScale = remember { Animatable(0.92f) }
    val subAlpha = remember { Animatable(0f) }
    val charAlpha = remember { Animatable(0f) }
    val charScale = remember { Animatable(0.85f) }

    LaunchedEffect(Unit) {
        delay(120)
        charAlpha.animateTo(1f, tween(700, easing = FastOutSlowInEasing))
        charScale.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)
        )
        delay(120)
        titleAlpha.animateTo(1f, tween(620, easing = FastOutSlowInEasing))
        titleScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioLowBouncy))
        delay(180)
        subAlpha.animateTo(1f, tween(520))
        delay(1200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(GradientStart, GradientMid, GradientEnd))
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(charAlpha.value)
                    .scale(charScale.value)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Bottom
            ) {
                GojoCharacter(modifier = Modifier.size(78.dp))
                YujiCharacter(modifier = Modifier.size(94.dp))
                MegumiCharacter(modifier = Modifier.size(78.dp))
                NobaraCharacter(modifier = Modifier.size(78.dp))
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Fit AI",
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1.5).sp,
                color = TextPrimary,
                modifier = Modifier
                    .alpha(titleAlpha.value)
                    .scale(titleScale.value)
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = "Tracker",
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 4.sp,
                color = IosBlue,
                modifier = Modifier.alpha(titleAlpha.value)
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.alpha(subAlpha.value),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 22.dp, height = 1.dp)
                        .background(TextTertiary)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Smarter nutrition for athletes",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(width = 22.dp, height = 1.dp)
                        .background(TextTertiary)
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Powered by Claude",
                fontSize = 11.sp,
                color = TextTertiary,
                modifier = Modifier.alpha(subAlpha.value),
                textAlign = TextAlign.Center
            )
        }
    }
}
