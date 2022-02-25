package com.example.reminder

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.reminder.MainActivity.Companion.TAG
import com.example.reminder.ui.theme.ReminderTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.example.reminder.notificationBody as notificationBody

class MainActivity : ComponentActivity() {
    companion object {
        val TAG : String = MainActivity::class.java.simpleName
    }

    fun runtimeEnableAutoInit() {
        // [START fcm_runtime_enable_auto_init]
        Firebase.messaging.isAutoInitEnabled = true
        // [END fcm_runtime_enable_auto_init]
    }

    private val auth by lazy {
        Firebase.auth
    }
    lateinit var navController: NavHostController
    @ExperimentalAnimationApi
    @ExperimentalFoundationApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReminderTheme {
                navController = rememberNavController()
                Navigation(navController = navController, User(), this)
                Graph.provide(this)
//                binding = ActivityMainBinding.inflate(layoutInflater)
//                LoginScreen(auth, navController)
//                LoginScreen(auth)
//                BottomAppBarWithFab()

                // A surface container using the 'background' color from the theme
//                Surface(color = MaterialTheme.colors.background) {
//                    Greeting("Android")
//                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun LoginScreen(auth: FirebaseAuth, navController: NavController, context: Context) {
//fun LoginScreen(auth: FirebaseAuth) {
    val focusManager = LocalFocusManager.current

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    val isEmailValid by derivedStateOf {
        Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    var isPasswordVisible by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .background(color = Color(0xffffffff))
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Remind me",
            color = Color(0xff0b06a6),
            fontFamily = FontFamily.Companion.SansSerif,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            fontSize = 32.sp,
            modifier = Modifier.padding(top = 16.dp)
        )

        Image(painter = painterResource(id = R.drawable.ic_undraw_reminder_pa79), contentDescription = "Logo", modifier = Modifier.size(150.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.Black)
        ) {
           Column(
               horizontalAlignment = Alignment.CenterHorizontally,
               verticalArrangement = Arrangement.spacedBy(8.dp),
               modifier = Modifier.padding(all = 10.dp)
           ) {
               OutlinedTextField(value = email,
                   onValueChange = {email = it},
                   label = { Text("Email Address")},
                   placeholder = { Text("sample@sample.com")},
                   singleLine = true,
                   modifier = Modifier.fillMaxWidth(),
                   keyboardOptions = KeyboardOptions(
                       keyboardType = KeyboardType.Email,
                       imeAction = ImeAction.Next
                   ),
                   keyboardActions = KeyboardActions(
                       onNext = { focusManager.moveFocus(FocusDirection.Down)}
                   )
               )

               OutlinedTextField(value = password,
                   onValueChange = {password = it},
                   label = { Text("Password")},
                   singleLine = true,
                   modifier = Modifier.fillMaxWidth(),
                   keyboardOptions = KeyboardOptions(
                       keyboardType = KeyboardType.Password,
                       imeAction = ImeAction.Done
                   ),
                   keyboardActions = KeyboardActions(
                       onNext = { focusManager.clearFocus()}
                   )
               )

               Button(
                   onClick = {
                       auth.signInWithEmailAndPassword(email, password)
                           .addOnCompleteListener {
                               if (it.isSuccessful) {
                                   Log.d(TAG,"user logged in")
                                   navController.navigate(route = Screens.Reminders.route)
//                                  navController.navigate(route = "profile")
                               } else {
                                   Log.d(TAG,"failed", it.exception)
                               }
                           }
                   },
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(all = 16.dp),
                   colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff0b06a6)),
                   enabled = isEmailValid
               ) {
                    Text(
                        text = "Log in",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
               }
           }
        }
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = { /*TODO*/ }) {
                Text(
                    color = Color.Black,
                    fontStyle = FontStyle.Italic,
                    text = "Forgot password ?",
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        Button(
            onClick = {

                navController.navigate(route = Screens.Register.route)


            },
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff0b06a6))
        ) {
            Text(
                text = "Register",
                fontWeight = FontWeight.Bold,
                color = Color(0xffffffff),
                fontSize = 16.sp
            )
        }
//        GoogleButton(text = "Sign Up with Google",
//            loadingText = "Creating Account...",
//            onClicked = {})
        AuthScreen(AuthViewModel())
//        notificationBody(Graph.appContext)


    }
}



@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ReminderTheme {
//        LoginScreen(Firebase.auth )
//        LoginScreen(Firebase.auth, navController = rememberNavController(), this)
    }
}