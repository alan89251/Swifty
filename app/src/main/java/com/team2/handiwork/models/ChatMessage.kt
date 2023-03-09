package com.team2.handiwork.models

class ChatMessage(val text: String = "", val isAgent: Boolean = true) {

    val createdAt: Long = System.currentTimeMillis()
}