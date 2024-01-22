package com.harsh.askgemini.feature.text

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.harsh.askgemini.R
import com.harsh.askgemini.navigation.WindowNavigationItem
import com.harsh.askgemini.ui.DotLoadingAnimation
import com.harsh.askgemini.ui.ErrorLayout
import com.harsh.askgemini.ui.SuccessLayout
import com.harsh.askgemini.util.Cupboard.randomSuggestion
import com.harsh.askgemini.util.GenerativeViewModelFactory
import kotlinx.coroutines.launch

@Composable
internal fun SummarizeRoute(
    summarizeViewModel: SummarizeViewModel = viewModel(factory = GenerativeViewModelFactory),
    navController: NavHostController,
) {
    val summarizeUiState by summarizeViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    BackHandler { navController.popBackStack() }
    SummarizedScreen(uiState = summarizeUiState, navController = navController) { inputText ->
        coroutineScope.launch {
            summarizeViewModel.summarizeStreaming(inputText = inputText)
        }
    }
}

var textCopyHolder: String = "" // Holding generated content to make copy to clipboard

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SummarizedScreen(
    uiState: SummarizeUiState = SummarizeUiState.Loading,
    navController: NavHostController,
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
                .fillMaxWidth()
                .clip(RoundedCornerShape(25.dp)),
            shape = MaterialTheme.shapes.large,
        ) {
            TextField(
                value = textToSummarize,
                onValueChange = { textToSummarize = it },
                placeholder = {
                    Text(
                        text = "What's cooking in your head?",
                        fontFamily = FontFamily(Font(R.font.mavitya, FontWeight.Bold)),
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(fontFamily = FontFamily.SansSerif),
                leadingIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate(WindowNavigationItem.Menu.route) {
                                popUpTo(WindowNavigationItem.Menu.route) {
                                    inclusive = true
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Go back"
                        )
                    }
                },
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
                                fontFamily = FontFamily(Font(R.font.mavitya)),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = Color.LightGray,
                            labelColor = Color.Black.copy(0.8F)
                        ),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Chip Icon"
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
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontFamily = FontFamily(Font(R.font.mavitya)),
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send Icon",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        when (uiState) {
            SummarizeUiState.Initial -> {}

            SummarizeUiState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    DotLoadingAnimation()
                }
            }

            is SummarizeUiState.Success -> {
                textCopyHolder = uiState.outputText
                SuccessLayout(outputText = uiState.outputText, textToCopy = textCopyHolder)
            }

            is SummarizeUiState.Error -> ErrorLayout(errorMessage = uiState.errorMessage)
        }
    }
}
