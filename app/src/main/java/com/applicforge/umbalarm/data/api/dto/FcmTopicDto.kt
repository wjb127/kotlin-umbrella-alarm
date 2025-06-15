package com.applicforge.umbalarm.data.api.dto

data class FcmTopicDto(
    val topic_name: String,
    val topic_id: String,
    val is_default: Boolean,
    val is_active: Boolean
) 