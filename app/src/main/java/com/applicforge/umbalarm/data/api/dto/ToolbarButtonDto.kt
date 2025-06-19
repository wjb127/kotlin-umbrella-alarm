package com.applicforge.umbalarm.data.api.dto

import com.google.gson.annotations.SerializedName

data class ToolbarButtonDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("action_type")
    val actionType: String? = null,
    @SerializedName("action_value")
    val actionValue: String? = null,
    @SerializedName("order_index")
    val orderIndex: Int? = null
) 