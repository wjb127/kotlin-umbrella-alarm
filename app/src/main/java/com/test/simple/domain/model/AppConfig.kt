package com.test.simple.domain.model

data class AppConfig(
    val menus: List<Menu>,
    val toolbars: List<Toolbar>,
    val styles: List<Style>,
    val fcmTopics: List<FcmTopic>,
    val buttons: List<Button> = emptyList()
) 