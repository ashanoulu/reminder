package com.example.reminder

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

data class ReminderModel @RequiresApi(Build.VERSION_CODES.O) constructor(
    var userId: String ="",
    var description: String="",
    var longitude: String="",
    var latitude: String="",
    var date: String="",
    var time: String="",
    var created_at: String= LocalDateTime.now().toString(),
    var seen: Boolean= false,
    var reference: String=""
)
