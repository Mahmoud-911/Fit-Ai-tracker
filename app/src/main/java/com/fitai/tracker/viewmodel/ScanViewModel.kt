package com.fitai.tracker.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitai.tracker.BuildConfig
import com.fitai.tracker.data.model.FoodEntry
import com.fitai.tracker.data.model.MealType
import com.fitai.tracker.data.model.NutritionInfo
import com.fitai.tracker.data.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val repository: FoodRepository
) : ViewModel() {

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    private val _apiKey = MutableStateFlow(BuildConfig.CLAUDE_API_KEY)
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    fun analyzeImage(uri: Uri) {
        viewModelScope.launch {
            _scanState.value = ScanState.Analyzing
            val key = _apiKey.value
            if (key.isBlank()) {
                _scanState.value = ScanState.Error("API key not set. Go to Settings to add your Claude API key.")
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
        _apiKey.value = key
    }

    fun resetScan() {
        _scanState.value = ScanState.Idle
    }
}
