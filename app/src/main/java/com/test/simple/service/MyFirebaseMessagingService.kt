package com.test.simple.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.test.simple.R
import com.test.simple.presentation.ui.MainActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "fcm_default_channel"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Log.d(TAG, "🎉 FCM 메시지 수신!")
        Log.d(TAG, "📨 From: ${remoteMessage.from}")
        Log.d(TAG, "🕐 수신 시간: ${System.currentTimeMillis()}")
        Log.d(TAG, "🔔 메시지 ID: ${remoteMessage.messageId}")
        Log.d(TAG, "⏰ TTL: ${remoteMessage.ttl}")
        
        // 알림 데이터 확인
        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "📢 [알림] 제목: ${notification.title}")
            Log.d(TAG, "📢 [알림] 내용: ${notification.body}")
            Log.d(TAG, "📢 [알림] 아이콘: ${notification.icon}")
            Log.d(TAG, "📢 [알림] 이미지: ${notification.imageUrl}")
            
            // 알림 표시
            sendNotification(
                title = notification.title ?: "새 알림",
                body = notification.body ?: "새 메시지가 있습니다",
                imageUrl = notification.imageUrl?.toString()
            )
        }
        
        // 데이터 메시지 처리
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "📦 [데이터] 페이로드 개수: ${remoteMessage.data.size}")
            remoteMessage.data.forEach { (key, value) ->
                Log.d(TAG, "📦 [데이터] $key: $value")
            }
            
            // 데이터 전용 메시지인 경우 알림 생성
            if (remoteMessage.notification == null) {
                Log.d(TAG, "🔄 데이터 전용 메시지로 알림 생성")
                val title = remoteMessage.data["title"] ?: "새 알림"
                val body = remoteMessage.data["body"] ?: "새 메시지가 있습니다"
                sendNotification(title, body)
            }
        }
        
        // 둘 다 없는 경우
        if (remoteMessage.notification == null && remoteMessage.data.isEmpty()) {
            Log.w(TAG, "⚠️ 빈 메시지 수신 - 알림과 데이터 모두 없음")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "🔄 새 FCM 토큰: $token")
        
        // 새 토큰을 서버에 전송
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        Log.d(TAG, "📤 서버에 토큰 전송: $token")
        // TODO: 필요시 서버 API 호출하여 토큰 저장
        // 예: API 호출로 토큰을 서버에 저장
    }

    private fun sendNotification(
        title: String, 
        body: String, 
        imageUrl: String? = null
    ) {
        Log.d(TAG, "🔔 알림 생성: $title")
        
        // MainActivity로 이동하는 Intent
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 
            0, 
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 알림 채널 생성
        createNotificationChannel()

        // 알림 빌더
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        // 알림 표시
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        
        Log.d(TAG, "✅ 알림 표시 완료")
    }

    private fun createNotificationChannel() {
        // Android O (API 26) 이상에서만 알림 채널 생성
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "FCM 알림"
            val descriptionText = "Firebase Cloud Messaging 알림 채널"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            
            // 알림 매니저에 채널 등록
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
} 