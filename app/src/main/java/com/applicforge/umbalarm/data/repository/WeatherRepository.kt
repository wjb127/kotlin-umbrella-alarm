package com.applicforge.umbalarm.data.repository

import com.applicforge.umbalarm.data.api.WeatherApi
import com.applicforge.umbalarm.data.mapper.WeatherMapper
import com.applicforge.umbalarm.domain.model.WeatherInfo
import com.applicforge.umbalarm.domain.model.WeatherForecast
import com.applicforge.umbalarm.config.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ☂️ 날씨 데이터 Repository
 */
@Singleton
class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi,
    private val weatherMapper: WeatherMapper
) {

    /**
     * 현재 날씨 정보 조회
     */
    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): Result<WeatherInfo> {
        return try {
            val response = weatherApi.getCurrentWeather(latitude, longitude, apiKey)
            if (response.isSuccessful && response.body() != null) {
                val weatherInfo = weatherMapper.mapCurrentWeatherToDomain(response.body()!!)
                Result.success(weatherInfo)
            } else {
                Result.failure(Exception("날씨 정보를 가져올 수 없습니다: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 5일 예보 조회
     */
    suspend fun getForecast(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): Result<List<WeatherForecast>> {
        return try {
            val response = weatherApi.getForecast(latitude, longitude, apiKey)
            if (response.isSuccessful && response.body() != null) {
                val forecasts = weatherMapper.mapForecastToDomain(response.body()!!)
                Result.success(forecasts)
            } else {
                Result.failure(Exception("예보 정보를 가져올 수 없습니다: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 오늘의 날씨 예보 조회
     */
    suspend fun getTodayForecast(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): Result<List<WeatherForecast>> {
        return try {
            val forecastResult = getForecast(latitude, longitude, apiKey)
            if (forecastResult.isSuccess) {
                val allForecasts = forecastResult.getOrNull() ?: emptyList()
                val todayForecasts = weatherMapper.getTodayForecast(allForecasts)
                Result.success(todayForecasts)
            } else {
                forecastResult
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 내일의 날씨 예보 조회
     */
    suspend fun getTomorrowForecast(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): Result<List<WeatherForecast>> {
        return try {
            val forecastResult = getForecast(latitude, longitude, apiKey)
            if (forecastResult.isSuccess) {
                val allForecasts = forecastResult.getOrNull() ?: emptyList()
                val tomorrowForecasts = weatherMapper.getTomorrowForecast(allForecasts)
                Result.success(tomorrowForecasts)
            } else {
                forecastResult
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 우산이 필요한지 확인
     */
    suspend fun checkUmbrellaNeeded(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): Result<Boolean> {
        return try {
            val weatherResult = getCurrentWeather(latitude, longitude, apiKey)
            val forecastResult = getTodayForecast(latitude, longitude, apiKey)

            if (weatherResult.isSuccess) {
                val currentWeather = weatherResult.getOrNull()
                val todayForecasts = forecastResult.getOrNull() ?: emptyList()

                // 현재 날씨 또는 오늘 예보에서 우산이 필요한지 확인
                val currentNeedsUmbrella = currentWeather?.umbrellaNeeded?.let { 
                    it != com.applicforge.umbalarm.domain.model.UmbrellaStatus.NOT_NEEDED 
                } ?: false

                val forecastNeedsUmbrella = todayForecasts.any { 
                    it.umbrellaNeeded != com.applicforge.umbalarm.domain.model.UmbrellaStatus.NOT_NEEDED 
                }

                Result.success(currentNeedsUmbrella || forecastNeedsUmbrella)
            } else {
                weatherResult.fold(
                    onSuccess = { Result.success(false) },
                    onFailure = { Result.failure(it) }
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 