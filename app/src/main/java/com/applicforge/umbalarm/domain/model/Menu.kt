package com.applicforge.umbalarm.domain.model

data class Menu(
    val menuId: String,
    val title: String,
    val icon: String,
    val orderIndex: Int,
    val menuType: MenuType,
    val actionType: ActionType,
    val actionValue: String,
    val isVisible: Boolean,
    val isEnabled: Boolean
)

enum class MenuType {
    ITEM, CATEGORY, DIVIDER
}

enum class ActionType {
    NAVIGATE, EXTERNAL_LINK, API_CALL
} 