package com.example.reminder

import GoogleButton
import android.nfc.Tag
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.reminder.MainActivity.Companion.TAG
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch
import kotlin.math.log

@ExperimentalMaterialApi
@Composable
fun AuthView(errorText:String?,
             onClick:() -> Unit){
    Scaffold {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            GoogleButton(text = "Sign Up With Google",
                loadingText = "Signing In....",
                onClicked = {onClick()})
            errorText?.let {
                Spacer(modifier = Modifier.height(30.dp))
                Text(text = it)
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@Composable
fun AuthScreen(authViewModel: AuthViewModel){

    val coroutineScope = rememberCoroutineScope()
    var text by remember { mutableStateOf<String?>(null)}
    val user by remember(authViewModel){authViewModel.user}.collectAsState()
    val signInRequestCode = 1
    Log.d(TAG,"-------------------------------------------------------------------------Call to 00000 User login google")
    val authResultLauncher =
        rememberLauncherForActivityResult(contract = AuthResultContract()){
                task ->
            try {
                val account = task?.getResult(ApiException::class.java)
                if (account==null){
                    Log.d(TAG,"-------------------------------------------------------------------------User login google")
                    text = "--------------------------------------------------------------------------------Google sign in failed"
                }else{
                    Log.d(TAG,"-------------------------------------------------------------------------User login google")
                    coroutineScope.launch {
                        account.email?.let { account.displayName?.let { it1 -> authViewModel.signIn(email = it,displayName = it1) } }
                    }
                }
            }catch (e:ApiException){
                text="Google sign in failed"
            }
        }
    AuthView(errorText = text,onClick = {text=null
        authResultLauncher.launch(signInRequestCode)
    })
    user?.let{
//        ProfileScreen(user = it)
        Log.d(TAG,"-------------------------------------------------------------------------User login google")
    }
}