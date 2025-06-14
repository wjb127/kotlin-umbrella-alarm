package com.test.simple.domain.model

data class Toolbar(
    val toolbarId: String,
    val title: String,
    val position: ToolbarPosition,
    val backgroundColor: String,
    val textColor: String,
    val isVisible: Boolean,
    val buttons: List<ToolbarButton> = emptyList()
)

enum class ToolbarPosition {
    TOP, BOTTOM
} 