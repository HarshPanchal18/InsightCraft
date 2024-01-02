package com.harsh.askgemini.feature.chat

import androidx.compose.runtime.toMutableStateList

class ChatUiState(messages: List<ChatMessage> = emptyList()) {

    private val _message: MutableList<ChatMessage> = messages.toMutableStateList()
    val messages: List<ChatMessage> = _message

    fun addMessage(newMessage: ChatMessage) {
        _message.add(newMessage)
    }

    fun replaceLastPendingMessage() {
        val lastMessage = _message.lastOrNull()
        lastMessage?.let {
            val newMessage = lastMessage.apply { isPending = false }
            _message.removeLast()
            _message.add(newMessage)
        }
    }
}
