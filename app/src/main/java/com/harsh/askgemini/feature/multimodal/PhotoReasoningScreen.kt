package com.harsh.askgemini.feature.multimodal

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Precision
import com.harsh.askgemini.R
import com.harsh.askgemini.feature.text.ErrorLayout
import com.harsh.askgemini.feature.text.SuccessLayout
import com.harsh.askgemini.util.GenerativeViewModelFactory
import com.harsh.askgemini.util.UriSaver
import kotlinx.coroutines.launch

@Composable
internal fun PhotoReasoningRoute(
    photoReasoningViewModel: PhotoReasoningViewModel = viewModel(factory = GenerativeViewModelFactory),
    navController: NavHostController,
) {
    val photoReasoningUiState by photoReasoningViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val imageRequestBuilder = ImageRequest.Builder(context)
    val imageLoader = ImageLoader.Builder(context).build()

    PhotoReasoningScreen(uiState = photoReasoningUiState) { inputText, selectedItems ->
        coroutineScope.launch {
            val bitmaps = selectedItems.mapNotNull { uri ->
                val imageRequest = imageRequestBuilder
                    .data(uri)
                    .size(size = 768) // Scale the image down to 768px for faster uploads
                    .precision(Precision.EXACT)
                    .build()

                try {
                    val result = imageLoader.execute(imageRequest)
                    if (result is SuccessResult)
                        return@mapNotNull (result.drawable as BitmapDrawable).bitmap
                    else
                        return@mapNotNull null
                } catch (e: Exception) {
                    return@mapNotNull null
                }
            }
            photoReasoningViewModel.reason(inputText, bitmaps)
        }
    }
}

@Composable
fun PhotoReasoningScreen(
    uiState: PhotoReasoningUiState = PhotoReasoningUiState.Loading,
    onReasonClicked: (String, List<Uri>) -> Unit = { question, uris -> },
) {
    var userQuestion by rememberSaveable { mutableStateOf("") }
    val imageUris = rememberSaveable(saver = UriSaver()) { mutableStateListOf() }
    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { imageUri ->
        imageUri?.let { imageUris.add(it) }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(top = 16.dp)) {

                IconButton(
                    onClick = {
                        pickMedia.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .padding(all = 4.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.add_image),
                    )
                }

                OutlinedTextField(
                    value = userQuestion,
                    onValueChange = { userQuestion = it },
                    label = { Text(stringResource(R.string.reason_label)) },
                    placeholder = { Text(stringResource(R.string.reason_hint)) },
                    modifier = Modifier.fillMaxWidth(fraction = 0.8F)
                )

                TextButton(
                    onClick = {
                        if (userQuestion.isNotBlank())
                            onReasonClicked(userQuestion, imageUris.toList())
                    },
                    modifier = Modifier
                        .padding(all = 4.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(text = stringResource(id = R.string.action_go))
                }

                LazyRow(modifier = Modifier.padding(8.dp)) {
                    items(imageUris) { imageUri ->
                        AsyncImage(
                            model = imageUri,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(4.dp)
                                .requiredSize(72.dp)
                        )
                    }
                }
            }

            when (uiState) {
                PhotoReasoningUiState.Initial -> {}

                PhotoReasoningUiState.Loading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is PhotoReasoningUiState.Success -> {
                    SuccessLayout(uiState.output)
                }

                is PhotoReasoningUiState.Error -> {
                    ErrorLayout(uiState.errorMessage)
                }
            }
        }
    }
}
