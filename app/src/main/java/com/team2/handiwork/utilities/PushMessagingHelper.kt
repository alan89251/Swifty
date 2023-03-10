package com.team2.handiwork.utilities

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.team2.handiwork.AppConst.AGENT
import com.team2.handiwork.AppConst.BODY
import com.team2.handiwork.AppConst.DATA
import com.team2.handiwork.AppConst.FCM_BASE_URL
import com.team2.handiwork.AppConst.FCM_MESSAGE_KEY
import com.team2.handiwork.AppConst.FCM_SCOPES
import com.team2.handiwork.AppConst.FCM_SEND_ENDPOINT
import com.team2.handiwork.AppConst.FCM_SERVER_KEY
import com.team2.handiwork.AppConst.IS_AGENT
import com.team2.handiwork.AppConst.MISSION
import com.team2.handiwork.AppConst.TITLE
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class PushMessagingHelper(private val context: Context) {

    /**
     * Retrieve a valid access token that can be use to authorize requests to the FCM REST API.
     */
    // [START retrieve_access_token]
    @get:Throws(IOException::class)
    private val accessToken: String
        get() {
            val assetManager: AssetManager = context.assets
            val inputStream = assetManager.open(FCM_SERVER_KEY)
            val credentials =
                GoogleCredentials.fromStream(inputStream)
                    .createScoped(listOf(*FCM_SCOPES))
            credentials.refreshIfExpired()
            return credentials.accessToken.tokenValue
        }

    /**
     * Create HttpURLConnection.
     */
    @get:Throws(IOException::class)
    private val connection: HttpURLConnection
        get() {
            val url = URL(FCM_BASE_URL + FCM_SEND_ENDPOINT)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.setRequestProperty("Authorization", "Bearer $accessToken")
            httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8")

            return httpURLConnection
        }

    /**
     * Send request to FCM message using HTTP.
     */
    @Throws(IOException::class)
    private fun sendMessage(fcmMessage: JsonObject) {
        val connection = connection
        connection.doOutput = true
        val outputStream = DataOutputStream(connection.outputStream)
        outputStream.writeBytes(fcmMessage.toString())
        outputStream.flush()
        outputStream.close()
        val responseCode = connection.responseCode
        if (responseCode == 200) {
            val response = inputStreamToString(connection.inputStream)
            println("Message sent to Firebase for delivery, response:")
            Log.d("PushMessagingHelper", response)
        } else {
            println("Unable to send message to Firebase:")
            val response = inputStreamToString(connection.errorStream)
            Log.e("PushMessagingHelper", response)
        }
    }

    /**
     * Read contents of InputStream into String.
     */
    @Throws(IOException::class)
    private fun inputStreamToString(inputStream: InputStream): String {
        val stringBuilder = StringBuilder()
        val scanner = Scanner(inputStream)
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine())
        }
        return stringBuilder.toString()
    }

    /**
     * Pretty print a JsonObject.
     */
    private fun logJsonObject(jsonObject: JsonObject) {
        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        Log.d("PushMessagingHelper",gson.toJson(jsonObject) + "\n")
    }

    /**
     * Construct the body of a notification message request.
     */
    private fun buildNotificationMessage(fcmToken: String, sendTitle: String, sendBody: String, sendAgent: String, sendMission: String, isAgent: Boolean): JsonObject {
        val jMessage = JsonObject()
        jMessage.addProperty("token", fcmToken)
        //construct the data json
        val jData = JsonObject()
        jData.addProperty(TITLE, sendTitle)
        jData.addProperty(BODY, sendBody)
        jData.addProperty(AGENT, sendAgent)
        jData.addProperty(MISSION, sendMission)
        jData.addProperty(IS_AGENT, isAgent.toString())
        jMessage.add(DATA, jData)
        val jFcm = JsonObject()
        jFcm.add(FCM_MESSAGE_KEY, jMessage)
        return jFcm
    }

    /**
     * Send notification message to FCM for delivery to registered devices.
     */
    @Throws(IOException::class)
    fun sendPushMessage(fcmToken: String, sendTitle: String, sendBody: String, sendAgent: String, sendMission: String, isAgent: Boolean) {
        val notificationMessage: JsonObject = buildNotificationMessage(fcmToken, sendTitle, sendBody, sendAgent, sendMission, isAgent)
        logJsonObject(notificationMessage)
        sendMessage(notificationMessage)
    }
}