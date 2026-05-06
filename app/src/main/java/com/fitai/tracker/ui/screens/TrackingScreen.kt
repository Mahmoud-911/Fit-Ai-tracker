package com.fitai.tracker.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitai.tracker.ui.components.FoodEntryCard
import com.fitai.tracker.ui.theme.*
import com.fitai.tracker.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TrackingScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkBackground, Color(0xFF0A0A20), DarkBackground)))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                TrackingHeader()
            }

            item {
                WeeklyBarChart(weeklyData = state.weeklyData, calorieTarget = state.athleteGoal.dailyCalorieTarget)
            }

            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Today's Log",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (state.todayEntries.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard)
                    ) {
                        Text(
                            "No meals logged today. Scan or add a meal!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(24.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(state.todayEntries, key = { it.id }) { entry ->
                    FoodEntryCard(
                        entry = entry,
                        onDelete = { viewModel.deleteEntry(entry) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 3.dp)
                    )
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                TodaySummaryCard(
                    totalCal = state.totalCaloriesToday,
                    targetCal = state.athleteGoal.dailyCalorieTarget,
                    totalProtein = state.totalProteinToday,
                    totalCarbs = state.totalCarbsToday,
                    totalFat = state.totalFatToday,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun TrackingHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(CarbsBlue.copy(0.15f), Color.Transparent)))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("📊", fontSize = 24.sp)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    "FOOD DIARY",
                    style = MaterialTheme.typography.titleMedium.copy(
                        letterSpacing = 2.sp, fontWeight = FontWeight.Black
                    ),
                    color = TextPrimary
                )
                Text("Track every meal like a pro", style = MaterialTheme.typography.labelSmall, color = CarbsBlue)
            }
        }
    }
}

@Composable
private fun WeeklyBarChart(
    weeklyData: List<com.fitai.tracker.data.local.DailyNutritionTuple>,
    calorieTarget: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Weekly Overview", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Text("Calories vs target", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(Modifier.height(12.dp))

            if (weeklyData.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No weekly data yet", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    val maxCal = maxOf(weeklyData.maxOf { it.totalCalories }, calorieTarget) * 1.1f
                    weeklyData.takeLast(7).forEach { day ->
                        val pct = (day.totalCalories / maxCal).coerceIn(0f, 1f)
                        val targetPct = (calorieTarget / maxCal).coerceIn(0f, 1f)
                        val date = try {
                            LocalDate.parse(day.dateKey).format(DateTimeFormatter.ofPattern("EEE"))
                        } catch (e: Exception) {
                            day.dateKey.takeLast(5)
                        }
                        val isToday = day.dateKey == LocalDate.now().toString()
                        val barColor = when {
                            isToday -> GojoViolet
                            pct >= targetPct -> ProteinGreen
                            pct >= targetPct * 0.7f -> CalorieOrange
                            else -> CarbsBlue
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "${day.totalCalories.toInt()}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                color = barColor
                            )
                            Spacer(Modifier.height(2.dp))
                            val animPct = remember { Animatable(0f) }
                            LaunchedEffect(pct) { animPct.animateTo(pct, tween(800)) }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .fillMaxHeight(animPct.value.coerceAtLeast(0.02f))
                                        .background(
                                            Brush.verticalGradient(listOf(barColor.copy(0.6f), barColor)),
                                            RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                        )
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .align(Alignment.BottomStart)
                                        .offset(y = -(80 * targetPct).dp)
                                        .background(CalorieOrange.copy(0.5f))
                                )
                            }
                            Text(
                                date,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isToday) GojoViolet else TextSecondary
                            )
                        }
                    }
                }
            }

            Row(modifier = Modifier.padding(top = 8.dp)) {
                LegendDot(CalorieOrange, "Target")
                Spacer(Modifier.width(12.dp))
                LegendDot(ProteinGreen, "On track")
                Spacer(Modifier.width(12.dp))
                LegendDot(GojoViolet, "Today")
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, RoundedCornerShape(50)))
        Spacer(Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

@Composable
private fun TodaySummaryCard(
    totalCal: Float,
    targetCal: Float,
    totalProtein: Float,
    totalCarbs: Float,
    totalFat: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Today's Summary", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                SummaryItem("🔥", "${totalCal.toInt()}", "kcal eaten", CalorieOrange)
                SummaryItem("🍚", "${totalCarbs.toInt()}g", "carbs", CarbsBlue)
                SummaryItem("🥩", "${totalProtein.toInt()}g", "protein", ProteinGreen)
                SummaryItem("🥑", "${totalFat.toInt()}g", "fat", FatYellow)
            }
            Spacer(Modifier.height(12.dp))
            val remaining = (targetCal - totalCal).coerceAtLeast(0f)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (remaining > 0) ProteinGreen.copy(0.1f) else CursedRed.copy(0.1f)
                )
            ) {
                Text(
                    if (remaining > 0) "⚡ ${remaining.toInt()} kcal remaining today"
                    else "✅ Daily calorie target reached!",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (remaining > 0) ProteinGreen else CursedRed,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(emoji: String, value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 18.sp)
        Text(value, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black), color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}
