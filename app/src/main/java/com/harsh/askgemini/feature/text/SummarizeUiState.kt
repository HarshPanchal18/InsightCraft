package com.harsh.askgemini.feature.text

sealed interface SummarizeUiState {

    // Empty state when the screen is first shown
    data object Initial: SummarizeUiState

    // Still loading
    data object Loading: SummarizeUiState

    // Text has been generated
    data class Success(val outputText:String): SummarizeUiState

    // Error while generating output
    data class Error(val errorMessage:String): SummarizeUiState

}
