package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppTab
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BorderLight
import com.example.ui.theme.Navy900
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@Composable
fun CarWashBottomNav(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        border = BorderStroke(1.dp, BorderLight),
        shadowElevation = 8.dp,
        modifier = modifier
    ) {
        NavigationBar(
            modifier = Modifier
                .testTag("bottom_nav_bar")
                .windowInsetsPadding(WindowInsets.navigationBars),
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {
            val items = listOf(
                Triple(AppTab.ACCUEIL, Icons.Filled.Home, Icons.Outlined.Home),
                Triple(AppTab.PRESTATIONS, Icons.Filled.ListAlt, Icons.Outlined.ListAlt),
                Triple(AppTab.CLIENTS, Icons.Filled.People, Icons.Outlined.People),
                Triple(AppTab.VEHICULES, Icons.Filled.DirectionsCar, Icons.Outlined.DirectionsCar),
                Triple(AppTab.PLUS, Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz)
            )

            items.forEach { (tab, selectedIcon, unselectedIcon) ->
                val isSelected = currentTab == tab
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onTabSelected(tab) },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) selectedIcon else unselectedIcon,
                            contentDescription = tab.title
                        )
                    },
                    label = {
                        Text(
                            text = tab.title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Navy900,
                        indicatorColor = Navy900,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                )
            }
        }
    }
}
