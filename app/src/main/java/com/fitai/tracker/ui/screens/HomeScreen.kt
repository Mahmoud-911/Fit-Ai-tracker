package com.fitai.tracker.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocalFireDepartment
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitai.tracker.data.model.FoodEntry
import com.fitai.tracker.data.model.JJK_CHARACTERS
import com.fitai.tracker.data.model.MealType
import com.fitai.tracker.ui.components.FoodEntryCard
import com.fitai.tracker.ui.components.GojoCharacter
import com.fitai.tracker.ui.components.MegumiCharacter
import com.fitai.tracker.ui.components.NobaraCharacter
import com.fitai.tracker.ui.components.YujiCharacter
import com.fitai.tracker.ui.theme.*
import com.fitai.tracker.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onScanClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val guideCharacter = JJK_CHARACTERS.firstOrNull { it.associatedGoal == state.athleteGoal.goalType }
        ?: JJK_CHARACTERS[0]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF02060F), Color(0xFF050C1F), Color(0xFF000000))
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                GreetingHeader(
                    characterName = guideCharacter.name,
                    date = state.currentDate,
                    character = guideCharacter,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                AnimatedVisibility(
                    visible = state.motivationalMessage.isNotBlank(),
                    enter = fadeIn() + expandVertically()
                ) {
                    MotivationalChip(
                        message = state.motivationalMessage,
                        character = guideCharacter,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }

            item {
                CalorieHeroCard(
                    eaten = state.totalCaloriesToday,
                    target = state.athleteGoal.dailyCalorieTarget,
                    carbsEaten = state.totalCarbsToday,
                    carbsTarget = state.athleteGoal.dailyCarbsTarget,
                    proteinEaten = state.totalProteinToday,
                    proteinTarget = state.athleteGoal.dailyProteinTarget,
                    fatEaten = state.totalFatToday,
                    fatTarget = state.athleteGoal.dailyFatTarget,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                MacroGrid(
                    carbsEaten = state.totalCarbsToday,
                    carbsTarget = state.athleteGoal.dailyCarbsTarget,
                    proteinEaten = state.totalProteinToday,
                    proteinTarget = state.athleteGoal.dailyProteinTarget,
                    fatEaten = state.totalFatToday,
                    fatTarget = state.athleteGoal.dailyFatTarget,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                QuickActionRow(
                    onScan = onScanClick,
                    onAddManual = { showAddDialog = true },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            if (state.todayEntries.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Today's meals",
                        trailing = "${state.todayEntries.size} items",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }

                items(
                    items = state.todayEntries.groupBy { it.mealType }.entries.toList(),
                    key = { it.key.name }
                ) { (mealType, entries) ->
                    MealGroup(
                        mealType = mealType,
                        entries = entries,
                        onDeleteEntry = viewModel::deleteEntry,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            } else {
                item {
                    EmptyMealsCard(
                        onScanClick = onScanClick,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        ManualEntryDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { entry ->
                viewModel.addManualEntry(entry)
                showAddDialog = false
            }
        )
    }
}

// ─── Header ──────────────────────────────────────────────────

@Composable
private fun GreetingHeader(
    characterName: String,
    date: String,
    character: com.fitai.tracker.data.model.JJKCharacter,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Hello, athlete",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Text(
                "Today, $date",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
        }
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(character.primaryColor).copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            CharacterAvatar(character = character, modifier = Modifier.size(56.dp))
        }
    }
}

@Composable
private fun CharacterAvatar(
    character: com.fitai.tracker.data.model.JJKCharacter,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when (character.name) {
            "Gojo Satoru" -> GojoCharacter(Modifier.fillMaxSize())
            "Yuji Itadori" -> YujiCharacter(Modifier.fillMaxSize())
            "Megumi Fushiguro" -> MegumiCharacter(Modifier.fillMaxSize())
            "Nobara Kugisaki" -> NobaraCharacter(Modifier.fillMaxSize())
            else -> GojoCharacter(Modifier.fillMaxSize())
        }
    }
}

// ─── Motivational chip ──────────────────────────────────────

@Composable
private fun MotivationalChip(
    message: String,
    character: com.fitai.tracker.data.model.JJKCharacter,
    modifier: Modifier = Modifier
) {
    val accent = Color(character.primaryColor)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.10f)),
        border = BorderStroke(0.5.dp, accent.copy(alpha = 0.30f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center
            ) {
                Text("⚡", fontSize = 14.sp)
            }
            Spacer(Modifier.width(12.dp))
            Text(
                message,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary
            )
        }
    }
}

