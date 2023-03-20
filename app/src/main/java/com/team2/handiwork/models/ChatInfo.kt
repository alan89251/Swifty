package com.team2.handiwork.models

class ChatInfo(
) {
    var missionId: String = ""
    var employer: String = ""
    var name: String = ""
    var imageURi: String = ""
    var missionName: String = ""
    var users: Map<String, ChatUser> = mapOf()
}