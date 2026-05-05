package com.fitai.tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitai.tracker.data.local.DailyNutritionTuple
import com.fitai.tracker.data.model.AthleteGoal
import com.fitai.tracker.data.model.FoodEntry
import com.fitai.tracker.data.model.GoalType
import com.fitai.tracker.data.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class HomeUiState(
    val todayEntries: List<FoodEntry> = emptyList(),
    val athleteGoal: AthleteGoal = AthleteGoal(),
    val weeklyData: List<DailyNutritionTuple> = emptyList(),
    val totalCaloriesToday: Float = 0f,
    val totalCarbsToday: Float = 0f,
    val totalProteinToday: Float = 0f,
    val totalFatToday: Float = 0f,
    val currentDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, MMM d")),
    val motivationalMessage: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                repository.getEntriesForToday(),
                repository.getAthleteGoal(),
                repository.getWeeklyNutrition()
            ) { entries, goal, weekly ->
                val totalCals = entries.sumOf { it.calories.toDouble() }.toFloat()
                val totalCarbs = entries.sumOf { it.carbs.toDouble() }.toFloat()
                val totalProtein = entries.sumOf { it.protein.toDouble() }.toFloat()
                val totalFat = entries.sumOf { it.fat.toDouble() }.toFloat()
                val currentGoal = goal ?: AthleteGoal()

                HomeUiState(
                    todayEntries = entries,
                    athleteGoal = currentGoal,
                    weeklyData = weekly,
                    totalCaloriesToday = totalCals,
                    totalCarbsToday = totalCarbs,
                    totalProteinToday = totalProtein,
                    totalFatToday = totalFat,
                    motivationalMessage = getMotivationalMessage(currentGoal.goalType, totalCals, currentGoal.dailyCalorieTarget)
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun deleteEntry(entry: FoodEntry) {
        viewModelScope.launch {
            repository.deleteFoodEntry(entry)
        }
    }

    fun addManualEntry(entry: FoodEntry) {
        viewModelScope.launch {
            repository.addFoodEntry(entry)
        }
    }

    private fun getMotivationalMessage(goal: GoalType, eaten: Float, target: Float): String {
        val percent = if (target > 0) eaten / target else 0f
        return when {
            percent < 0.3f -> when (goal) {
                GoalType.BUILD_MUSCLE -> "Gojo says: Power up! You need fuel to go Plus Ultra! 💪"
                GoalType.LOSE_WEIGHT -> "Nobara says: You're crushing it—keep the deficit going! 🎯"
                GoalType.GAIN_WEIGHT -> "Yuji says: EAT MORE! Every calorie counts for growth! 🍗"
                else -> "Start fueling your cursed energy! ⚡"
            }
            percent < 0.7f -> when (goal) {
                GoalType.BUILD_MUSCLE -> "Yuji says: Halfway there! Keep feeding those muscles! 💪"
                GoalType.PEAK_PERFORMANCE -> "Gojo says: On track! Match day performance demands this! ⚽"
                else -> "Megumi says: Keep going. Consistency wins. 🗡️"
            }
            percent < 1.0f -> "Almost at your target! The final stretch—like 99% domain expansion! 🔵"
            else -> when (goal) {
                GoalType.LOSE_WEIGHT -> "Nobara says: Hit the limit! Stop now and let your body work! 🔥"
                else -> "Daily target reached! Recovery mode: activated! 🌙"
            }
        }
    }
}
