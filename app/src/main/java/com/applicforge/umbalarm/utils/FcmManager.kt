package com.applicforge.umbalarm.utils

import com.google.firebase.messaging.FirebaseMessaging
import com.applicforge.umbalarm.domain.model.FcmTopic
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmManager @Inject constructor() {
    
    private val firebaseMessaging by lazy { 
        try {
            FirebaseMessaging.getInstance()
        } catch (e: Exception) {
            null
        }
    }
    
    fun subscribeToTopics(topics: List<FcmTopic>) {
        topics.forEach { topic ->
            if (topic.isActive) {
                subscribeToTopic(topic.topicId)
            }
        }
    }
    
    private fun subscribeToTopic(topicId: String) {
        firebaseMessaging?.subscribeToTopic(topicId)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Successfully subscribed to topic: $topicId")
                } else {
                    println("Failed to subscribe to topic: $topicId")
                }
            } ?: println("Firebase not initialized, cannot subscribe to topic: $topicId")
    }
    
    fun unsubscribeFromTopic(topicId: String) {
        firebaseMessaging?.unsubscribeFromTopic(topicId)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Successfully unsubscribed from topic: $topicId")
                } else {
                    println("Failed to unsubscribe from topic: $topicId")
                }
            } ?: println("Firebase not initialized, cannot unsubscribe from topic: $topicId")
    }
    
    fun getToken(callback: (String?) -> Unit) {
        firebaseMessaging?.token?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                callback(token)
            } else {
                callback(null)
            }
        } ?: callback(null)
    }
} 