package com.kaizehuang.vernier.features.update.data

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import java.io.IOException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first

internal interface UpdateCompareCache {
    suspend fun read(key: String): String?

    suspend fun write(key: String, json: String)
}

internal class UpdateCacheStore private constructor(context: Context) : UpdateCompareCache {

    private val dataStore = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("nightly_update_cache") },
    )

    override suspend fun read(key: String): String? {
        val prefs = dataStore.data
            .catch { throwable ->
                if (throwable is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw throwable
                }
            }
            .first()
        return if (prefs[KEY_COMPARE_KEY] == key) prefs[KEY_COMPARE_JSON] else null
    }

    override suspend fun write(key: String, json: String) {
        dataStore.edit { prefs ->
            prefs[KEY_COMPARE_KEY] = key
            prefs[KEY_COMPARE_JSON] = json
        }
    }

    companion object {
        @Volatile
        private var instance: UpdateCacheStore? = null

        private val KEY_COMPARE_KEY = stringPreferencesKey("compare_key")
        private val KEY_COMPARE_JSON = stringPreferencesKey("compare_json")

        fun getInstance(context: Context): UpdateCacheStore {
            return instance ?: synchronized(this) {
                instance ?: UpdateCacheStore(context.applicationContext).also { created ->
                    instance = created
                }
            }
        }
    }
}
