package com.applicforge.umbalarm.data.api.dto

import com.google.gson.annotations.SerializedName

data class MenuDto(
    @SerializedName("menu_id")
    val menuId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("order_index")
    val orderIndex: Int,
    @SerializedName("menu_type")
    val menuType: String,
    @SerializedName("action_type")
    val actionType: String,
    @SerializedName("action_value")
    val actionValue: String,
    @SerializedName("is_visible")
    val isVisible: Boolean,
    @SerializedName("is_enabled")
    val isEnabled: Boolean
) 