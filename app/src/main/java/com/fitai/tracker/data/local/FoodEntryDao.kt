package com.fitai.tracker.data.local

import androidx.room.*
import com.fitai.tracker.data.model.AthleteGoal
import com.fitai.tracker.data.model.FoodEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodEntryDao {
    @Query("SELECT * FROM food_entries WHERE dateKey = :date ORDER BY timestamp DESC")
    fun getEntriesForDate(date: String): Flow<List<FoodEntry>>

    @Query("SELECT * FROM food_entries ORDER BY timestamp DESC LIMIT 50")
    fun getRecentEntries(): Flow<List<FoodEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: FoodEntry): Long

    @Delete
    suspend fun deleteEntry(entry: FoodEntry)

    @Query("DELETE FROM food_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT SUM(calories) FROM food_entries WHERE dateKey = :date")
    fun getTotalCaloriesForDate(date: String): Flow<Float?>

    @Query("SELECT dateKey, SUM(calories) as totalCalories, SUM(carbs) as totalCarbs, SUM(protein) as totalProtein, SUM(fat) as totalFat FROM food_entries GROUP BY dateKey ORDER BY dateKey DESC LIMIT 7")
    fun getWeeklyNutrition(): Flow<List<DailyNutritionTuple>>
}

@Dao
interface AthleteGoalDao {
    @Query("SELECT * FROM athlete_goals WHERE id = 1")
    fun getGoal(): Flow<AthleteGoal?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGoal(goal: AthleteGoal)
}

data class DailyNutritionTuple(
    val dateKey: String,
    val totalCalories: Float,
    val totalCarbs: Float,
    val totalProtein: Float,
    val totalFat: Float
)
