package com.example.reminder

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.remi.RegisterScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun Navigation(
    navController: NavHostController,
    user: User,
    con: Context
) {
    NavHost(navController = navController, startDestination = Screens.Login.route) {
        composable(route = Screens.Login.route) {
            LoginScreen(Firebase.auth, navController)
        }
        composable(route = Screens.Profile.route) {
            ProfileScreen(user = User(email = "ashan@gmail.com", displayName = ""), navController)
        }
        composable(route = Screens.Register.route) {
            RegisterScreen(Firebase.auth, navController)
        }
        composable(route = Screens.Reminders.route) {
            RemindersScreen(Firebase.auth, navController, con)
        }
    }
}