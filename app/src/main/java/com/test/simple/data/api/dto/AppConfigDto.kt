package com.test.simple.data.api.dto

data class AppConfigDto(
    val menus: List<MenuDto>,
    val toolbars: List<ToolbarDto>,
    val styles: List<StyleDto>,
    val fcm_topics: List<FcmTopicDto>,
    val buttons: List<ButtonDto> = emptyList()
) 