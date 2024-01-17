package com.harsh.askgemini.feature.chat

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.BubbleChart
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedAssistChip
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.harsh.askgemini.R
import com.harsh.askgemini.navigation.WindowNavigationItem
import com.harsh.askgemini.ui.DotLoadingAnimation
import com.harsh.askgemini.util.Cupboard.cleanedString
import com.harsh.askgemini.util.Cupboard.startSpeechToText
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
        containerColor = MaterialTheme.colorScheme.primary.copy(0.40F),
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
        reverseLayout = true // reverse the direction of scrolling and layout.
    ) {
        items(chatMessage.reversed()) { message ->
            ChatBubbleItem(message) // last message will be the current index
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

    val modelMessageShape = RoundedCornerShape(
        topStart = 4.dp, topEnd = 20.dp,
        bottomEnd = 20.dp, bottomStart = 20.dp
    )

    val userMessageShape = RoundedCornerShape(
        topStart = 20.dp, topEnd = 4.dp,
        bottomEnd = 20.dp, bottomStart = 20.dp
    )

    val bubbleShape =
        if (isModelMessage)
            modelMessageShape
        else
            userMessageShape

    val horizontalAlignment = if (isModelMessage) Alignment.Start else Alignment.End

    Column(
        horizontalAlignment = horizontalAlignment,
        modifier = Modifier
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .fillMaxWidth()
    ) {
        Row {
            if (message.isPending)
                DotLoadingAnimation(
                    modifier = Modifier.align(Alignment.CenterVertically).padding(end = 8.dp),
                    circleSize = 24.dp
                )

            Bubble(
                message = message.text,
                bubbleShape = bubbleShape,
                backgroundColor = backgroundColor,
                isModelMessage = isModelMessage
            )
        }
    }
}

@Composable
fun Bubble(
    message: String, bubbleShape: Shape,
    backgroundColor: Color, isModelMessage: Boolean,
) {

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

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

            if (isModelMessage)
                ElevatedAssistChip(
                    modifier = Modifier.padding(start = 10.dp),
                    onClick = {
                        clipboardManager.setText(AnnotatedString(message.cleanedString()))
                        Toast
                            .makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT)
                            .show()
                    },
                    label = {
                        Text(
                            text = " Copy",
                            fontFamily = FontFamily.Serif
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Copy answer",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.requiredSize(20.dp)
                        )
                    }
                )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalPermissionsApi::class)
@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    resetScroll: () -> Unit = {},
) {
    var userMessage by rememberSaveable { mutableStateOf("") }
    val localKeyboardManager = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val audioPermission =
        rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp),
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
                    fontFamily = FontFamily.Serif,//FontFamily(Font(R.font.lemony, FontWeight.Bold)),
                    fontWeight = FontWeight.SemiBold
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(0.8F),
                unfocusedContainerColor = Color.White.copy(0.8F),

                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,

                focusedTextColor = Color.DarkGray,
                unfocusedTextColor = Color.DarkGray,
            ),
        )
        FloatingActionButton(
            containerColor = Color.DarkGray,
            onClick = {
                when {
                    userMessage.isNotBlank() -> {
                        onSendMessage(userMessage); userMessage = ""
                        resetScroll()
                        localKeyboardManager?.hide()
                    }

                    else -> {
                        if (audioPermission.status.isGranted) {
                            startSpeechToText(context) { result ->
                                userMessage = result
                            }
                        } else {
                            audioPermission.launchPermissionRequest()
                            if (audioPermission.status.isGranted)
                                startSpeechToText(context) { result ->
                                    userMessage = result
                                }
                            else
                                Toast.makeText(
                                    context,
                                    "Allow permission to record speech",
                                    Toast.LENGTH_SHORT
                                ).show()
                        }
                    }
                }
            }
        ) {
            if (userMessage.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = stringResource(id = R.string.action_send),
                    tint = Color.White.copy(0.9F)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = stringResource(id = R.string.action_send),
                    tint = Color.White.copy(0.9F)
                )
            }
        }
    }
}

@Composable
fun TopAppBarOfChat(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White.copy(0.8F))
            .padding(all = 4.dp) // Adjust padding as needed
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = {
            navController.navigate(WindowNavigationItem.Menu.route) {
                popUpTo(WindowNavigationItem.Menu.route) { inclusive = true }
            }
        }) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = null,
                tint = Color.DarkGray
            )
        }

        Spacer(modifier = Modifier.weight(1F))

        Text(
            text = "Chat with AI",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )

        Icon(
            imageVector = Icons.Outlined.BubbleChart,
            contentDescription = "Prompt icon",
            tint = Color.DarkGray,
            modifier = Modifier.requiredSize(24.dp)
        )

        Spacer(modifier = Modifier.weight(1F))

        IconButton(onClick = {
            navController.navigate(WindowNavigationItem.Menu.route) {
                popUpTo(WindowNavigationItem.Menu.route) { inclusive = true }
            }
        }, enabled = false) {}
    }
}
