package com.fitai.tracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitai.tracker.data.model.*
import com.fitai.tracker.ui.components.*
import com.fitai.tracker.ui.theme.*
import com.fitai.tracker.viewmodel.GoalsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(viewModel: GoalsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var activeTab by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkBackground, Color(0xFF0A0A20), DarkBackground)))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GoalsHeader()

            TabRow(
                selectedTabIndex = activeTab,
                containerColor = Color.Transparent,
                contentColor = GojoViolet,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                        color = GojoViolet
                    )
                }
            ) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                    Text(
                        "⚽ My Goal",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (activeTab == 0) GojoViolet else TextSecondary,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                    Text(
                        "💪 Training Plan",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (activeTab == 1) GojoViolet else TextSecondary,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
                Tab(selected = activeTab == 2, onClick = { activeTab = 2 }) {
                    Text(
                        "🏟️ Characters",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (activeTab == 2) GojoViolet else TextSecondary,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }

            when (activeTab) {
                0 -> GoalSettingsTab(state = state, viewModel = viewModel)
                1 -> TrainingPlanTab(tips = state.nutritionTips, goalType = state.athleteGoal.goalType)
                2 -> CharactersTab(currentGoal = state.athleteGoal.goalType)
            }
        }
    }
}

@Composable
private fun GoalsHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(GojoViolet.copy(0.15f), Color.Transparent)))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("⚡", fontSize = 24.sp)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    "ATHLETE GOALS",
                    style = MaterialTheme.typography.titleMedium.copy(
                        letterSpacing = 2.sp, fontWeight = FontWeight.Black
                    ),
                    color = TextPrimary
                )
                Text("Powered by Cursed Energy", style = MaterialTheme.typography.labelSmall, color = GojoViolet)
            }
        }
    }
}

