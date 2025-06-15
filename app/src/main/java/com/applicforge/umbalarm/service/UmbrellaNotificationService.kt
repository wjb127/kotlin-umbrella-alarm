package com.applicforge.umbalarm.service

import android.content.Context
import androidx.work.*
import com.applicforge.umbalarm.worker.UmbrellaCheckWorker
import com.applicforge.umbalarm.manager.PreferencesManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ☂️ 우산 알림 워커 스케줄링 서비스
 */
@Singleton
class UmbrellaNotificationService @Inject constructor(
    private val context: Context,
    private val preferencesManager: PreferencesManager
) {

    companion object {
        private const val UMBRELLA_WORK_TAG = "umbrella_check_work"
        private const val CHECK_INTERVAL_HOURS = 2L // 2시간마다 체크
    }

    /**
     * 우산 알림 워커 시작
     */
    fun startUmbrellaNotifications(apiKey: String) {
        if (!preferencesManager.isUmbrellaNotificationsEnabled()) {
            return
        }

        // API 키 저장
        preferencesManager.setWeatherApiKey(apiKey)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(false)
            .build()

        val inputData = Data.Builder()
            .putString(UmbrellaCheckWorker.API_KEY_INPUT, apiKey)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<UmbrellaCheckWorker>(
            CHECK_INTERVAL_HOURS, TimeUnit.HOURS,
            30, TimeUnit.MINUTES // 30분 flex period
        )
            .setConstraints(constraints)
            .setInputData(inputData)
            .addTag(UMBRELLA_WORK_TAG)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                1, TimeUnit.HOURS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UmbrellaCheckWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )

        android.util.Log.d("UmbrellaService", "우산 알림 워커 시작됨")
    }

    /**
     * 우산 알림 워커 중지
     */
    fun stopUmbrellaNotifications() {
        WorkManager.getInstance(context).cancelUniqueWork(UmbrellaCheckWorker.WORK_NAME)
        WorkManager.getInstance(context).cancelAllWorkByTag(UMBRELLA_WORK_TAG)
        
        android.util.Log.d("UmbrellaService", "우산 알림 워커 중지됨")
    }

    /**
     * 즉시 날씨 확인 실행
     */
    fun checkWeatherNow(apiKey: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = Data.Builder()
            .putString(UmbrellaCheckWorker.API_KEY_INPUT, apiKey)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<UmbrellaCheckWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .addTag("immediate_weather_check")
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
        
        android.util.Log.d("UmbrellaService", "즉시 날씨 확인 실행")
    }

    /**
     * 워커 상태 확인
     */
    fun isWorkerRunning(): Boolean {
        val workInfos = WorkManager.getInstance(context)
            .getWorkInfosForUniqueWork(UmbrellaCheckWorker.WORK_NAME)
            .get()
        
        return workInfos?.any { workInfo ->
            workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING
        } ?: false
    }

    /**
     * 워커 재시작 (설정 변경 시)
     */
    fun restartWithNewSettings(apiKey: String) {
        stopUmbrellaNotifications()
        // 잠깐 대기 후 재시작
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            startUmbrellaNotifications(apiKey)
        }, 1000)
    }
} 