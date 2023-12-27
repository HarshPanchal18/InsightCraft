package com.harsh.askgemini.feature.text

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harsh.askgemini.GenerativeViewModelFactory
import com.harsh.askgemini.R

@Composable
internal fun SummarizeRoute(
    summarizeViewModel: SummarizeViewModel = viewModel(factory = GenerativeViewModelFactory),
) {
    val summarizeUiState by summarizeViewModel.uiState.collectAsState()

    SummarizedScreen(summarizeUiState) { inputText ->
        summarizeViewModel.summarizeStreaming(inputText = inputText)
    }
}

@Composable
fun SummarizedScreen(
    uiState: SummarizeUiState = SummarizeUiState.Loading,
    onSummarizeClicked: (String) -> Unit = {},
) {
    var textToSummarize by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        ElevatedCard(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            OutlinedTextField(
                value = textToSummarize,
                onValueChange = { textToSummarize = it },
                label = { Text(text = stringResource(id = R.string.summarize_label)) },
                placeholder = { Text(text = stringResource(id = R.string.summarize_hint)) },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )

            TextButton(
                onClick = {
                    if (textToSummarize.isNotBlank())
                        onSummarizeClicked(textToSummarize)
                }
            ) {
                Text(text = stringResource(id = R.string.action_go))
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

            is SummarizeUiState.Success -> SuccessLayout(outputText = uiState.outputText)

            is SummarizeUiState.Error -> ErrorLayout(errorMessage = uiState.errorMessage)
        }
    }
}

@Composable
fun SuccessLayout(outputText: String) {
    Card(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
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
                text = outputText,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth()
            )
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
