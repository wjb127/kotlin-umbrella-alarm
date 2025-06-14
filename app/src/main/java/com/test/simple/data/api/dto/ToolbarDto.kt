package com.test.simple.data.api.dto

import com.google.gson.annotations.SerializedName

data class ToolbarDto(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("app_id")
    val appId: String? = null,
    @SerializedName("toolbar_id")
    val toolbar_id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("position")
    val position: String,
    @SerializedName("background_color")
    val background_color: String,
    @SerializedName("text_color")
    val text_color: String,
    @SerializedName("height")
    val height: Int? = null,
    @SerializedName("is_visible")
    val is_visible: Boolean,
    @SerializedName("buttons")
    val buttons: List<ToolbarButtonDto>? = null,
    @SerializedName("created_at")
    val created_at: String? = null,
    @SerializedName("updated_at")
    val updated_at: String? = null
) 