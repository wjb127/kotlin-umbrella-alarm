package com.applicforge.umbalarm.domain.model

data class Style(
    val styleKey: String,
    val styleValue: String,
    val styleCategory: StyleCategory
)

enum class StyleCategory {
    COLOR, TYPOGRAPHY, SIZE
} 