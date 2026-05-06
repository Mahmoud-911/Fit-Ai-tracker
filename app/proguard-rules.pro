-keep class com.fitai.tracker.api.** { *; }
-keep class com.fitai.tracker.data.model.** { *; }
-keep class com.fitai.tracker.data.local.DailyNutritionTuple { *; }

-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**

# Retrofit
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Gson — keep TypeToken and SerializedName
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class sun.misc.Unsafe { *; }

# Room — keep generated DAOs/database
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Hilt
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keep,allowobfuscation,allowshrinking @dagger.hilt.android.lifecycle.HiltViewModel class *

# Kotlin metadata for sealed classes
-keepattributes KotlinMetadata
