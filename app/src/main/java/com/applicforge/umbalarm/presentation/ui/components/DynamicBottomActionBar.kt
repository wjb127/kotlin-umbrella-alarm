package com.applicforge.umbalarm.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import com.applicforge.umbalarm.domain.model.Button
import com.applicforge.umbalarm.domain.model.ButtonType
import com.applicforge.umbalarm.domain.model.ButtonPosition
import com.applicforge.umbalarm.domain.model.ActionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicBottomActionBar(
    buttons: List<Button>,
    onButtonClick: (Button) -> Unit,
    modifier: Modifier = Modifier
) {
    // 보이는 버튼만 필터링하고 순서대로 정렬
    val visibleButtons = buttons
        .filter { it.isVisible }
        .sortedBy { it.orderIndex }
    
    if (visibleButtons.isEmpty()) return
    
    BottomAppBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp
    ) {
        // 포지션별로 그룹화
        val leftButtons = visibleButtons.filter { it.position == ButtonPosition.LEFT }
        val centerButtons = visibleButtons.filter { it.position == ButtonPosition.CENTER }
        val rightButtons = visibleButtons.filter { it.position == ButtonPosition.RIGHT }
        
        // 왼쪽 버튼들
        leftButtons.forEach { button ->
            DynamicActionButton(
                button = button,
                onClick = { onButtonClick(button) },
                modifier = Modifier.weight(1f)
            )
        }
        
        // 중앙 정렬을 위한 Spacer
        if (centerButtons.isNotEmpty()) {
            Spacer(modifier = Modifier.weight(1f))
            
            // 중앙 버튼들
            centerButtons.forEach { button ->
                DynamicActionButton(
                    button = button,
                    onClick = { onButtonClick(button) },
                    modifier = Modifier.wrapContentWidth()
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
        
        // 오른쪽 버튼들
        rightButtons.forEach { button ->
            DynamicActionButton(
                button = button,
                onClick = { onButtonClick(button) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DynamicActionButton(
    button: Button,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = getIconFromString(button.icon)
    val backgroundColor = parseColor(button.backgroundColor)
    val textColor = parseColor(button.textColor) ?: getContrastColor(backgroundColor)
    
    when (button.buttonType) {
        ButtonType.PRIMARY -> {
            Button(
                onClick = onClick,
                enabled = button.isEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = backgroundColor ?: MaterialTheme.colorScheme.primary,
                    contentColor = textColor
                ),
                modifier = modifier.padding(horizontal = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = button.title,
                        modifier = Modifier.size(18.dp)
                    )
                    if (button.title.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = button.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        ButtonType.OUTLINED -> {
            OutlinedButton(
                onClick = onClick,
                enabled = button.isEnabled,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = textColor ?: MaterialTheme.colorScheme.primary
                ),
                modifier = modifier.padding(horizontal = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = button.title,
                        modifier = Modifier.size(18.dp)
                    )
                    if (button.title.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = button.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        ButtonType.TEXT -> {
            TextButton(
                onClick = onClick,
                enabled = button.isEnabled,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = textColor ?: MaterialTheme.colorScheme.primary
                ),
                modifier = modifier.padding(horizontal = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = button.title,
                        modifier = Modifier.size(18.dp)
                    )
                    if (button.title.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = button.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        ButtonType.SECONDARY -> {
            FilledTonalButton(
                onClick = onClick,
                enabled = button.isEnabled,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = backgroundColor ?: MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = textColor ?: MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = modifier.padding(horizontal = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = button.title,
                        modifier = Modifier.size(18.dp)
                    )
                    if (button.title.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = button.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

private fun getIconFromString(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "home" -> Icons.Default.Home
        "favorite", "heart" -> Icons.Default.Favorite
        "share" -> Icons.Default.Share
        "add" -> Icons.Default.Add
        "edit" -> Icons.Default.Edit
        "delete" -> Icons.Default.Delete
        "search" -> Icons.Default.Search
        "settings" -> Icons.Default.Settings
        "person" -> Icons.Default.Person
        "notification", "notifications" -> Icons.Default.Notifications
        "mail" -> Icons.Default.MailOutline
        "phone" -> Icons.Default.Phone
        "info" -> Icons.Default.Info
        "help" -> Icons.Default.Info // Help 아이콘이 없으므로 Info로 대체
        "refresh" -> Icons.Default.Refresh
        "download" -> Icons.Default.KeyboardArrowDown // Download 아이콘이 없으므로 KeyboardArrowDown로 대체
        "upload" -> Icons.Default.KeyboardArrowUp // Upload 아이콘이 없으므로 KeyboardArrowUp로 대체
        "bookmark" -> Icons.Default.Star // Bookmark 아이콘이 없으므로 Star로 대체
        "play" -> Icons.Default.PlayArrow
        "pause" -> Icons.Default.PlayArrow // Pause 아이콘이 없으므로 PlayArrow로 대체
        "stop" -> Icons.Default.Clear // Stop 아이콘이 없으므로 Clear로 대체
        "check" -> Icons.Default.Check
        "close" -> Icons.Default.Close
        "arrow_back" -> Icons.Default.ArrowBack
        "arrow_forward" -> Icons.Default.ArrowForward
        "menu" -> Icons.Default.Menu
        else -> Icons.Default.Star
    }
}

private fun parseColor(colorString: String?): Color? {
    if (colorString.isNullOrEmpty()) return null
    return try {
        val cleanColor = if (colorString.startsWith("#")) {
            colorString
        } else {
            "#$colorString"
        }
        Color(android.graphics.Color.parseColor(cleanColor))
    } catch (e: Exception) {
        null
    }
}

private fun getContrastColor(backgroundColor: Color?): Color {
    if (backgroundColor == null) return Color.White
    
    val luminance = ColorUtils.calculateLuminance(backgroundColor.toArgb())
    return if (luminance > 0.5) Color.Black else Color.White
} 