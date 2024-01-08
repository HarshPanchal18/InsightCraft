package com.harsh.askgemini.util

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
}
