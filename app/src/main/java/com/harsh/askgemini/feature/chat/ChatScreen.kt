package com.harsh.askgemini.feature.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.harsh.askgemini.R
import com.harsh.askgemini.navigation.WindowNavigationItem
import com.harsh.askgemini.util.GenerativeViewModelFactory
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch

@Composable
internal fun ChatRoute(
    // Making function internal to restrict the accessibility within the same module
    chatViewModel: ChatViewModel = viewModel(factory = GenerativeViewModelFactory),
    navController: NavHostController,
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
            TopAppBarOfChat(navController = navController)
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

            Bubble(
                message = message.text,
                bubbleShape = bubbleShape,
                backgroundColor = backgroundColor
            )
        }
    }
}

@Composable
fun Bubble(
    message: String, bubbleShape: Shape,
    backgroundColor: Color,
) {

    BoxWithConstraints {
        Card(
            shape = bubbleShape,
            modifier = Modifier.widthIn(min = 0.dp, max = maxWidth * 0.9F),
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            MarkdownText(
                markdown = message,
                modifier = Modifier
                    .padding(all = 14.dp)
                    .clip(RoundedCornerShape(6.dp)),
                style = TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold
                ),
                lineHeight = 10.sp
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    resetScroll: () -> Unit = {},
) {
    var userMessage by rememberSaveable { mutableStateOf("") }
    val localKeyboardManager = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = userMessage,
            onValueChange = { userMessage = it },
            shape = RoundedCornerShape(16.dp),
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
                focusedContainerColor = MaterialTheme.colorScheme.inversePrimary,
                unfocusedContainerColor = MaterialTheme.colorScheme.inversePrimary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
        )
        FloatingActionButton(
            onClick = {
                if (userMessage.isNotBlank()) {
                    onSendMessage(userMessage); userMessage = ""
                    resetScroll()
                    localKeyboardManager?.hide()
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

@Composable
fun TopAppBarOfChat(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.inversePrimary)
            .padding(all = 4.dp) // Adjust padding as needed
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            navController.navigate(WindowNavigationItem.Menu.route) {
                popUpTo(WindowNavigationItem.Menu.route) { inclusive = true }
            }
        }) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Chat with AI",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold
        )
    }

}
