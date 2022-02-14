package com.example.reminder

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
//import coil.compose.rememberImagePainter
//import com.catalin.profilepage.ui.theme.ProfilePageTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.ImagePainter.State.Empty.painter
import coil.compose.rememberImagePainter
import com.example.reminder.MainActivity.Companion.TAG
import com.example.reminder.ui.theme.Purple500
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


//@Composable
//fun ProfileScreen() {
//    val notification = rememberSaveable(mutableSetOf("")) { }
//    if (notification.value.isNotEmpty()) {
//        Toast
//    }
//}

@Composable
fun ProfileScreen(user : User, navController: NavController) {

    val db = Firebase.firestore
    val notification = rememberSaveable { mutableStateOf("") }
    if (notification.value.isNotEmpty()) {
        Toast.makeText(LocalContext.current, notification.value, Toast.LENGTH_LONG).show()
        notification.value = ""
    }
    lateinit var auth: FirebaseAuth
// ...
// Initialize Firebase Auth
    auth = Firebase.auth
    var loginUser = auth.currentUser
    var name by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf(loginUser?.email.toString()) }
    var bio by rememberSaveable { mutableStateOf("") }

    if (loginUser?.uid != "") {
        val docRef = db.collection("users"). document(loginUser?.uid.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fileStoreDocument = document?.data;
                    name = fileStoreDocument?.get("displayName").toString();
                    bio = fileStoreDocument?.get("bio").toString();

                    Log.w(TAG, "?????????????????????????? ${document?.data?.get("displayName")}");
                } else {
                    Log.w(TAG, "??????????????????????????No data = ${document?.data}");
                }
            }
    }


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
                            navController.navigate(route = Screens.Reminders.route)
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
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Cancel",
                        modifier = Modifier.clickable { notification.value = "Cancelled" })
//            navController.navigate(route = Screens.Reminders.route)
                    Text(text = "Save",
                        modifier = Modifier.clickable {
                            user.displayName = name
                            user.bio = bio
                            user.userId = loginUser?.uid.toString()
                            db.collection("users")
                                .document(user.userId)
                                .set(user)
//                        .add(user)
                                .addOnSuccessListener { documentReference ->
                                    Log.d(TAG, "------------------------------------------------------------DocumentSnapshot added with ID: ${user.userId}")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++Error adding document", e)
                                }

                            notification.value = "////////////////////////////////////////////////////////////Profile updated"
                            navController.navigate(route = Screens.Reminders.route)
                        })
                }

                ProfileImage()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Name", modifier = Modifier.width(100.dp))
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            textColor = Color.Black
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Username", modifier = Modifier.width(100.dp))
                    TextField(
                        value = username,
                        onValueChange = { username = it },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            textColor = Color.Black
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "Bio", modifier = Modifier
                            .width(100.dp)
                            .padding(top = 8.dp)
                    )
                    TextField(
                        value = bio,
                        onValueChange = { bio = it },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            textColor = Color.Black
                        ),
                        singleLine = false,
                        modifier = Modifier.height(150.dp)
                    )
                }
            }
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
                                navController.navigate(route = Screens.Reminders.route)
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
fun ProfileImage() {
    val imageUri = rememberSaveable { mutableStateOf("") }
    val painter = rememberImagePainter(
        if (imageUri.value.isEmpty())
            R.drawable.ic_download
        else
            imageUri.value
    )
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUri.value = it.toString() }
    }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = CircleShape,
            modifier = Modifier
                .padding(8.dp)
                .size(100.dp)
        ) {
            painter?.let {
                Image(
                    painter = it,
                    contentDescription = null,
                    modifier = Modifier
                        .wrapContentSize()
                        .clickable { launcher.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
            }
        }
        Text(text = "Change profile picture")
    }
}


@ExperimentalMaterialApi
@Composable
//@Preview(showBackground = false)
private fun ProfileScreenPreview(user: User) {
//        ProfileScreen(user = User(email = "", displayName = ""))
}


