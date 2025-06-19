package com.applicforge.umbalarm.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.foundation.background
import androidx.compose.ui.res.painterResource
import com.applicforge.umbalarm.R
import androidx.compose.runtime.*
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.applicforge.umbalarm.manager.FcmManager
import com.applicforge.umbalarm.config.AppConfig
import javax.inject.Inject
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.applicforge.umbalarm.presentation.theme.TestAppTheme
import com.applicforge.umbalarm.presentation.viewmodel.MainViewModel
import com.applicforge.umbalarm.data.api.AppInfoDto
import com.applicforge.umbalarm.data.api.ConfigResponseDto
import com.applicforge.umbalarm.domain.model.Toolbar
import com.applicforge.umbalarm.domain.model.ToolbarPosition
import com.applicforge.umbalarm.domain.model.Menu
import com.applicforge.umbalarm.domain.model.ActionType
import com.applicforge.umbalarm.domain.model.MenuType
import com.applicforge.umbalarm.domain.model.Button
import com.applicforge.umbalarm.domain.model.ToolbarButton
import com.applicforge.umbalarm.presentation.ui.components.DynamicBottomActionBar
import com.applicforge.umbalarm.domain.model.WeatherInfo
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilledTonalButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var fcmManager: FcmManager
    
    companion object {
        private const val TAG = "MainActivity"
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // FCM 초기화
        initializeFirebaseMessaging()
        
        // 알림 권한 요청 (Android 13+)
        requestNotificationPermission()
        
        setContent {
            TestAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
    
    private fun initializeFirebaseMessaging() {
        Log.d(TAG, "🚀 FCM 초기화 시작")
        
        // FCM 상태 로깅
        fcmManager.logFcmStatus()
        
        // FCM 토큰 가져오기
        fcmManager.getToken { token ->
            if (token != null) {
                Log.d(TAG, "🔑 FCM 토큰: ${token.substring(0, 20)}...")
                Toast.makeText(this, "FCM 토큰 획득 완료!", Toast.LENGTH_SHORT).show()
                sendTokenToServer(token)
            } else {
                Log.e(TAG, "❌ FCM 토큰 획득 실패!")
                Toast.makeText(this, "❌ FCM 토큰 획득 실패!", Toast.LENGTH_LONG).show()
            }
        }
        
        // 기본 토픽들 구독
        fcmManager.subscribeToBasicTopics()
        
        Log.d(TAG, "✅ FCM 초기화 완료")
    }
    
    private fun sendTokenToServer(token: String) {
        // ⚡ 템플릿 설정: AppConfig에서 앱 ID 가져오기
        val appId = AppConfig.APP_ID
        Log.d(TAG, "📤 서버에 토큰 전송 - 앱: $appId, 토큰: ${token.substring(0, 20)}...")
        // TODO: 필요시 서버 API 호출하여 토큰 저장
        // 예: configRepository.sendFcmToken(appId, token)
    }
    
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, 
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "✅ 알림 권한 승인됨")
                    Toast.makeText(this, "알림 권한이 승인되었습니다", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d(TAG, "❌ 알림 권한 거부됨")
                    Toast.makeText(this, "알림 권한이 필요합니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(navController = navController)
        }
        composable("menu") {
            MenuScreen(navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // 디버그 로그 추가
    LaunchedEffect(uiState) {
        Log.d("MainScreen", "🔍 UI State 업데이트!")
        Log.d("MainScreen", "📋 전체 툴바 개수: ${uiState.toolbars.size}")
        uiState.toolbars.forEachIndexed { index, toolbar ->
            Log.d("MainScreen", "🔧 툴바 $index: ${toolbar.title} (위치: ${toolbar.position}, 버튼 개수: ${toolbar.buttons.size})")
            toolbar.buttons.forEachIndexed { btnIndex, button ->
                Log.d("MainScreen", "  🔘 버튼 $btnIndex: ${button.title} (${button.icon})")
            }
        }
        Log.d("MainScreen", "📋 전체 메뉴 개수: ${uiState.menus.size}")
        uiState.menus.forEachIndexed { index, menu ->
            Log.d("MainScreen", "📱 메뉴 $index: ${menu.title} (아이콘: ${menu.icon})")
        }
        Log.d("MainScreen", "📋 전체 버튼 개수: ${uiState.buttons.size}")
        uiState.buttons.forEachIndexed { index, button ->
            Log.d("MainScreen", "🔘 버튼 $index: ${button.title} (타입: ${button.buttonType})")
        }
    }
    
    // 툴바 필터링 (visible한 것만)
    val topToolbars = uiState.toolbars.filter { it.position == ToolbarPosition.TOP && it.isVisible }
    val bottomToolbars = uiState.toolbars.filter { it.position == ToolbarPosition.BOTTOM && it.isVisible }
    
    // 메뉴 필터링 (visible하고 enabled한 것만)
    val visibleMenus = uiState.menus.filter { it.isVisible && it.isEnabled }
    
    // 앱 이름 가져오기 (인터넷 연결 문제 시 친숙한 메시지)
    val appName = uiState.appInfo?.app_name ?: "인터넷 연결을 확인해주세요"
    
    // 스타일에서 primary_color 찾기
    val primaryColorStyle = uiState.styles.find { it.styleKey == "primary_color" }
    val primaryColor = primaryColorStyle?.let { style ->
        try {
            Color(android.graphics.Color.parseColor(style.styleValue))
        } catch (e: Exception) {
            MaterialTheme.colorScheme.primary
        }
    } ?: MaterialTheme.colorScheme.primary

    // 스타일에서 background_color 찾기
    val backgroundColorStyle = uiState.styles.find { it.styleKey == "background_color" }
    val backgroundColor = backgroundColorStyle?.let { style ->
        try {
            Color(android.graphics.Color.parseColor(style.styleValue))
        } catch (e: Exception) {
            MaterialTheme.colorScheme.background
        }
    } ?: MaterialTheme.colorScheme.background

    Scaffold(
        containerColor = backgroundColor, // 배경색 적용
        topBar = {
            Column {
                // 기본 상단바 (동적 툴바가 없을 때만 표시)
                if (topToolbars.isEmpty()) {
                    DefaultTopBar(
                        title = appName,
                        backgroundColor = primaryColor,
                        menus = visibleMenus,
                        onMenuClick = { navController.navigate("menu") }
                    )
                }
                
                // 동적 상단 툴바들
                topToolbars.forEach { toolbar ->
                    DynamicToolbar(
                        toolbar = toolbar,
                        menus = visibleMenus,
                        onMenuClick = { navController.navigate("menu") },
                        isTop = true
                    )
                }
            }
        },
        bottomBar = {
            // 하단 툴바들과 액션 버튼들을 세로로 배치
            Column {
                // 동적 하단 툴바들
                bottomToolbars.forEach { toolbar ->
                    DynamicToolbar(
                        toolbar = toolbar,
                        menus = visibleMenus,
                        onMenuClick = { navController.navigate("menu") },
                        isTop = false
                    )
                }
                
                // 하단 액션 버튼들
                if (uiState.buttons.isNotEmpty()) {
                    DynamicBottomActionBar(
                        buttons = uiState.buttons,
                        onButtonClick = { button ->
                            handleButtonClick(button, navController)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        // 메인 콘텐츠 - Scaffold가 자동으로 적절한 패딩 제공
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("설정을 불러오는 중...")
                        }
                    }
                }
            }

            // ☂️ 날씨 기반 우산 알림 카드
            if (uiState.weatherMessage.isNotEmpty()) {
                item {
                    WeatherAlarmCard(
                        weatherMessage = uiState.weatherMessage,
                        rainProbability = uiState.rainProbability,
                        isRainyDay = uiState.isRainyDay,
                        weatherInfo = uiState.weatherInfo,
                        onSetAlarmClick = {
                            // 알림 설정 페이지로 이동
                            navController.navigate("menu")
                        }
                    )
                }
            }
            
            // 메인 콘텐츠 영역 - 깔끔한 사용자 경험을 위해 개발용 섹션들 제거됨
            // FCM 토픽 관리는 메뉴에서, 스타일과 툴바는 서버에서 완전히 관리됨
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "☂️",
                            fontSize = 48.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        Text(
                            text = appName,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "날씨 API 기반 스마트 우산 알림",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "비가 올 때마다 알림으로 우산을 챙겨드려요!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DefaultTopBar(
    title: String,
    backgroundColor: Color,
    menus: List<Menu>,
    onMenuClick: () -> Unit
) {
    // 텍스트 색상 계산 (배경색에 따라 자동으로 밝기 조절)
    val textColor = if (isColorLight(backgroundColor)) {
        Color.Black
    } else {
        Color.White
    }
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = backgroundColor,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // 앱 제목 (가운데 정렬)
            Text(
                text = title,
                color = textColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
            
            // 메뉴 버튼 (우측 정렬)
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "메뉴",
                    tint = textColor
                )
            }
        }
    }
}

// 색상이 밝은지 어두운지 판단하는 함수
private fun isColorLight(color: Color): Boolean {
    val red = color.red * 255
    val green = color.green * 255
    val blue = color.blue * 255
    
    // 표준 휘도 공식 사용
    val luminance = (0.299 * red + 0.587 * green + 0.114 * blue)
    return luminance > 186
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val fcmManager = remember { 
        (context as? MainActivity)?.fcmManager 
            ?: throw IllegalStateException("FcmManager not available")
    }
    val uiState by viewModel.uiState.collectAsState()
    val visibleMenus = uiState.menus.filter { it.isVisible && it.isEnabled }.sortedBy { it.orderIndex }
    
    // FCM 토픽 구독 상태
    var topicStates by remember { mutableStateOf(fcmManager.getTopicSubscriptionStatus()) }
    
    // 앱 이름 가져오기 (인터넷 연결 문제 시 친숙한 메시지)
    val appName = uiState.appInfo?.app_name ?: "인터넷 연결을 확인해주세요"
    
    // 스타일에서 primary_color 찾기
    val primaryColorStyle = uiState.styles.find { it.styleKey == "primary_color" }
    val primaryColor = primaryColorStyle?.let { style ->
        try {
            Color(android.graphics.Color.parseColor(style.styleValue))
        } catch (e: Exception) {
            MaterialTheme.colorScheme.primary
        }
    } ?: MaterialTheme.colorScheme.primary
    
    // 스타일에서 background_color 찾기
    val backgroundColorStyle = uiState.styles.find { it.styleKey == "background_color" }
    val backgroundColor = backgroundColorStyle?.let { style ->
        try {
            Color(android.graphics.Color.parseColor(style.styleValue))
        } catch (e: Exception) {
            MaterialTheme.colorScheme.background
        }
    } ?: MaterialTheme.colorScheme.background
    
    // 텍스트 색상 계산
    val textColor = if (isColorLight(primaryColor)) {
        Color.Black
    } else {
        Color.White
    }

    Scaffold(
        containerColor = backgroundColor, // 배경색 적용
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = primaryColor,
                shadowElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    // 뒤로가기 버튼 (왼쪽 정렬)
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = textColor
                        )
                    }
                    
                    // 제목 (가운데 정렬)
                    Text(
                        text = "$appName - 메뉴",
                        color = textColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Top
        ) {
            // FCM 알림 설정 섹션
            item {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 섹션 헤더
                    Text(
                        text = "알림",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                    
                    // 알림 설정 메뉴 아이템
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* 알림 설정 화면으로 이동 */ },
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_notification),
                                contentDescription = "알림 설정",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Text(
                                text = "알림 설정",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "이동",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    // FCM 토픽 상세 설정 (인라인으로 표시)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 20.dp)
                    ) {
                        topicStates.forEach { (topicName, isSubscribed) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 40.dp, end = 0.dp, top = 8.dp, bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = getTopicDisplayName(topicName),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = getTopicDescription(topicName),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                
                                Switch(
                                    checked = isSubscribed,
                                    onCheckedChange = { 
                                        fcmManager.toggleTopicSubscription(topicName, it)
                                        topicStates = topicStates.toMutableMap().apply {
                                            this[topicName] = it
                                        }
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )
                            }
                            
                            if (topicName != topicStates.keys.last()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 40.dp)
                                        .height(0.5.dp)
                                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            
            // 일반 메뉴 섹션
            if (visibleMenus.isNotEmpty()) {
                item {
                    // 섹션 헤더
                    Text(
                        text = "메뉴",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
                
                items(visibleMenus) { menu ->
                    MenuListItem(
                        menu = menu,
                        onClick = { handleMenuClick(menu) }
                    )
                }
            } else {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "표시할 메뉴가 없습니다",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuListItem(
    menu: Menu,
    onClick: () -> Unit
) {
    // 인스타그램 스타일의 미려한 메뉴 아이템 🎨
    when (menu.menuType) {
        MenuType.DIVIDER -> {
            // 구분선 스타일
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            )
            if (menu.title.isNotEmpty()) {
                Text(
                    text = menu.title,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        else -> {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() },
                color = androidx.compose.ui.graphics.Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 아이콘 (Material Design 아이콘 사용)
                    val iconRes = getMenuIconResource(menu.icon)
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = menu.title,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // 메뉴 제목
                    Text(
                        text = menu.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // 오른쪽 화살표 아이콘
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "이동",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // 각 아이템 사이의 구분선
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 60.dp) // 아이콘 너비만큼 들여쓰기
                    .height(0.5.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            )
        }
    }
}

private fun handleMenuClick(menu: Menu) {
    // 메뉴 클릭 처리 로직
    when (menu.actionType) {
        ActionType.NAVIGATE -> {
            // 네비게이션 처리
            println("Navigate to: ${menu.actionValue}")
        }
        ActionType.EXTERNAL_LINK -> {
            // 외부 링크 처리
            println("Open link: ${menu.actionValue}")
        }
        ActionType.API_CALL -> {
            // API 호출 처리
            println("Call API: ${menu.actionValue}")
        }
    }
}



private fun getTopicDisplayName(topicName: String): String {
    // ⚡ 템플릿 설정: AppConfig에서 토픽 표시 이름 가져오기
    return AppConfig.FCM.TOPIC_DISPLAY_NAMES[topicName] ?: "📱 $topicName"
}

private fun getTopicDescription(topicName: String): String {
    // ⚡ 템플릿 설정: AppConfig에서 토픽 설명 가져오기
    return AppConfig.FCM.TOPIC_DESCRIPTIONS[topicName] ?: "기타 알림"
}

private fun getMenuIconResource(iconName: String): Int {
    // 메뉴 아이콘 매핑 - 인스타그램 스타일 🎨
    return when (iconName.lowercase()) {
        // Core Navigation
        "home" -> R.drawable.ic_home
        "profile", "user", "person" -> R.drawable.ic_profile
        "settings", "setting" -> R.drawable.ic_settings
        "search" -> R.drawable.ic_search
        "menu" -> R.drawable.ic_menu
        
        // Communication
        "notification", "notifications", "bell" -> R.drawable.ic_notification
        "message", "chat", "msg" -> R.drawable.ic_message
        "phone", "call" -> R.drawable.ic_phone
        "email", "mail" -> R.drawable.ic_email
        
        // Actions
        "plus", "add" -> R.drawable.ic_add
        "share" -> R.drawable.ic_share
        "heart", "favorite" -> R.drawable.ic_favorite
        "bookmark" -> R.drawable.ic_bookmark
        "download" -> R.drawable.ic_download
        "upload" -> R.drawable.ic_upload
        
        // Commerce & Utility
        "cart", "shopping" -> R.drawable.ic_cart
        "location", "map", "place" -> R.drawable.ic_location
        "camera", "photo" -> R.drawable.ic_camera
        "calendar", "date" -> R.drawable.ic_calendar
        "help", "info", "question" -> R.drawable.ic_help
        "help-circle" -> R.drawable.ic_help
        
        else -> R.drawable.ic_home // 기본값
    }
}

@Composable
fun DynamicToolbar(
    toolbar: Toolbar,
    menus: List<Menu>,
    onMenuClick: () -> Unit,
    isTop: Boolean = true
) {
    // 색상 파싱 (hex 색상을 Color로 변환)
    val backgroundColor = try {
        Color(android.graphics.Color.parseColor(toolbar.backgroundColor))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }
    
    val textColor = try {
        Color(android.graphics.Color.parseColor(toolbar.textColor))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.onPrimary
    }
    
    // 자연스러운 네이티브 툴바 스타일
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = backgroundColor,
        shadowElevation = if (isTop) 4.dp else 8.dp
    ) {
        if (isTop || toolbar.buttons.isEmpty()) {
            // 상단 툴바 또는 버튼이 없는 경우 - 기존 UI
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // 툴바 제목 (가운데 정렬)
                Text(
                    text = toolbar.title,
                    color = textColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
                
                // 메뉴 버튼 (우측 정렬)
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "메뉴",
                        tint = textColor
                    )
                }
            }
        } else {
            // 하단 툴바에 버튼이 있는 경우 - 버튼들을 가로로 배치
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                toolbar.buttons.forEach { button ->
                    ToolbarActionButton(
                        button = button,
                        textColor = textColor,
                        onClick = {
                            println("Toolbar button clicked: ${button.title} -> ${button.action}")
                            // 여기서 버튼 액션 처리
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolbarActionButton(
    button: ToolbarButton,
    textColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        // 모든 아이콘을 커스텀 드로어블로 사용
        val iconRes = when (button.icon.lowercase()) {
            // Core Navigation
            "home" -> R.drawable.ic_home
            "profile", "user", "person" -> R.drawable.ic_profile
            "settings", "setting" -> R.drawable.ic_settings
            "search" -> R.drawable.ic_search
            "menu" -> R.drawable.ic_menu
            
            // Communication
            "notification", "notifications", "bell" -> R.drawable.ic_notification
            "message", "chat", "msg" -> R.drawable.ic_message
            "phone", "call" -> R.drawable.ic_phone
            "email", "mail" -> R.drawable.ic_email
            
            // Actions
            "plus", "add" -> R.drawable.ic_add
            "share" -> R.drawable.ic_share
            "heart", "favorite" -> R.drawable.ic_favorite
            "bookmark" -> R.drawable.ic_bookmark
            "download" -> R.drawable.ic_download
            "upload" -> R.drawable.ic_upload
            
            // Commerce & Utility
            "cart", "shopping" -> R.drawable.ic_cart
            "location", "map", "place" -> R.drawable.ic_location
            "camera", "photo" -> R.drawable.ic_camera
            "calendar", "date" -> R.drawable.ic_calendar
            "help", "info", "question" -> R.drawable.ic_help
            
            else -> R.drawable.ic_home // 기본값
        }

        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = button.title,
            tint = textColor,
            modifier = Modifier.size(24.dp)
        )
        if (button.title.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = button.title,
                color = textColor,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}



// 개발용 섹션들 제거됨 - 깔끔한 사용자 경험을 위해
// FCM 토픽 관리는 메뉴에서, 스타일과 툴바는 서버에서 완전히 관리됨

// 버튼 클릭 핸들러
private fun handleButtonClick(button: Button, navController: NavController) {
    when (button.actionType) {
        ActionType.NAVIGATE -> {
            try {
                navController.navigate(button.actionValue)
            } catch (e: Exception) {
                println("Navigation error: ${e.message}")
            }
        }
        ActionType.EXTERNAL_LINK -> {
            // 외부 링크 처리 (여기서는 로그만 출력)
            println("External link clicked: ${button.actionValue}")
        }
        ActionType.API_CALL -> {
            // API 호출 처리 (여기서는 로그만 출력)
            println("API call triggered: ${button.actionValue}")
        }
    }
}

// ☂️ 날씨 기반 우산 알림 카드 컴포넌트
@Composable
fun WeatherAlarmCard(
    weatherMessage: String,
    rainProbability: Double,
    isRainyDay: Boolean,
    weatherInfo: WeatherInfo?,
    onSetAlarmClick: () -> Unit
) {
    // 날씨에 따른 색상 계산
    val cardColor = if (isRainyDay) {
        Color(0xFF1E88E5) // 파란색 (비 오는 날)
    } else {
        Color(0xFFFF9800) // 오렌지색 (맑은 날)
    }
    
    val textColor = Color.White
    
    // 날씨 아이콘 선택
    val weatherIcon = when (weatherInfo?.weatherType) {
        com.applicforge.umbalarm.domain.model.WeatherType.STORMY -> "⛈️" // 폭풍
        com.applicforge.umbalarm.domain.model.WeatherType.RAINY -> "☔" // 비
        com.applicforge.umbalarm.domain.model.WeatherType.SNOWY -> "❄️" // 눈
        com.applicforge.umbalarm.domain.model.WeatherType.SUNNY -> "☀️" // 맑음
        com.applicforge.umbalarm.domain.model.WeatherType.CLOUDY -> "☁️" // 구름
        else -> "🌤️" // 기본값
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 날씨 아이콘과 확률
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 날씨 아이콘
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = weatherIcon,
                        fontSize = 30.sp
                    )
                }
                
                // 비올 확률
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "비올 확률",
                        color = textColor.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "${rainProbability.toInt()}%",
                        color = textColor,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 메인 메시지
            Text(
                text = weatherMessage,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // 액션 버튼들
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 알림 설정 버튼 (메인)
                FilledTonalButton(
                    onClick = onSetAlarmClick,
                    modifier = Modifier.weight(1f),
                    colors = androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color.White,
                        contentColor = cardColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRainyDay) "알림 설정하기" else "알림 관리",
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // 현재 날씨 정보 칩
                AssistChip(
                    onClick = { /* 날씨 상세 정보 */ },
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = weatherInfo?.currentTemp?.toInt()?.toString() ?: "--",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "°C",
                                fontSize = 12.sp
                            )
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
                        containerColor = Color.White.copy(alpha = 0.2f),
                        labelColor = textColor,
                        leadingIconContentColor = textColor
                    ),
                    border = androidx.compose.material3.AssistChipDefaults.assistChipBorder(
                        borderColor = Color.White.copy(alpha = 0.3f)
                    )
                )
            }
        }
    }
} 