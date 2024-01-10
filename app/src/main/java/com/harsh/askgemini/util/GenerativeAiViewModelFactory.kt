package com.harsh.askgemini.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.harsh.askgemini.feature.chat.ChatViewModel
import com.harsh.askgemini.feature.multimodal.PhotoReasoningViewModel
import com.harsh.askgemini.feature.text.SummarizeViewModel

@Suppress("UNCHECKED_CAST")
val GenerativeViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {

        val config = generationConfig { temperature = 0.7F }

        return with(modelClass) {
            when {
                isAssignableFrom(SummarizeViewModel::class.java) -> {
                    // Initialize a GenerativeModel with the `gemini-pro` AI model for text generation
                    val generativeModel = GenerativeModel(
                        modelName = "gemini-pro",
                        apiKey = Cupboard.apiKey,
                        generationConfig = config
                    )
                    SummarizeViewModel(generativeModel)
                }

                isAssignableFrom(ChatViewModel::class.java) -> {
                    // Initialize a GenerativeModel with the `gemini-pro` AI model for text generation
                    val generativeModel = GenerativeModel(
                        modelName = "gemini-pro",
                        apiKey = Cupboard.apiKey,
                        generationConfig = config
                    )
                    ChatViewModel(generativeModel)
                }

                isAssignableFrom(PhotoReasoningViewModel::class.java) -> {
                    val generativeModel = GenerativeModel(
                        modelName = "gemini-pro-vision",
                        apiKey = Cupboard.apiKey,
                        generationConfig = config
                    )
                    PhotoReasoningViewModel(generativeModel)
                }

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
    }
}
