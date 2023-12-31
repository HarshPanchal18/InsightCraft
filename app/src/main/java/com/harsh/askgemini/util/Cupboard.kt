package com.harsh.askgemini.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

object Cupboard {
    const val apiKey = "YOUR_API_KEY" // https://makersuite.google.com/app/apikey

    private val recommendList = listOf(
        "Can aging be reversed?",
        "How do earthquake occurs?",
        "Best spring destinations",
        "Meditation techniques",
        "Can we hear colors?",
        "Plan a 7-day trip to Australia",
        "Compare the Google Pixel 7 and Pixel 8",
        "How much does a cloud weigh?",
        "Can hugs relieve pain?",
        "Compare monsoon in India and Mexico",
        "Finland's population",
        "What does space smell like?",
        "Most dangerous insects in the world",
        "Write a Python code to reverse a number",
        "Write a short story about a detective solving a mystery",
        "Find a recipe for vegan lasagna",
        "Translate this sentence into French",
        "Summarize this article for me",
        "Generate a catchy headline for my blog post",
        "Tell me a joke",
        "Recommend a book to read",
        "Calculate the square root of 144",
        "Suggest a workout routine for beginners",
    )

    fun randomSuggestion(): String {
        return recommendList.random()
    }

    fun String.cleanedString() = this.replace(Regex("```[\\w+#]*\n"), "")
        .removeSuffix("\n```")

    fun startSpeechToText(context: Context, onResult: (String) -> Unit) {

        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {

            override fun onResults(results: Bundle?) {
                results?.let {
                    val matches = it.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty())
                        onResult(matches.first())
                }
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer.startListening(intent)
    }

}
