package com.fitai.tracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitai.tracker.data.model.FoodEntry
import com.fitai.tracker.data.model.GoalType
import com.fitai.tracker.data.model.JJK_CHARACTERS
import com.fitai.tracker.data.model.MealType
import com.fitai.tracker.ui.components.*
import com.fitai.tracker.ui.theme.*
import com.fitai.tracker.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
                    listOf(DarkBackground, Color(0xFF0A0A20), DarkBackground)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                HomeHeader(
                    characterName = guideCharacter.name,
                    date = state.currentDate,
                    position = state.athleteGoal.position.emoji
                )
            }

            item {
                AnimatedVisibility(
                    visible = state.motivationalMessage.isNotBlank(),
                    enter = fadeIn() + expandVertically()
                ) {
                    MotivationalBanner(
                        message = state.motivationalMessage,
                        goalType = state.athleteGoal.goalType,
                        character = guideCharacter,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            item {
                NutritionRingCard(
                    caloriesEaten = state.totalCaloriesToday,
                    caloriesTarget = state.athleteGoal.dailyCalorieTarget,
                    carbsEaten = state.totalCarbsToday,
                    carbsTarget = state.athleteGoal.dailyCarbsTarget,
                    proteinEaten = state.totalProteinToday,
                    proteinTarget = state.athleteGoal.dailyProteinTarget,
                    fatEaten = state.totalFatToday,
                    fatTarget = state.athleteGoal.dailyFatTarget,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            item {
                QuickActionsRow(
                    onScanClick = onScanClick,
                    onAddManualClick = { showAddDialog = true },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            item {
                CharacterGuideCard(
                    character = guideCharacter,
                    goalType = state.athleteGoal.goalType,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (state.todayEntries.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Today's Meals",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "${state.todayEntries.size} items",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                    }
                }

                items(
                    items = state.todayEntries.groupBy { it.mealType }.entries.toList(),
                    key = { it.key.name }
                ) { (mealType, entries) ->
                    MealGroup(
                        mealType = mealType,
                        entries = entries,
                        onDeleteEntry = viewModel::deleteEntry,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            } else {
                item {
                    EmptyMealsCard(
                        onScanClick = onScanClick,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onScanClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 90.dp),
            containerColor = GojoViolet,
            contentColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CameraAlt, "Scan Meal", modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Scan Meal", fontWeight = FontWeight.Bold)
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

@Composable
private fun HomeHeader(characterName: String, date: String, position: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(listOf(GojoViolet.copy(0.2f), Color.Transparent))
            )
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⚽", fontSize = 22.sp)
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        "FIT AI TRACKER",
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 3.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GojoViolet
                    )
                    Text(date, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GojoViolet.copy(0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(position, fontSize = 22.sp)
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "Guide: $characterName",
                style = MaterialTheme.typography.bodySmall,
                color = GojoViolet.copy(0.8f)
            )
        }
    }
}

@Composable
private fun MotivationalBanner(
    message: String,
    goalType: GoalType,
    character: com.fitai.tracker.data.model.JJKCharacter,
    modifier: Modifier = Modifier
) {
    val color = Color(character.primaryColor)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.FlashOn, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                message,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun QuickActionsRow(
    onScanClick: () -> Unit,
    onAddManualClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        QuickActionButton(
            icon = Icons.Default.CameraAlt,
            label = "Scan",
            subtitle = "Photo AI",
            color = GojoViolet,
            onClick = onScanClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            icon = Icons.Default.Add,
            label = "Manual",
            subtitle = "Add meal",
            color = DomainBlue,
            onClick = onAddManualClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            icon = Icons.Default.BarChart,
            label = "Stats",
            subtitle = "Weekly",
            color = ProteinGreen,
            onClick = {},
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.height(6.dp))
            Text(label, style = MaterialTheme.typography.labelLarge, color = TextPrimary)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}

@Composable
private fun CharacterGuideCard(
    character: com.fitai.tracker.data.model.JJKCharacter,
    goalType: GoalType,
    modifier: Modifier = Modifier
) {
    val primaryColor = Color(character.primaryColor)
    val secondaryColor = Color(character.secondaryColor)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = BorderStroke(1.dp, primaryColor.copy(0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(90.dp)) {
                when (character.name) {
                    "Gojo Satoru" -> GojoCharacter(Modifier.fillMaxSize())
                    "Yuji Itadori" -> YujiCharacter(Modifier.fillMaxSize())
                    "Megumi Fushiguro" -> MegumiCharacter(Modifier.fillMaxSize())
                    "Nobara Kugisaki" -> NobaraCharacter(Modifier.fillMaxSize())
                    else -> GojoCharacter(Modifier.fillMaxSize())
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        character.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = primaryColor
                    )
                    Spacer(Modifier.width(6.dp))
                    Card(
                        shape = RoundedCornerShape(6.dp),
                        colors = CardDefaults.cardColors(containerColor = primaryColor.copy(0.15f))
                    ) {
                        Text(
                            character.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = primaryColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "${goalType.emoji} ${goalType.label} Guide",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    character.quote,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = TextPrimary.copy(0.85f),
                    lineHeight = 14.sp
                )
            }
        }
    }
}

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
            Text("${mealType.emoji} ${mealType.label}", style = MaterialTheme.typography.labelLarge, color = GojoViolet)
            Spacer(Modifier.width(8.dp))
            Text(
                "${entries.sumOf { it.calories.toDouble() }.toInt()} kcal",
                style = MaterialTheme.typography.labelSmall,
                color = CalorieOrange
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = BorderStroke(1.dp, GojoViolet.copy(0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GojoCharacter(modifier = Modifier.size(80.dp))
            Spacer(Modifier.height(12.dp))
            Text("No meals tracked yet", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Text(
                "Use Infinity Scan to analyze your food!",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onScanClick,
                colors = ButtonDefaults.buttonColors(containerColor = GojoViolet),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Scan a Meal")
            }
        }
    }
}

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
        title = { Text("Add Meal Manually", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Food name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GojoViolet,
                        focusedLabelColor = GojoViolet,
                        cursorColor = GojoViolet,
                        unfocusedTextColor = TextPrimary,
                        focusedTextColor = TextPrimary
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = calories, onValueChange = { calories = it },
                        label = { Text("kcal") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CalorieOrange, focusedLabelColor = CalorieOrange,
                            cursorColor = CalorieOrange, unfocusedTextColor = TextPrimary, focusedTextColor = TextPrimary
                        )
                    )
                    OutlinedTextField(
                        value = protein, onValueChange = { protein = it },
                        label = { Text("Protein g") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ProteinGreen, focusedLabelColor = ProteinGreen,
                            cursorColor = ProteinGreen, unfocusedTextColor = TextPrimary, focusedTextColor = TextPrimary
                        )
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = carbs, onValueChange = { carbs = it },
                        label = { Text("Carbs g") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CarbsBlue, focusedLabelColor = CarbsBlue,
                            cursorColor = CarbsBlue, unfocusedTextColor = TextPrimary, focusedTextColor = TextPrimary
                        )
                    )
                    OutlinedTextField(
                        value = fat, onValueChange = { fat = it },
                        label = { Text("Fat g") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FatYellow, focusedLabelColor = FatYellow,
                            cursorColor = FatYellow, unfocusedTextColor = TextPrimary, focusedTextColor = TextPrimary
                        )
                    )
                }
                Text("Meal Type:", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    MealType.values().forEach { type ->
                        FilterChip(
                            selected = type == selectedMealType,
                            onClick = { selectedMealType = type },
                            label = { Text("${type.emoji} ${type.label}", style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GojoViolet.copy(0.3f),
                                selectedLabelColor = GojoVioletLight
                            )
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
                            com.fitai.tracker.data.model.FoodEntry(
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
                colors = ButtonDefaults.buttonColors(containerColor = GojoViolet),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}
