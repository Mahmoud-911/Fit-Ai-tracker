package com.fitai.tracker.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitai.tracker.BuildConfig
import com.fitai.tracker.data.model.FoodEntry
import com.fitai.tracker.data.model.MealType
import com.fitai.tracker.data.model.NutritionInfo
import com.fitai.tracker.data.repository.FoodRepository
import com.fitai.tracker.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ScanState {
    object Idle : ScanState()
    object Analyzing : ScanState()
    data class Success(val nutrition: NutritionInfo, val imageUri: Uri) : ScanState()
    data class Error(val message: String) : ScanState()
}

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val repository: FoodRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    val apiKey: StateFlow<String> = settings.claudeApiKey
        .map { stored -> stored.ifBlank { BuildConfig.CLAUDE_API_KEY } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, BuildConfig.CLAUDE_API_KEY)

    fun analyzeImage(uri: Uri) {
        viewModelScope.launch {
            _scanState.value = ScanState.Analyzing
            val key = apiKey.value
            if (key.isBlank()) {
                _scanState.value = ScanState.Error("API key not set. Tap the key icon to add your Claude API key.")
                return@launch
            }

            repository.analyzeFoodImage(uri, key)
                .onSuccess { nutrition ->
                    _scanState.value = ScanState.Success(nutrition, uri)
                }
                .onFailure { error ->
                    _scanState.value = ScanState.Error(error.message ?: "Analysis failed")
                }
        }
    }

    fun saveFoodEntry(nutrition: NutritionInfo, imageUri: Uri, mealType: MealType) {
        viewModelScope.launch {
            val totalNutrition = nutrition.additionalItems.fold(nutrition) { acc, item ->
                acc.copy(
                    calories = acc.calories + item.calories,
                    carbs = acc.carbs + item.carbs,
                    protein = acc.protein + item.protein,
                    fat = acc.fat + item.fat,
                    fiber = acc.fiber + item.fiber
                )
            }

            val entry = FoodEntry(
                name = nutrition.foodName,
                calories = totalNutrition.calories,
                carbs = totalNutrition.carbs,
                protein = totalNutrition.protein,
                fat = totalNutrition.fat,
                fiber = totalNutrition.fiber,
                imageUri = imageUri.toString(),
                mealType = mealType
            )
            repository.addFoodEntry(entry)
            _scanState.value = ScanState.Idle
        }
    }

    fun updateApiKey(key: String) {
        viewModelScope.launch {
            settings.setClaudeApiKey(key.trim())
        }
    }

    fun resetScan() {
        _scanState.value = ScanState.Idle
    }
}
