package com.tut.badge

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import androidx.core.app.NotificationCompat

class SMSReceiver : BroadcastReceiver() {
    var msgs = 0
    val CHANNEL_ID = "msg_count"
    lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.provider.Telephony.SMS_RECEIVED") {
            /*  Listening to only new message arrived
//            val msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent)
//            if (msgs != null) {
//                Log.d("Unread Messages: ", "${msgs.size}")
//                for (msg in msgs) {
//                    Log.d("MessageBody : ",msg.messageBody)
//                }
//            } else {
//                Log.d("Unread Messages", "${msgs}")
//            }*/

            //  region Counting all unread messages
            val msgReader = Message(context?.contentResolver)
            msgs = msgReader.messageCountUnread
//            Log.d("Unread Messages", "${msgs}")
            //  endregion
        }
        /*else if (intent?.action == "SMS_RECEIVED") {
            msgs = intent.getIntExtra("value", -1)
        }*/
        Log.d("Unread Mess: ", "${msgs}")
        notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        setNotificationChannel()
        updateMessageCount(context, msgs)
    }

    private fun updateMessageCount(context: Context?, num_messages: Int) {
        Log.d("num messages: ", "$num_messages")
        val notification = Notification.Builder(context, CHANNEL_ID)
            .setContentTitle("Unread Messages")
            .setContentText("You've unread message $num_messages")
            .setSmallIcon(R.drawable.ic_launcher)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setNumber(num_messages)
            .build()
        notificationManager.notify(CHANNEL_ID, 5, notification)
    }

    private fun setNotificationChannel() {
        val mChannel = NotificationChannel(CHANNEL_ID, "Unread Message",
            NotificationManager.IMPORTANCE_MIN)
        notificationManager.createNotificationChannel(mChannel)
    }
}