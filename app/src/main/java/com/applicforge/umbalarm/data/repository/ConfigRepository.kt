package com.applicforge.umbalarm.data.repository

import com.applicforge.umbalarm.data.api.RemoteConfigApi
import com.applicforge.umbalarm.data.api.AppInfoDto
import com.applicforge.umbalarm.data.api.ConfigResponseDto
import com.applicforge.umbalarm.data.local.ConfigPreferences
import com.applicforge.umbalarm.data.mapper.ConfigMapper
import com.applicforge.umbalarm.domain.model.AppConfig
import com.applicforge.umbalarm.domain.model.Menu
import com.applicforge.umbalarm.domain.model.MenuType
import com.applicforge.umbalarm.domain.model.ActionType
import com.applicforge.umbalarm.domain.model.Toolbar
import com.applicforge.umbalarm.domain.model.ToolbarButton
import com.applicforge.umbalarm.domain.model.ToolbarPosition
import com.applicforge.umbalarm.domain.model.Style
import com.applicforge.umbalarm.domain.model.StyleCategory
import com.applicforge.umbalarm.domain.model.FcmTopic
import com.applicforge.umbalarm.domain.model.Button
import com.applicforge.umbalarm.domain.model.ButtonType
import com.applicforge.umbalarm.domain.model.ButtonPosition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigRepository @Inject constructor(
    private val api: RemoteConfigApi,
    private val preferences: ConfigPreferences
) {
    
    // 🆕 새로운 통합 API - 패키지명 기반
    fun getAppConfigByPackage(packageName: String): Flow<Result<AppConfig>> = flow {
        try {
            android.util.Log.d("ConfigRepository", "🚀 통합 API 호출 시작")
            android.util.Log.d("ConfigRepository", "📍 엔드포인트: GET /api/config/$packageName")
            android.util.Log.d("ConfigRepository", "🌐 서버: https://remote-config-supabase.vercel.app/")
            
            val response = api.getAppConfigByPackage(packageName)
            android.util.Log.d("ConfigRepository", "📡 통합 API 응답 코드: ${response.code()}")
            
            if (response.isSuccessful && response.body() != null) {
                val supabaseResponse = response.body()!!
                android.util.Log.d("ConfigRepository", "📦 통합 응답: $supabaseResponse")
                
                if (supabaseResponse.success && supabaseResponse.data != null) {
                    val data = supabaseResponse.data
                    
                    // SupabaseConfigDataDto를 AppConfig로 변환
                    val config = AppConfig(
                        menus = data.menus.map { ConfigMapper.mapMenuDtoToDomain(it) },
                        toolbars = data.toolbars.map { ConfigMapper.mapToolbarDtoToDomain(it) },
                        fcmTopics = data.fcm_topics.map { ConfigMapper.mapFcmTopicDtoToDomain(it) },
                        styles = data.styles?.map { ConfigMapper.mapStyleDtoToDomain(it) } ?: emptyList(),
                        buttons = data.buttons?.map { ConfigMapper.mapButtonDtoToDomain(it) } ?: emptyList()
                    )
                
                    android.util.Log.d("ConfigRepository", "✅ 통합 데이터 파싱 성공!")
                    android.util.Log.d("ConfigRepository", "   📱 메뉴: ${config.menus.size}개")
                    android.util.Log.d("ConfigRepository", "   🔧 툴바: ${config.toolbars.size}개")
                    android.util.Log.d("ConfigRepository", "   📡 FCM 토픽: ${config.fcmTopics.size}개")
                    android.util.Log.d("ConfigRepository", "   🎨 스타일: ${config.styles.size}개")
                    android.util.Log.d("ConfigRepository", "   🔘 버튼: ${config.buttons.size}개")
                    
                    emit(Result.success(config))
                } else {
                    android.util.Log.d("ConfigRepository", "🔄 Mock 데이터 사용 (Supabase success=false)")
                    emit(Result.success(getMockAppConfig()))
                }
            } else {
                android.util.Log.d("ConfigRepository", "🔄 Mock 데이터 사용 (통합 API 실패)")
                emit(Result.success(getMockAppConfig()))
            }
        } catch (e: Exception) {
            android.util.Log.e("ConfigRepository", "💥 통합 API 호출 실패!")
            android.util.Log.e("ConfigRepository", "💥 예외: ${e.message}")
            android.util.Log.d("ConfigRepository", "🔄 Mock 데이터 사용 (예외)")
            emit(Result.success(getMockAppConfig()))
        }
    }

    fun getAppConfig(appId: String): Flow<Result<AppConfig>> = flow {
        try {
            // 먼저 로컬 캐시된 설정을 방출
            val cachedConfig = preferences.getAppConfig()
            if (cachedConfig != null) {
                emit(Result.success(cachedConfig))
            }
            
            // 서버에서 최신 설정 조회
            val response = api.getAppConfig(appId)
            if (response.isSuccessful && response.body() != null) {
                val configResponse = response.body()!!
                
                // ConfigResponseDto를 AppConfig로 변환
                val config = AppConfig(
                    menus = configResponse.menus.map { ConfigMapper.mapMenuDtoToDomain(it) },
                    toolbars = configResponse.toolbars.map { ConfigMapper.mapToolbarDtoToDomain(it) },
                    fcmTopics = configResponse.fcm_topics.map { ConfigMapper.mapFcmTopicDtoToDomain(it) },
                    styles = configResponse.styles?.map { ConfigMapper.mapStyleDtoToDomain(it) } ?: emptyList(),
                    buttons = configResponse.buttons?.map { ConfigMapper.mapButtonDtoToDomain(it) } ?: emptyList()
                )
                
                // 로컬에 저장
                preferences.saveAppConfig(config)
                
                // 최신 설정 방출
                emit(Result.success(config))
            } else {
                // 서버 오류 시 캐시된 설정 사용
                if (cachedConfig != null) {
                    emit(Result.success(cachedConfig))
                } else {
                    // 캐시도 없으면 Mock 데이터 사용
                    emit(Result.success(getMockAppConfig()))
                }
            }
        } catch (e: Exception) {
            // 네트워크 오류 시 캐시된 설정 사용
            val cachedConfig = preferences.getAppConfig()
            if (cachedConfig != null) {
                emit(Result.success(cachedConfig))
            } else {
                // 캐시도 없으면 Mock 데이터 사용
                emit(Result.success(getMockAppConfig()))
            }
        }
    }
    
    fun getConfigResponse(appId: String): Flow<Result<ConfigResponseDto>> = flow {
        try {
            val response = api.getAppConfig(appId)
            if (response.isSuccessful && response.body() != null) {
                val configResponse = response.body()!!
                emit(Result.success(configResponse))
            } else {
                emit(Result.success(getMockConfigResponse()))
            }
        } catch (e: Exception) {
            emit(Result.success(getMockConfigResponse()))
        }
    }
    
    fun getAppInfo(appId: String): Flow<Result<AppInfoDto>> = flow {
        try {
            val response = api.getAppInfo(appId)
            if (response.isSuccessful && response.body() != null) {
                val appInfo = response.body()!!
                emit(Result.success(appInfo))
            } else {
                emit(Result.success(getMockAppInfo()))
            }
        } catch (e: Exception) {
            emit(Result.success(getMockAppInfo()))
        }
    }
    
    fun getMenus(appId: String): Flow<Result<List<Menu>>> = flow {
        try {
            android.util.Log.d("ConfigRepository", "🚀 메뉴 API 호출 시작")
            android.util.Log.d("ConfigRepository", "📍 엔드포인트: GET /api/apps/$appId/menus")
            android.util.Log.d("ConfigRepository", "🌐 서버: ${com.applicforge.umbalarm.config.AppConfig.SERVER_BASE_URL}")
            val response = api.getMenus(appId)
            android.util.Log.d("ConfigRepository", "📡 메뉴 API 응답 코드: ${response.code()}")
            android.util.Log.d("ConfigRepository", "📄 응답 헤더: ${response.headers()}")
            android.util.Log.d("ConfigRepository", "📦 응답 body null 여부: ${response.body() == null}")
            
            if (response.isSuccessful && response.body() != null) {
                val wrapper = response.body()!!
                android.util.Log.d("ConfigRepository", "📦 메뉴 응답 wrapper: success=${wrapper.success}")
                android.util.Log.d("ConfigRepository", "📊 data null 여부: ${wrapper.data == null}")
                android.util.Log.d("ConfigRepository", "📊 data 크기: ${wrapper.data?.size ?: 0}")
                android.util.Log.d("ConfigRepository", "💬 message: ${wrapper.message}")
                android.util.Log.d("ConfigRepository", "❌ error: ${wrapper.error}")
                android.util.Log.d("ConfigRepository", "📝 전체 응답: $wrapper")
                if (wrapper.success && wrapper.data != null && wrapper.data.isNotEmpty()) {
                    val menus = wrapper.data.map { ConfigMapper.mapMenuDtoToDomain(it) }
                    android.util.Log.d("ConfigRepository", "✅ 서버 메뉴 데이터 사용: ${menus.size}개")
                    emit(Result.success(menus))
                } else {
                    android.util.Log.d("ConfigRepository", "🔄 Mock 메뉴 데이터 사용 (빈 데이터)")
                    emit(Result.success(getMockMenus()))
                }
            } else {
                android.util.Log.d("ConfigRepository", "🔄 Mock 메뉴 데이터 사용 (응답 실패)")
                emit(Result.success(getMockMenus()))
            }
        } catch (e: Exception) {
            android.util.Log.e("ConfigRepository", "💥 메뉴 API 호출 실패!")
            android.util.Log.e("ConfigRepository", "💥 예외 타입: ${e.javaClass.simpleName}")
            android.util.Log.e("ConfigRepository", "💥 예외 메시지: ${e.message}")
            android.util.Log.e("ConfigRepository", "💥 스택 트레이스: ${e.stackTrace.take(3).joinToString()}")
            android.util.Log.d("ConfigRepository", "🔄 Mock 메뉴 데이터 사용 (예외)")
            emit(Result.success(getMockMenus()))
        }
    }
    
    fun getToolbars(appId: String): Flow<Result<List<Toolbar>>> = flow {
        try {
            val response = api.getToolbars(appId)
            if (response.isSuccessful && response.body() != null) {
                val wrapper = response.body()!!
                if (wrapper.success && wrapper.data != null && wrapper.data.isNotEmpty()) {
                    val toolbars = wrapper.data.map { ConfigMapper.mapToolbarDtoToDomain(it) }
                    emit(Result.success(toolbars))
                } else {
                    emit(Result.success(getMockToolbars()))
                }
            } else {
                emit(Result.success(getMockToolbars()))
            }
        } catch (e: Exception) {
            emit(Result.success(getMockToolbars()))
        }
    }
    
    fun getFcmTopics(appId: String): Flow<Result<List<FcmTopic>>> = flow {
        try {
            val response = api.getFcmTopics(appId)
            if (response.isSuccessful && response.body() != null) {
                val wrapper = response.body()!!
                if (wrapper.success && wrapper.data != null) {
                    val fcmTopics = wrapper.data.map { ConfigMapper.mapFcmTopicDtoToDomain(it) }
                    emit(Result.success(fcmTopics))
                } else {
                    emit(Result.success(getMockFcmTopics()))
                }
            } else {
                emit(Result.success(getMockFcmTopics()))
            }
        } catch (e: Exception) {
            emit(Result.success(getMockFcmTopics()))
        }
    }
    
    fun getButtons(appId: String): Flow<Result<List<Button>>> = flow {
        try {
            val response = api.getButtons(appId)
            if (response.isSuccessful && response.body() != null) {
                val wrapper = response.body()!!
                if (wrapper.success && wrapper.data != null) {
                    val buttons = wrapper.data.map { ConfigMapper.mapButtonDtoToDomain(it) }
                    emit(Result.success(buttons))
                } else {
                    emit(Result.success(getMockButtons()))
                }
            } else {
                emit(Result.success(getMockButtons()))
            }
        } catch (e: Exception) {
            emit(Result.success(getMockButtons()))
        }
    }
    
    fun getStyles(appId: String): Flow<Result<List<Style>>> = flow {
        try {
            val response = api.getStyles(appId)
            if (response.isSuccessful && response.body() != null) {
                val wrapper = response.body()!!
                if (wrapper.success && wrapper.data != null) {
                    val styles = wrapper.data.map { ConfigMapper.mapStyleDtoToDomain(it) }
                    emit(Result.success(styles))
                } else {
                    emit(Result.success(getMockStyles()))
                }
            } else {
                emit(Result.success(getMockStyles()))
            }
        } catch (e: Exception) {
            emit(Result.success(getMockStyles()))
        }
    }
    
    // Mock 데이터 생성 메서드들
    private fun getMockAppConfig(): AppConfig {
        return AppConfig(
            menus = getMockMenus(),
            toolbars = getMockToolbars(),
            fcmTopics = getMockFcmTopics(),
            styles = getMockStyles(),
            buttons = getMockButtons()
        )
    }
    
    private fun getMockConfigResponse(): ConfigResponseDto {
        return ConfigResponseDto(
            message = "Mock 데이터 로드 성공 ☂️",
            timestamp = "2024-06-19T23:00:00Z",
            appId = "550e8400-e29b-41d4-a716-446655440001",
            app = getMockAppInfo(),
            menus = emptyList(),
            toolbars = emptyList(),
            fcm_topics = emptyList(),
            styles = emptyList(),
            buttons = emptyList()
        )
    }
    
    private fun getMockAppInfo(): AppInfoDto {
        return AppInfoDto(
            id = "550e8400-e29b-41d4-a716-446655440001",
            app_name = "☂️ 우산 알림 (Mock)",
            app_id = "com.applicforge.umbalarm",
            package_name = "com.applicforge.umbalarm",
            version = "1.0.0",
            description = "우산이 필요한 날을 미리 알려주는 스마트 알림 앱",
            status = "active",
            created_at = "2024-06-19T00:00:00Z",
            updated_at = "2024-06-19T23:00:00Z"
        )
    }
    
    private fun getMockMenus(): List<Menu> {
        android.util.Log.d("ConfigRepository", "🎭 Mock 메뉴 데이터 생성 중...")
        val mockMenus = listOf(
            Menu(
                menuId = "1",
                title = "🏠 홈",
                icon = "home",
                menuType = MenuType.ITEM,
                actionType = ActionType.NAVIGATE,
                actionValue = "/home",
                isVisible = true,
                isEnabled = true,
                orderIndex = 1
            ),
            Menu(
                menuId = "2",
                title = "☂️ 우산 알림",
                icon = "umbrella",
                menuType = MenuType.ITEM,
                actionType = ActionType.NAVIGATE,
                actionValue = "/umbrella",
                isVisible = true,
                isEnabled = true,
                orderIndex = 2
            ),
            Menu(
                menuId = "3",
                title = "🌤️ 날씨 정보",
                icon = "weather_sunny",
                menuType = MenuType.ITEM,
                actionType = ActionType.NAVIGATE,
                actionValue = "/weather",
                isVisible = true,
                isEnabled = true,
                orderIndex = 3
            ),
            Menu(
                menuId = "4",
                title = "🔔 알림 설정",
                icon = "notification",
                menuType = MenuType.ITEM,
                actionType = ActionType.NAVIGATE,
                actionValue = "/notifications",
                isVisible = true,
                isEnabled = true,
                orderIndex = 4
            ),
            Menu(
                menuId = "5",
                title = "⚙️ 설정",
                icon = "settings",
                menuType = MenuType.ITEM,
                actionType = ActionType.NAVIGATE,
                actionValue = "/settings",
                isVisible = true,
                isEnabled = true,
                orderIndex = 5
            )
        )
        android.util.Log.d("ConfigRepository", "🎭 Mock 메뉴 데이터 생성 완료: ${mockMenus.size}개")
        return mockMenus
    }
    
    private fun getMockToolbars(): List<Toolbar> {
        return listOf(
            Toolbar(
                toolbarId = "1",
                title = "우산 알림",
                position = ToolbarPosition.TOP,
                backgroundColor = "#1976D2",
                textColor = "#FFFFFF",
                isVisible = true,
                buttons = listOf(
                    ToolbarButton(
                        id = "1",
                        icon = "search",
                        title = "검색",
                        action = "SEARCH"
                    ),
                    ToolbarButton(
                        id = "2",
                        icon = "notification",
                        title = "알림",
                        action = "/notifications"
                    )
                )
            )
        )
    }
    
    private fun getMockFcmTopics(): List<FcmTopic> {
        return listOf(
            FcmTopic(
                topicName = "umbrella_reminders",
                topicId = "1",
                isDefault = true,
                isActive = true
            ),
            FcmTopic(
                topicName = "rain_notifications",
                topicId = "2",
                isDefault = true,
                isActive = true
            ),
            FcmTopic(
                topicName = "morning_briefing",
                topicId = "3",
                isDefault = true,
                isActive = true
            ),
            FcmTopic(
                topicName = "severe_weather",
                topicId = "4",
                isDefault = false,
                isActive = true
            )
        )
    }
    
    private fun getMockButtons(): List<Button> {
        return listOf(
            Button(
                buttonId = "1",
                title = "☂️ 오늘 날씨 확인",
                icon = "weather_sunny",
                buttonType = ButtonType.PRIMARY,
                actionType = ActionType.NAVIGATE,
                actionValue = "/weather",
                backgroundColor = "#1976D2",
                textColor = "#FFFFFF",
                position = ButtonPosition.CENTER,
                orderIndex = 1,
                isVisible = true,
                isEnabled = true
            ),
            Button(
                buttonId = "2",
                title = "🔔 알림 설정",
                icon = "notification",
                buttonType = ButtonType.SECONDARY,
                actionType = ActionType.NAVIGATE,
                actionValue = "/notifications",
                backgroundColor = "#FF9800",
                textColor = "#FFFFFF",
                position = ButtonPosition.CENTER,
                orderIndex = 2,
                isVisible = true,
                isEnabled = true
            )
        )
    }
    
    private fun getMockStyles(): List<Style> {
        return listOf(
            Style(
                styleKey = "primary_color",
                styleValue = "#1976D2",
                styleCategory = StyleCategory.COLOR
            ),
            Style(
                styleKey = "secondary_color",
                styleValue = "#FF9800",
                styleCategory = StyleCategory.COLOR
            ),
            Style(
                styleKey = "text_color",
                styleValue = "#212121",
                styleCategory = StyleCategory.COLOR
            ),
            Style(
                styleKey = "font_size",
                styleValue = "16sp",
                styleCategory = StyleCategory.SIZE
            )
        )
    }
    
    fun getCachedConfig(): AppConfig? {
        return preferences.getAppConfig()
    }
    
    fun getLastUpdateTime(): Long {
        return preferences.getLastUpdateTime()
    }
    
    fun clearCache() {
        preferences.clearConfig()
    }
} 