package com.test.simple.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.simple.domain.model.AppConfig
import com.test.simple.domain.model.Menu
import com.test.simple.domain.model.Toolbar
import com.test.simple.domain.model.Style
import com.test.simple.domain.model.FcmTopic
import com.test.simple.domain.model.Button
import com.test.simple.domain.usecase.GetConfigUseCase
import com.test.simple.data.repository.ConfigRepository
import com.test.simple.data.api.AppInfoDto
import com.test.simple.data.api.ConfigResponseDto
import com.test.simple.utils.FcmManager
import com.test.simple.config.AppConfig as AppConfigTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getConfigUseCase: GetConfigUseCase,
    private val configRepository: ConfigRepository,
    private val fcmManager: FcmManager
) : ViewModel() {
    
    // ⚡ 템플릿 설정: AppConfig에서 앱 ID 가져오기
    private val APP_ID = AppConfigTemplate.APP_ID
    
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    init {
        loadAllData()
        initializeFcm()
    }
    
    fun loadAllData() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        // 7개 API 호출 (Config API, Buttons API 추가)
        loadConfig()
        loadConfigResponse()
        loadAppInfo()
        loadMenus()
        loadToolbars()
        loadFcmTopics()
        loadStyles()
        loadButtons()
    }
    
    private fun loadConfig() {
        viewModelScope.launch {
            getConfigUseCase.execute(APP_ID).collect { result ->
                result.fold(
                    onSuccess = { config ->
                        _uiState.value = _uiState.value.copy(
                            config = config,
                            apiResults = _uiState.value.apiResults.copy(
                                configResult = "✅ Config (UseCase) 로드 성공"
                            )
                        )
                        // FCM 토픽 구독
                        fcmManager.subscribeToTopics(config.fcmTopics)
                        checkAllLoaded()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            apiResults = _uiState.value.apiResults.copy(
                                configResult = "❌ Config (UseCase) 로드 실패: ${error.message}"
                            )
                        )
                        checkAllLoaded()
                    }
                )
            }
        }
    }
    
    private fun loadConfigResponse() {
        viewModelScope.launch {
            configRepository.getConfigResponse(APP_ID).collect { result ->
                result.fold(
                    onSuccess = { configResponse ->
                        _uiState.value = _uiState.value.copy(
                            configResponse = configResponse,
                            apiResults = _uiState.value.apiResults.copy(
                                configResponseResult = "✅ Config (통합) 로드 성공: ${configResponse.message}"
                            )
                        )
                        checkAllLoaded()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            apiResults = _uiState.value.apiResults.copy(
                                configResponseResult = "❌ Config (통합) 로드 실패: ${error.message}"
                            )
                        )
                        checkAllLoaded()
                    }
                )
            }
        }
    }
    
    private fun loadAppInfo() {
        viewModelScope.launch {
            configRepository.getAppInfo(APP_ID).collect { result ->
                result.fold(
                    onSuccess = { appInfo ->
                        _uiState.value = _uiState.value.copy(
                            appInfo = appInfo,
                            apiResults = _uiState.value.apiResults.copy(
                                appInfoResult = "✅ 앱 정보 로드 성공: ${appInfo.app_name}"
                            )
                        )
                        checkAllLoaded()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            apiResults = _uiState.value.apiResults.copy(
                                appInfoResult = "❌ 앱 정보 로드 실패: ${error.message}"
                            )
                        )
                        checkAllLoaded()
                    }
                )
            }
        }
    }
    
    private fun loadMenus() {
        viewModelScope.launch {
            configRepository.getMenus(APP_ID).collect { result ->
                result.fold(
                    onSuccess = { menus ->
                        _uiState.value = _uiState.value.copy(
                            menus = menus,
                            apiResults = _uiState.value.apiResults.copy(
                                menuResult = "✅ 메뉴 ${menus.size}개 로드 성공"
                            )
                        )
                        checkAllLoaded()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            apiResults = _uiState.value.apiResults.copy(
                                menuResult = "❌ 메뉴 로드 실패: ${error.message}"
                            )
                        )
                        checkAllLoaded()
                    }
                )
            }
        }
    }
    
    private fun loadToolbars() {
        viewModelScope.launch {
            configRepository.getToolbars(APP_ID).collect { result ->
                result.fold(
                    onSuccess = { toolbars ->
                        _uiState.value = _uiState.value.copy(
                            toolbars = toolbars,
                            apiResults = _uiState.value.apiResults.copy(
                                toolbarResult = "✅ 툴바 ${toolbars.size}개 로드 성공"
                            )
                        )
                        checkAllLoaded()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            apiResults = _uiState.value.apiResults.copy(
                                toolbarResult = "❌ 툴바 로드 실패: ${error.message}"
                            )
                        )
                        checkAllLoaded()
                    }
                )
            }
        }
    }
    
    private fun loadFcmTopics() {
        viewModelScope.launch {
            configRepository.getFcmTopics(APP_ID).collect { result ->
                result.fold(
                    onSuccess = { fcmTopics ->
                        _uiState.value = _uiState.value.copy(
                            fcmTopics = fcmTopics,
                            apiResults = _uiState.value.apiResults.copy(
                                fcmTopicResult = "✅ FCM 토픽 ${fcmTopics.size}개 로드 성공"
                            )
                        )
                        // FCM 토픽 구독
                        fcmManager.subscribeToTopics(fcmTopics)
                        checkAllLoaded()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            apiResults = _uiState.value.apiResults.copy(
                                fcmTopicResult = "❌ FCM 토픽 로드 실패: ${error.message}"
                            )
                        )
                        checkAllLoaded()
                    }
                )
            }
        }
    }
    
    private fun loadStyles() {
        viewModelScope.launch {
            configRepository.getStyles(APP_ID).collect { result ->
                result.fold(
                    onSuccess = { styles ->
                        _uiState.value = _uiState.value.copy(
                            styles = styles,
                            apiResults = _uiState.value.apiResults.copy(
                                styleResult = "✅ 스타일 ${styles.size}개 로드 성공"
                            )
                        )
                        checkAllLoaded()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            apiResults = _uiState.value.apiResults.copy(
                                styleResult = "❌ 스타일 로드 실패: ${error.message}"
                            )
                        )
                        checkAllLoaded()
                    }
                )
            }
        }
    }
    
    private fun loadButtons() {
        viewModelScope.launch {
            configRepository.getButtons(APP_ID).collect { result ->
                result.fold(
                    onSuccess = { buttons ->
                        println("DEBUG: Buttons loaded successfully - count: ${buttons.size}")
                        buttons.forEach { button ->
                            println("DEBUG: Button - id: ${button.buttonId}, title: ${button.title}, visible: ${button.isVisible}, enabled: ${button.isEnabled}")
                        }
                        _uiState.value = _uiState.value.copy(
                            buttons = buttons,
                            apiResults = _uiState.value.apiResults.copy(
                                buttonResult = "✅ 버튼 ${buttons.size}개 로드 성공"
                            )
                        )
                        checkAllLoaded()
                    },
                    onFailure = { error ->
                        println("DEBUG: Button loading failed - error: ${error.message}")
                        _uiState.value = _uiState.value.copy(
                            apiResults = _uiState.value.apiResults.copy(
                                buttonResult = "❌ 버튼 로드 실패: ${error.message}"
                            )
                        )
                        checkAllLoaded()
                    }
                )
            }
        }
    }
    
    private fun checkAllLoaded() {
        val results = _uiState.value.apiResults
        // 8개 API 모두 체크 (Config 2개 + 나머지 6개)
        if (results.configResult.isNotEmpty() &&
            results.configResponseResult.isNotEmpty() &&
            results.appInfoResult.isNotEmpty() &&
            results.menuResult.isNotEmpty() && 
            results.toolbarResult.isNotEmpty() && 
            results.fcmTopicResult.isNotEmpty() && 
            results.styleResult.isNotEmpty() &&
            results.buttonResult.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
    
    private fun initializeFcm() {
        fcmManager.getToken { token ->
            if (token != null) {
                println("FCM Token: $token")
                sendTokenToServer(token)
            }
        }
    }
    
    private fun sendTokenToServer(token: String) {
        viewModelScope.launch {
            try {
                println("Sending FCM token to server for app: $APP_ID, token: $token")
            } catch (e: Exception) {
                println("Failed to send token to server: ${e.message}")
            }
        }
    }
    
    fun refreshConfig() {
        loadAllData()
    }
    
    data class UiState(
        val isLoading: Boolean = false,
        val config: AppConfig? = null,
        val configResponse: ConfigResponseDto? = null,
        val appInfo: AppInfoDto? = null,
        val menus: List<Menu> = emptyList(),
        val toolbars: List<Toolbar> = emptyList(),
        val fcmTopics: List<FcmTopic> = emptyList(),
        val styles: List<Style> = emptyList(),
        val buttons: List<Button> = emptyList(),
        val apiResults: ApiResults = ApiResults(),
        val error: String? = null
    )
    
    data class ApiResults(
        val configResult: String = "",
        val configResponseResult: String = "",
        val appInfoResult: String = "",
        val menuResult: String = "",
        val toolbarResult: String = "",
        val fcmTopicResult: String = "",
        val styleResult: String = "",
        val buttonResult: String = ""
    )
} 