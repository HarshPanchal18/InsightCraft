package com.harsh.askgemini

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.harsh.askgemini.feature.text.SummarizeRoute
import com.harsh.askgemini.ui.MenuScreen
import com.harsh.askgemini.ui.theme.AskGeminiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AskGeminiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "menu") {

                        composable("menu") {
                            MenuScreen(onItemClicked = { route ->
                                navController.navigate(route = route)
                            })
                        }

                        composable("summarize") {
                            SummarizeRoute()
                        }
                    }
                }
            }
        }
    }
}
