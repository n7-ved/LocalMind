package com.n7.localmind.core.cache

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json


class CacheObjectImpl<T>(
    private val context: Context,
    private val key: String,
    private val serializer: KSerializer<T>,
    private val defaultValue: T,
) : CacheObject<T> {

    companion object{
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cache-datastore")
    }

    private val dataStore = context.dataStore

    private val preferencesKey = stringPreferencesKey(key)

    override suspend fun put(value: T) {
        dataStore.edit { preferences ->
            preferences[preferencesKey] = Json.encodeToString(serializer, value)
        }
    }

    override suspend fun get(): T {
        val jsonString = dataStore.data.first()[preferencesKey]
        return jsonString?.let { Json.decodeFromString(serializer, it) } ?: defaultValue
    }

    override fun observe(): Flow<T> {
        return dataStore.data.map { preferences ->
            preferences[preferencesKey]?.let { Json.decodeFromString(serializer, it) } ?: defaultValue
        }
    }
} 