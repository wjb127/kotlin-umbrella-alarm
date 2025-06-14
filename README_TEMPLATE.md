# 🚀 Server-Driven UI 앱 템플릿

## 📱 완전한 Server-Driven UI 시스템

이 프로젝트는 **Server-Driven UI (SDUI)** 기반의 Android 앱 템플릿입니다. 
한 개의 설정 파일만 수정하면 완전히 새로운 앱을 만들 수 있습니다!

## ✨ 주요 기능

- 🎯 **Server-Driven UI**: 서버에서 UI 구조를 완전히 제어
- 🔔 **FCM 푸시 알림**: 토픽별 구독 관리 가능
- 🎨 **동적 테마**: 서버에서 색상, 스타일 제어
- 📱 **동적 툴바**: 버튼, 아이콘, 액션 서버 제어
- 🎛️ **알림 설정**: 사용자가 토픽 구독 직접 관리
- 🏗️ **Clean Architecture**: MVVM, Hilt DI, Repository 패턴

## 🎯 새로운 앱 만들기 (5분 완성!)

### 1단계: AppConfig.kt 파일 수정

`app/src/main/java/com/test/simple/config/AppConfig.kt` 파일을 열고 다음 값들을 수정하세요:

```kotlin
object AppConfig {
    // 🆔 새로운 UUID 생성 (https://www.uuidgenerator.net/)
    const val APP_ID = "YOUR_NEW_UUID_HERE"
    
    // 📦 패키지명 변경 (com.회사명.앱명)
    const val PACKAGE_NAME = "com.yourcompany.yourapp"
    
    // 📱 앱 이름 변경
    const val APP_NAME = "Your App Name"
    
    // 🌐 서버 주소 변경
    const val SERVER_BASE_URL = "https://your-server.com"
    
    // 🔔 FCM 토픽 커스터마이징
    object FCM {
        const val GENERAL_NOTIFICATIONS = "your_general_topic"
        const val APP_UPDATES = "your_updates_topic"
        // ... 더 많은 토픽들
    }
}
```

### 2단계: 패키지명 변경 (선택사항)

Android Studio에서:
1. `com.test.simple` 패키지를 우클릭
2. **Refactor** → **Rename**
3. 새로운 패키지명 입력 (예: `com.yourcompany.yourapp`)

### 3단계: 앱 아이콘 & 이름 변경

`app/src/main/res/values/strings.xml`:
```xml
<string name="app_name">Your App Name</string>
```

`app/src/main/AndroidManifest.xml`:
```xml
<application
    android:label="@string/app_name"
    android:icon="@mipmap/your_app_icon">
```

### 4단계: 빌드 & 실행

```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

**🎉 완성! 새로운 앱이 준비되었습니다!**

## 🔧 서버 설정

### Node.js 서버 실행

```bash
cd server
npm install
npm start
```

### 서버 API 엔드포인트

- 📱 **앱 정보**: `GET /api/config/app-info/:appId`
- 🎨 **스타일**: `GET /api/config/styles/:appId`
- 📋 **메뉴**: `GET /api/config/menus/:appId`
- 🔧 **툴바**: `GET /api/config/toolbars/:appId`
- 🔔 **FCM 토픽**: `GET /api/config/fcm-topics/:appId`
- 🔴 **버튼**: `GET /api/config/buttons/:appId`

### FCM 푸시 알림 전송

```bash
# 모든 토픽에 브로드캐스트
curl -X POST http://localhost:3000/api/fcm/broadcast/:appId \
  -H "Content-Type: application/json" \
  -d '{
    "title": "알림 제목",
    "body": "알림 내용",
    "data": {"key": "value"}
  }'
```

## 🏗️ 프로젝트 구조

```
app/src/main/java/com/test/simple/
├── 📁 config/
│   └── AppConfig.kt          # ⚡ 템플릿 설정 파일 (여기만 수정!)
├── 📁 data/
│   ├── api/                  # API 인터페이스
│   └── repository/           # 데이터 저장소
├── 📁 domain/
│   ├── model/               # 도메인 모델
│   └── usecase/             # 비즈니스 로직
├── 📁 presentation/
│   ├── ui/                  # UI 컴포넌트
│   └── viewmodel/           # ViewModel
├── 📁 manager/
│   ├── FcmManager.kt        # FCM 관리
│   └── PreferencesManager.kt # 설정 저장
└── 📁 di/                   # 의존성 주입
```

## 🎨 UI 커스터마이징

### 아이콘 변경

`app/src/main/res/drawable/`에서 아이콘들을 교체:
- `ic_home.xml`
- `ic_profile.xml`
- `ic_settings.xml`
- ... 20개 아이콘

### 색상 테마

서버에서 실시간으로 변경 가능:
```json
{
  "style_key": "primary_color",
  "style_value": "#FF6B35"
}
```

## 🔔 FCM 알림 관리

### 토픽 구독 상태 확인

앱 메뉴 → **🔔 알림 설정**에서 토픽별 구독/해제 가능

### 새로운 토픽 추가

1. `AppConfig.kt`에서 토픽 추가
2. 서버 데이터베이스에 토픽 추가
3. 앱 재빌드

## 🧪 테스트

### FCM 테스트

```bash
# 특정 토픽에 알림 전송
curl -X POST http://localhost:3000/api/fcm/send-to-topic \
  -H "Content-Type: application/json" \
  -d '{
    "topicName": "test_topic",
    "title": "테스트 알림",
    "body": "FCM 동작 확인"
  }'
```

### UI 동작 테스트

1. 서버에서 메뉴/툴바 데이터 변경
2. 앱에서 새로고침
3. UI 실시간 변경 확인

## 🔧 개발 팁

### 로그 확인

```bash
adb logcat | grep -E "(FCM|SDUI|MainActivity)"
```

### 디버그 모드

`AppConfig.Debug.ENABLE_LOGGING = true`로 설정하면 상세 로그 출력

### 서버 연동 테스트

Postman 컬렉션을 제공하여 API 테스트 가능

## 📋 체크리스트

새로운 앱을 만들 때 확인할 사항들:

- [ ] `AppConfig.kt`에서 APP_ID 변경
- [ ] `AppConfig.kt`에서 PACKAGE_NAME 변경  
- [ ] `AppConfig.kt`에서 APP_NAME 변경
- [ ] `AppConfig.kt`에서 SERVER_BASE_URL 변경
- [ ] FCM 토픽들 커스터마이징
- [ ] 앱 아이콘 교체
- [ ] strings.xml에서 앱 이름 변경
- [ ] 서버 데이터베이스 설정
- [ ] Firebase 프로젝트 연결
- [ ] 빌드 & 테스트

## 🚀 배포

### 디버그 빌드

```bash
./gradlew assembleDebug
```

### 릴리즈 빌드

```bash
./gradlew assembleRelease
```

### Play Store 업로드

AAB 파일 생성:
```bash
./gradlew bundleRelease
```

## 🤝 기여하기

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 라이선스

MIT License

---

**🎯 이 템플릿으로 몇 분만에 완전한 Server-Driven UI 앱을 만들어보세요!** 