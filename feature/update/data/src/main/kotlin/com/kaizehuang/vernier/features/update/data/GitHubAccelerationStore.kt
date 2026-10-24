package com.kaizehuang.vernier.features.update.data

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.kaizehuang.vernier.features.update.domain.GitHubAcceleration
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GitHubAccelerationStore private constructor(context: Context) {

    private val dataStore = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("github_acceleration_prefs") },
    )

    val acceleration: Flow<GitHubAcceleration> = dataStore.data
        .catch { throwable ->
            if (throwable is IOException) {
                emit(emptyPreferences())
            } else {
                throw throwable
            }
        }
        .map { prefs ->
            when (prefs[KEY_ENABLED]) {
                null -> GitHubAcceleration.UNDECIDED
                true -> GitHubAcceleration.ENABLED
                false -> GitHubAcceleration.DISABLED
            }
        }

    suspend fun setEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_ENABLED] = enabled
        }
    }

    companion object {
        @Volatile
        private var instance: GitHubAccelerationStore? = null

        private val KEY_ENABLED = booleanPreferencesKey("enabled")

        fun getInstance(context: Context): GitHubAccelerationStore {
            return instance ?: synchronized(this) {
                instance ?: GitHubAccelerationStore(context.applicationContext).also { created ->
                    instance = created
                }
            }
        }
    }
}