@Composable
private fun GoalSettingsTab(
    state: com.fitai.tracker.viewmodel.GoalsUiState,
    viewModel: GoalsViewModel
) {
    val goal = state.athleteGoal

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Choose Your Goal", style = MaterialTheme.typography.titleMedium, color = TextPrimary)

        GoalType.entries.forEach { goalType ->
            GoalTypeCard(
                goalType = goalType,
                isSelected = goal.goalType == goalType,
                onClick = { viewModel.updateGoalType(goalType) }
            )
        }

        Spacer(Modifier.height(4.dp))
        HorizontalDivider(color = TextTertiary.copy(0.3f))
        Spacer(Modifier.height(4.dp))

        Text("Athlete Profile", style = MaterialTheme.typography.titleMedium, color = TextPrimary)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                ProfileSlider(
                    label = "Current Weight",
                    value = goal.currentWeight,
                    range = 40f..150f,
                    unit = "kg",
                    color = DomainBlue,
                    onValueChange = viewModel::updateWeight
                )
                ProfileSlider(
                    label = "Target Weight",
                    value = goal.targetWeight,
                    range = 40f..150f,
                    unit = "kg",
                    color = ProteinGreen,
                    onValueChange = viewModel::updateTargetWeight
                )
                ProfileSlider(
                    label = "Age",
                    value = goal.ageYears.toFloat(),
                    range = 15f..45f,
                    unit = "yr",
                    color = YujiPink,
                    onValueChange = { viewModel.updateAge(it.toInt()) }
                )
            }
        }

        Text("Playing Position", style = MaterialTheme.typography.titleSmall, color = TextSecondary)

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FootballPosition.entries.forEach { position ->
                FilterChip(
                    selected = goal.position == position,
                    onClick = { viewModel.updatePosition(position) },
                    label = {
                        Text(
                            "${position.emoji} ${position.label}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GojoViolet.copy(0.3f),
                        selectedLabelColor = GojoVioletLight,
                        containerColor = DarkCard,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GojoViolet.copy(0.1f)),
            border = BorderStroke(1.dp, GojoViolet.copy(0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Daily Targets", style = MaterialTheme.typography.titleSmall, color = GojoViolet)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TargetPill("🔥", "${goal.dailyCalorieTarget.toInt()}", "kcal", CalorieOrange)
                    TargetPill("🍚", "${goal.dailyCarbsTarget.toInt()}g", "carbs", CarbsBlue)
                    TargetPill("🥩", "${goal.dailyProteinTarget.toInt()}g", "protein", ProteinGreen)
                    TargetPill("🥑", "${goal.dailyFatTarget.toInt()}g", "fat", FatYellow)
                }
            }
        }

        Button(
            onClick = viewModel::saveGoal,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GojoViolet
            )
        ) {
            Icon(Icons.Default.Save, null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Save Goal Profile", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun GoalTypeCard(goalType: GoalType, isSelected: Boolean, onClick: () -> Unit) {
    val character = JJK_CHARACTERS.firstOrNull { it.associatedGoal == goalType }
    val color = if (character != null) Color(character.primaryColor) else GojoViolet

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(0.15f) else DarkCard
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) color else TextTertiary.copy(0.3f)
        ),
        elevation = CardDefaults.cardElevation(if (isSelected) 8.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(goalType.emoji, fontSize = 28.sp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(goalType.label, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(goalType.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            if (isSelected) {
                Icon(Icons.Default.CheckCircle, null, tint = color, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
private fun ProfileSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    unit: String,
    color: Color,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Text(
                "${value.toInt()} $unit",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = color.copy(0.2f)
            )
        )
    }
}

@Composable
private fun TargetPill(emoji: String, value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 16.sp)
        Text(
            value,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
            color = color
        )
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

@Composable
private fun TrainingPlanTab(tips: List<String>, goalType: GoalType) {
    val character = JJK_CHARACTERS.firstOrNull { it.associatedGoal == goalType } ?: JJK_CHARACTERS[0]
    val color = Color(character.primaryColor)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = color.copy(0.1f)),
            border = BorderStroke(1.dp, color.copy(0.3f))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(80.dp)) {
                    when (character.name) {
                        "Gojo Satoru" -> GojoCharacter(Modifier.fillMaxSize())
                        "Yuji Itadori" -> YujiCharacter(Modifier.fillMaxSize())
                        "Megumi Fushiguro" -> MegumiCharacter(Modifier.fillMaxSize())
                        "Nobara Kugisaki" -> NobaraCharacter(Modifier.fillMaxSize())
                        else -> GojoCharacter(Modifier.fillMaxSize())
                    }
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        "${goalType.emoji} ${goalType.label} Plan",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                        color = color
                    )
                    Text(
                        "${character.name}'s Expert Guide",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        character.quote.split("\n").first(),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = TextPrimary.copy(0.8f)
                    )
                }
            }
        }

        Text(
            "Nutrition & Training Tips",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )

        tips.forEachIndexed { index, tip ->
            TipCard(tip = tip, index = index, color = color)
        }

        Spacer(Modifier.height(16.dp))

        SampleMealPlanCard(goalType = goalType)

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun TipCard(tip: String, index: Int, color: Color) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 80L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 2 }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, color.copy(0.15f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color.copy(0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${index + 1}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                        color = color
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    tip,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun SampleMealPlanCard(goalType: GoalType) {
    val mealPlan = when (goalType) {
        GoalType.BUILD_MUSCLE -> listOf(
            "🌅 Breakfast" to "4 eggs + oats (100g) + banana + milk",
            "🍎 Snack 1" to "Greek yogurt + almonds + whey shake",
            "☀️ Lunch" to "200g chicken + 150g brown rice + salad",
            "⚡ Pre-Workout" to "Banana + rice cake + coffee",
            "💪 Post-Workout" to "Whey protein + 50g oats + honey",
            "🌙 Dinner" to "200g salmon + sweet potato + broccoli"
        )
        GoalType.LOSE_WEIGHT -> listOf(
            "🌅 Breakfast" to "3 egg whites + veggies + black coffee",
            "🍎 Snack 1" to "Apple + 20g almonds",
            "☀️ Lunch" to "150g chicken breast + large salad + olive oil",
            "⚡ Pre-Workout" to "Banana",
            "💪 Post-Workout" to "Whey protein shake (water)",
            "🌙 Dinner" to "150g fish + steamed veg + small portion rice"
        )
        GoalType.GAIN_WEIGHT -> listOf(
            "🌅 Breakfast" to "Oats (200g) + 4 eggs + whole milk + peanut butter",
            "🍎 Snack 1" to "Mass gainer shake + banana + nuts",
            "☀️ Lunch" to "300g pasta + beef mince + sauce",
            "⚡ Pre-Workout" to "Rice + chicken + avocado",
            "💪 Post-Workout" to "Mass shake + milk",
            "🌙 Dinner" to "Whole chicken leg + rice + potato + cheese"
        )
        GoalType.PEAK_PERFORMANCE -> listOf(
            "🌅 Breakfast" to "Porridge + banana + honey + eggs",
            "🍎 Snack 1" to "Energy bar + orange juice",
            "☀️ Lunch (match-3h)" to "Pasta + tomato sauce + chicken + sports drink",
            "⚡ Half-time" to "Banana + energy gel + water",
            "💪 Post-Match" to "Recovery shake + chocolate milk",
            "🌙 Dinner" to "Lean meat + rice + vegetables + fruit"
        )
        GoalType.MAINTAIN -> listOf(
            "🌅 Breakfast" to "Eggs (2) + wholegrain toast + avocado",
            "🍎 Snack 1" to "Yogurt + fruit",
            "☀️ Lunch" to "Balanced plate: protein + carbs + veg",
            "⚡ Pre-Training" to "Banana + oats",
            "💪 Post-Training" to "Protein shake + piece of fruit",
            "🌙 Dinner" to "Fish or chicken + vegetables + moderate carbs"
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🗓️", fontSize = 20.sp)
                Spacer(Modifier.width(8.dp))
                Text("Sample Daily Meal Plan", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            }
            Spacer(Modifier.height(12.dp))
            mealPlan.forEach { (meal, food) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                ) {
                    Text(
                        meal,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = GojoViolet,
                        modifier = Modifier.width(110.dp)
                    )
                    Text(
                        food,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (meal != mealPlan.last().first) {
                    HorizontalDivider(color = TextTertiary.copy(0.2f), modifier = Modifier.padding(vertical = 2.dp))
                }
            }
        }
    }
}

@Composable
private fun CharactersTab(currentGoal: GoalType) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            "Your JJK Mentors",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Text(
            "Each sorcerer specializes in a different athletic goal",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        JJK_CHARACTERS.forEach { character ->
            val isActive = character.associatedGoal == currentGoal
            CharacterDetailCard(character = character, isActive = isActive)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun CharacterDetailCard(
    character: JJKCharacter,
    isActive: Boolean
) {
    val primaryColor = Color(character.primaryColor)
    val secondaryColor = Color(character.secondaryColor)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) primaryColor.copy(0.12f) else DarkCard
        ),
        border = BorderStroke(
            if (isActive) 2.dp else 1.dp,
            if (isActive) primaryColor.copy(0.6f) else TextTertiary.copy(0.2f)
        ),
        elevation = CardDefaults.cardElevation(if (isActive) 10.dp else 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(primaryColor.copy(0.2f), secondaryColor.copy(0.1f), Color.Transparent)
                            )
                        )
                ) {
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
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                            color = primaryColor
                        )
                        if (isActive) {
                            Spacer(Modifier.width(6.dp))
                            Card(
                                shape = RoundedCornerShape(6.dp),
                                colors = CardDefaults.cardColors(containerColor = primaryColor.copy(0.25f))
                            ) {
                                Text(
                                    "YOUR GUIDE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = primaryColor,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(character.title, style = MaterialTheme.typography.bodySmall, color = secondaryColor)
                    Spacer(Modifier.height(4.dp))
                    character.associatedGoal?.let { goal ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(goal.emoji, fontSize = 12.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Expert: ${goal.label}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = primaryColor.copy(0.08f))
            ) {
                Text(
                    "\"${character.quote}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary.copy(0.9f),
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 18.sp
                )
            }
        }
    }
}
