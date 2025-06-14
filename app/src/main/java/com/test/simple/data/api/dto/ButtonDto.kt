package com.test.simple.data.api.dto

import com.google.gson.annotations.SerializedName

data class ButtonDto(
    @SerializedName("button_id")
    val buttonId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("button_type")
    val buttonType: String, // PRIMARY, SECONDARY, OUTLINED, TEXT
    @SerializedName("action_type")
    val actionType: String, // NAVIGATE, EXTERNAL_LINK, API_CALL, FUNCTION
    @SerializedName("action_value")
    val actionValue: String,
    @SerializedName("background_color")
    val backgroundColor: String?,
    @SerializedName("text_color")
    val textColor: String?,
    @SerializedName("position")
    val position: String, // LEFT, CENTER, RIGHT
    @SerializedName("order_index")
    val orderIndex: Int,
    @SerializedName("is_visible")
    val isVisible: Boolean,
    @SerializedName("is_enabled")
    val isEnabled: Boolean
) 