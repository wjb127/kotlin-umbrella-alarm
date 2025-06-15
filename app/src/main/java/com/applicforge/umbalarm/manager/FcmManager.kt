package com.applicforge.umbalarm.manager

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessaging
import com.applicforge.umbalarm.config.AppConfig
import com.applicforge.umbalarm.domain.model.FcmTopic
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmManager @Inject constructor(
    private val context: Context,
    private val preferencesManager: PreferencesManager
) {
    
    companion object {
        private const val TAG = "FcmManager"
    }
    
    fun getToken(callback: (String?) -> Unit) {
        Log.d(TAG, "🔄 FCM 토큰 요청 시작...")
        
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "❌ FCM 토큰 가져오기 실패", task.exception)
                Toast.makeText(context, "❌ FCM 토큰 획득 실패", Toast.LENGTH_SHORT).show()
                callback(null)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d(TAG, "🔑 FCM 토큰 성공: ${token?.substring(0, 20)}...")
            Toast.makeText(context, "✅ FCM 토큰 획득 성공!", Toast.LENGTH_SHORT).show()
            callback(token)
        }
    }
    
    fun subscribeToTopics(fcmTopics: List<FcmTopic>) {
        Log.d(TAG, "📋 Remote Config 토픽 구독 시작: ${fcmTopics.size}개")
        
        fcmTopics.forEach { fcmTopic ->
            if (fcmTopic.isActive) {
                Log.d(TAG, "🟢 활성 토픽 구독: ${fcmTopic.topicName}")
                subscribeToTopic(fcmTopic.topicName)
            } else {
                Log.d(TAG, "🔴 비활성 토픽 구독 해제: ${fcmTopic.topicName}")
                unsubscribeFromTopic(fcmTopic.topicName)
            }
        }
    }
    
    fun subscribeToTopic(topicName: String) {
        Log.d(TAG, "🔔 토픽 구독 시도: $topicName")
        
        FirebaseMessaging.getInstance().subscribeToTopic(topicName)
            .addOnCompleteListener { task ->
                val msg = if (task.isSuccessful) {
                    "✅ 토픽 구독 성공: $topicName"
                } else {
                    "❌ 토픽 구독 실패: $topicName - ${task.exception?.message}"
                }
                Log.d(TAG, msg)
                
                // Toast로 피드백 (개발용)
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
    }
    
    fun unsubscribeFromTopic(topicName: String) {
        Log.d(TAG, "🚫 토픽 구독 해제 시도: $topicName")
        
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topicName)
            .addOnCompleteListener { task ->
                val msg = if (task.isSuccessful) {
                    "✅ 토픽 구독 취소 성공: $topicName"
                } else {
                    "❌ 토픽 구독 취소 실패: $topicName - ${task.exception?.message}"
                }
                Log.d(TAG, msg)
            }
    }
    
    fun subscribeToBasicTopics() {
        // 사용자 설정 가능한 토픽들 처리
        val userTopics = AppConfig.FCM.USER_VISIBLE_TOPICS
        Log.d(TAG, "🚀 사용자 토픽 구독 상태 확인: ${userTopics.size}개")
        Log.d(TAG, "📝 사용자 토픽들: ${userTopics.joinToString(", ")}")
        
        userTopics.forEach { topic ->
            if (preferencesManager.isTopicSubscribed(topic)) {
                Log.d(TAG, "✅ 구독 설정된 사용자 토픽: $topic")
                subscribeToTopic(topic)
            } else {
                Log.d(TAG, "❌ 구독 해제된 사용자 토픽: $topic")
                unsubscribeFromTopic(topic)
            }
        }
        
        // 자동 구독 토픽들 (항상 구독)
        val autoTopics = AppConfig.FCM.AUTO_SUBSCRIBE_TOPICS
        Log.d(TAG, "🔒 자동 구독 토픽 처리: ${autoTopics.size}개")
        Log.d(TAG, "📝 자동 구독 토픽들: ${autoTopics.joinToString(", ")}")
        
        autoTopics.forEach { topic ->
            Log.d(TAG, "✅ 자동 구독 토픽: $topic")
            subscribeToTopic(topic)
            // 자동 구독 토픽은 항상 구독 상태로 저장
            preferencesManager.setTopicSubscribed(topic, true)
        }
        
        // 숨겨진 토픽들 (개발/테스트용)
        val hiddenTopics = AppConfig.FCM.HIDDEN_TOPICS
        Log.d(TAG, "🔍 숨겨진 토픽 처리: ${hiddenTopics.size}개")
        Log.d(TAG, "📝 숨겨진 토픽들: ${hiddenTopics.joinToString(", ")}")
        
        hiddenTopics.forEach { topic ->
            // 숨겨진 토픽은 기본적으로 구독하지 않음
            Log.d(TAG, "🚫 숨겨진 토픽 구독 해제: $topic")
            unsubscribeFromTopic(topic)
            preferencesManager.setTopicSubscribed(topic, false)
        }
    }
    
    fun toggleTopicSubscription(topicName: String, subscribe: Boolean) {
        Log.d(TAG, "🔄 토픽 구독 상태 변경: $topicName -> ${if (subscribe) "구독" else "구독 해제"}")
        
        preferencesManager.setTopicSubscribed(topicName, subscribe)
        
        if (subscribe) {
            subscribeToTopic(topicName)
        } else {
            unsubscribeFromTopic(topicName)
        }
    }
    
    fun getAvailableTopics(): List<String> {
        // 모든 토픽 목록 반환 (내부 관리용)
        return AppConfig.FCM.ALL_TOPICS
    }
    
    fun getUserVisibleTopics(): List<String> {
        // 사용자에게 보여줄 토픽만 반환 ⚡ 템플릿 설정!
        return AppConfig.FCM.USER_VISIBLE_TOPICS
    }
    
    fun getTopicSubscriptionStatus(): Map<String, Boolean> {
        // 사용자에게 보여줄 토픽들의 구독 상태만 반환
        val topics = getUserVisibleTopics()
        return topics.associateWith { preferencesManager.isTopicSubscribed(it) }
    }
    
    fun getTopicSubscriptionStatus(topicName: String): Boolean {
        // 특정 토픽의 구독 상태 반환
        return preferencesManager.isTopicSubscribed(topicName)
    }
    
    fun logFcmStatus() {
        Log.d(TAG, "📊 FCM 상태 확인")
        Log.d(TAG, "💾 Context: ${context.javaClass.simpleName}")
        Log.d(TAG, "🎯 Firebase Messaging 인스턴스: ${FirebaseMessaging.getInstance()}")
        
        // 토큰 다시 확인
        getToken { token ->
            Log.d(TAG, "🔍 현재 FCM 토큰 상태: ${if (token != null) "정상" else "없음"}")
        }
    }
} 