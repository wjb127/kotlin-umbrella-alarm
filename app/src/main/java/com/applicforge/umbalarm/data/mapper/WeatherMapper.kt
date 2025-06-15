package com.applicforge.umbalarm.data.mapper

import com.applicforge.umbalarm.data.api.dto.CurrentWeatherDto
import com.applicforge.umbalarm.data.api.dto.ForecastDto
import com.applicforge.umbalarm.data.api.dto.ForecastItemDto
import com.applicforge.umbalarm.domain.model.WeatherInfo
import com.applicforge.umbalarm.domain.model.WeatherForecast
import com.applicforge.umbalarm.domain.model.WeatherType
import com.applicforge.umbalarm.domain.model.UmbrellaStatus
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ☂️ 날씨 데이터 매퍼
 */
@Singleton
class WeatherMapper @Inject constructor() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /**
     * 현재 날씨 DTO를 도메인 모델로 변환
     */
    fun mapCurrentWeatherToDomain(dto: CurrentWeatherDto): WeatherInfo {
        val weatherType = mapWeatherIdToType(dto.weather.firstOrNull()?.id ?: 800)
        val rainProbability = calculateRainProbability(dto)
        val umbrellaStatus = determineUmbrellaStatus(weatherType, rainProbability)

        return WeatherInfo(
            location = dto.name,
            currentTemp = dto.main.temp,
            description = dto.weather.firstOrNull()?.description ?: "정보 없음",
            rainProbability = rainProbability,
            humidity = dto.main.humidity,
            windSpeed = dto.wind.speed,
            weatherType = weatherType,
            umbrellaNeeded = umbrellaStatus,
            lastUpdated = System.currentTimeMillis()
        )
    }

    /**
     * 예보 DTO를 도메인 모델로 변환
     */
    fun mapForecastToDomain(dto: ForecastDto): List<WeatherForecast> {
        return dto.list.map { forecastItem ->
            val weatherType = mapWeatherIdToType(forecastItem.weather.firstOrNull()?.id ?: 800)
            val rainProbability = (forecastItem.pop * 100).toInt()
            val umbrellaStatus = determineUmbrellaStatus(weatherType, rainProbability)

            WeatherForecast(
                date = forecastItem.dtTxt.substring(0, 10), // yyyy-MM-dd 형식
                maxTemp = forecastItem.main.tempMax,
                minTemp = forecastItem.main.tempMin,
                weatherType = weatherType,
                rainProbability = rainProbability,
                umbrellaNeeded = umbrellaStatus
            )
        }
    }

    /**
     * 오늘의 예보만 필터링
     */
    fun getTodayForecast(forecasts: List<WeatherForecast>): List<WeatherForecast> {
        val today = dateFormat.format(Date())
        return forecasts.filter { it.date == today }
    }

    /**
     * 내일의 예보만 필터링
     */
    fun getTomorrowForecast(forecasts: List<WeatherForecast>): List<WeatherForecast> {
        val tomorrow = Calendar.getInstance().apply {
            time = Date()
            add(Calendar.DAY_OF_MONTH, 1)
        }
        val tomorrowDate = dateFormat.format(tomorrow.time)
        return forecasts.filter { it.date == tomorrowDate }
    }

    /**
     * 날씨 ID를 WeatherType으로 변환
     */
    private fun mapWeatherIdToType(weatherId: Int): WeatherType {
        return when (weatherId) {
            in 200..299 -> WeatherType.STORMY    // 뇌우
            in 300..399 -> WeatherType.RAINY     // 이슬비
            in 500..599 -> WeatherType.RAINY     // 비
            in 600..699 -> WeatherType.SNOWY     // 눈
            800 -> WeatherType.SUNNY             // 맑음
            in 801..899 -> WeatherType.CLOUDY    // 구름
            else -> WeatherType.CLOUDY
        }
    }

    /**
     * 현재 날씨에서 강수 확률 계산 (근사치)
     */
    private fun calculateRainProbability(dto: CurrentWeatherDto): Int {
        val weatherId = dto.weather.firstOrNull()?.id ?: 800
        val hasRain = dto.rain != null
        val hasSnow = dto.snow != null
        val humidity = dto.main.humidity

        return when {
            weatherId in 200..599 || hasRain || hasSnow -> {
                // 비/눈이 있으면 높은 확률
                when {
                    hasRain || hasSnow -> 90
                    weatherId in 200..299 -> 85  // 뇌우
                    weatherId in 300..399 -> 70  // 이슬비
                    weatherId in 500..599 -> 80  // 비
                    else -> 60
                }
            }
            humidity > 80 -> 40  // 습도가 높으면 중간 확률
            humidity > 60 -> 20  // 습도가 보통이면 낮은 확률
            else -> 5            // 습도가 낮으면 매우 낮은 확률
        }
    }

    /**
     * 우산 필요성 판단
     */
    private fun determineUmbrellaStatus(weatherType: WeatherType, rainProbability: Int): UmbrellaStatus {
        return when {
            weatherType == WeatherType.RAINY || weatherType == WeatherType.STORMY -> UmbrellaStatus.NEEDED
            rainProbability >= 60 -> UmbrellaStatus.NEEDED
            rainProbability >= 30 -> UmbrellaStatus.MAYBE
            else -> UmbrellaStatus.NOT_NEEDED
        }
    }
} 