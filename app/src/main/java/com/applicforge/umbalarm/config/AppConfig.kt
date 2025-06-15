package com.applicforge.umbalarm.config

/**
 * ☂️ 우산 알림 앱 설정 파일
 * 
 * 🌦️ 날씨와 우산 필요성에 따른 알림 서비스를 제공하는 앱
 * 
 * 🔧 주요 기능:
 * 1. 날씨 정보 기반 우산 필요 알림
 * 2. 지역별 맞춤 알림 설정
 * 3. 시간대별 알림 스케줄링
 * 4. 날씨 변화 실시간 푸시 알림
 */
object AppConfig {
    
    // 📦 앱 패키지명 (Android 애플리케이션 ID)
    const val PACKAGE_NAME = "com.applicforge.umbalarm"
    
    // 🆔 앱 고유 식별자 
    const val APP_ID = "550e8400-e29b-41d4-a716-446655440001"
    
    // 📱 앱 이름
    const val APP_NAME = "우산 알림"
    
    // 🌐 서버 기본 URL
    const val SERVER_BASE_URL = "http://localhost:3000"
    
    // 🔔 FCM 토픽 설정 (우산 알림 앱 전용)
    object FCM {
        const val WEATHER_ALERTS = "weather_alerts"           // 날씨 경보
        const val UMBRELLA_REMINDERS = "umbrella_reminders"   // 우산 챙기기 알림
        const val RAIN_NOTIFICATIONS = "rain_notifications"   // 비 예보 알림
        const val LOCATION_WEATHER = "location_weather"       // 지역별 날씨
        const val MORNING_BRIEFING = "morning_briefing"       // 아침 날씨 브리핑
        const val EVENING_FORECAST = "evening_forecast"       // 저녁 내일 예보
        const val SEVERE_WEATHER = "severe_weather"           // 악천후 경보
        const val APP_UPDATES = "app_updates"                 // 앱 업데이트
        
        // 모든 토픽 리스트
        val ALL_TOPICS = listOf(
            WEATHER_ALERTS,
            UMBRELLA_REMINDERS,
            RAIN_NOTIFICATIONS,
            LOCATION_WEATHER,
            MORNING_BRIEFING,
            EVENING_FORECAST,
            SEVERE_WEATHER,
            APP_UPDATES
        )
        
        // 사용자에게 보여줄 토픽 (설정에서 토글 가능)
        val USER_VISIBLE_TOPICS = listOf(
            WEATHER_ALERTS,
            UMBRELLA_REMINDERS,
            RAIN_NOTIFICATIONS,
            MORNING_BRIEFING,
            EVENING_FORECAST
        )
        
        // 자동 구독 토픽 (필수 알림)
        val AUTO_SUBSCRIBE_TOPICS = listOf(
            SEVERE_WEATHER,  // 악천후는 안전을 위해 필수
            APP_UPDATES      // 업데이트 알림
        )
        
        // 숨겨진 토픽 (시스템용)
        val HIDDEN_TOPICS = listOf(
            LOCATION_WEATHER
        )
        
        // 토픽 표시 이름
        val TOPIC_DISPLAY_NAMES = mapOf(
            WEATHER_ALERTS to "🌦️ 날씨 경보",
            UMBRELLA_REMINDERS to "☂️ 우산 알림",
            RAIN_NOTIFICATIONS to "🌧️ 비 예보",
            LOCATION_WEATHER to "📍 지역 날씨",
            MORNING_BRIEFING to "🌅 아침 브리핑",
            EVENING_FORECAST to "🌙 저녁 예보",
            SEVERE_WEATHER to "⚠️ 악천후 경보",
            APP_UPDATES to "🔄 앱 업데이트"
        )
        
        // 토픽 설명
        val TOPIC_DESCRIPTIONS = mapOf(
            WEATHER_ALERTS to "중요한 날씨 변화 및 경보 알림",
            UMBRELLA_REMINDERS to "우산이 필요한 날 미리 알림",
            RAIN_NOTIFICATIONS to "비 예보 및 강수 확률 정보",
            LOCATION_WEATHER to "현재 위치 기반 날씨 정보",
            MORNING_BRIEFING to "오늘의 날씨와 우산 필요성 브리핑",
            EVENING_FORECAST to "내일 날씨 예보 및 준비사항",
            SEVERE_WEATHER to "태풍, 폭우 등 위험 기상 경보",
            APP_UPDATES to "새로운 기능 및 업데이트 안내"
        )
    }
    
    // 🎨 디자인 설정 (우산 앱 테마)
    object Design {
        const val DEFAULT_PRIMARY_COLOR = "#1976D2"      // 파란색 (하늘)
        const val DEFAULT_SECONDARY_COLOR = "#FFB74D"    // 주황색 (해)
        const val DEFAULT_BACKGROUND_COLOR = "#F5F5F5"   // 연한 회색
        const val DEFAULT_TEXT_COLOR = "#212121"         // 진한 회색
        const val RAIN_COLOR = "#607D8B"                 // 비 색상
        const val SUNNY_COLOR = "#FFC107"                // 맑음 색상
    }
    
    // ☂️ 우산 알림 관련 설정
    object Umbrella {
        const val RAIN_PROBABILITY_THRESHOLD = 30        // 비 확률 30% 이상시 알림
        const val MORNING_ALERT_TIME = "07:00"          // 아침 알림 시간
        const val EVENING_ALERT_TIME = "18:00"          // 저녁 알림 시간
        const val WEATHER_UPDATE_INTERVAL = 3600000L    // 1시간마다 날씨 업데이트 (ms)
    }
    
    // 🔧 개발/디버그 설정
    object Debug {
        const val ENABLE_LOGGING = true
        const val LOG_TAG_PREFIX = "UMBRELLA"
    }
} 