package com.test.simple.data.repository

import com.test.simple.data.api.RemoteConfigApi
import com.test.simple.data.api.AppInfoDto
import com.test.simple.data.api.ConfigResponseDto
import com.test.simple.data.local.ConfigPreferences
import com.test.simple.data.mapper.ConfigMapper
import com.test.simple.domain.model.AppConfig
import com.test.simple.domain.model.Menu
import com.test.simple.domain.model.Toolbar
import com.test.simple.domain.model.Style
import com.test.simple.domain.model.FcmTopic
import com.test.simple.domain.model.Button
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigRepository @Inject constructor(
    private val api: RemoteConfigApi,
    private val preferences: ConfigPreferences
) {
    
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
                    emit(Result.failure(Exception("설정을 불러올 수 없습니다. 응답 코드: ${response.code()}")))
                }
            }
        } catch (e: Exception) {
            // 네트워크 오류 시 캐시된 설정 사용
            val cachedConfig = preferences.getAppConfig()
            if (cachedConfig != null) {
                emit(Result.success(cachedConfig))
            } else {
                emit(Result.failure(e))
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
                emit(Result.failure(Exception("통합 설정을 불러올 수 없습니다. 응답 코드: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun getAppInfo(appId: String): Flow<Result<AppInfoDto>> = flow {
        try {
            val response = api.getAppInfo(appId)
            if (response.isSuccessful && response.body() != null) {
                val appInfo = response.body()!!
                emit(Result.success(appInfo))
            } else {
                emit(Result.failure(Exception("앱 정보를 불러올 수 없습니다. 응답 코드: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun getMenus(appId: String): Flow<Result<List<Menu>>> = flow {
        try {
            val response = api.getMenus(appId)
            if (response.isSuccessful && response.body() != null) {
                val wrapper = response.body()!!
                if (wrapper.success && wrapper.data != null) {
                    val menus = wrapper.data.map { ConfigMapper.mapMenuDtoToDomain(it) }
                    emit(Result.success(menus))
                } else {
                    emit(Result.failure(Exception("메뉴 데이터 오류: ${wrapper.error ?: wrapper.message}")))
                }
            } else {
                emit(Result.failure(Exception("메뉴를 불러올 수 없습니다. 응답 코드: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun getToolbars(appId: String): Flow<Result<List<Toolbar>>> = flow {
        try {
            val response = api.getToolbars(appId)
            if (response.isSuccessful && response.body() != null) {
                val wrapper = response.body()!!
                if (wrapper.success && wrapper.data != null) {
                    val toolbars = wrapper.data.map { ConfigMapper.mapToolbarDtoToDomain(it) }
                    emit(Result.success(toolbars))
                } else {
                    emit(Result.failure(Exception("툴바 데이터 오류: ${wrapper.error ?: wrapper.message}")))
                }
            } else {
                emit(Result.failure(Exception("툴바를 불러올 수 없습니다. 응답 코드: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
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
                    emit(Result.failure(Exception("FCM 토픽 데이터 오류: ${wrapper.error ?: wrapper.message}")))
                }
            } else {
                emit(Result.failure(Exception("FCM 토픽을 불러올 수 없습니다. 응답 코드: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
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
                    emit(Result.failure(Exception("버튼 데이터 오류: ${wrapper.error ?: wrapper.message}")))
                }
            } else {
                emit(Result.failure(Exception("버튼을 불러올 수 없습니다. 응답 코드: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
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
                    emit(Result.failure(Exception("스타일 데이터 오류: ${wrapper.error ?: wrapper.message}")))
                }
            } else {
                emit(Result.failure(Exception("스타일을 불러올 수 없습니다. 응답 코드: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
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