package com.test.simple.data.mapper

import com.test.simple.data.api.dto.*
import com.test.simple.domain.model.*

object ConfigMapper {
    
    fun mapMenuDtoToDomain(dto: MenuDto): Menu {
        return Menu(
            menuId = dto.menuId,
            title = dto.title,
            icon = dto.icon,
            orderIndex = dto.orderIndex,
            menuType = when (dto.menuType.uppercase()) {
                "ITEM" -> MenuType.ITEM
                "CATEGORY" -> MenuType.CATEGORY
                "DIVIDER" -> MenuType.DIVIDER
                else -> MenuType.ITEM
            },
            actionType = when (dto.actionType.uppercase()) {
                "NAVIGATE" -> ActionType.NAVIGATE
                "EXTERNAL_LINK" -> ActionType.EXTERNAL_LINK
                "API_CALL" -> ActionType.API_CALL
                else -> ActionType.NAVIGATE
            },
            actionValue = dto.actionValue,
            isVisible = dto.isVisible,
            isEnabled = dto.isEnabled
        )
    }
    
    fun mapToolbarDtoToDomain(dto: ToolbarDto): Toolbar {
        return Toolbar(
            toolbarId = dto.toolbar_id,
            title = dto.title,
            position = when (dto.position.uppercase()) {
                "TOP" -> ToolbarPosition.TOP
                "BOTTOM" -> ToolbarPosition.BOTTOM
                else -> ToolbarPosition.TOP
            },
            backgroundColor = dto.background_color,
            textColor = dto.text_color,
            isVisible = dto.is_visible,
            buttons = dto.buttons?.map { mapToolbarButtonDtoToDomain(it) } ?: emptyList()
        )
    }
    
    fun mapToolbarButtonDtoToDomain(dto: com.test.simple.data.api.dto.ToolbarButtonDto): com.test.simple.domain.model.ToolbarButton {
        return com.test.simple.domain.model.ToolbarButton(
            id = dto.id,
            icon = dto.icon,
            title = dto.title,
            action = dto.action
        )
    }
    
    fun mapButtonDtoToDomain(dto: ButtonDto): Button {
        return Button(
            buttonId = dto.buttonId,
            title = dto.title,
            icon = dto.icon,
            buttonType = when (dto.buttonType.uppercase()) {
                "PRIMARY" -> ButtonType.PRIMARY
                "SECONDARY" -> ButtonType.SECONDARY
                "OUTLINED" -> ButtonType.OUTLINED
                "TEXT" -> ButtonType.TEXT
                else -> ButtonType.PRIMARY
            },
            actionType = when (dto.actionType.uppercase()) {
                "NAVIGATE" -> ActionType.NAVIGATE
                "EXTERNAL_LINK" -> ActionType.EXTERNAL_LINK
                "API_CALL" -> ActionType.API_CALL
                else -> ActionType.NAVIGATE
            },
            actionValue = dto.actionValue,
            backgroundColor = dto.backgroundColor,
            textColor = dto.textColor,
            position = when (dto.position.uppercase()) {
                "LEFT" -> ButtonPosition.LEFT
                "CENTER" -> ButtonPosition.CENTER
                "RIGHT" -> ButtonPosition.RIGHT
                else -> ButtonPosition.CENTER
            },
            orderIndex = dto.orderIndex,
            isVisible = dto.isVisible,
            isEnabled = dto.isEnabled
        )
    }
    
    fun mapStyleDtoToDomain(dto: StyleDto): Style {
        return Style(
            styleKey = dto.style_key,
            styleValue = dto.style_value,
            styleCategory = when (dto.style_category.uppercase()) {
                "COLOR" -> StyleCategory.COLOR
                "TYPOGRAPHY" -> StyleCategory.TYPOGRAPHY
                "SIZE" -> StyleCategory.SIZE
                else -> StyleCategory.COLOR
            }
        )
    }
    
    fun mapFcmTopicDtoToDomain(dto: FcmTopicDto): FcmTopic {
        return FcmTopic(
            topicName = dto.topic_name,
            topicId = dto.topic_id,
            isDefault = dto.is_default,
            isActive = dto.is_active
        )
    }
    
    fun mapAppConfigDtoToDomain(dto: AppConfigDto): AppConfig {
        return AppConfig(
            menus = dto.menus.map { mapMenuDtoToDomain(it) },
            toolbars = dto.toolbars.map { mapToolbarDtoToDomain(it) },
            styles = dto.styles.map { mapStyleDtoToDomain(it) },
            fcmTopics = dto.fcm_topics.map { mapFcmTopicDtoToDomain(it) },
            buttons = dto.buttons.map { mapButtonDtoToDomain(it) }
        )
    }
    
    fun mapConfigResponseDtoToDomain(dto: com.test.simple.data.api.ConfigResponseDto): AppConfig {
        return AppConfig(
            menus = dto.menus.map { mapMenuDtoToDomain(it) },
            toolbars = dto.toolbars.map { mapToolbarDtoToDomain(it) },
            styles = dto.styles?.map { mapStyleDtoToDomain(it) } ?: emptyList(),
            fcmTopics = dto.fcm_topics.map { mapFcmTopicDtoToDomain(it) },
            buttons = dto.buttons?.map { mapButtonDtoToDomain(it) } ?: emptyList()
        )
    }
} 