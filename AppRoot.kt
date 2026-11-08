package com.strengthtracker.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findStartDestination
import androidx.navigation.navArgument
import com.strengthtracker.ui.session.SessionScreen
import com.strengthtracker.ui.today.TodayScreen

sealed class Destination(val route: String, val label: String, val icon: ImageVector) {
    data object Today : Destination("today", "Today", Icons.Filled.Home)
    data object Schedule : Destination("schedule", "Schedule", Icons.Filled.DateRange)
    data object History : Destination("history", "History", Icons.Filled.List)
    data object Progress : Destination("progress", "Progress", Icons.Filled.Star)
}

private val tabs = listOf(
    Destination.Today,
    Destination.Schedule,
    Destination.History,
    Destination.Progress
)

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // The bottom bar is hidden while a workout is open, so logging has the full screen.
            if (tabs.any { it.route == currentRoute }) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Today.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destination.Today.route) {
                TodayScreen(onOpenSession = { id -> navController.navigate("session/$id") })
            }
            composable(Destination.Schedule.route) { ComingNext("Schedule editing") }
            composable(Destination.History.route) { ComingNext("History") }
            composable(Destination.Progress.route) { ComingNext("Progress charts") }
            composable(
                route = "session/{sessionId}",
                arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
            ) { backStack ->
                SessionScreen(
                    sessionId = backStack.arguments?.getLong("sessionId") ?: 0L,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun ComingNext(name: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "$name arrives in the next build step",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
