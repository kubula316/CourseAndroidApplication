package eu.tutorials.courseapplication.navigation

import android.service.autofill.OnClickAction
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNotification: Boolean,
    val badgeCount: Int? = null,
    val onClickAction: ()-> Unit
)


