package com.applicforge.umbalarm.manager

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    private val context: Context
) {
    
    companion object {
        private const val PREFS_NAME = "fcm_topic_prefs"
        private const val KEY_PREFIX_TOPIC = "topic_subscribed_"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    fun isTopicSubscribed(topicName: String): Boolean {
        // 기본값은 true (기본 토픽들은 구독된 상태로 시작)
        return prefs.getBoolean(KEY_PREFIX_TOPIC + topicName, true)
    }
    
    fun setTopicSubscribed(topicName: String, isSubscribed: Boolean) {
        prefs.edit()
            .putBoolean(KEY_PREFIX_TOPIC + topicName, isSubscribed)
            .apply()
    }
    
    fun getAllSubscribedTopics(): Set<String> {
        return prefs.all
            .filterKeys { it.startsWith(KEY_PREFIX_TOPIC) }
            .filterValues { it as? Boolean == true }
            .keys
            .map { it.removePrefix(KEY_PREFIX_TOPIC) }
            .toSet()
    }
    
    fun getAllTopicPreferences(): Map<String, Boolean> {
        return prefs.all
            .filterKeys { it.startsWith(KEY_PREFIX_TOPIC) }
            .mapKeys { it.key.removePrefix(KEY_PREFIX_TOPIC) }
            .mapValues { it.value as? Boolean ?: true }
    }
} 