// ─── Hero calorie card ──────────────────────────────────────

@Composable
private fun CalorieHeroCard(
    eaten: Float,
    target: Float,
    carbsEaten: Float,
    carbsTarget: Float,
    proteinEaten: Float,
    proteinTarget: Float,
    fatEaten: Float,
    fatTarget: Float,
    modifier: Modifier = Modifier
) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(eaten, target) {
        animProgress.animateTo(1f, tween(1100, easing = FastOutSlowInEasing))
    }
    val prog = animProgress.value

    val calPct = (eaten / target.coerceAtLeast(1f)).coerceIn(0f, 1f) * prog
    val carbPct = (carbsEaten / carbsTarget.coerceAtLeast(1f)).coerceIn(0f, 1f) * prog
    val protPct = (proteinEaten / proteinTarget.coerceAtLeast(1f)).coerceIn(0f, 1f) * prog
    val fatPct = (fatEaten / fatTarget.coerceAtLeast(1f)).coerceIn(0f, 1f) * prog

    val remaining = (target - eaten).coerceAtLeast(0f).toInt()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1426)),
        border = BorderStroke(0.5.dp, IosSeparator),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0F1B33),
                            Color(0xFF06122A)
                        )
                    )
                )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocalFireDepartment,
                        null,
                        tint = CalorieOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Calories",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        "${eaten.toInt()} / ${target.toInt()} kcal",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                }

                Spacer(Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.size(160.dp)) {
                            val stroke = 14.dp.toPx()
                            val gap = 5.dp.toPx()

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
                                            colors = listOf(
                                                color.copy(alpha = 0.7f),
                                                color,
                                                color.copy(alpha = 0.7f)
                                            ),
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

                            drawRing(calPct, CalorieOrange, stroke / 2)
                            drawRing(carbPct, CarbsBlue, stroke + gap + stroke / 2)
                            drawRing(protPct, ProteinGreen, 2 * stroke + 2 * gap + stroke / 2)
                            drawRing(fatPct, FatYellow, 3 * stroke + 3 * gap + stroke / 2)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "$remaining",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                style = MaterialTheme.typography.headlineLarge
                            )
                            Text(
                                "kcal left",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(Modifier.width(18.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MiniMacroLine("Calories", eaten, target, CalorieOrange, "kcal")
                        MiniMacroLine("Carbs", carbsEaten, carbsTarget, CarbsBlue, "g")
                        MiniMacroLine("Protein", proteinEaten, proteinTarget, ProteinGreen, "g")
                        MiniMacroLine("Fat", fatEaten, fatTarget, FatYellow, "g")
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniMacroLine(
    label: String,
    eaten: Float,
    target: Float,
    color: Color,
    unit: String
) {
    val pct = (eaten / target.coerceAtLeast(1f)).coerceIn(0f, 1f)
    val animPct = remember { Animatable(0f) }
    LaunchedEffect(eaten) { animPct.animateTo(pct, tween(900, easing = FastOutSlowInEasing)) }
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(
                "${eaten.toInt()} $unit",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = TextPrimary
            )
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.16f))
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

// ─── Macro grid (3 cards) ───────────────────────────────────

@Composable
private fun MacroGrid(
    carbsEaten: Float,
    carbsTarget: Float,
    proteinEaten: Float,
    proteinTarget: Float,
    fatEaten: Float,
    fatTarget: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MacroDonut("Carbs", carbsEaten, carbsTarget, CarbsBlue, "🍚", Modifier.weight(1f))
        MacroDonut("Protein", proteinEaten, proteinTarget, ProteinGreen, "🥩", Modifier.weight(1f))
        MacroDonut("Fat", fatEaten, fatTarget, FatYellow, "🥑", Modifier.weight(1f))
    }
}

@Composable
private fun MacroDonut(
    label: String,
    eaten: Float,
    target: Float,
    color: Color,
    emoji: String,
    modifier: Modifier = Modifier
) {
    val pct = (eaten / target.coerceAtLeast(1f)).coerceIn(0f, 1f)
    val animPct = remember { Animatable(0f) }
    LaunchedEffect(eaten) { animPct.animateTo(pct, tween(1000, easing = FastOutSlowInEasing)) }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = BorderStroke(0.5.dp, IosSeparator),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(64.dp)) {
                    val stroke = 7.dp.toPx()
                    drawArc(
                        color = color.copy(alpha = 0.16f),
                        startAngle = -90f, sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(stroke, cap = StrokeCap.Round),
                        topLeft = androidx.compose.ui.geometry.Offset(stroke / 2, stroke / 2),
                        size = androidx.compose.ui.geometry.Size(size.width - stroke, size.height - stroke)
                    )
                    drawArc(
                        color = color,
                        startAngle = -90f, sweepAngle = 360f * animPct.value,
                        useCenter = false,
                        style = Stroke(stroke, cap = StrokeCap.Round),
                        topLeft = androidx.compose.ui.geometry.Offset(stroke / 2, stroke / 2),
                        size = androidx.compose.ui.geometry.Size(size.width - stroke, size.height - stroke)
                    )
                }
                Text(emoji, fontSize = 22.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "${eaten.toInt()}g",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TextPrimary
            )
            Text(
                "$label · ${target.toInt()}g",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

// ─── Quick actions ──────────────────────────────────────────

@Composable
private fun QuickActionRow(
    onScan: () -> Unit,
    onAddManual: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = onScan,
            modifier = Modifier
                .weight(1f)
                .height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IosBlue, contentColor = Color.White),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Scan meal", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
        OutlinedButton(
            onClick = onAddManual,
            modifier = Modifier
                .weight(1f)
                .height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
            border = BorderStroke(0.5.dp, IosSeparator)
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Add manually", fontWeight = FontWeight.Medium, fontSize = 15.sp)
        }
    }
}

