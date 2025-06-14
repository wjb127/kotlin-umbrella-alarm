package com.test.simple.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.test.simple.domain.model.AppConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )
    
    private val gson = Gson()
    
    fun saveAppConfig(config: AppConfig) {
        val configJson = gson.toJson(config)
        prefs.edit()
            .putString(KEY_APP_CONFIG, configJson)
            .putLong(KEY_LAST_UPDATE, System.currentTimeMillis())
            .apply()
    }
    
    fun getAppConfig(): AppConfig? {
        val configJson = prefs.getString(KEY_APP_CONFIG, null)
        return if (configJson != null) {
            try {
                gson.fromJson(configJson, AppConfig::class.java)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }
    
    fun getLastUpdateTime(): Long {
        return prefs.getLong(KEY_LAST_UPDATE, 0)
    }
    
    fun clearConfig() {
        prefs.edit()
            .remove(KEY_APP_CONFIG)
            .remove(KEY_LAST_UPDATE)
            .apply()
    }
    
    companion object {
        private const val PREFS_NAME = "remote_config_prefs"
        private const val KEY_APP_CONFIG = "app_config"
        private const val KEY_LAST_UPDATE = "last_update"
    }
} 