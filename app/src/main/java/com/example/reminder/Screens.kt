package com.example.reminder

sealed class Screens(val route: String) {
    object Login: Screens(route = "login")
    object Profile: Screens(route = "profile")
    object Register: Screens(route = "register")
    object Reminders: Screens(route = "reminders")
}