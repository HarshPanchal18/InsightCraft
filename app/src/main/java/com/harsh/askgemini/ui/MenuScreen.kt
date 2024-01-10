package com.harsh.askgemini.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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

@Composable
fun ItemCard(item: MenuItem, onItemClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        Column(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(item.titleResId),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Text(
                text = stringResource(item.descriptionResId),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            TextButton(
                onClick = { onItemClick(item.routeId) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = stringResource(id = R.string.action_try))
            }
        }
    }
}
