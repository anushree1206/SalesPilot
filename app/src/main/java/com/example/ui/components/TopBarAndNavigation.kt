package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.example.data.local.entity.BusinessProfileEntity
import com.example.ui.NavigationTab
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesPilotTopBar(
    profile: BusinessProfileEntity?,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    onSimulateClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSignOutClick: () -> Unit = {}
) {
    var showLangMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = profile?.businessName ?: "SalesPilot",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = LavenderPrimaryContainer
                    ) {
                        Text(
                            text = "AI",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = LavenderPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "${profile?.category ?: "Boutique"} • ${profile?.ownerName ?: "Owner"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        },
        actions = {
            // Language selector button
            Box {
                OutlinedButton(
                    onClick = { showLangMenu = true },
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("language_selector_btn"),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LavenderCardBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Language",
                        tint = LavenderPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when (currentLanguage) {
                            "hi" -> "हिंदी"
                            "kn" -> "ಕನ್ನಡ"
                            else -> "EN"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LavenderPrimary
                    )
                }

                DropdownMenu(
                    expanded = showLangMenu,
                    onDismissRequest = { showLangMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("English (EN)") },
                        onClick = {
                            onLanguageChange("en")
                            showLangMenu = false
                        },
                        leadingIcon = {
                            if (currentLanguage == "en") {
                                Icon(Icons.Default.Check, contentDescription = null, tint = LavenderPrimary)
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("हिंदी (Hindi)") },
                        onClick = {
                            onLanguageChange("hi")
                            showLangMenu = false
                        },
                        leadingIcon = {
                            if (currentLanguage == "hi") {
                                Icon(Icons.Default.Check, contentDescription = null, tint = LavenderPrimary)
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("ಕನ್ನಡ (Kannada)") },
                        onClick = {
                            onLanguageChange("kn")
                            showLangMenu = false
                        },
                        leadingIcon = {
                            if (currentLanguage == "kn") {
                                Icon(Icons.Default.Check, contentDescription = null, tint = LavenderPrimary)
                            }
                        }
                    )
                }
            }

            // Quick Simulate Lead Test Button
            IconButton(
                onClick = onSimulateClick,
                modifier = Modifier.testTag("simulate_test_dm_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Simulate Customer DM",
                    tint = LavenderPrimary
                )
            }

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.testTag("settings_top_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }

            IconButton(
                onClick = onSignOutClick,
                modifier = Modifier.testTag("top_bar_sign_out_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Sign Out to Welcome Screen",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun SalesPilotBottomBar(
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    pendingFollowUpsCount: Int,
    newEnquiriesCount: Int
) {
    val strings = com.example.ui.i18n.LocalAppStrings.current
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        val tabs = listOf(
            Triple(NavigationTab.DASHBOARD, Icons.Default.Dashboard, strings.navDashboard),
            Triple(NavigationTab.PIPELINE, Icons.Default.ViewWeek, strings.navPipeline),
            Triple(NavigationTab.CATALOG, Icons.Default.Inventory2, strings.navCatalog),
            Triple(NavigationTab.COPILOT, Icons.Default.AutoAwesome, strings.navCopilot),
            Triple(NavigationTab.ORDERS, Icons.Default.ShoppingBag, strings.navOrders),
            Triple(NavigationTab.FOLLOW_UPS, Icons.Default.NotificationsActive, strings.navFollowUps)
        )

        tabs.forEach { (tab, icon, title) ->
            val isSelected = currentTab == tab
            val badgeCount = when (tab) {
                NavigationTab.FOLLOW_UPS -> pendingFollowUpsCount
                NavigationTab.PIPELINE -> newEnquiriesCount
                else -> 0
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}"),
                icon = {
                    BadgedBox(
                        badge = {
                            if (badgeCount > 0) {
                                Badge(
                                    containerColor = if (tab == NavigationTab.FOLLOW_UPS) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                ) {
                                    Text(text = badgeCount.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title
                        )
                    }
                },
                label = {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
