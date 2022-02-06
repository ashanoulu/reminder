package com.example.remi
import android.util.Log
import android.util.Patterns
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.example.reminder.MainActivity.Companion.TAG
import com.example.reminder.Screens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun RegisterScreen(auth: FirebaseAuth, navController: NavController) {

//    lateinit var auth: FirebaseAuth
    val focusManager = LocalFocusManager
        .current

//    auth = Firebase.auth

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
            text = "Create an account with Remind me",
            color = Color(0xff0b06a6),
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            fontSize = 32.sp,
            modifier = Modifier.padding(top = 16.dp)
        )

//        Image(painter = painterResource(id = R.drawable.ic_undraw_reminder_pa79), contentDescription = "Logo", modifier = Modifier.size(150.dp))

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
                    label = { Text("Email Address") },
                    placeholder = { Text("sample@sample.com") },
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
                    label = { Text("Password") },
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
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener() { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success")
                                    val user = auth.currentUser
                                    navController.navigate(route = Screens.Profile.route)
//                                    updateUI(user)
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
//                                    Toast.makeText( "Authentication failed.",
//                                        Toast.LENGTH_SHORT).show()
//                                    updateUI(null)
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
                        text = "Create an Account",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}



//@ExperimentalMaterialApi
//@Composable
//@Preview(showBackground = false)
//private fun RegisterScreenPreview(user: User) {
//    ProfileScreen(user = User(email = "asss", displayName = "Ashan"))
//}