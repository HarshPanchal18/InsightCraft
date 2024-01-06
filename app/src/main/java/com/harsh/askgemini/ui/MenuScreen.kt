package com.harsh.askgemini.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.harsh.askgemini.R
import com.harsh.askgemini.data.MenuItem

@Composable
fun MenuScreen(onItemClicked: (String) -> Unit = {}) {
    val menuItems = listOf(
        MenuItem("summarize", R.string.menu_summarize_title, R.string.menu_summarize_description),
        MenuItem("chat", R.string.menu_chat_title, R.string.menu_chat_description),
        MenuItem("reasoning", R.string.menu_reason_title, R.string.menu_reason_description)
    )

    LazyColumn(
        modifier = Modifier.padding(vertical = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        items(menuItems, key = { it.routeId }) { item ->
            ItemCard(item) { onItemClicked(item.routeId) }
        }
    }
}
