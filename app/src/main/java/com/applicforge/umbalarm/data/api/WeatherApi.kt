package com.applicforge.umbalarm.data.api

import com.applicforge.umbalarm.data.api.dto.CurrentWeatherDto
import com.applicforge.umbalarm.data.api.dto.ForecastDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * ☂️ OpenWeatherMap API 인터페이스
 */
interface WeatherApi {

    /**
     * 현재 날씨 조회
     */
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "kr"
    ): Response<CurrentWeatherDto>

    /**
     * 5일 예보 조회 (3시간 간격)
     */
    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "kr"
    ): Response<ForecastDto>

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        
        // 날씨 상태 ID 상수들
        val THUNDERSTORM_GROUP = 200..299  // 뇌우
        val DRIZZLE_GROUP = 300..399       // 이슬비
        val RAIN_GROUP = 500..599          // 비
        val SNOW_GROUP = 600..699          // 눈
        const val CLEAR_SKY = 800                // 맑음
        val CLOUDS_GROUP = 801..899        // 구름
    }
} 