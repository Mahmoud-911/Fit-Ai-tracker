package com.fitai.tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitai.tracker.data.model.AthleteGoal
import com.fitai.tracker.data.model.FootballPosition
import com.fitai.tracker.data.model.GoalType
import com.fitai.tracker.data.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GoalsUiState(
    val athleteGoal: AthleteGoal = AthleteGoal(),
    val nutritionTips: List<String> = emptyList(),
    val isEditMode: Boolean = false
)

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val repository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAthleteGoal().collect { goal ->
                val currentGoal = goal ?: AthleteGoal()
                _uiState.value = _uiState.value.copy(
                    athleteGoal = currentGoal,
                    nutritionTips = repository.generateWorkoutPlan(currentGoal)
                )
            }
        }
    }

    fun updateGoalType(goalType: GoalType) {
        val updated = _uiState.value.athleteGoal.copy(
            goalType = goalType,
            dailyCalorieTarget = calculateCalorieTarget(goalType, _uiState.value.athleteGoal),
            dailyProteinTarget = calculateProteinTarget(goalType, _uiState.value.athleteGoal),
            dailyCarbsTarget = calculateCarbsTarget(goalType, _uiState.value.athleteGoal)
        )
        _uiState.value = _uiState.value.copy(
            athleteGoal = updated,
            nutritionTips = repository.generateWorkoutPlan(updated)
        )
    }

    fun updateWeight(weight: Float) {
        _uiState.value = _uiState.value.copy(
            athleteGoal = _uiState.value.athleteGoal.copy(currentWeight = weight)
        )
    }

    fun updateTargetWeight(weight: Float) {
        _uiState.value = _uiState.value.copy(
            athleteGoal = _uiState.value.athleteGoal.copy(targetWeight = weight)
        )
    }

    fun updatePosition(position: FootballPosition) {
        _uiState.value = _uiState.value.copy(
            athleteGoal = _uiState.value.athleteGoal.copy(position = position)
        )
    }

    fun updateAge(age: Int) {
        _uiState.value = _uiState.value.copy(
            athleteGoal = _uiState.value.athleteGoal.copy(ageYears = age)
        )
    }

    fun setEditMode(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isEditMode = enabled)
    }

    fun saveGoal() {
        viewModelScope.launch {
            val goal = _uiState.value.athleteGoal
            val updatedGoal = goal.copy(
                dailyCalorieTarget = calculateCalorieTarget(goal.goalType, goal),
                dailyProteinTarget = calculateProteinTarget(goal.goalType, goal),
                dailyCarbsTarget = calculateCarbsTarget(goal.goalType, goal),
                dailyFatTarget = calculateFatTarget(goal.goalType, goal)
            )
            repository.saveAthleteGoal(updatedGoal)
            _uiState.value = _uiState.value.copy(
                athleteGoal = updatedGoal,
                isEditMode = false,
                nutritionTips = repository.generateWorkoutPlan(updatedGoal)
            )
        }
    }

    private fun calculateCalorieTarget(goalType: GoalType, goal: AthleteGoal): Float {
        val bmr = 10 * goal.currentWeight + 6.25f * goal.heightCm - 5 * goal.ageYears + 5
        val tdee = bmr * 1.725f
        return when (goalType) {
            GoalType.BUILD_MUSCLE -> tdee + 400
            GoalType.GAIN_WEIGHT -> tdee + 600
            GoalType.LOSE_WEIGHT -> tdee - 400
            GoalType.MAINTAIN -> tdee
            GoalType.PEAK_PERFORMANCE -> tdee + 200
        }
    }

    private fun calculateProteinTarget(goalType: GoalType, goal: AthleteGoal): Float {
        val multiplier = when (goalType) {
            GoalType.BUILD_MUSCLE -> 2.0f
            GoalType.GAIN_WEIGHT -> 1.8f
            GoalType.LOSE_WEIGHT -> 2.2f
            GoalType.MAINTAIN -> 1.6f
            GoalType.PEAK_PERFORMANCE -> 1.8f
        }
        return goal.currentWeight * multiplier
    }

    private fun calculateCarbsTarget(goalType: GoalType, goal: AthleteGoal): Float {
        val multiplier = when (goalType) {
            GoalType.BUILD_MUSCLE -> 5.0f
            GoalType.GAIN_WEIGHT -> 6.0f
            GoalType.LOSE_WEIGHT -> 3.0f
            GoalType.MAINTAIN -> 5.0f
            GoalType.PEAK_PERFORMANCE -> 8.0f
        }
        return goal.currentWeight * multiplier
    }

    private fun calculateFatTarget(goalType: GoalType, goal: AthleteGoal): Float {
        return when (goalType) {
            GoalType.LOSE_WEIGHT -> 60f
            GoalType.BUILD_MUSCLE -> 80f
            GoalType.GAIN_WEIGHT -> 100f
            else -> 70f
        }
    }
}
