package com.example.reminder

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import java.util.*
import android.app.*
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.annotation.RequiresApi
import java.lang.Exception
import java.text.SimpleDateFormat

const val notificationID = 3
const val channelID = "channel1"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

class NotificationBroadCast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent)
    {
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .build()

        val  manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun scheduleNotification(reminderModel: ReminderModel)
{
    val intent = Intent(Graph.appContext, NotificationBroadCast::class.java)
    val title = "Check reminder me..."
    val message = "You have scheduled reminder -" + reminderModel.description
    intent.putExtra(titleExtra, title)
    intent.putExtra(messageExtra, message)

    val pendingIntent = PendingIntent.getBroadcast(
        Graph.appContext,
        notificationID,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val alarmManager = Graph.appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val time = getTime(reminderModel.date + " " + reminderModel.time)
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        time,
        pendingIntent
    )
    showAlert(time, reminderModel.description, "Scheduled time")
}
//
private fun showAlert(time: Long, title: String, message: String)
{
    val date = Date(time)
    val dateFormat = android.text.format.DateFormat.getLongDateFormat(Graph.appContext)
    val timeFormat = android.text.format.DateFormat.getTimeFormat(Graph.appContext)

    AlertDialog.Builder(Graph.appContext)
        .setTitle("Reminder Scheduler Details")
        .setMessage(
            "Title: " + title +
                    "\n" + message +
                    "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date))
        .setPositiveButton("Okay"){_,_ ->}
        .show()
}
//
@RequiresApi(Build.VERSION_CODES.O)
fun getTime(stringDate: String): Long
{
    try {
        val formatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")
        val date: Date = formatter.parse(stringDate)
        return date.time
    } catch (e: Exception) {
        return 0
    }
}
//
@RequiresApi(Build.VERSION_CODES.O)
fun createNotificationChannel3()
{
    val name = "Notif Channel"
    val desc = "A Description of the Channel"
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel(channelID, name, importance)
    channel.description = desc
    val notificationManager = Graph.appContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}