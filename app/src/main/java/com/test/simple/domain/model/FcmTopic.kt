package com.test.simple.domain.model

data class FcmTopic(
    val topicName: String,
    val topicId: String,
    val isDefault: Boolean,
    val isActive: Boolean
) 