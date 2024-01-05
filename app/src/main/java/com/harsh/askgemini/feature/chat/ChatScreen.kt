package com.harsh.askgemini.feature.chat

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harsh.askgemini.R
import com.harsh.askgemini.util.GenerativeViewModelFactory
import kotlinx.coroutines.launch

@Composable
internal fun ChatRoute(
    chatViewModel: ChatViewModel = viewModel(factory = GenerativeViewModelFactory),
) {

    val chatUiState by chatViewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary.copy(0.45F),
        bottomBar = {
            MessageInput(
                onSendMessage = { inputText ->
                    chatViewModel.sendMessage(inputText)
                },
                resetScroll = {
                    coroutineScope.launch {
                        listState.scrollToItem(0)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            ChatList(chatMessage = chatUiState.messages, listState = listState)
        }
    }
}

@Composable
fun ChatList(
    chatMessage: List<ChatMessage>,
    listState: LazyListState,
) {
    LazyColumn(
        state = listState,
        reverseLayout = true // last element(message) will be the current index
    ) {
        items(chatMessage.reversed()) { message ->
            ChatBubbleItem(message)
        }
    }
}

@Composable
fun ChatBubbleItem(message: ChatMessage) {
    val isModelMessage =
        message.participant == Participant.MODEL || message.participant == Participant.ERROR

    val backgroundColor = when (message.participant) {
        Participant.USER -> MaterialTheme.colorScheme.tertiaryContainer
        Participant.MODEL -> MaterialTheme.colorScheme.primaryContainer
        Participant.ERROR -> MaterialTheme.colorScheme.errorContainer
    }

    val bubbleShape =
        if (isModelMessage)
            RoundedCornerShape(
                topStart = 4.dp, topEnd = 20.dp,
                bottomEnd = 20.dp, bottomStart = 20.dp
            )
        else
            RoundedCornerShape(
                topStart = 20.dp, topEnd = 4.dp,
                bottomEnd = 20.dp, bottomStart = 20.dp
            )

    val horizontalAlignment = if (isModelMessage) Alignment.Start else Alignment.End

    Column(
        horizontalAlignment = horizontalAlignment,
        modifier = Modifier
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .fillMaxWidth()
    ) {
        Row {
            if (message.isPending)
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(all = 8.dp)
                )

            BoxWithConstraints {
                Card(
                    shape = bubbleShape,
                    modifier = Modifier.widthIn(min = 0.dp, max = maxWidth * 0.9F),
                    colors = CardDefaults.cardColors(containerColor = backgroundColor)
                ) {
                    Text(
                        text = message.text,
                        modifier = Modifier.padding(all = 14.dp),
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    resetScroll: () -> Unit = {},
) {
    var userMessage by rememberSaveable { mutableStateOf("") }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(all = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = userMessage,
                onValueChange = { userMessage = it },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.85F)
                    .padding(end = 10.dp),
                placeholder = {
                    Text(
                        stringResource(R.string.chat_label),
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.LightGray,
                    unfocusedContainerColor = Color.LightGray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
            )
            FloatingActionButton(
                onClick = {
                    if (userMessage.isNotBlank()) {
                        onSendMessage(userMessage)
                        userMessage = ""
                        resetScroll()
                    }
                },
                containerColor = Color.DarkGray
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = stringResource(id = R.string.action_send),
                    tint = Color.White.copy(0.9F)
                )
            }
        }
    }
}
