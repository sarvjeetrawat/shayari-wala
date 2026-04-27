package com.kunpitech.shayariwala.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {

    // ── Bottom nav screens ────────────────────────────
    data object Home     : Screen("home")
    data object Explore  : Screen("explore")
    data object Saved : Screen("saved")
    data object Profile  : Screen("profile")

    // ── Detail ────────────────────────────────────────
    data object Detail   : Screen("detail/{shayariId}") {
        fun createRoute(shayariId: String) = "detail/$shayariId"
    }

    // ── Mood feed ─────────────────────────────────────
    data object MoodFeed : Screen("mood/{category}") {
        fun createRoute(category: String) = "mood/$category"
    }

    // ── Write shayari ─────────────────────────────────
    data object Write    : Screen("write")
}

data class BottomNavItem(
    val screen         : Screen,
    val label          : String,
    val selectedIcon   : ImageVector,
    val unselectedIcon : ImageVector,
)

val bottomNavItems = listOf(
    BottomNavItem(
        screen         = Screen.Home,
        label          = "Home",
        selectedIcon   = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
    ),
    BottomNavItem(
        screen         = Screen.Explore,
        label          = "Explore",
        selectedIcon   = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore,
    ),
    BottomNavItem(
        screen         = Screen.Saved,              // ← add this
        label          = "Saved",
        selectedIcon   = Icons.Filled.Bookmark,
        unselectedIcon = Icons.Outlined.BookmarkBorder,
    ),
    BottomNavItem(
        screen         = Screen.Profile,
        label          = "Profile",
        selectedIcon   = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.PersonOutline,
    ),
)

// Add to bottomNavRoutes
val bottomNavRoutes = setOf(
    Screen.Home.route,
    Screen.Explore.route,
    Screen.Saved.route,       // ← add this
    Screen.Profile.route,
)