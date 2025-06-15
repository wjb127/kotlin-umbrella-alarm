package com.applicforge.umbalarm.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.applicforge.umbalarm.data.repository.WeatherRepository
import com.applicforge.umbalarm.manager.LocationManager
import com.applicforge.umbalarm.manager.NotificationManager
import com.applicforge.umbalarm.manager.PreferencesManager
import com.applicforge.umbalarm.config.AppConfig
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * ☂️ 우산 필요 여부를 체크하는 백그라운드 Worker
 */
@HiltWorker
class UmbrellaCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val weatherRepository: WeatherRepository,
    private val locationManager: LocationManager,
    private val notificationManager: NotificationManager,
    private val preferencesManager: PreferencesManager
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "umbrella_check_work"
        const val API_KEY_INPUT = "api_key"
    }

    override suspend fun doWork(): Result {
        return try {
            // API 키 확인
            val apiKey = inputData.getString(API_KEY_INPUT) 
                ?: preferencesManager.getWeatherApiKey()
                ?: return Result.failure()

            // 위치 정보 가져오기
            val location = locationManager.getCurrentLocation()
                ?: return Result.retry()

            // 날씨 확인
            val umbrellaNeeded = weatherRepository.checkUmbrellaNeeded(
                latitude = location.latitude,
                longitude = location.longitude,
                apiKey = apiKey
            )

            umbrellaNeeded.fold(
                onSuccess = { needed ->
                    if (needed) {
                        // 우산 필요시 알림 발송
                        sendUmbrellaNotification()
                        
                        // 마지막 알림 시간 저장
                        preferencesManager.setLastUmbrellaNotificationTime(
                            System.currentTimeMillis()
                        )
                    }
                    Result.success()
                },
                onFailure = { error ->
                    android.util.Log.e("UmbrellaWorker", "날씨 확인 실패", error)
                    Result.retry()
                }
            )
        } catch (e: Exception) {
            android.util.Log.e("UmbrellaWorker", "Worker 실행 실패", e)
            Result.failure()
        }
    }

    private suspend fun sendUmbrellaNotification() {
        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        
        // 6시 이후에만 알림 발송
        if (currentHour >= 6) {
            val lastNotificationTime = preferencesManager.getLastUmbrellaNotificationTime()
            val currentTime = System.currentTimeMillis()
            val hoursSinceLastNotification = (currentTime - lastNotificationTime) / (1000 * 60 * 60)

            // 1시간에 한 번만 알림 (스팸 방지)
            if (hoursSinceLastNotification >= 1) {
                val title = "☂️ 우산을 챙기세요!"
                val message = when (currentHour) {
                    in 6..8 -> "오늘 비가 올 예정이에요. 출근할 때 우산을 챙기세요!"
                    in 9..11 -> "곧 비가 올 수 있어요. 외출 시 우산을 준비하세요!"
                    in 12..17 -> "오후에 비가 올 예정이에요. 우산을 챙기세요!"
                    in 18..20 -> "퇴근길에 비가 올 수 있어요. 우산을 준비하세요!"
                    else -> "비가 올 예정이에요. 우산을 챙기세요!"
                }

                notificationManager.sendUmbrellaNotification(
                    title = title,
                    message = message
                )
            }
        }
    }
} 