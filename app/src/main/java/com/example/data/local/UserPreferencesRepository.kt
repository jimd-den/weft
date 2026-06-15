package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepository(private val context: Context) {

    private val KEY_OPENROUTER_API_KEY = stringPreferencesKey("openrouter_api_key")
    private val KEY_SELECTED_AGENT = stringPreferencesKey("selected_agent")

    val apiKeyFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_OPENROUTER_API_KEY]
    }

    val selectedAgentFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_SELECTED_AGENT] ?: "google/gemini-2.5-flash"
    }

    suspend fun saveApiKey(apiKey: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_OPENROUTER_API_KEY] = apiKey
        }
    }

    suspend fun saveSelectedAgent(agent: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SELECTED_AGENT] = agent
        }
    }
}
