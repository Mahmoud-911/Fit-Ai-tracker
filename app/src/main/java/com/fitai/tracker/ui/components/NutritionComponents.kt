package com.fitai.tracker.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitai.tracker.data.model.FoodEntry
import com.fitai.tracker.data.model.MealType
import com.fitai.tracker.ui.theme.*
import java.text.DecimalFormat

// ─────────────────────────────────────────────────────────────
//  iOS-styled card primitives.
//  Apple's design uses minimal shadow elevation and a subtle
//  hairline border on cards, with generous corner radii.
// ─────────────────────────────────────────────────────────────
private val IosCardShape = RoundedCornerShape(18.dp)
private val IosTightShape = RoundedCornerShape(14.dp)
private val IosPillShape = RoundedCornerShape(50)

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
        shape = IosCardShape,
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = BorderStroke(0.5.dp, IosSeparator),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(130.dp), contentAlignment = Alignment.Center) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(130.dp)) {
                    val stroke = 11.dp.toPx()
                    val gap = 6.dp.toPx()

                    fun drawRing(pct: Float, color: Color, radiusOffset: Float) {
                        val r = size.minDimension / 2f - radiusOffset
                        drawArc(
                            color = color.copy(alpha = 0.14f),
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
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
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
                "${eaten.toInt()} / ${target.toInt()} $unit",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TextPrimary
            )
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.14f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animPct.value)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(color)
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
        shape = IosTightShape,
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = BorderStroke(0.5.dp, IosSeparator),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(IosBlue.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Text(entry.mealType.emoji, fontSize = 20.sp)
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
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = TextPrimary
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
        text = "$label ${value.toInt()}g",
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
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
        shape = IosCardShape,
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = BorderStroke(0.5.dp, IosSeparator),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(IosBlue.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Restaurant, null, tint = IosBlue, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(foodName, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Text(servingSize, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            Spacer(Modifier.height(16.dp))

            val df = DecimalFormat("#.#")

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NutrientChip("Calories", "${calories.toInt()}", "kcal", CalorieOrange, Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NutrientChip("Carbs", df.format(carbs), "g", CarbsBlue, Modifier.weight(1f))
                NutrientChip("Protein", df.format(protein), "g", ProteinGreen, Modifier.weight(1f))
                NutrientChip("Fat", df.format(fat), "g", FatYellow, Modifier.weight(1f))
            }
            if (fiber > 0f) {
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    NutrientChip("Fiber", df.format(fiber), "g", SuccessGreen, Modifier.fillMaxWidth(0.33f))
                }
            }

            if (tips.isNotBlank()) {
                Spacer(Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = IosTightShape,
                    colors = CardDefaults.cardColors(containerColor = IosBlue.copy(alpha = 0.10f)),
                    border = BorderStroke(0.5.dp, IosBlue.copy(alpha = 0.25f)),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                        Text("💡", fontSize = 14.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            tips,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
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
        shape = IosTightShape,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.10f)),
        border = BorderStroke(0.5.dp, color.copy(alpha = 0.20f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = TextPrimary
                )
                Spacer(Modifier.width(3.dp))
                Text(unit, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
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
        MealType.entries.forEach { type ->
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
                shape = IosPillShape,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = IosBlue,
                    selectedLabelColor = Color.White,
                    containerColor = DarkCardElevated,
                    labelColor = TextSecondary
                ),
                border = if (isSelected) null else BorderStroke(0.5.dp, IosSeparator),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
