package com.kunpitech.shayariwala.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kunpitech.shayariwala.ui.detail.DetailScreen
import com.kunpitech.shayariwala.ui.explore.ExploreScreen
import com.kunpitech.shayariwala.ui.home.HomeScreen
import com.kunpitech.shayariwala.ui.moodfeed.MoodFeedScreen
import com.kunpitech.shayariwala.ui.profile.ProfileScreen
import com.kunpitech.shayariwala.ui.saved.SavedScreen
import com.kunpitech.shayariwala.ui.write.WriteShayariScreen
import com.kunpitech.shayariwala.ui.theme.Gold400
import com.kunpitech.shayariwala.ui.theme.TextDisabled

@Composable
fun ShayariNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry     by navController.currentBackStackEntryAsState()
    val currentDestination  = backStackEntry?.destination
    val currentRoute        = currentDestination?.route

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                BottomNavBar(
                    currentDestination = currentDestination,
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    },
                    onComposeClick = {
                        navController.navigate(Screen.Write.route)
                    },
                )
            }
        },
    ) { innerPadding ->

        NavHost(
            navController    = navController,
            startDestination = Screen.Home.route,
            modifier         = Modifier.padding(innerPadding),
        ) {

            // ── Home ──────────────────────────────────────────────
            composable(
                route              = Screen.Home.route,
                enterTransition    = { fadeIn(tween(300)) },
                exitTransition     = { fadeOut(tween(200)) },
                popEnterTransition = { fadeIn(tween(300)) },
                popExitTransition  = { fadeOut(tween(200)) },
            ) {
                HomeScreen(
                    onShayariClick = { shayariId ->
                        navController.navigate(Screen.Detail.createRoute(shayariId))
                    },
                    onSearchClick = {
                        navController.navigate(Screen.Explore.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    },
                )
            }

            // ── Explore ───────────────────────────────────────────
            composable(
                route              = Screen.Explore.route,
                enterTransition    = { fadeIn(tween(300)) },
                exitTransition     = { fadeOut(tween(200)) },
                popEnterTransition = { fadeIn(tween(300)) },
                popExitTransition  = { fadeOut(tween(200)) },
            ) {
                ExploreScreen(
                    onShayariClick = { shayariId ->
                        navController.navigate(Screen.Detail.createRoute(shayariId))
                    },
                    onMoodClick = { category ->
                        navController.navigate(Screen.MoodFeed.createRoute(category))
                    },
                )
            }
// saved
            composable(
                route              = Screen.Saved.route,
                enterTransition    = { fadeIn(tween(300)) },
                exitTransition     = { fadeOut(tween(200)) },
                popEnterTransition = { fadeIn(tween(300)) },
                popExitTransition  = { fadeOut(tween(200)) },
            ) {
                SavedScreen(
                    onShayariClick = { shayariId ->
                        navController.navigate(Screen.Detail.createRoute(shayariId))
                    },
                )
            }

            // ── Profile ───────────────────────────────────────────
            composable(
                route              = Screen.Profile.route,
                enterTransition    = { fadeIn(tween(300)) },
                exitTransition     = { fadeOut(tween(200)) },
                popEnterTransition = { fadeIn(tween(300)) },
                popExitTransition  = { fadeOut(tween(200)) },
            ) {
                ProfileScreen(
                    onShayariClick = { shayariId ->
                        navController.navigate(Screen.Detail.createRoute(shayariId))
                    },
                    onWriteClick = {
                        navController.navigate(Screen.Write.route)
                    },
                )
            }

            // ── Detail ────────────────────────────────────────────
            composable(
                route              = Screen.Detail.route,
                arguments          = listOf(
                    navArgument("shayariId") { type = NavType.StringType }
                ),
                enterTransition    = {
                    slideInVertically(tween(400)) { it / 8 } + fadeIn(tween(350))
                },
                exitTransition     = {
                    slideOutVertically(tween(300)) { it / 8 } + fadeOut(tween(250))
                },
                popEnterTransition = {
                    slideInVertically(tween(350)) { -it / 8 } + fadeIn(tween(300))
                },
                popExitTransition  = {
                    slideOutVertically(tween(300)) { it / 8 } + fadeOut(tween(250))
                },
            ) { backStackEntry ->
                val shayariId = backStackEntry.arguments
                    ?.getString("shayariId")
                    ?: return@composable

                DetailScreen(
                    shayariId      = shayariId,
                    onBack         = { navController.popBackStack() },
                    onRelatedClick = { relatedId ->
                        navController.navigate(Screen.Detail.createRoute(relatedId))
                    },
                )
            }

            // ── Mood Feed ─────────────────────────────────────────
            composable(
                route              = Screen.MoodFeed.route,
                arguments          = listOf(
                    navArgument("category") { type = NavType.StringType }
                ),
                enterTransition    = {
                    slideInHorizontally(tween(350)) { it } + fadeIn(tween(300))
                },
                exitTransition     = {
                    slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(250))
                },
                popEnterTransition = {
                    slideInHorizontally(tween(350)) { -it } + fadeIn(tween(300))
                },
                popExitTransition  = {
                    slideOutHorizontally(tween(300)) { it } + fadeOut(tween(250))
                },
            ) { backStackEntry ->
                val category = backStackEntry.arguments
                    ?.getString("category")
                    ?: return@composable

                MoodFeedScreen(
                    category       = category,
                    onBack         = { navController.popBackStack() },
                    onShayariClick = { shayariId ->
                        navController.navigate(Screen.Detail.createRoute(shayariId))
                    },
                )
            }

            // ── Write Shayari ─────────────────────────────────────
            composable(
                route              = Screen.Write.route,
                enterTransition    = {
                    slideInVertically(tween(400)) { it } + fadeIn(tween(350))
                },
                exitTransition     = {
                    slideOutVertically(tween(300)) { it } + fadeOut(tween(250))
                },
                popEnterTransition = {
                    slideInVertically(tween(350)) { it } + fadeIn(tween(300))
                },
                popExitTransition  = {
                    slideOutVertically(tween(300)) { it } + fadeOut(tween(250))
                },
            ) {
                WriteShayariScreen(
                    onBack      = { navController.popBackStack() },
                    onSubmitted = { navController.popBackStack() },
                )
            }
        }
    }
}