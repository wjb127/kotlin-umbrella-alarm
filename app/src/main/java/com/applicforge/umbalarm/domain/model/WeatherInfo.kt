package com.applicforge.umbalarm.domain.model

/**
 * ☂️ 날씨 정보 모델
 */
data class WeatherInfo(
    val location: String,                    // 지역명
    val currentTemp: Double,                 // 현재 온도
    val description: String,                 // 날씨 설명
    val rainProbability: Int,               // 강수 확률 (%)
    val humidity: Int,                      // 습도 (%)
    val windSpeed: Double,                  // 풍속 (m/s)
    val weatherType: WeatherType,           // 날씨 유형
    val umbrellaNeeded: UmbrellaStatus,     // 우산 필요성
    val lastUpdated: Long                   // 마지막 업데이트 시간
)

/**
 * 날씨 유형
 */
enum class WeatherType {
    SUNNY,      // 맑음
    CLOUDY,     // 흐림
    RAINY,      // 비
    STORMY,     // 폭풍
    SNOWY       // 눈
}

/**
 * 우산 필요성
 */
enum class UmbrellaStatus {
    NEEDED,     // 필요
    NOT_NEEDED, // 불필요
    MAYBE       // 고려해볼만함
}

/**
 * 날씨 예보 정보
 */
data class WeatherForecast(
    val date: String,                       // 날짜
    val maxTemp: Double,                    // 최고 온도
    val minTemp: Double,                    // 최저 온도
    val weatherType: WeatherType,           // 날씨 유형
    val rainProbability: Int,               // 강수 확률
    val umbrellaNeeded: UmbrellaStatus      // 우산 필요성
)

/**
 * 우산 알림 정보
 */
data class UmbrellaAlert(
    val title: String,                      // 알림 제목
    val message: String,                    // 알림 메시지
    val alertType: AlertType,               // 알림 유형
    val time: String,                       // 알림 시간
    val weatherInfo: WeatherInfo            // 관련 날씨 정보
)

/**
 * 알림 유형
 */
enum class AlertType {
    MORNING_BRIEFING,   // 아침 브리핑
    UMBRELLA_REMINDER,  // 우산 알림
    WEATHER_CHANGE,     // 날씨 변화
    SEVERE_WEATHER      // 악천후
} 