package com.harsh.askgemini.feature.text

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SummarizeViewModel(private val generativeModel: GenerativeModel) : ViewModel() {

    private val _uiState: MutableStateFlow<SummarizeUiState> =
        MutableStateFlow(SummarizeUiState.Initial)
    val uiState: StateFlow<SummarizeUiState> = _uiState.asStateFlow()

    /*fun summarize(inputText: String) {
        _uiState.value = SummarizeUiState.Loading

        viewModelScope.launch {
            try {
                val response = generativeModel.generateContent(inputText)
                response.text?.let { outputText ->
                    _uiState.value = SummarizeUiState.Success(outputText = outputText)
                }
            } catch (e: Exception) {
                _uiState.value = SummarizeUiState.Error(errorMessage = e.localizedMessage ?: "")
            }
        }
    }*/

    /*fun summarizeForNotification(inputText: String): String? {
        var facts: String? = null
        viewModelScope.launch {
            try {
                val response = generativeModel.generateContent(inputText)
                response.text?.let { outputText ->
                    //_uiState.value = SummarizeUiState.Success(outputText = outputText)
                    facts = outputText
                }
            } catch (e: Exception) {
                //_uiState.value = SummarizeUiState.Error(errorMessage = e.localizedMessage ?: "")
                facts = e.localizedMessage
            }
        }
        return facts
    }*/

    suspend fun summarizeStreaming(inputText: String) {
        _uiState.value = SummarizeUiState.Loading

        viewModelScope.launch {
            try {
                var outputText = ""
                generativeModel.generateContentStream(inputText)
                    .collect { response ->
                        outputText += response.text
                        _uiState.value = SummarizeUiState.Success(outputText = outputText)
                    }
            } catch (e: Exception) {
                _uiState.value = SummarizeUiState.Error(errorMessage = e.localizedMessage ?: "")
            }
        }
    }

}
