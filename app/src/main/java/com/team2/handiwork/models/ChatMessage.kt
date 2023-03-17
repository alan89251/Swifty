package com.team2.handiwork.models

class ChatMessage(val text: String = "", var isAgent: Boolean = false) {

    val createdAt: Long = System.currentTimeMillis()
}