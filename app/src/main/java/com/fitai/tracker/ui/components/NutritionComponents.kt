package com.fitai.tracker.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitai.tracker.data.model.FoodEntry
import com.fitai.tracker.data.model.MealType
import com.fitai.tracker.ui.theme.*
import java.text.DecimalFormat

@Composable
fun NutritionRingCard(
    caloriesEaten: Float,
    caloriesTarget: Float,
    carbsEaten: Float,
    carbsTarget: Float,
    proteinEaten: Float,
    proteinTarget: Float,
    fatEaten: Float,
    fatTarget: Float,
    modifier: Modifier = Modifier
) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(caloriesEaten) {
        animProgress.animateTo(1f, tween(1200, easing = FastOutSlowInEasing))
    }
    val prog = animProgress.value

    val calPct = (caloriesEaten / caloriesTarget.coerceAtLeast(1f)).coerceIn(0f, 1f) * prog
    val carbPct = (carbsEaten / carbsTarget.coerceAtLeast(1f)).coerceIn(0f, 1f) * prog
    val protPct = (proteinEaten / proteinTarget.coerceAtLeast(1f)).coerceIn(0f, 1f) * prog
    val fatPct = (fatEaten / fatTarget.coerceAtLeast(1f)).coerceIn(0f, 1f) * prog

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(130.dp), contentAlignment = Alignment.Center) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(130.dp)) {
                    val stroke = 12.dp.toPx()
                    val gap = 8.dp.toPx()

                    fun drawRing(pct: Float, color: Color, radiusOffset: Float) {
                        val r = size.minDimension / 2f - radiusOffset
                        drawArc(
                            color = color.copy(alpha = 0.15f),
                            startAngle = -90f, sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(stroke, cap = StrokeCap.Round),
                            topLeft = androidx.compose.ui.geometry.Offset(size.width / 2f - r, size.height / 2f - r),
                            size = androidx.compose.ui.geometry.Size(r * 2, r * 2)
                        )
                        if (pct > 0f) {
                            drawArc(
                                brush = Brush.sweepGradient(
                                    colors = listOf(color.copy(alpha = 0.7f), color, color.copy(alpha = 0.7f)),
                                    center = center
                                ),
                                startAngle = -90f, sweepAngle = 360f * pct,
                                useCenter = false,
                                style = Stroke(stroke, cap = StrokeCap.Round),
                                topLeft = androidx.compose.ui.geometry.Offset(size.width / 2f - r, size.height / 2f - r),
                                size = androidx.compose.ui.geometry.Size(r * 2, r * 2)
                            )
                        }
                    }

                    drawRing(calPct, CalorieOrange, stroke / 2 + gap * 0)
                    drawRing(carbPct, CarbsBlue, stroke + gap * 1 + stroke / 2)
                    drawRing(protPct, ProteinGreen, stroke * 2 + gap * 2 + stroke / 2)
                    drawRing(fatPct, FatYellow, stroke * 3 + gap * 3 + stroke / 2)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = caloriesEaten.toInt().toString(),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, fontSize = 20.sp),
                        color = CalorieOrange
                    )
                    Text("kcal", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
            }

            Spacer(Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                MacroRow("Calories", caloriesEaten, caloriesTarget, CalorieOrange, "kcal")
                MacroRow("Carbs", carbsEaten, carbsTarget, CarbsBlue, "g")
                MacroRow("Protein", proteinEaten, proteinTarget, ProteinGreen, "g")
                MacroRow("Fat", fatEaten, fatTarget, FatYellow, "g")
            }
        }
    }
}

@Composable
private fun MacroRow(label: String, eaten: Float, target: Float, color: Color, unit: String) {
    val pct = (eaten / target.coerceAtLeast(1f)).coerceIn(0f, 1f)
    val animPct = remember { Animatable(0f) }
    LaunchedEffect(eaten) {
        animPct.animateTo(pct, tween(1000, easing = FastOutSlowInEasing))
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Text(
                "${eaten.toInt()}/${target.toInt()}$unit",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = color
            )
        }
        Spacer(Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animPct.value)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(
                        brush = Brush.horizontalGradient(listOf(color.copy(0.8f), color))
                    )
            )
        }
    }
}

@Composable
fun FoodEntryCard(
    entry: FoodEntry,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDelete by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDelete = !showDelete },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardElevated),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GojoViolet.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(entry.mealType.emoji, fontSize = 22.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    entry.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    entry.mealType.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            Spacer(Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${entry.calories.toInt()} kcal",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = CalorieOrange
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    MiniMacro("C", entry.carbs, CarbsBlue)
                    MiniMacro("P", entry.protein, ProteinGreen)
                    MiniMacro("F", entry.fat, FatYellow)
                }
            }

            if (showDelete) {
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Delete, "Delete", tint = CursedRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun MiniMacro(label: String, value: Float, color: Color) {
    Text(
        text = "$label:${value.toInt()}g",
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
        color = color
    )
}

@Composable
fun NutritionResultCard(
    foodName: String,
    calories: Float,
    carbs: Float,
    protein: Float,
    fat: Float,
    fiber: Float,
    servingSize: String,
    tips: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Restaurant, null, tint = GojoViolet, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(foodName, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Text(servingSize, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            Spacer(Modifier.height(16.dp))

            val df = DecimalFormat("#.#")

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NutrientChip("🔥 Calories", "${calories.toInt()}", "kcal", CalorieOrange, Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NutrientChip("🍚 Carbs", df.format(carbs), "g", CarbsBlue, Modifier.weight(1f))
                NutrientChip("🥩 Protein", df.format(protein), "g", ProteinGreen, Modifier.weight(1f))
                NutrientChip("🥑 Fat", df.format(fat), "g", FatYellow, Modifier.weight(1f))
            }
            if (fiber > 0f) {
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    NutrientChip("🌾 Fiber", df.format(fiber), "g", SuccessGreen, Modifier.fillMaxWidth(0.33f))
                }
            }

            if (tips.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GojoViolet.copy(alpha = 0.12f))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                        Text("⚡", fontSize = 16.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            tips,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NutrientChip(label: String, value: String, unit: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = color.copy(alpha = 0.8f))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = color
                )
                Spacer(Modifier.width(3.dp))
                Text(unit, style = MaterialTheme.typography.labelSmall, color = color.copy(0.6f))
            }
        }
    }
}

@Composable
fun MealTypeSelector(selected: MealType, onSelect: (MealType) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        MealType.values().forEach { type ->
            val isSelected = type == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(type) },
                label = {
                    Text(
                        "${type.emoji} ${type.label}",
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GojoViolet.copy(alpha = 0.3f),
                    selectedLabelColor = GojoVioletLight,
                    containerColor = DarkCard,
                    labelColor = TextSecondary
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
