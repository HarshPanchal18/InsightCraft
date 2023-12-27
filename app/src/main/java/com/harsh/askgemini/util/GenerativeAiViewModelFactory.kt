package com.harsh.askgemini.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.harsh.askgemini.feature.text.SummarizeViewModel

@Suppress("UNCHECKED_CAST")
val GenerativeViewModelFactory = object: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {

        val config = generationConfig { temperature = 0.7F }

        return with(modelClass) {
            when {
                isAssignableFrom(SummarizeViewModel::class.java) -> {
                    // Initialize a GenerativeModel with the `gemini-pro` AI model for text generation
                    val generativeModel = GenerativeModel(
                        modelName = "gemini-pro",
                        apiKey = ApiObject.apiKey,
                        generationConfig = config
                    )
                    SummarizeViewModel(generativeModel)
                }
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
    }
}
