package com.harsh.askgemini.feature.multimodal

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PhotoReasoningModel(private val generativeModel: GenerativeModel) : ViewModel() {

    private val _uiState: MutableStateFlow<PhotoReasoningUiState> =
        MutableStateFlow(PhotoReasoningUiState.Initial)

    val uiState: StateFlow<PhotoReasoningUiState> = _uiState.asStateFlow()

    fun reason(
        userInput: String,
        selectedImages: List<Bitmap>
    ) {
        _uiState.value = PhotoReasoningUiState.Loading

        val prompt = "Look at the image(s), and then answer the following question: $userInput"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputContent = content {
                    for (bitmap in selectedImages) {
                        image(bitmap)
                    }
                    text(prompt)
                }

                var outputContent = ""

                generativeModel.generateContentStream(inputContent)
                    .collect { response ->
                        outputContent += response.text
                        _uiState.value = PhotoReasoningUiState.Success(outputContent)
                    }

            } catch (e: Exception) {
                _uiState.value = PhotoReasoningUiState.Error(e.localizedMessage ?: "")
            }
        }
    }
}
