package com.harsh.askgemini.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
            backgroundColor = Color(0xFFE03C39)
        ),
        Screen(
            routeId = "chat",
            titleResId = R.string.menu_chat_title,
            descriptionResId = R.string.menu_chat_description,
            backgroundColor = Color(0xFFFFC313)
        ),
        Screen(
            routeId = "reasoning",
            titleResId = R.string.menu_reason_title,
            descriptionResId = R.string.menu_reason_description,
            backgroundColor = Color(0xFF4DC2D4)
        )
    )

    val isApiValid = Cupboard.getApiKey().length == 39
    val openDialog = remember { mutableStateOf(!isApiValid) }

    if (openDialog.value)
        ApiInputDialog(closeDialog = {
            Cupboard.initPreference(context)
            val newApiKey = Cupboard.getApiKey()
            openDialog.value = newApiKey.length != 39
        })

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
