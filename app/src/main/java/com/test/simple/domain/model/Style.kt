package com.test.simple.domain.model

data class Style(
    val styleKey: String,
    val styleValue: String,
    val styleCategory: StyleCategory
)

enum class StyleCategory {
    COLOR, TYPOGRAPHY, SIZE
} 