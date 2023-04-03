package com.team2.handiwork

object AppConst {
    const val PREF_UID = "uniqueID"                 //preference UID
    const val PREF_SIGNED_IN_BY = "signed_in_by"
    const val PREF_CALL_FROM_SIGNUP = "from_signup"
    const val EMAIL = "email"
    const val CURRENT_USER = "current_user"
    const val CURRENT_THEME = "current_theme"
    const val CHANNEL_ID = "CHAT_NOTIFICATION"
    const val TITLE = "title"
    const val BODY = "body"
    const val DATA = "data"
    const val AGENT = "agent"
    const val MISSION = "mission"
    const val IS_AGENT = "is_agent"
    const val NOTIFICATION_MSG = "NotificationMessage"
    private const val FCM_PROJECT_ID = "mapd726-t2"
    const val FCM_BASE_URL = "https://fcm.googleapis.com"
    const val FCM_SEND_ENDPOINT = "/v1/projects/$FCM_PROJECT_ID/messages:send"
    private const val FCM_MESSAGING_SCOPE1 = "https://www.googleapis.com/auth/cloud-platform"
    private const val FCM_MESSAGING_SCOPE2 = "https://www.googleapis.com/auth/firebase.messaging"
    const val FCM_MESSAGE_KEY = "message"
    const val FCM_SERVER_KEY = "mapd726_t2_fcm_auth_key.json"
    val FCM_SCOPES = arrayOf(FCM_MESSAGING_SCOPE1, FCM_MESSAGING_SCOPE2)
    const val PREF_DEVICE_TOKEN = "fcm_device_token"
    const val PREF_SUGGESTED_MISSION_COUNT = "user_suggested_mission_count"
    const val PREF_SCHEDULE_SUGGESTION_WORK = "get_mission_suggestion_count"
    const val SUGGESTION_CHANNEL_ID = "SUGGESTION_NOTIFICATION"
    const val PREF_USER_ICON_URL = "user_icon_url"
    const val PREF_TARGET_ICON_URL = "target_user_icon_url"
}