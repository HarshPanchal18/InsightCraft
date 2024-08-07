package com.harsh.askgemini.feature.multimodal

import android.Manifest
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Precision
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.harsh.askgemini.R
import com.harsh.askgemini.ui.DotLoadingAnimation
import com.harsh.askgemini.ui.ErrorLayout
import com.harsh.askgemini.ui.SuccessLayout
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

    BackHandler { navController.navigateUp() }

    PhotoReasoningScreen(
        uiState = photoReasoningUiState,
        navController = navController
    ) { inputText, selectedItems ->
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PhotoReasoningScreen(
    uiState: PhotoReasoningUiState = PhotoReasoningUiState.Loading,
    navController: NavHostController,
    onReasonClicked: (String, List<Uri>) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    var userQuestion by rememberSaveable { mutableStateOf("") }
    val keyboardManager = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val imageUris = rememberSaveable(saver = UriSaver()) { mutableStateListOf() }
    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { imageUri ->
        imageUri?.let { imageUris.add(it) }
    }
    val storagePermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberPermissionState(permission = Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    val buttonContentColor = Color.Black.copy(0.8F)

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Card(
            modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(25.dp))
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .padding(top = 4.dp)
                ) {

                    TextField(
                        value = userQuestion,
                        onValueChange = { userQuestion = it },
                        placeholder = {
                            Text(
                                stringResource(R.string.reason_hint),
                                fontFamily = FontFamily(Font(R.font.mavitya)),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black
                            )
                        },
                        modifier = Modifier
                            .padding(4.dp)
                            .padding(horizontal = 4.dp)
                            .weight(1F)
                            .clip(RoundedCornerShape(20.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        maxLines = 3,
                        leadingIcon = {
                            IconButton(
                                onClick = {
                                    navController.navigateUp()
                                },
                                modifier = Modifier.padding(all = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ChevronLeft,
                                    contentDescription = "Menu Screen",
                                    tint = Color.Black
                                )
                            }
                        },
                        trailingIcon = {
                            if (userQuestion.isNotEmpty()) {
                                IconButton(onClick = {
                                    userQuestion = ""
                                    keyboardManager?.show()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
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
                } // TextField Row

                // Button Row
                Row(modifier = Modifier.padding(horizontal = 4.dp)) {
                    TextButton(
                        onClick = {
                            if (storagePermission.status.isGranted) {
                                pickMedia.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            } else {
                                Toast.makeText(context, "Allow permission", Toast.LENGTH_SHORT)
                                    .show()
                                storagePermission.launchPermissionRequest()
                            }
                        },
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .weight(2F)
                            .background(color = Color.White)
                    ) {
                        Text(
                            text = "Add Image",
                            fontFamily = FontFamily(Font(R.font.mavitya)),
                            style = MaterialTheme.typography.bodyLarge,
                            color = buttonContentColor
                        )
                    }

                    if (imageUris.isNotEmpty()) {
                        TextButton(
                            onClick = { imageUris.clear() },
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .weight(1F)
                                .background(color = Color.White)
                        ) {
                            Text(
                                text = "Clear",
                                fontFamily = FontFamily(Font(R.font.mavitya)),
                                style = MaterialTheme.typography.bodyLarge,
                                color = buttonContentColor
                            )
                        }
                    }

                    TextButton(
                        onClick = {
                            if (userQuestion.isNotBlank())
                                onReasonClicked(userQuestion, imageUris.toList())
                            keyboardManager?.hide()
                            focusManager.clearFocus(force = true)
                        },
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .weight(1F)
                            .background(color = Color.White)
                    ) {
                        Text(
                            text = stringResource(id = R.string.action_go),
                            fontFamily = FontFamily(Font(R.font.mavitya)),
                            style = MaterialTheme.typography.bodyLarge,
                            color = buttonContentColor
                        )
                    }
                }
            } // Input Box

            // Image Row
            LazyRow(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
            ) {
                items(imageUris) { imageUri ->
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .requiredSize(125.dp)
                            .wrapContentSize()
                            .padding(4.dp)
                            .clip(RoundedCornerShape(20.dp))
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
                        .fillMaxSize()
                        .padding(all = 8.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    DotLoadingAnimation()
                }
            }

            is PhotoReasoningUiState.Success -> {
                SuccessLayout(outputText = uiState.output, textToCopy = uiState.output)
            }

            is PhotoReasoningUiState.Error -> {
                ErrorLayout(uiState.errorMessage)
            }
        }
    } // Column
}
