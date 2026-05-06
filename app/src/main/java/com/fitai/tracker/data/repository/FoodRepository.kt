package com.fitai.tracker.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.fitai.tracker.api.ClaudeApiService
import com.fitai.tracker.api.ClaudeContent
import com.fitai.tracker.api.ClaudeMessage
import com.fitai.tracker.api.ClaudeRequest
import com.fitai.tracker.api.ImageSource
import com.fitai.tracker.data.local.AppDatabase
import com.fitai.tracker.data.local.DailyNutritionTuple
import com.fitai.tracker.data.model.AthleteGoal
import com.fitai.tracker.data.model.FoodEntry
import com.fitai.tracker.data.model.GoalType
import com.fitai.tracker.data.model.NutritionInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepository @Inject constructor(
    private val db: AppDatabase,
    private val claudeApi: ClaudeApiService,
    @ApplicationContext private val context: Context
) {
    fun getEntriesForToday(): Flow<List<FoodEntry>> =
        db.foodEntryDao().getEntriesForDate(LocalDate.now().toString())

    fun getEntriesForDate(date: String): Flow<List<FoodEntry>> =
        db.foodEntryDao().getEntriesForDate(date)

    fun getRecentEntries(): Flow<List<FoodEntry>> =
        db.foodEntryDao().getRecentEntries()

    fun getWeeklyNutrition(): Flow<List<DailyNutritionTuple>> =
        db.foodEntryDao().getWeeklyNutrition()

    fun getAthleteGoal(): Flow<AthleteGoal?> =
        db.athleteGoalDao().getGoal()

    suspend fun addFoodEntry(entry: FoodEntry): Long =
        db.foodEntryDao().insertEntry(entry)

    suspend fun deleteFoodEntry(entry: FoodEntry) =
        db.foodEntryDao().deleteEntry(entry)

    suspend fun saveAthleteGoal(goal: AthleteGoal) =
        db.athleteGoalDao().saveGoal(goal)

    suspend fun analyzeFoodImage(imageUri: Uri, apiKey: String): Result<NutritionInfo> {
        return try {
            val bitmap = loadAndResizeBitmap(imageUri) ?: return Result.failure(Exception("Cannot load image"))
            val base64Image = bitmapToBase64(bitmap)

            val prompt = """
                Analyze this food image for a football (soccer) athlete. Provide a precise nutritional breakdown.

                Return ONLY a valid JSON object in this exact format:
                {
                  "foodName": "name of main dish/food",
                  "servingSize": "estimated portion size",
                  "calories": 000.0,
                  "carbs": 00.0,
                  "protein": 00.0,
                  "fat": 00.0,
                  "fiber": 00.0,
                  "confidence": 0.0,
                  "additionalItems": [
                    {
                      "foodName": "side dish name",
                      "servingSize": "portion",
                      "calories": 000.0,
                      "carbs": 00.0,
                      "protein": 00.0,
                      "fat": 00.0,
                      "fiber": 00.0,
                      "confidence": 0.0,
                      "additionalItems": []
                    }
                  ],
                  "tips": "Brief tip for a football player about this meal"
                }

                Use grams for macros. Be accurate. Include all visible food items.
            """.trimIndent()

            val request = ClaudeRequest(
                messages = listOf(
                    ClaudeMessage(
                        content = listOf(
                            ClaudeContent.Image(
                                source = ImageSource(
                                    mediaType = "image/jpeg",
                                    data = base64Image
                                )
                            ),
                            ClaudeContent.Text(text = prompt)
                        )
                    )
                )
            )

            val response = claudeApi.analyzeFood(
                apiKey = apiKey,
                request = request
            )

            val jsonText = response.content.firstOrNull()?.text
                ?: return Result.failure(Exception("Empty response"))

            val cleanJson = jsonText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val json = JSONObject(cleanJson)
            val nutritionInfo = parseNutritionJson(json)
            Result.success(nutritionInfo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseNutritionJson(json: JSONObject): NutritionInfo {
        val additionalItems = mutableListOf<NutritionInfo>()
        val additionalArray = json.optJSONArray("additionalItems")
        if (additionalArray != null) {
            for (i in 0 until additionalArray.length()) {
                additionalItems.add(parseNutritionJson(additionalArray.getJSONObject(i)))
            }
        }

        return NutritionInfo(
            foodName = json.optString("foodName", "Unknown Food"),
            calories = json.optDouble("calories", 0.0).toFloat(),
            carbs = json.optDouble("carbs", 0.0).toFloat(),
            protein = json.optDouble("protein", 0.0).toFloat(),
            fat = json.optDouble("fat", 0.0).toFloat(),
            fiber = json.optDouble("fiber", 0.0).toFloat(),
            servingSize = json.optString("servingSize", "1 serving"),
            confidence = json.optDouble("confidence", 0.8).toFloat(),
            additionalItems = additionalItems,
            tips = json.optString("tips", "")
        )
    }

    private fun loadAndResizeBitmap(uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val original = BitmapFactory.decodeStream(stream)
                val maxDim = 1024
                if (original.width > maxDim || original.height > maxDim) {
                    val scale = maxDim.toFloat() / maxOf(original.width, original.height)
                    Bitmap.createScaledBitmap(
                        original,
                        (original.width * scale).toInt(),
                        (original.height * scale).toInt(),
                        true
                    )
                } else original
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    fun generateWorkoutPlan(goal: AthleteGoal): List<String> {
        return when (goal.goalType) {
            GoalType.BUILD_MUSCLE -> listOf(
                "🍗 Eat 1.8–2.2g protein per kg bodyweight daily",
                "🍚 Prioritize complex carbs: brown rice, oats, sweet potato",
                "⏰ Eat within 30 mins post-training (protein + carbs)",
                "🥚 Include eggs, chicken, fish, legumes in every main meal",
                "💧 Drink 3–4L water daily, more on training days",
                "🥑 Don't skip healthy fats: avocado, nuts, olive oil",
                "🌙 Casein protein or Greek yogurt before bed for overnight recovery",
                "📈 Aim for 300–500 calorie surplus above maintenance"
            )
            GoalType.GAIN_WEIGHT -> listOf(
                "📊 Create a 500–700 calorie surplus daily",
                "🍝 Add calorie-dense foods: nut butters, whole milk, pasta",
                "🥤 Drink calories: smoothies with banana, oats, protein powder",
                "⏰ Eat every 3 hours — never skip meals",
                "🍽️ Increase portion sizes gradually",
                "🏋️ Focus on compound lifts: squats, deadlifts, bench press",
                "😴 Sleep 8–9 hours — most muscle is built during sleep",
                "🔄 Track weight weekly, adjust calories if not gaining"
            )
            GoalType.LOSE_WEIGHT -> listOf(
                "📉 Create a 300–500 calorie deficit (not more!)",
                "🥩 Keep protein HIGH: 2.0–2.4g per kg to preserve muscle",
                "🥗 Fill 50% of plate with vegetables at each meal",
                "🍭 Cut refined sugars and ultra-processed foods",
                "💧 Drink water before each meal to reduce hunger",
                "🚫 Avoid liquid calories except protein shakes",
                "🏃 Add 2–3 light cardio sessions per week (30 min)",
                "⚽ Match-day nutrition stays the same — fuel performance!"
            )
            GoalType.PEAK_PERFORMANCE -> listOf(
                "⚡ 6–10g carbs per kg on match/training days",
                "🍌 Pre-match meal: high carbs, moderate protein, low fat (3h before)",
                "🧃 Half-time: banana, energy gel, or sports drink",
                "🔄 Post-match recovery: 1:3 protein-to-carb ratio within 30 mins",
                "💊 Consider creatine (5g/day) for explosive sprint power",
                "🫀 Beetroot juice 2h before for improved VO2 max",
                "🧂 Replenish electrolytes after intense sessions",
                "📅 Carb-load 2 days before important matches"
            )
            GoalType.MAINTAIN -> listOf(
                "⚖️ Match calorie intake to energy expenditure",
                "🔄 Maintain training schedule consistently",
                "📊 Monitor body weight weekly — adjust if drifting",
                "🌈 Eat a variety of whole foods for micronutrients",
                "🥩 Keep protein at 1.6g per kg for muscle maintenance",
                "💧 Stay hydrated — 35ml per kg bodyweight daily",
                "🍎 Time meals around training sessions",
                "😴 Prioritize sleep and recovery"
            )
        }
    }
}
