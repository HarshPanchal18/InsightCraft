package com.harsh.askgemini.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.harsh.askgemini.R
import com.harsh.askgemini.data.Screen

@Composable
fun MenuScreen(onItemClicked: (String) -> Unit = {}) {
    val screens = listOf(
        Screen("summarize", R.string.menu_summarize_title, R.string.menu_summarize_description, Color.Red),
        Screen("chat", R.string.menu_chat_title, R.string.menu_chat_description, Color.Green),
        Screen("reasoning", R.string.menu_reason_title, R.string.menu_reason_description, Color.Blue)
    )

    Column(modifier = Modifier
        .fillMaxHeight()
        .background(color = Color.White)) {
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
