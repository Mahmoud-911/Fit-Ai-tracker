package com.fitai.tracker.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore by preferencesDataStore(name = "fitai_settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val claudeApiKeyKey = stringPreferencesKey("claude_api_key")

    val claudeApiKey: Flow<String> = context.settingsDataStore.data
        .map { it[claudeApiKeyKey].orEmpty() }

    suspend fun setClaudeApiKey(key: String) {
        context.settingsDataStore.edit { it[claudeApiKeyKey] = key }
    }
}
