package com.harsh.askgemini.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.harsh.askgemini.R
import com.harsh.askgemini.data.Screen

@Composable
fun MenuScreen(onItemClicked: (String) -> Unit = {}) {
    val screens = listOf(
        Screen("summarize", R.string.menu_summarize_title, R.string.menu_summarize_description),
        Screen("chat", R.string.menu_chat_title, R.string.menu_chat_description),
        Screen("reasoning", R.string.menu_reason_title, R.string.menu_reason_description)
    )

    Column(modifier = Modifier.fillMaxHeight()) {
        screens.forEach { screen ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                ScreenCard(screen = screen, onItemClick = onItemClicked)
            }
        }
    }
}
