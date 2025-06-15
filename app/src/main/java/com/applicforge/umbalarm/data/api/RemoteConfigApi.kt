package com.applicforge.umbalarm.data.api

import com.applicforge.umbalarm.data.api.dto.AppConfigDto
import com.applicforge.umbalarm.data.api.dto.ButtonDto
import com.applicforge.umbalarm.data.api.dto.FcmTokenDto
import com.applicforge.umbalarm.data.api.dto.MenuDto
import com.applicforge.umbalarm.data.api.dto.ToolbarDto
import com.applicforge.umbalarm.data.api.dto.StyleDto
import com.applicforge.umbalarm.data.api.dto.FcmTopicDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// 앱 정보 응답용 DTO
data class AppInfoDto(
    val id: String,
    val app_name: String,
    val app_id: String,
    val package_name: String,
    val version: String,
    val description: String,
    val status: String,
    val created_at: String,
    val updated_at: String
)

// Config API 통합 응답용 DTO
data class ConfigResponseDto(
    val message: String,
    val timestamp: String,
    val appId: String,
    val app: AppInfoDto,
    val menus: List<MenuDto>,
    val toolbars: List<ToolbarDto>,
    val fcm_topics: List<FcmTopicDto>,
    val styles: List<StyleDto>? = null, // styles는 응답에 없을 수도 있음
    val buttons: List<ButtonDto>? = null // buttons 추가
)

// API 응답 래퍼 DTO
data class ApiResponseWrapper<T>(
    val success: Boolean,
    val data: T,
    val message: String?,
    val error: String?
)

interface RemoteConfigApi {
    
    // 통합 설정 조회
    @GET("api/config/{appId}")
    suspend fun getAppConfig(@Path("appId") appId: String): Response<ConfigResponseDto>
    
    // 앱 정보 조회
    @GET("api/apps/{appId}")
    suspend fun getAppInfo(@Path("appId") appId: String): Response<AppInfoDto>
    
    // 메뉴 조회
    @GET("api/apps/{appId}/menus")
    suspend fun getMenus(@Path("appId") appId: String): Response<ApiResponseWrapper<List<MenuDto>>>
    
    // 툴바 조회
    @GET("api/apps/{appId}/toolbars")
    suspend fun getToolbars(@Path("appId") appId: String): Response<ApiResponseWrapper<List<ToolbarDto>>>
    
    // 버튼 조회
    @GET("api/apps/{appId}/buttons")
    suspend fun getButtons(@Path("appId") appId: String): Response<ApiResponseWrapper<List<ButtonDto>>>
    
    // FCM 토픽 조회
    @GET("api/apps/{appId}/fcm-topics")
    suspend fun getFcmTopics(@Path("appId") appId: String): Response<ApiResponseWrapper<List<FcmTopicDto>>>
    
    // 스타일 조회
    @GET("api/apps/{appId}/styles")
    suspend fun getStyles(@Path("appId") appId: String): Response<ApiResponseWrapper<List<StyleDto>>>
    
    // FCM 토큰 전송
    @POST("api/apps/{appId}/fcm/token")
    suspend fun sendFcmToken(
        @Path("appId") appId: String,
        @Body tokenDto: FcmTokenDto
    ): Response<Unit>
} 