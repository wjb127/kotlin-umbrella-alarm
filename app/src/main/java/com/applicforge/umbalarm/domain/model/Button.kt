package com.applicforge.umbalarm.domain.model

data class Button(
    val buttonId: String,
    val title: String,
    val icon: String,
    val buttonType: ButtonType,
    val actionType: ActionType,
    val actionValue: String,
    val backgroundColor: String?,
    val textColor: String?,
    val position: ButtonPosition,
    val orderIndex: Int,
    val isVisible: Boolean,
    val isEnabled: Boolean
)

enum class ButtonType {
    PRIMARY, SECONDARY, OUTLINED, TEXT
}

enum class ButtonPosition {
    LEFT, CENTER, RIGHT
} 