package com.applicforge.umbalarm.manager

import android.app.NotificationChannel
import android.app.NotificationManager as AndroidNotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.applicforge.umbalarm.R
import com.applicforge.umbalarm.presentation.ui.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ☂️ 우산 알림 관리 매니저
 */
@Singleton
class NotificationManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val UMBRELLA_CHANNEL_ID = "umbrella_notifications"
        private const val UMBRELLA_CHANNEL_NAME = "우산 알림"
        private const val UMBRELLA_NOTIFICATION_ID = 1001
    }

    init {
        createNotificationChannel()
    }

    /**
     * 우산 알림 발송
     */
    fun sendUmbrellaNotification(title: String, message: String) {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            android.util.Log.w("NotificationManager", "알림 권한이 비활성화됨")
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, UMBRELLA_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_umbrella)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(UMBRELLA_NOTIFICATION_ID, notification)
            android.util.Log.d("NotificationManager", "우산 알림 발송: $title")
        } catch (e: SecurityException) {
            android.util.Log.e("NotificationManager", "알림 발송 권한 없음", e)
        } catch (e: Exception) {
            android.util.Log.e("NotificationManager", "알림 발송 실패", e)
        }
    }

    /**
     * 날씨 업데이트 알림 발송
     */
    fun sendWeatherUpdateNotification(title: String, message: String) {
        sendUmbrellaNotification(title, message)
    }

    /**
     * 알림 권한 확인
     */
    fun areNotificationsEnabled(): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    /**
     * 알림 채널 생성
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                UMBRELLA_CHANNEL_ID,
                UMBRELLA_CHANNEL_NAME,
                AndroidNotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "우산이 필요한 날 알림을 받습니다"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as AndroidNotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * 알림 취소
     */
    fun cancelUmbrellaNotification() {
        NotificationManagerCompat.from(context).cancel(UMBRELLA_NOTIFICATION_ID)
    }

    /**
     * 모든 알림 취소
     */
    fun cancelAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }
} 