# ⚡ 2분 빠른 시작 가이드

새로운 SDUI 앱을 **2분 안에** 만들어보세요! 🚀

## 🚀 1단계: 템플릿 복사 (30초)

```bash
# 1. 템플릿 클론
git clone https://github.com/wjb127/sdui-kotlin-node.git my-new-app
cd my-new-app

# 2. Git 연결 끊기
rm -rf .git
git init
```

## 🔧 2단계: 앱 설정 변경 (30초)

**`app/src/main/java/com/test/simple/config/AppConfig.kt`** 파일에서 **패키지명만** 수정:

```kotlin
object AppConfig {
    // 📦 원하는 패키지명으로 변경
    const val PACKAGE_NAME = "com.mycompany.myapp"
    
    // 🎯 나머지는 기본값 사용!
    // APP_ID: 현재는 기본값, 실제 운영시 서버에서 패키지명으로 검색
    // APP_NAME: 서버에서 동적으로 가져오거나 기본값 사용
    // FCM_TOPICS: 서버에 등록된 토픽들 자동 설정
}
```

### ✨ 템플릿의 장점:
- **패키지명만 변경**: 가장 중요한 식별자만 수정
- **서버 연동**: 기존 서버 API 그대로 사용 가능
- **FCM 알림**: 토픽 기반 푸시 알림 바로 동작
- **확장성**: 실제 운영시 서버에서 패키지명 기반 앱 관리 가능

## 🏗️ 3단계: 빌드 및 실행 (2분)

```bash
# 1. 서버 실행
cd server
npm install
npm start &

# 2. 앱 빌드
cd ../app
./gradlew clean assembleDebug

# 3. 앱 설치
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 🎉 완성!

축하합니다! 새로운 앱이 완성되었습니다!

### ✅ 확인해보세요
- 스플래시 화면에 새 앱 이름 표시
- 메인 화면 정상 작동
- 메뉴에서 알림 설정 가능
- WiFi 끊으면 "인터넷 연결을 확인해주세요" 메시지

---

## 📚 더 자세한 설정

더 많은 커스터마이징을 원한다면 **[TEMPLATE_GUIDE.md](./TEMPLATE_GUIDE.md)**를 참고하세요!

**Happy Coding! 🚀** 