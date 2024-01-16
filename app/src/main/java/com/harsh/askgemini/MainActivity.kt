package com.harsh.askgemini

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.harsh.askgemini.navigation.WindowNavigation
import com.harsh.askgemini.ui.splash.SplashViewModel
import com.harsh.askgemini.ui.theme.AskGeminiTheme

class MainActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
            val splashScreen = installSplashScreen()
            splashScreen.setKeepOnScreenCondition { viewModel.isLoading.value }
        }

        setContent {
            AskGeminiTheme {
                val uiColor = MaterialTheme.colorScheme.primary.copy(0.6F)
                val systemUiController = rememberSystemUiController()

                systemUiController.setSystemBarsColor(color = uiColor)

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = uiColor
                ) {
                    WindowNavigation()

                    var pressedTime: Long = 0
                    BackHandler(enabled = true) {
                        if (pressedTime + 2000 > System.currentTimeMillis())
                            finish()
                        else
                            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()
                        pressedTime = System.currentTimeMillis()
                    }
                }
            }
        }
    }
}
