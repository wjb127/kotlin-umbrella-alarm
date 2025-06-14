# 🚀 SDUI 앱 템플릿 가이드

이 가이드를 따라하면 **5분 안에** 완전히 새로운 Server-Driven UI 앱을 만들 수 있습니다!

## 📋 목차
1. [템플릿 복사하기](#1-템플릿-복사하기)
2. [새 Git 저장소 설정](#2-새-git-저장소-설정)
3. [앱 설정 변경](#3-앱-설정-변경)
4. [패키지명 변경](#4-패키지명-변경)
5. [앱 이름 및 아이콘 변경](#5-앱-이름-및-아이콘-변경)
6. [서버 설정](#6-서버-설정)
7. [빌드 및 테스트](#7-빌드-및-테스트)

---

## 1. 템플릿 복사하기

### 1.1 템플릿 클론
```bash
# 템플릿 저장소 클론
git clone https://github.com/wjb127/sdui-kotlin-node.git my-new-app
cd my-new-app

# 기존 Git 연결 끊기
rm -rf .git

# 새 Git 저장소 초기화
git init
```

### 1.2 프로젝트 구조 확인
```
my-new-app/
├── app/                    # Android 앱
├── server/                 # Node.js 서버
├── TEMPLATE_GUIDE.md       # 이 가이드
└── README.md              # 프로젝트 설명
```

---

## 2. 새 Git 저장소 설정

### 2.1 GitHub에서 새 저장소 생성
1. GitHub에서 **New Repository** 클릭
2. 저장소 이름 입력 (예: `my-awesome-app`)
3. **Create repository** 클릭

### 2.2 로컬과 연결
```bash
# 첫 커밋
git add .
git commit -m "🎉 Initial commit from SDUI template"

# GitHub 저장소와 연결
git remote add origin https://github.com/YOUR_USERNAME/my-awesome-app.git
git branch -M main
git push -u origin main
```

---

## 3. 앱 설정 변경

### 3.1 핵심 설정 파일 수정
**`app/src/main/java/com/test/simple/config/AppConfig.kt`** 파일을 열고 다음 값들을 변경하세요:

```kotlin
object AppConfig {
    
    // 🆔 새로운 UUID 생성 (https://www.uuidgenerator.net/)
    const val APP_ID = "YOUR_NEW_UUID_HERE"
    
    // 📦 새로운 패키지명
    const val PACKAGE_NAME = "com.yourcompany.yourapp"
    
    // 📱 앱 이름
    const val APP_NAME = "My Awesome App"
    
    // 🌐 서버 URL (나중에 실제 서버로 변경)
    const val SERVER_BASE_URL = "http://localhost:3000"
    
    // 🔔 FCM 토픽들 (필요에 따라 수정)
    object FCM {
        const val GENERAL_NOTIFICATIONS = "general_notifications"
        const val APP_UPDATES = "app_updates"
        const val USER_NOTIFICATIONS = "user_notifications"
        // ... 나머지는 그대로 유지
    }
}
```

### 3.2 UUID 생성하기
1. https://www.uuidgenerator.net/ 방문
2. **Generate** 클릭
3. 생성된 UUID를 `APP_ID`에 복사

---

## 4. 패키지명 변경

### 4.1 Android Studio에서 패키지명 변경
1. **Android Studio**에서 프로젝트 열기
2. **Project** 뷰에서 `com.test.simple` 패키지 우클릭
3. **Refactor** → **Rename** 선택
4. 새 패키지명 입력 (예: `com.yourcompany.yourapp`)
5. **Refactor** 클릭

### 4.2 build.gradle 파일 수정
**`app/build.gradle`** 파일에서:
```gradle
android {
    namespace 'com.yourcompany.yourapp'  // 변경
    // ...
    
    defaultConfig {
        applicationId "com.yourcompany.yourapp"  // 변경
        // ...
    }
}
```

---

## 5. 앱 이름 및 아이콘 변경

### 5.1 앱 이름 변경
**`app/src/main/res/values/strings.xml`**:
```xml
<resources>
    <string name="app_name">My Awesome App</string>
</resources>
```

### 5.2 앱 아이콘 변경
1. **Android Studio**에서 `app/src/main/res/mipmap` 폴더 우클릭
2. **New** → **Image Asset** 선택
3. 새 아이콘 이미지 선택
4. **Next** → **Finish**

---

## 6. 서버 설정

### 6.1 서버 앱 정보 수정
**`server/data/apps.json`** 파일에서 새 앱 정보 추가:
```json
{
  "YOUR_NEW_UUID_HERE": {
    "id": "YOUR_NEW_UUID_HERE",
    "app_name": "My Awesome App",
    "app_id": "com.yourcompany.yourapp",
    "package_name": "com.yourcompany.yourapp",
    "version": "1.0.0",
    "description": "My awesome SDUI-based app",
    "status": "active",
    "created_at": "2024-06-15T00:00:00Z",
    "updated_at": "2024-06-15T00:00:00Z"
  }
}
```

### 6.2 메뉴 설정
**`server/data/menus.json`**에서 새 앱의 메뉴 추가:
```json
{
  "YOUR_NEW_UUID_HERE": [
    {
      "id": 1,
      "menu_name": "홈",
      "icon_name": "home",
      "menu_type": "ITEM",
      "action_type": "NAVIGATE",
      "action_value": "/home",
      "is_visible": true,
      "is_enabled": true,
      "order_index": 1
    }
    // ... 더 많은 메뉴들
  ]
}
```

---

## 7. 빌드 및 테스트

### 7.1 서버 실행
```bash
cd server
npm install
npm start
```
서버가 http://localhost:3000 에서 실행됩니다.

### 7.2 Android 앱 빌드
```bash
cd app
./gradlew clean assembleDebug
```

### 7.3 앱 설치 및 테스트
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🎯 완성! 

축하합니다! 🎉 새로운 SDUI 앱이 완성되었습니다!

### ✅ 확인사항
- [ ] 스플래시 화면에 새 앱 이름 표시
- [ ] 메인 화면에 새 앱 이름 표시  
- [ ] 메뉴 화면에서 알림 설정 작동
- [ ] FCM 푸시 알림 수신
- [ ] 인터넷 연결 끊김 시 "인터넷 연결을 확인해주세요" 메시지 표시

---

## 🚀 다음 단계

### 고급 커스터마이징
1. **UI 테마 변경**: `server/data/styles.json`에서 색상 변경
2. **메뉴 추가**: `server/data/menus.json`에서 새 메뉴 추가
3. **FCM 토픽 관리**: `AppConfig.kt`에서 알림 카테고리 수정
4. **아이콘 추가**: `app/src/main/res/drawable/`에 새 아이콘 추가

### 배포 준비
1. **서버 배포**: Heroku, AWS, Google Cloud 등에 배포
2. **앱 서명**: Google Play Console용 서명 키 생성
3. **Firebase 설정**: FCM을 위한 Firebase 프로젝트 생성

---

## 🆘 문제 해결

### 자주 발생하는 문제들

**Q: 빌드 에러가 발생해요**
```bash
# 캐시 정리 후 다시 빌드
./gradlew clean
./gradlew assembleDebug
```

**Q: FCM이 작동하지 않아요**
1. `app/google-services.json` 파일이 있는지 확인
2. Firebase 프로젝트에서 패키지명이 일치하는지 확인

**Q: 서버 연결이 안 돼요**
1. 서버가 실행 중인지 확인: `npm start`
2. 방화벽 설정 확인
3. `AppConfig.kt`의 `SERVER_BASE_URL` 확인

---

## 📞 지원

문제가 있으시면 GitHub Issues에 등록해주세요!

**Happy Coding! 🚀** 