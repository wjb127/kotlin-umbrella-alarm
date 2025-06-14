package com.test.simple.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.test.simple.config.AppConfig
import com.test.simple.presentation.theme.TestAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            TestAppTheme {
                SplashScreen {
                    // 스플래시 화면 완료 후 메인 액티비티로 이동
                    navigateToMain()
                }
            }
        }
    }
    
    private fun navigateToMain() {
        lifecycleScope.launch {
            // 최소 2초간 스플래시 화면 표시
            delay(2000)
            
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            
            // 부드러운 전환 애니메이션
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    // 애니메이션 상태들
    val infiniteTransition = rememberInfiniteTransition(label = "splash_animation")
    
    // 로고 스케일 애니메이션
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )
    
    // 텍스트 알파 애니메이션
    val textAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "text_alpha"
    )
    
    // 배경 그라데이션 애니메이션
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_offset"
    )
    
    // 그라데이션 배경
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f + gradientOffset * 0.2f),
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f + gradientOffset * 0.3f),
            MaterialTheme.colorScheme.surface
        ),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 메인 로고/아이콘
            Text(
                text = "🚀",
                fontSize = (64 * logoScale).sp,
                modifier = Modifier
                    .scale(logoScale)
                    .padding(bottom = 24.dp)
            )
            
            // 앱 이름
            Text(
                text = AppConfig.APP_NAME,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(textAlpha)
                    .padding(horizontal = 32.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 서브타이틀
            Text(
                text = "Server-Driven UI 기반 앱",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(textAlpha * 0.8f)
                    .padding(horizontal = 32.dp)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // 로딩 인디케이터
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 로딩 텍스트
            Text(
                text = "앱을 준비하고 있습니다...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.alpha(textAlpha * 0.6f)
            )
        }
        
        // 하단 버전 정보
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Powered by SDUI Framework",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        }
    }
    
    // 스플래시 완료 트리거
    LaunchedEffect(Unit) {
        delay(2000) // 2초 후 완료
        onSplashComplete()
    }
} 