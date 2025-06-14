package com.test.simple.config

/**
 * 🎯 앱 템플릿 중앙 설정 파일
 * 
 * ⚡ 새로운 앱을 만들 때 PACKAGE_NAME만 바꾸면 끝!
 * 
 * 🔧 수정 방법:
 * 1. PACKAGE_NAME: com.회사명.앱명 형태로 변경 (예: "com.mycompany.myapp")
 * 2. 끝! 나머지는 기본값 사용하거나 서버에서 관리! ✨
 * 
 * 💡 실제 운영 시:
 * - 서버 DB에 패키지명별로 앱 정보 등록
 * - 앱에서 패키지명으로 서버 API 호출하여 APP_ID 자동 획득
 * - 현재는 템플릿 편의를 위해 고정값 사용
 */
object AppConfig {
    
    // 📦 앱 패키지명 (Android 애플리케이션 ID)
    // ⚡ 새 앱 만들 때 이것만 바꾸면 됩니다!
    const val PACKAGE_NAME = "com.test.simple"
    
    // 🆔 앱 고유 식별자 
    // 💡 실제 운영 시에는 서버에서 PACKAGE_NAME으로 검색해서 가져옴
    // 현재는 템플릿 편의를 위해 고정값 사용
    const val APP_ID = "550e8400-e29b-41d4-a716-446655440000"
    
    // 📱 앱 이름 (서버에서 가져오거나 기본값 사용)
    const val APP_NAME = "Simple SDUI App"
    
    // 🌐 서버 기본 URL
    // 새 앱을 만들 때 실제 서버 주소로 변경하세요!
    const val SERVER_BASE_URL = "http://localhost:3000"
    
    // 🔔 FCM 토픽 설정 (서버 데이터베이스와 동기화)
    // 새 앱을 만들 때 필요한 토픽들로 변경하세요!
    object FCM {
        const val GENERAL_NOTIFICATIONS = "general_notifications"
        const val APP_UPDATES = "app_updates"
        const val NOTIFICATION = "notification"  // 새로 추가된 공지사항 토픽
        const val TEST_TOPIC = "test_topic"
        
        // 모든 토픽 리스트 (서버 구독용)
        val ALL_TOPICS = listOf(
            GENERAL_NOTIFICATIONS,
            APP_UPDATES,
            NOTIFICATION,
            TEST_TOPIC
        )
        
        // 사용자에게 보여줄 토픽만 (설정에서 토글 가능한 토픽들)
        val USER_VISIBLE_TOPICS = listOf(
            GENERAL_NOTIFICATIONS,
            NOTIFICATION  // 공지사항 토픽을 사용자가 제어 가능하도록
        )
        
        // 자동 구독 토픽 (사용자가 끌 수 없는 필수 토픽들)
        val AUTO_SUBSCRIBE_TOPICS = listOf(
            APP_UPDATES  // 업데이트 알림은 항상 받아야 함
        )
        
        // 숨겨진 토픽 (개발/테스트용, 사용자에게 보이지 않음)
        val HIDDEN_TOPICS = listOf(
            TEST_TOPIC
        )
        
        // 토픽 표시 이름 (사용자에게 보이는 이름)
        val TOPIC_DISPLAY_NAMES = mapOf(
            GENERAL_NOTIFICATIONS to "📢 일반 알림",
            APP_UPDATES to "🔄 앱 업데이트",
            NOTIFICATION to "📋 공지사항",
            TEST_TOPIC to "🧪 테스트 알림"
        )
        
        // 토픽 설명
        val TOPIC_DESCRIPTIONS = mapOf(
            GENERAL_NOTIFICATIONS to "일반적인 앱 알림",
            APP_UPDATES to "새로운 버전, 기능 업데이트 알림",
            NOTIFICATION to "중요한 공지사항 및 안내",
            TEST_TOPIC to "개발자 테스트 목적 알림"
        )
    }
    
    // 🎨 디자인 설정
    object Design {
        // 기본 색상들 (서버에서 override 가능)
        const val DEFAULT_PRIMARY_COLOR = "#6200EE"
        const val DEFAULT_BACKGROUND_COLOR = "#FFFFFF"
        const val DEFAULT_TEXT_COLOR = "#000000"
    }
    
    // 🔧 개발/디버그 설정
    object Debug {
        const val ENABLE_LOGGING = true
        const val LOG_TAG_PREFIX = "SDUI"
    }
} 