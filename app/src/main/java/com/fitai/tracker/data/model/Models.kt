package com.fitai.tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "food_entries")
data class FoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val calories: Float,
    val carbs: Float,
    val protein: Float,
    val fat: Float,
    val fiber: Float = 0f,
    val imageUri: String? = null,
    val mealType: MealType = MealType.SNACK,
    val timestamp: Long = System.currentTimeMillis(),
    val dateKey: String = LocalDate.now().toString()
)

enum class MealType(val label: String, val emoji: String) {
    BREAKFAST("Breakfast", "🌅"),
    LUNCH("Lunch", "☀️"),
    DINNER("Dinner", "🌙"),
    PRE_WORKOUT("Pre-Workout", "⚡"),
    POST_WORKOUT("Post-Workout", "💪"),
    SNACK("Snack", "🍎")
}

data class DailyNutrition(
    val date: String,
    val totalCalories: Float,
    val totalCarbs: Float,
    val totalProtein: Float,
    val totalFat: Float,
    val entries: List<FoodEntry> = emptyList()
)

data class NutritionInfo(
    val foodName: String,
    val calories: Float,
    val carbs: Float,
    val protein: Float,
    val fat: Float,
    val fiber: Float,
    val servingSize: String,
    val confidence: Float,
    val additionalItems: List<NutritionInfo> = emptyList(),
    val tips: String = ""
)

@Entity(tableName = "athlete_goals")
data class AthleteGoal(
    @PrimaryKey val id: Int = 1,
    val goalType: GoalType = GoalType.MAINTAIN,
    val currentWeight: Float = 75f,
    val targetWeight: Float = 75f,
    val heightCm: Float = 175f,
    val ageYears: Int = 22,
    val dailyCalorieTarget: Float = 2500f,
    val dailyCarbsTarget: Float = 300f,
    val dailyProteinTarget: Float = 150f,
    val dailyFatTarget: Float = 80f,
    val position: FootballPosition = FootballPosition.MIDFIELDER,
    val weeklyTrainingDays: Int = 5
)

enum class GoalType(val label: String, val description: String, val emoji: String) {
    BUILD_MUSCLE("Build Muscle", "Increase muscle mass and strength", "💪"),
    GAIN_WEIGHT("Gain Weight", "Increase overall body mass", "⬆️"),
    LOSE_WEIGHT("Lose Weight", "Reduce body fat while maintaining muscle", "🎯"),
    MAINTAIN("Maintain", "Keep current physique and performance", "⚖️"),
    PEAK_PERFORMANCE("Peak Performance", "Optimize for match-day performance", "⚡")
}

enum class FootballPosition(val label: String, val emoji: String) {
    GOALKEEPER("Goalkeeper", "🧤"),
    DEFENDER("Defender", "🛡️"),
    MIDFIELDER("Midfielder", "⚡"),
    FORWARD("Forward", "⚽"),
    WINGER("Winger", "💨")
}

data class WorkoutPlan(
    val goalType: GoalType,
    val nutritionTips: List<String>,
    val mealPlan: List<MealSuggestion>,
    val trainingTips: List<String>
)

data class MealSuggestion(
    val mealType: MealType,
    val suggestion: String,
    val targetCalories: Float,
    val targetProtein: Float,
    val targetCarbs: Float
)

data class JJKCharacter(
    val name: String,
    val title: String,
    val quote: String,
    val associatedGoal: GoalType?,
    val primaryColor: Long,
    val secondaryColor: Long
)

val JJK_CHARACTERS = listOf(
    JJKCharacter(
        name = "Gojo Satoru",
        title = "The Strongest",
        quote = "Throughout Heaven and Earth, I alone am the honored one.\nNow eat right and become limitless!",
        associatedGoal = GoalType.PEAK_PERFORMANCE,
        primaryColor = 0xFF9B5DE5,
        secondaryColor = 0xFF00BBF9
    ),
    JJKCharacter(
        name = "Yuji Itadori",
        title = "Divergent Fist",
        quote = "I will eat properly and die surrounded by people!\nEvery rep, every meal, every gram of protein counts!",
        associatedGoal = GoalType.BUILD_MUSCLE,
        primaryColor = 0xFFF15BB5,
        secondaryColor = 0xFFFF9F1C
    ),
    JJKCharacter(
        name = "Megumi Fushiguro",
        title = "Ten Shadows",
        quote = "I don't care about being a hero.\nBut disciplined nutrition? That I can respect.",
        associatedGoal = GoalType.MAINTAIN,
        primaryColor = 0xFF4CC9F0,
        secondaryColor = 0xFF1A2035
    ),
    JJKCharacter(
        name = "Nobara Kugisaki",
        title = "Straw Doll",
        quote = "Whether it's a cursed spirit or stubborn fat—\nI'll smash it all! Protein first!",
        associatedGoal = GoalType.LOSE_WEIGHT,
        primaryColor = 0xFFFF9F1C,
        secondaryColor = 0xFFB5651D
    )
)
