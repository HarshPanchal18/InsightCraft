package com.harsh.askgemini.feature.multimodal

sealed interface PhotoReasoningUiState {

    data object Initial : PhotoReasoningUiState // When the screen is first shown

    data object Loading : PhotoReasoningUiState // Loading state

    data class Success(val output: String) : PhotoReasoningUiState // Text generated

    data class Error(val errorMessage: String) :
        PhotoReasoningUiState // Error getting text generated
}
