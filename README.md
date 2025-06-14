# 🚀 SDUI Android App Template

**5분 만에** 새로운 Server-Driven UI 앱을 만들 수 있는 완벽한 템플릿!

## 📱 프로젝트 개요

이 템플릿을 사용하면 **Server Driven UI (SDUI)** 기반의 완전한 안드로이드 앱을 빠르게 만들 수 있습니다!

### ✨ 주요 특징
- 🎨 **Instagram 스타일 UI**: 깔끔하고 모던한 디자인
- 🔔 **FCM 푸시 알림**: 토픽 기반 알림 관리
- 🚀 **전문적인 스플래시 화면**: 애니메이션과 로딩 효과
- 📱 **사용자 친화적**: 인터넷 연결 문제 시 직관적 안내
- 🔧 **완전한 템플릿**: 한 파일만 수정하면 새 앱 완성

## 🏗️ 기술 스택

- **Language**: Kotlin
- **Architecture**: MVVM + Repository Pattern
- **DI**: Hilt (Dagger)
- **UI**: Jetpack Compose
- **Network**: Retrofit2 + Gson
- **Async**: Coroutines + Flow
- **Local Storage**: SharedPreferences
- **Push Notification**: Firebase Cloud Messaging

## 🌐 API 서버

**Base URL**: https://remote-config-node-express.onrender.com

### 주요 API 엔드포인트

1. **통합 설정 조회**
   ```
   GET /api/config/{appId}
   ```

2. **앱 정보**
   ```
   GET /api/apps/{appId}
   ```

3. **메뉴**
   ```
   GET /api/apps/{appId}/menus
   ```

4. **툴바**
   ```
   GET /api/apps/{appId}/toolbars
   ```

5. **FCM 토픽**
   ```
   GET /api/apps/{appId}/fcm-topics
   ```

6. **스타일**
   ```
   GET /api/apps/{appId}/styles
   ```

## ⚡ 빠른 시작

### 🚀 5분 만에 새 앱 만들기
```bash
# 1. 템플릿 클론
git clone https://github.com/wjb127/sdui-kotlin-node.git my-new-app
cd my-new-app && rm -rf .git && git init

# 2. AppConfig.kt에서 앱 정보 변경 (UUID, 패키지명, 앱 이름)

# 3. 빌드 및 실행
cd server && npm install && npm start &
cd ../app && ./gradlew clean assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 📚 자세한 가이드
- **[⚡ 5분 빠른 시작](./QUICK_START.md)**: 최소한의 설정으로 빠르게 시작
- **[📖 완전한 가이드](./TEMPLATE_GUIDE.md)**: 모든 기능과 커스터마이징 방법

## 🔧 주요 기능

### ✅ 구현 완료
- Remote Config API 연동 (7개 엔드포인트)
- 동적 메뉴 구성
- 동적 툴바 설정
- FCM 토픽 자동 구독
- 스타일 동적 적용
- 로컬 캐싱 (SharedPreferences)
- 실시간 API 호출 상태 표시

### 📱 화면 구성
- API 호출 결과 상태
- Config 통합 응답 정보
- 앱 기본 정보
- 메뉴 목록
- 툴바 설정
- FCM 토픽 목록
- 스타일 설정

## 📋 사용된 패턴

### Clean Architecture
```
app/
├── data/
│   ├── api/          # Retrofit API 인터페이스
│   ├── dto/          # 데이터 전송 객체
│   ├── local/        # 로컬 저장소
│   ├── mapper/       # DTO ↔ Domain 매핑
│   └── repository/   # Repository 구현
├── domain/
│   ├── model/        # 도메인 모델
│   └── usecase/      # 비즈니스 로직
├── presentation/
│   ├── ui/           # Compose UI
│   ├── viewmodel/    # ViewModel
│   └── theme/        # UI 테마
├── di/               # Hilt 모듈
├── service/          # FCM 서비스
└── utils/            # 유틸리티
```

## 🔄 API 응답 형태

### 통합 Config API
```json
{
  "message": "Config API 작동 중",
  "timestamp": "2025-06-14T10:08:39.345Z",
  "appId": "550e8400-e29b-41d4-a716-446655440000",
  "app": { ... },
  "menus": [ ... ],
  "toolbars": [ ... ],
  "fcm_topics": [ ... ],
  "styles": [ ... ]
}
```

### Wrapper API (메뉴, 툴바, FCM, 스타일)
```json
{
  "success": true,
  "data": [ ... ],
  "message": "Success",
  "error": null
}
```

## 👨‍💻 개발자

- **Server**: Node.js + Express
- **Client**: Android + Kotlin

## 📄 라이선스

MIT License 