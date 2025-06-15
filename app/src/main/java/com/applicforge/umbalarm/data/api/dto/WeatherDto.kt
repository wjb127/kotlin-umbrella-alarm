package com.applicforge.umbalarm.data.api.dto

import com.google.gson.annotations.SerializedName

/**
 * ☂️ OpenWeatherMap API 현재 날씨 응답 DTO
 */
data class CurrentWeatherDto(
    @SerializedName("coord") val coord: CoordDto,
    @SerializedName("weather") val weather: List<WeatherConditionDto>,
    @SerializedName("main") val main: MainDto,
    @SerializedName("wind") val wind: WindDto,
    @SerializedName("clouds") val clouds: CloudsDto,
    @SerializedName("rain") val rain: RainDto?,
    @SerializedName("snow") val snow: SnowDto?,
    @SerializedName("dt") val dt: Long,
    @SerializedName("timezone") val timezone: Int,
    @SerializedName("name") val name: String
)

/**
 * 좌표 정보
 */
data class CoordDto(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double
)

/**
 * 날씨 상태 정보
 */
data class WeatherConditionDto(
    @SerializedName("id") val id: Int,
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)

/**
 * 주요 날씨 정보
 */
data class MainDto(
    @SerializedName("temp") val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    @SerializedName("pressure") val pressure: Int,
    @SerializedName("humidity") val humidity: Int
)

/**
 * 바람 정보
 */
data class WindDto(
    @SerializedName("speed") val speed: Double,
    @SerializedName("deg") val deg: Int?
)

/**
 * 구름 정보
 */
data class CloudsDto(
    @SerializedName("all") val all: Int
)

/**
 * 비 정보
 */
data class RainDto(
    @SerializedName("1h") val oneHour: Double?,
    @SerializedName("3h") val threeHours: Double?
)

/**
 * 눈 정보
 */
data class SnowDto(
    @SerializedName("1h") val oneHour: Double?,
    @SerializedName("3h") val threeHours: Double?
)

/**
 * 5일 예보 응답 DTO
 */
data class ForecastDto(
    @SerializedName("cod") val cod: String,
    @SerializedName("message") val message: Int,
    @SerializedName("cnt") val cnt: Int,
    @SerializedName("list") val list: List<ForecastItemDto>,
    @SerializedName("city") val city: CityDto
)

/**
 * 예보 아이템
 */
data class ForecastItemDto(
    @SerializedName("dt") val dt: Long,
    @SerializedName("main") val main: MainDto,
    @SerializedName("weather") val weather: List<WeatherConditionDto>,
    @SerializedName("clouds") val clouds: CloudsDto,
    @SerializedName("wind") val wind: WindDto,
    @SerializedName("rain") val rain: RainDto?,
    @SerializedName("snow") val snow: SnowDto?,
    @SerializedName("pop") val pop: Double, // 강수 확률
    @SerializedName("dt_txt") val dtTxt: String
)

/**
 * 도시 정보
 */
data class CityDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("coord") val coord: CoordDto,
    @SerializedName("country") val country: String,
    @SerializedName("timezone") val timezone: Int
) 