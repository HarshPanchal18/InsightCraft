package com.harsh.askgemini.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.harsh.askgemini.R
import com.harsh.askgemini.data.Screen
import com.harsh.askgemini.util.Cupboard

@Composable
fun MenuScreen(onItemClicked: (String) -> Unit = {}) {

    val context = LocalContext.current
    Cupboard.initPreference(context)

    val screens = listOf(
        Screen(
            routeId = "summarize",
            titleResId = R.string.menu_summarize_title,
            descriptionResId = R.string.menu_summarize_description,
            backgroundColor = Color.Red
        ),
        Screen(
            routeId = "chat",
            titleResId = R.string.menu_chat_title,
            descriptionResId = R.string.menu_chat_description,
            backgroundColor = Color.Yellow
        ),
        Screen(
            routeId = "reasoning",
            titleResId = R.string.menu_reason_title,
            descriptionResId = R.string.menu_reason_description,
            backgroundColor = Color(0xFF68F51D)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(color = Color.White)
    ) {
        screens.forEach { screen ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                ScreenEntryCard(
                    screen = screen,
                    background = screen.backgroundColor,
                    onItemClick = onItemClicked
                )
            }
        }
    }
}
