package com.team2.handiwork.models

class Comment {
    var rating = 0F
    var content = ""
    var firstname = ""
    var lastname = ""
    var missionId = ""
    var missionSubServiceType = ""
    var isFromAgent = false // is this comment written by an agent
    var createdAt: Long = System.currentTimeMillis()
}