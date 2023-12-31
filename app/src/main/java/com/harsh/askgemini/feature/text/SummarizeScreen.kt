package com.harsh.askgemini.feature.text

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harsh.askgemini.util.GenerativeViewModelFactory
import com.harsh.askgemini.R
import com.harsh.askgemini.util.Cupboard.randomSuggestion
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
internal fun SummarizeRoute(
    summarizeViewModel: SummarizeViewModel = viewModel(factory = GenerativeViewModelFactory),
) {
    val summarizeUiState by summarizeViewModel.uiState.collectAsState()

    SummarizedScreen(summarizeUiState) { inputText ->
        CoroutineScope(Dispatchers.IO).launch {
            summarizeViewModel.summarizeStreaming(inputText = inputText)
        }
    }
}

var textCopyHolder: String = "" // Holding generated content to make copy to clipboard

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SummarizedScreen(
    uiState: SummarizeUiState = SummarizeUiState.Loading,
    onSummarizeClicked: (String) -> Unit = {},
) {
    var textToSummarize by rememberSaveable { mutableStateOf("") }
    var suggestion = rememberSaveable { randomSuggestion() }
    val localKeyboardManager = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        ElevatedCard(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF7DF098)
            ),
            shape = MaterialTheme.shapes.large,
        ) {
            OutlinedTextField(
                value = textToSummarize,
                onValueChange = { textToSummarize = it },
                placeholder = {
                    Text(
                        text = "What's cooking in your head?",
                        fontFamily = FontFamily.SansSerif
                    )
                },
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
                    .border(BorderStroke(1.5.dp, Color.Black), RoundedCornerShape(5.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                textStyle = TextStyle(fontFamily = FontFamily.SansSerif),
                trailingIcon = {
                    if (textToSummarize.isNotEmpty()) {
                        IconButton(onClick = {
                            textToSummarize = ""
                            localKeyboardManager?.show()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close, contentDescription = null,
                                tint = Color.Black
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text,
                    autoCorrect = true
                ),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1F)) {
                    ElevatedSuggestionChip(
                        onClick = {
                            textToSummarize = suggestion
                            suggestion = randomSuggestion()
                        },
                        label = {
                            Text(
                                text = suggestion,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    )
                }

                TextButton(
                    onClick = {
                        if (textToSummarize.isNotBlank())
                            onSummarizeClicked(textToSummarize)

                        suggestion = randomSuggestion()
                        localKeyboardManager?.hide()
                    },
                    modifier = Modifier.padding(horizontal = 5.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.action_go),
                        color = Color.Blue,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send Icon",
                        tint = Color.Blue
                    )
                }
            }
        }

        when (uiState) {
            SummarizeUiState.Initial -> {
                /* Nothing is shown */
            }

            SummarizeUiState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    CircularProgressIndicator()
                }
            }

            is SummarizeUiState.Success -> {
                SuccessLayout(outputText = uiState.outputText)
                textCopyHolder = uiState.outputText
                //Log.d("output", "SuccessLayout: ${uiState.outputText}")
            }

            is SummarizeUiState.Error -> ErrorLayout(errorMessage = uiState.errorMessage)
        }
    }
}

@Composable
fun SuccessLayout(outputText: String) {
    val localClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp)),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Column {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Person icon",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .requiredSize(36.dp)
                        .drawBehind {
                            drawCircle(color = Color.White)
                        }
                )

                Text(
                    text = "Tap\nanswer\nto copy",
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Start,
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                MarkdownText(
                    markdown = outputText,
                    color = MaterialTheme.colorScheme.onSecondary,
                    style = TextStyle(fontFamily = FontFamily.SansSerif),
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .fillMaxWidth(),
                    isTextSelectable = true,
                    onClick = {
                        localClipboardManager.setText(AnnotatedString(textCopyHolder))
                        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun ErrorLayout(errorMessage: String) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Previews() {
    //SummarizedScreen(SummarizeUiState.Success("Output Text"))
    //SuccessLayout("User Output")
}
