package com.fitai.tracker.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitai.tracker.ui.components.GojoCharacter
import com.fitai.tracker.ui.components.MegumiCharacter
import com.fitai.tracker.ui.components.NobaraCharacter
import com.fitai.tracker.ui.components.YujiCharacter
import com.fitai.tracker.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var phase by remember { mutableStateOf(0) }

    val titleAlpha = remember { Animatable(0f) }
    val titleScale = remember { Animatable(0.6f) }
    val subAlpha = remember { Animatable(0f) }
    val charAlpha = remember { Animatable(0f) }
    val charScale = remember { Animatable(0.5f) }

    LaunchedEffect(Unit) {
        delay(200)
        titleAlpha.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
        titleScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        delay(300)
        subAlpha.animateTo(1f, tween(600))
        delay(200)
        charAlpha.animateTo(1f, tween(800))
        charScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow))
        delay(1500)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(GradientStart, GradientMid, GradientEnd)
                )
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
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                GojoCharacter(modifier = Modifier.size(90.dp))
                YujiCharacter(modifier = Modifier.size(110.dp))
                MegumiCharacter(modifier = Modifier.size(90.dp))
                NobaraCharacter(modifier = Modifier.size(90.dp))
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "⚽ FIT AI",
                fontSize = 52.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                modifier = Modifier
                    .alpha(titleAlpha.value)
                    .scale(titleScale.value)
            )

            Text(
                text = "TRACKER",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 10.sp,
                color = GojoViolet,
                modifier = Modifier.alpha(titleAlpha.value)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Powered by Cursed Energy & Science",
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.alpha(subAlpha.value)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "\"Throughout heaven and earth,\nI alone am the honored athlete\"",
                fontSize = 12.sp,
                color = TextTertiary,
                modifier = Modifier.alpha(subAlpha.value * 0.8f)
            )
        }
    }
}
