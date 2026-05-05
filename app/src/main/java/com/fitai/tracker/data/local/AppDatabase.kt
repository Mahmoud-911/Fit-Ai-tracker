package com.fitai.tracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fitai.tracker.data.model.AthleteGoal
import com.fitai.tracker.data.model.FoodEntry

@Database(
    entities = [FoodEntry::class, AthleteGoal::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodEntryDao(): FoodEntryDao
    abstract fun athleteGoalDao(): AthleteGoalDao
}
