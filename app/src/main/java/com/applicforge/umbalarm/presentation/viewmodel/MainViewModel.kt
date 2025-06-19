package com.applicforge.umbalarm.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applicforge.umbalarm.domain.model.AppConfig
import com.applicforge.umbalarm.domain.model.Menu
import com.applicforge.umbalarm.domain.model.Toolbar
import com.applicforge.umbalarm.domain.model.Style
import com.applicforge.umbalarm.domain.model.FcmTopic
import com.applicforge.umbalarm.domain.model.Button
import com.applicforge.umbalarm.domain.usecase.GetConfigUseCase
import com.applicforge.umbalarm.data.repository.ConfigRepository
import com.applicforge.umbalarm.data.api.AppInfoDto
import com.applicforge.umbalarm.data.api.ConfigResponseDto
import com.applicforge.umbalarm.utils.FcmManager
import com.applicforge.umbalarm.config.AppConfig as AppConfigTemplate
import com.applicforge.umbalarm.domain.model.WeatherInfo
import com.applicforge.umbalarm.data.repository.WeatherRepository
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
    private val weatherRepository: WeatherRepository,
    private val fcmManager: FcmManager
) : ViewModel() {
    
    // ⚡ 템플릿 설정: AppConfig에서 앱 ID 가져오기
    private val APP_ID = AppConfigTemplate.APP_ID
    private val PACKAGE_NAME = AppConfigTemplate.PACKAGE_NAME
    
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    init {
        loadAllData()
        initializeFcm()
    }
    
    fun loadAllData() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        // 🆕 새로운 통합 API 우선 시도
        loadConfigByPackage()
        
        // 기존 개별 API들 (백업용)
        loadConfig()
        loadConfigResponse()
        loadAppInfo()
        loadMenus()
        loadToolbars()
        loadFcmTopics()
        loadStyles()
        loadButtons()
        
        // ☂️ 날씨 정보 로드
        loadWeatherData()
    }
    
    // 🆕 새로운 통합 API - 패키지명 기반
    private fun loadConfigByPackage() {
        viewModelScope.launch {
            android.util.Log.d("MainViewModel", "🚀 통합 API 로딩 시작...")
            configRepository.getAppConfigByPackage(PACKAGE_NAME).collect { result ->
                result.fold(
                    onSuccess = { config ->
                        android.util.Log.d("MainViewModel", "✅ 통합 API 로드 성공!")
                        android.util.Log.d("MainViewModel", "  📱 메뉴: ${config.menus.size}개")
                        android.util.Log.d("MainViewModel", "  🔧 툴바: ${config.toolbars.size}개")
                        
                        _uiState.value = _uiState.value.copy(
                            config = config,
                            menus = config.menus,
                            toolbars = config.toolbars,
                            fcmTopics = config.fcmTopics,
                            styles = config.styles,
                            buttons = config.buttons,
                            apiResults = _uiState.value.apiResults.copy(
                                configResult = "🆕 통합 API 성공: 메뉴 ${config.menus.size}개, 툴바 ${config.toolbars.size}개"
                            )
                        )
                        
                        // FCM 토픽 구독
                        fcmManager.subscribeToTopics(config.fcmTopics)
                        checkAllLoaded()
                    },
                    onFailure = { error ->
                        android.util.Log.e("MainViewModel", "❌ 통합 API 로드 실패: ${error.message}")
                        _uiState.value = _uiState.value.copy(
                            apiResults = _uiState.value.apiResults.copy(
                                configResult = "❌ 통합 API 실패: ${error.message}"
                            )
                        )
                        checkAllLoaded()
                    }
                )
            }
        }
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
            android.util.Log.d("MainViewModel", "🚀 메뉴 로딩 시작...")
            configRepository.getMenus(APP_ID).collect { result ->
                result.fold(
                    onSuccess = { menus ->
                        android.util.Log.d("MainViewModel", "✅ 메뉴 로드 성공! 개수: ${menus.size}")
                        menus.forEachIndexed { index, menu ->
                            android.util.Log.d("MainViewModel", "  📱 메뉴 $index: ${menu.title}")
                        }
                        _uiState.value = _uiState.value.copy(
                            menus = menus,
                            apiResults = _uiState.value.apiResults.copy(
                                menuResult = "✅ 메뉴 ${menus.size}개 로드 성공"
                            )
                        )
                        checkAllLoaded()
                    },
                    onFailure = { error ->
                        android.util.Log.e("MainViewModel", "❌ 메뉴 로드 실패: ${error.message}")
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
            android.util.Log.d("MainViewModel", "🚀 툴바 로딩 시작...")
            configRepository.getToolbars(APP_ID).collect { result ->
                result.fold(
                    onSuccess = { toolbars ->
                        android.util.Log.d("MainViewModel", "✅ 툴바 로드 성공! 개수: ${toolbars.size}")
                        toolbars.forEachIndexed { index, toolbar ->
                            android.util.Log.d("MainViewModel", "  🔧 툴바 $index: ${toolbar.title} (버튼: ${toolbar.buttons.size}개)")
                        }
                        _uiState.value = _uiState.value.copy(
                            toolbars = toolbars,
                            apiResults = _uiState.value.apiResults.copy(
                                toolbarResult = "✅ 툴바 ${toolbars.size}개 로드 성공"
                            )
                        )
                        checkAllLoaded()
                    },
                    onFailure = { error ->
                        android.util.Log.e("MainViewModel", "❌ 툴바 로드 실패: ${error.message}")
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
    
    // ☂️ 날씨 데이터 로드
    private fun loadWeatherData() {
        viewModelScope.launch {
            android.util.Log.d("MainViewModel", "☂️ 날씨 데이터 로딩 시작...")
            
            // 서울 좌표 (예시)
            val latitude = 37.5665
            val longitude = 126.9780
            val apiKey = "demo_api_key" // Demo 키 (실제로는 AppConfig에서 가져올 예정)
            
            try {
                val result = weatherRepository.getCurrentWeather(latitude, longitude, apiKey)
                result.fold(
                    onSuccess = { weatherInfo ->
                        android.util.Log.d("MainViewModel", "✅ 날씨 로드 성공: ${weatherInfo.description}")
                        
                        val rainProbability = calculateRainProbability(weatherInfo)
                        val isRainyDay = rainProbability > 60.0
                        val weatherMessage = generateWeatherMessage(isRainyDay, rainProbability)
                        
                        _uiState.value = _uiState.value.copy(
                            weatherInfo = weatherInfo,
                            rainProbability = rainProbability,
                            isRainyDay = isRainyDay,
                            weatherMessage = weatherMessage
                        )
                        
                        android.util.Log.d("MainViewModel", "☂️ 비올 확률: ${rainProbability}%, 메시지: $weatherMessage")
                    },
                    onFailure = { error ->
                        android.util.Log.e("MainViewModel", "❌ 날씨 로드 실패: ${error.message}")
                        // 날씨 API 실패 시 기본 메시지 - 센스있는 Mock 데이터로 대체
                        val mockRainProbability = 75.0
                        val mockWeatherMessage = "오늘은 비가 올 가능성이 높아요! ☔\n우산 알림을 설정해드릴까요?"
                        
                        _uiState.value = _uiState.value.copy(
                            rainProbability = mockRainProbability,
                            isRainyDay = true,
                            weatherMessage = mockWeatherMessage
                        )
                        
                        android.util.Log.d("MainViewModel", "🔄 Mock 날씨 데이터 사용: $mockWeatherMessage")
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "💥 날씨 로딩 예외: ${e.message}")
                // 예외 발생 시에도 센스있는 Mock 데이터
                _uiState.value = _uiState.value.copy(
                    rainProbability = 65.0,
                    isRainyDay = true,
                    weatherMessage = "오늘은 비가 올 수 있어요 🌧️\n우산을 미리 준비해두시면 좋겠어요!"
                )
            }
        }
    }
    
    // 비올 확률 계산 (WeatherInfo 모델 기반)
    private fun calculateRainProbability(weatherInfo: WeatherInfo): Double {
        // 기존 강수 확률이 있으면 우선 사용
        if (weatherInfo.rainProbability > 0) {
            return weatherInfo.rainProbability.toDouble()
        }
        
        // 날씨 유형 기반으로 비올 확률 계산
        return when (weatherInfo.weatherType) {
            com.applicforge.umbalarm.domain.model.WeatherType.STORMY -> 85.0 // 폭풍
            com.applicforge.umbalarm.domain.model.WeatherType.RAINY -> 90.0 // 비
            com.applicforge.umbalarm.domain.model.WeatherType.SNOWY -> 75.0 // 눈 (우산 필요)
            com.applicforge.umbalarm.domain.model.WeatherType.SUNNY -> 5.0 // 맑음
            com.applicforge.umbalarm.domain.model.WeatherType.CLOUDY -> when (weatherInfo.humidity) {
                in 80..100 -> 65.0 // 습도 높은 흐림
                in 60..79 -> 35.0  // 보통 흐림
                else -> 15.0       // 약간 흐림
            }
        }
    }
    
    // 날씨 메시지 생성
    private fun generateWeatherMessage(isRainyDay: Boolean, rainProbability: Double): String {
        return if (isRainyDay) {
            when {
                rainProbability > 80 -> "오늘은 비가 올 가능성이 높아요! ☔\n우산 알림을 설정해드릴까요?"
                rainProbability > 60 -> "오늘은 비가 올 수 있어요 🌧️\n우산을 미리 준비해두시면 좋겠어요!"
                else -> "오늘은 구름이 많네요 ☁️\n혹시 모르니 우산을 챙겨보세요!"
            }
        } else {
            when {
                rainProbability < 10 -> "오늘은 맑은 날씨예요! ☀️\n우산 없이도 괜찮을 것 같아요"
                rainProbability < 30 -> "오늘은 괜찮은 날씨네요 😊\n우산은 필요 없을 것 같아요"
                else -> "오늘 날씨는 무난해요 🌤️\n우산은 선택사항이에요!"
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
        val weatherInfo: WeatherInfo? = null,
        val rainProbability: Double = 0.0,
        val isRainyDay: Boolean = false,
        val weatherMessage: String = "",
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