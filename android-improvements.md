# 안드로이드 앱 양산을 위한 개선 사항

## 1. 멀티 모듈 아키텍처

```
app/
├── core/                    # 공통 모듈
│   ├── network/            # 네트워크 레이어
│   ├── database/           # 로컬 DB
│   ├── common/             # 공통 유틸
│   └── design-system/      # 디자인 시스템
├── feature/                # 기능별 모듈
│   ├── config/            # Remote Config
│   ├── auth/              # 인증
│   └── analytics/         # 분석
└── app/                   # 메인 앱 모듈
```

## 2. 개선된 Repository Pattern

```kotlin
// Base Repository
abstract class BaseRepository<T> {
    protected abstract val api: T
    protected abstract val localDataSource: LocalDataSource
    
    suspend fun <R> safeApiCall(
        apiCall: suspend () -> Response<R>
    ): Result<R> = try {
        val response = apiCall()
        if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(ApiException(response.code(), response.message()))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

// 캐싱이 포함된 Config Repository
class ConfigRepository @Inject constructor(
    private val api: RemoteConfigApi,
    private val cache: ConfigCache,
    private val preferences: ConfigPreferences
) : BaseRepository<RemoteConfigApi>() {
    
    override val api = api
    override val localDataSource = cache
    
    suspend fun getConfig(appId: String, forceRefresh: Boolean = false): Flow<Resource<AppConfig>> = flow {
        emit(Resource.Loading())
        
        // 캐시에서 먼저 로드
        if (!forceRefresh) {
            val cached = cache.getConfig(appId)
            if (cached != null && !isExpired(cached)) {
                emit(Resource.Success(cached))
            }
        }
        
        // 네트워크에서 최신 데이터 가져오기
        safeApiCall { api.getConfig(appId) }
            .onSuccess { response ->
                val config = response.data
                cache.saveConfig(appId, config)
                emit(Resource.Success(config))
            }
            .onFailure { error ->
                // 캐시된 데이터가 있으면 그것을 사용
                val fallback = cache.getConfig(appId)
                if (fallback != null) {
                    emit(Resource.Success(fallback))
                } else {
                    emit(Resource.Error(error.message ?: "Unknown error"))
                }
            }
    }
}
```

## 3. 동적 UI 렌더링 시스템

```kotlin
// UI Component Factory
class DynamicUIFactory @Inject constructor() {
    
    @Composable
    fun CreateComponent(component: UIComponent) {
        when (component.type) {
            "button" -> DynamicButton(component.properties)
            "text" -> DynamicText(component.properties)
            "list" -> DynamicList(component.properties)
            "card" -> DynamicCard(component.properties)
            "menu" -> DynamicMenu(component.properties)
            else -> UnknownComponent(component)
        }
    }
}

// 동적 테마 시스템
@Composable
fun DynamicTheme(
    styleConfig: StyleConfig,
    content: @Composable () -> Unit
) {
    val colors = ColorScheme(
        primary = Color(parseColor(styleConfig.primaryColor)),
        secondary = Color(parseColor(styleConfig.secondaryColor)),
        background = Color(parseColor(styleConfig.backgroundColor))
    )
    
    MaterialTheme(
        colorScheme = colors,
        typography = createTypography(styleConfig.fonts),
        content = content
    )
}
```

## 4. 오프라인 지원

```kotlin
// Room Database
@Database(
    entities = [ConfigEntity::class, MenuEntity::class, StyleEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun configDao(): ConfigDao
    abstract fun menuDao(): MenuDao
    abstract fun styleDao(): StyleDao
}

// Offline First Repository
class OfflineFirstRepository @Inject constructor(
    private val api: RemoteConfigApi,
    private val dao: ConfigDao,
    private val networkManager: NetworkManager
) {
    
    fun getConfig(appId: String) = flow {
        // 로컬 데이터 먼저 emit
        val localData = dao.getConfig(appId)
        if (localData != null) {
            emit(Resource.Success(localData.toModel()))
        }
        
        // 네트워크 연결시 원격 데이터 가져오기
        if (networkManager.isConnected()) {
            try {
                val remoteData = api.getConfig(appId)
                dao.insertConfig(remoteData.toEntity())
                emit(Resource.Success(remoteData.data))
            } catch (e: Exception) {
                if (localData == null) {
                    emit(Resource.Error(e.message))
                }
            }
        }
    }
}
```

## 5. 성능 최적화

### 이미지 캐싱
```kotlin
// Coil 이미지 로더 설정
val imageLoader = ImageLoader.Builder(context)
    .memoryCache {
        MemoryCache.Builder(context)
            .maxSizePercent(0.25)
            .build()
    }
    .diskCache {
        DiskCache.Builder()
            .directory(context.cacheDir.resolve("image_cache"))
            .maxSizePercent(0.02)
            .build()
    }
    .build()
```

### LazyLoading
```kotlin
@Composable
fun DynamicList(items: List<UIComponent>) {
    LazyColumn {
        items(
            items = items,
            key = { it.id }
        ) { item ->
            CreateComponent(
                component = item,
                modifier = Modifier.animateItemPlacement()
            )
        }
    }
}
```

## 6. 테스팅 전략

```kotlin
// Repository Unit Test
@RunWith(MockitoJUnitRunner::class)
class ConfigRepositoryTest {
    
    @Mock
    private lateinit var api: RemoteConfigApi
    
    @Mock 
    private lateinit var cache: ConfigCache
    
    private lateinit var repository: ConfigRepository
    
    @Test
    fun `getConfig returns cached data when available`() = runTest {
        // Given
        val appId = "test-app"
        val cachedConfig = mockAppConfig()
        whenever(cache.getConfig(appId)).thenReturn(cachedConfig)
        
        // When
        val result = repository.getConfig(appId).first()
        
        // Then
        assertTrue(result is Resource.Success)
        assertEquals(cachedConfig, result.data)
    }
}

// UI Test
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun displaysConfigDataCorrectly() {
        val mockConfig = createMockConfig()
        
        composeTestRule.setContent {
            MainScreen(config = mockConfig)
        }
        
        composeTestRule
            .onNodeWithText(mockConfig.appInfo.name)
            .assertIsDisplayed()
    }
}
```

## 7. CI/CD 파이프라인

```yaml
# .github/workflows/android.yml
name: Android CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'temurin'
    
    - name: Run tests
      run: ./gradlew test
    
    - name: Run UI tests
      run: ./gradlew connectedAndroidTest
  
  build:
    needs: test
    runs-on: ubuntu-latest
    steps:
    - name: Build APK
      run: ./gradlew assembleRelease
    
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-release
        path: app/build/outputs/apk/release/
``` 