package com.applicforge.umbalarm.data.api.dto

data class FcmTokenDto(
    val token: String,
    val app_id: String,
    val device_type: String = "android",
    val timestamp: Long = System.currentTimeMillis()
) 