// ─── Section header ─────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, trailing: String? = null, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        trailing?.let {
            Text(
                it,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

// ─── Meal group + Empty state ───────────────────────────────

@Composable
private fun MealGroup(
    mealType: MealType,
    entries: List<FoodEntry>,
    onDeleteEntry: (FoodEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "${mealType.emoji} ${mealType.label}",
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "${entries.sumOf { it.calories.toDouble() }.toInt()} kcal",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
        entries.forEach { entry ->
            FoodEntryCard(
                entry = entry,
                onDelete = { onDeleteEntry(entry) },
                modifier = Modifier.padding(vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun EmptyMealsCard(onScanClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = BorderStroke(0.5.dp, IosSeparator),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            YujiCharacter(modifier = Modifier.size(110.dp))
            Spacer(Modifier.height(8.dp))
            Text(
                "No meals tracked yet",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "Snap a photo and let the AI do the math",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onScanClick,
                colors = ButtonDefaults.buttonColors(containerColor = IosBlue),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Scan a meal")
            }
        }
    }
}

// ─── Manual entry dialog ────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManualEntryDialog(onDismiss: () -> Unit, onConfirm: (FoodEntry) -> Unit) {
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var selectedMealType by remember { mutableStateOf(MealType.SNACK) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        shape = RoundedCornerShape(22.dp),
        title = { Text("Add meal", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Food name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IosBlue,
                        focusedLabelColor = IosBlue,
                        cursorColor = IosBlue,
                        unfocusedTextColor = TextPrimary,
                        focusedTextColor = TextPrimary
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = calories, onValueChange = { calories = it },
                        label = { Text("kcal") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = iosTextFieldColors(CalorieOrange)
                    )
                    OutlinedTextField(
                        value = protein, onValueChange = { protein = it },
                        label = { Text("Protein g") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = iosTextFieldColors(ProteinGreen)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = carbs, onValueChange = { carbs = it },
                        label = { Text("Carbs g") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = iosTextFieldColors(CarbsBlue)
                    )
                    OutlinedTextField(
                        value = fat, onValueChange = { fat = it },
                        label = { Text("Fat g") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = iosTextFieldColors(FatYellow)
                    )
                }
                Text("Meal type", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    MealType.entries.forEach { type ->
                        FilterChip(
                            selected = type == selectedMealType,
                            onClick = { selectedMealType = type },
                            label = {
                                Text(
                                    "${type.emoji} ${type.label}",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IosBlue,
                                selectedLabelColor = Color.White,
                                containerColor = DarkCardElevated,
                                labelColor = TextSecondary
                            ),
                            border = if (type == selectedMealType) null else BorderStroke(0.5.dp, IosSeparator)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            FoodEntry(
                                name = name,
                                calories = calories.toFloatOrNull() ?: 0f,
                                carbs = carbs.toFloatOrNull() ?: 0f,
                                protein = protein.toFloatOrNull() ?: 0f,
                                fat = fat.toFloatOrNull() ?: 0f,
                                mealType = selectedMealType
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = IosBlue),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun iosTextFieldColors(accent: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = accent,
    focusedLabelColor = accent,
    cursorColor = accent,
    unfocusedTextColor = TextPrimary,
    focusedTextColor = TextPrimary
)
