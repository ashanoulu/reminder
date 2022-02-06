package com.example.reminder


//import android.content.Context
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.content.Context
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()

    return GoogleSignIn.getClient(context,signInOptions)

}