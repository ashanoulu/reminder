package com.example.reminder

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.reminder.ui.theme.Purple500
import com.example.reminder.ui.theme.ReminderTheme
import com.example.reminder.ui.theme.Teal200

@Composable
fun BottomAppBarWithFab() {
    val content = remember { mutableStateOf("Home Screen") }
    val selectedItem = remember { mutableStateOf("home") }
    val openDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bottom App Bar with FAB"
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {


                        }
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "")
                    }
                },
                backgroundColor = Purple500, //Color(0xff0b06a6),
                elevation = AppBarDefaults.TopAppBarElevation
            )
        },

        content = {

        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    openDialog.value = true
                },
                shape = RoundedCornerShape(50),
                backgroundColor = Color(0xff0b06a6)
            ) {
                Icon(Icons.Filled.Add, tint = Color.White, contentDescription = "Add")
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center, //if its not set, it's show default position

        bottomBar = {
            BottomAppBar(
                cutoutShape = RoundedCornerShape(50),
                content = {
                    BottomNavigation {
                        BottomNavigationItem(
                            selected = selectedItem.value == "home",
                            onClick = {
//                                ProfileScreen(user = User(email = "ashan@gmail.com", displayName = ""), navController = rememberNavController())
                                selectedItem.value = "home"
                            },
                            icon = {
                                Icon(Icons.Filled.Home, contentDescription = "home")
                            },
                            label = { Text(text = "Home") },
                            alwaysShowLabel = false
                        )

                        BottomNavigationItem(
                            selected = selectedItem.value == "Setting",
                            onClick = {
                                content.value = "Setting Screen"
                                selectedItem.value = "setting"
                            },
                            icon = {
                                Icon(Icons.Filled.AccountCircle, contentDescription = "setting")
                            },
                            label = { Text(text = "Setting") },
                            alwaysShowLabel = false
                        )
                    }
                }
            )
        }
    )
}


@Composable
//@Preview(showBackground = false)
fun BottomAppBarWithFabPreview(user: User) {
    ReminderTheme {
        BottomAppBarWithFab()
    }
}