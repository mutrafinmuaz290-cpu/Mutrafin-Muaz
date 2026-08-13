package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

sealed class BottomNavItem(
    val title: String, 
    val selectedIcon: ImageVector, 
    val unselectedIcon: ImageVector,
    val route: String
) {
    object Home : BottomNavItem("Home", Icons.Filled.Home, Icons.Outlined.Home, "home")
    object Downloads : BottomNavItem("Downloads", Icons.Filled.Download, Icons.Filled.Download, "downloads")
    object Add : BottomNavItem("Add", Icons.Filled.AddCircle, Icons.Filled.AddCircle, "add")
    object Subscriptions : BottomNavItem("Subscriptions", Icons.Filled.Subscriptions, Icons.Outlined.Subscriptions, "subs")
    object Library : BottomNavItem("Library", Icons.Filled.VideoLibrary, Icons.Outlined.VideoLibrary, "library")
}

@Composable
fun MainScreen(onVideoClick: (String) -> Unit) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Downloads,
        BottomNavItem.Add,
        BottomNavItem.Subscriptions,
        BottomNavItem.Library
    )
    var selectedItem by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                items.forEach { item ->
                    val selected = selectedItem == item
                    NavigationBarItem(
                        icon = { 
                            if (item == BottomNavItem.Add) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(MaterialTheme.colorScheme.primary, shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AddCircle,
                                        contentDescription = "Add",
                                        tint = Color.White
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            }
                        },
                        label = {
                            if (item != BottomNavItem.Add) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (selected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium
                                    )
                                )
                            }
                        },
                        selected = selected,
                        onClick = { selectedItem = item },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = selectedItem,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            modifier = Modifier.padding(innerPadding),
            label = "tab_transition"
        ) { targetState ->
            when (targetState) {
                BottomNavItem.Home -> HomeTab(onVideoClick = onVideoClick)
                BottomNavItem.Downloads -> DownloadsTab(onVideoClick = onVideoClick)
                BottomNavItem.Add -> AddVideoTab()
                BottomNavItem.Subscriptions -> SubscriptionsTab()
                BottomNavItem.Library -> LibraryTab()
            }
        }
    }
}
