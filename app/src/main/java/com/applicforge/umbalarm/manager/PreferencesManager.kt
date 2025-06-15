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
        
        // ☂️ 우산 알림 관련 키들
        private const val KEY_WEATHER_API_KEY = "weather_api_key"
        private const val KEY_LAST_UMBRELLA_NOTIFICATION = "last_umbrella_notification"
        private const val KEY_UMBRELLA_NOTIFICATIONS_ENABLED = "umbrella_notifications_enabled"
        private const val KEY_NOTIFICATION_START_HOUR = "notification_start_hour"
        private const val KEY_NOTIFICATION_END_HOUR = "notification_end_hour"
        private const val KEY_RAIN_THRESHOLD = "rain_threshold"
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
    
    // ☂️ 우산 알림 관련 메서드들
    
    /**
     * 날씨 API 키 저장
     */
    fun setWeatherApiKey(apiKey: String) {
        prefs.edit()
            .putString(KEY_WEATHER_API_KEY, apiKey)
            .apply()
    }
    
    /**
     * 날씨 API 키 가져오기
     */
    fun getWeatherApiKey(): String? {
        return prefs.getString(KEY_WEATHER_API_KEY, null)
    }
    
    /**
     * 마지막 우산 알림 시간 저장
     */
    fun setLastUmbrellaNotificationTime(timestamp: Long) {
        prefs.edit()
            .putLong(KEY_LAST_UMBRELLA_NOTIFICATION, timestamp)
            .apply()
    }
    
    /**
     * 마지막 우산 알림 시간 가져오기
     */
    fun getLastUmbrellaNotificationTime(): Long {
        return prefs.getLong(KEY_LAST_UMBRELLA_NOTIFICATION, 0L)
    }
    
    /**
     * 우산 알림 활성화 상태 설정
     */
    fun setUmbrellaNotificationsEnabled(enabled: Boolean) {
        prefs.edit()
            .putBoolean(KEY_UMBRELLA_NOTIFICATIONS_ENABLED, enabled)
            .apply()
    }
    
    /**
     * 우산 알림 활성화 상태 확인
     */
    fun isUmbrellaNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_UMBRELLA_NOTIFICATIONS_ENABLED, true)
    }
    
    /**
     * 알림 시작 시간 설정 (0-23시)
     */
    fun setNotificationStartHour(hour: Int) {
        prefs.edit()
            .putInt(KEY_NOTIFICATION_START_HOUR, hour)
            .apply()
    }
    
    /**
     * 알림 시작 시간 가져오기 (기본값: 6시)
     */
    fun getNotificationStartHour(): Int {
        return prefs.getInt(KEY_NOTIFICATION_START_HOUR, 6)
    }
    
    /**
     * 알림 종료 시간 설정 (0-23시)
     */
    fun setNotificationEndHour(hour: Int) {
        prefs.edit()
            .putInt(KEY_NOTIFICATION_END_HOUR, hour)
            .apply()
    }
    
    /**
     * 알림 종료 시간 가져오기 (기본값: 19시)
     */
    fun getNotificationEndHour(): Int {
        return prefs.getInt(KEY_NOTIFICATION_END_HOUR, 19)
    }
    
    /**
     * 비 확률 임계값 설정 (%)
     */
    fun setRainThreshold(threshold: Int) {
        prefs.edit()
            .putInt(KEY_RAIN_THRESHOLD, threshold)
            .apply()
    }
    
    /**
     * 비 확률 임계값 가져오기 (기본값: 30%)
     */
    fun getRainThreshold(): Int {
        return prefs.getInt(KEY_RAIN_THRESHOLD, 30)
    }
} 