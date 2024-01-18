package com.harsh.askgemini.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.harsh.askgemini.feature.chat.ChatRoute
import com.harsh.askgemini.feature.multimodal.PhotoReasoningRoute
import com.harsh.askgemini.feature.text.SummarizeRoute
import com.harsh.askgemini.ui.MenuScreen

@Composable
fun WindowNavigation() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = WindowNavigationItem.Menu.route) {

        composable(WindowNavigationItem.Menu.route) {
            MenuScreen(onItemClicked = { route ->
                navController.navigate(route = route)
            })
        }

        composable(WindowNavigationItem.Summarize.route) {
            SummarizeRoute(navController = navController)
        }

        composable(WindowNavigationItem.Chat.route) {
            ChatRoute(navController = navController)
        }

        composable(WindowNavigationItem.PhotoReasoning.route) {
            PhotoReasoningRoute(navController = navController)
        }
    }
}

sealed class WindowNavigationItem(val route: String) {
    data object Menu : WindowNavigationItem("menu")
    data object Summarize : WindowNavigationItem("summarize")
    data object Chat : WindowNavigationItem("chat")
    data object PhotoReasoning : WindowNavigationItem("reasoning")
}
