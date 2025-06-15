package com.applicforge.umbalarm.domain.model

data class FcmTopic(
    val topicName: String,
    val topicId: String,
    val isDefault: Boolean,
    val isActive: Boolean
) 