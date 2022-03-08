package com.example.reminder

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.ContextThemeWrapper
import android.widget.CalendarView
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.reminder.MainActivity.Companion.TAG
import com.example.reminder.ui.theme.Purple500
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import androidx.compose.material.AlertDialog as MaterialAlertDialog

//@ExperimentalMaterialApi
//@ExperimentalFoundationApi
//@ExperimentalAnimationApi
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RemindersScreen(auth: FirebaseAuth, navController: NavController, con: Context) {

    val db = Firebase.firestore
    val content = remember { mutableStateOf("Home Screen") }
    val selectedItem = remember { mutableStateOf("home") }
    val openDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Reminders"
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
            Column(
                modifier = Modifier
                    .background(color = Color(0xffffffff))
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                var queryResults = db.collection("reminders").whereEqualTo("userId", auth.currentUser?.uid.toString())
                    .get()
                LazyColumn {
                    var remindersList: MutableList<ReminderModel> = mutableListOf()
                    var i : Int =1

                    val numOfItems: Int = queryResults.result.documents?.size

                    if (numOfItems > 0)
                    items(numOfItems) { reminder ->
                        ReminderItem(reminder, queryResults.result.documents,
                            auth, con, navController
                        )
                    }
//                    items(remindersList.size) { reminder ->
//                        ReminderItem(reminder, remindersList,
//                            auth, con
//                        )
//                    }
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
                                navController.navigate(route = Screens.Profile.route)

//                                selectedItem.value = "setting"
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
    AddNewReminder(openDialog, con, auth, navController)
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReminderItem(
    number: Int, remindList: MutableList<DocumentSnapshot>,
    auth: FirebaseAuth, con: Context, navController: NavController) {
    val focusManager = LocalFocusManager
        .current
    val time = remember { mutableStateOf("") }
    val timePickerDialog = TimePickerDialog(
        con,
        {_, hour : Int, minute: Int ->
            time.value = "$hour:$minute"
        }, 0, 0, false
    )
    val db = Firebase.firestore

    var description = remindList.get(number).get("description").toString()
    var userId = remindList.get(number).get("userId").toString()
    var longitude = remindList.get(number).get("longitude").toString()
    var latitude = remindList.get(number).get("latitude").toString()
    var date = remindList.get(number).get("date").toString()
    var enteredTime = remindList.get(number).get("time").toString()
    var created_at = remindList.get(number).get("created_at").toString()
    var is_seen = remember { mutableStateOf(false)}
    var reference = remindList.get(number).id

    val openDialog = remember { mutableStateOf(false) }
    val openMessageBox = remember { mutableStateOf(false) }
    val openMapBox = remember { mutableStateOf(false) }
    val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/calendar/render?action=TEMPLATE&text=Remind%20Me%20Service%20Time&dates=20220227T090000/20220227T090000&ctz=Europe/London&details=GaragesNear.me%20Its%20Her%20Birthday%20time&location=1%20Tervakkukatie%20Rajakyla,%20Oulu&trp=false&sprop=&sprop=name:")) }

//    auth = Firebase.auth


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
            Text(
                text = description,
                color = Color(0xff0b06a6),
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = date,
                color = Color(0xff0b06a6),
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = enteredTime,
                color = Color(0xff0b06a6),
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Lat: " + latitude + " Long: " + longitude,
                color = Color(0xff0b06a6),
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 16.dp)
            )


            Button(
                onClick = {
                    openDialog.value = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff0b06a6)),
            ) {
                Text(
                    text = "View",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
            Button(
                onClick = {
                    openMessageBox.value = true
//
//                    navController.navigate(route = Screens.Reminders.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff0b06a6)),
            ) {
                Text(
                    text = "Delete",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }


            Button(
                onClick = {
                    openMapBox.value = true
//
//                    navController.navigate(route = Screens.Reminders.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff0b06a6)),
            ) {
                Text(
                    text = "Add Location",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
            Button(onClick = { con.startActivity(intent) }) {
                Text(text = "Add to Google Calender")
            }
        }
    }

    messageDialogBox(openMessageBox, reference, navController)
    AddReminder(openDialog, con, auth, remindList.get(number), remindList.get(number).id, navController)
    AddLocation(openMapDialogBox = openMapBox, con = con, auth = auth, remindList.get(number), remindList.get(number).id, navController = navController)
}

@Composable
fun messageDialogBox(openMessageBox: MutableState<Boolean>, reference: String, navController: NavController) {
    val db = Firebase.firestore
    if (openMessageBox.value) {

        MaterialAlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onCloseRequest.
                openMessageBox.value = false
            },
            title = {
                Text(text = "Are you sure, Do you want to Delete Reminder")
            },
            text = {
                Text("If you allow, reminder will permanently delete")
            },
            confirmButton = {
                Button(
                    onClick = {
                        openMessageBox.value = false
                        db.collection("reminders").document(reference)
                            .delete()
                            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
                    navController.navigate(route = Screens.Reminders.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 16.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xffF51720)),
////
                ) {
                    Text(
                        text = "Delete",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                        )
                }
            },
            dismissButton = {
                Button(

                    onClick = {
                        openMessageBox.value = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 16.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff0b06a6)),

                ) {
                    Text(
                        text = "Cancel",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        )
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddReminder(openDialogBox: MutableState<Boolean>, con: Context, auth: FirebaseAuth, remindList: DocumentSnapshot, reminderId: String, navController: NavController) {

    val db = Firebase.firestore
    val time = remember { mutableStateOf("") }
    val timePickerDialog = TimePickerDialog(
        con,
        {_, hour : Int, minute: Int ->
            time.value = "$hour:$minute"
        }, 0, 0, false
    )

    var description by rememberSaveable { mutableStateOf("") }
    var userId by rememberSaveable { mutableStateOf("") }
    var longitude by rememberSaveable { mutableStateOf("") }
    var latitude by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }
    var enteredTime by rememberSaveable { mutableStateOf("") }
    var created_at by rememberSaveable { mutableStateOf("") }
    var is_seen by rememberSaveable { mutableStateOf(false) }

    if (reminderId != "" ){
        description = remindList.get("description").toString()
        userId = remindList.get("userId").toString()
        longitude = remindList.get("longitude").toString()
        latitude = remindList.get("latitude").toString()
        date = remindList.get("date").toString()
        enteredTime = remindList.get("time").toString()
        created_at = remindList.get("created_at").toString()
//        is_seen = remember { mutableStateOf(false)}
//        var reference = remindList.get(number).id
    }

    if (openDialogBox.value) {

        Dialog(onDismissRequest = { /*TODO*/ }) {
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
                    OutlinedTextField(value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        placeholder = { Text("It's her birthday") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
//                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    OutlinedTextField(value = date,
                        onValueChange = { date = it },
                        label = { Text("Date") },
                        placeholder = { Text("YYYY-MM-DD") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
//                        onNext = { focusManager.clearFocus() }
                        )
                    )

                    OutlinedTextField(value = enteredTime,
                        onValueChange = { enteredTime = it },
                        label = { Text("Time") },
                        placeholder = { Text("HH:MM") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
//                        onNext = { focusManager.clearFocus() }
                        )
                    )

//                    OutlinedTextField(value = longitude,
//                        onValueChange = { longitude = it },
//                        label = { Text("Longitude") },
//                        placeholder = { Text("-77.0364") },
//                        singleLine = true,
//                        modifier = Modifier.fillMaxWidth(),
//                        keyboardOptions = KeyboardOptions(
//                            keyboardType = KeyboardType.Text,
//                            imeAction = ImeAction.Done
//                        ),
//                        keyboardActions = KeyboardActions(
////                        onNext = { focusManager.clearFocus() }
//                        )
//                    )
//
//                    OutlinedTextField(value = latitude,
//                        onValueChange = { latitude = it },
//                        label = { Text("Latitude") },
//                        placeholder = { Text("-77.0364") },
//                        singleLine = true,
//                        modifier = Modifier.fillMaxWidth(),
//                        keyboardOptions = KeyboardOptions(
//                            keyboardType = KeyboardType.Text,
//                            imeAction = ImeAction.Done
//                        ),
//                        keyboardActions = KeyboardActions(
////                        onNext = { focusManager.clearFocus() }
//                        )
//                    )

                    if (reminderId == "") {
                        Button(
                            onClick = {
                                val reminderModel = ReminderModel()
                                reminderModel.userId = auth.currentUser?.uid.toString()
                                reminderModel.description = description
                                reminderModel.date = date
                                reminderModel.seen = is_seen
                                reminderModel.latitude = latitude
                                reminderModel.longitude = longitude
                                reminderModel.time = enteredTime

                                db.collection("reminders")
                                    .add(reminderModel)
                                    .addOnSuccessListener { documentReference ->
                                        Log.d(MainActivity.TAG, "------------------------------------------------------------DocumentSnapshot added with ID: ${reminderModel.userId}")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(MainActivity.TAG, "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++Error adding document", e)
                                    }
                                openDialogBox.value = false

                                createNotificationChannel(context = Graph.appContext)
                                createReminderMadeNotification(reminderModel)
                                createNotificationChannel3()
                                scheduleNotification(reminderModel)

                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 16.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff0b06a6)),
//                    enabled = isEmailValid
                        ) {
                            Text(
                                text = "Add New Reminder",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                var reminderModel = ReminderModel()
                                reminderModel.userId = auth.currentUser?.uid.toString()
                                reminderModel.description = description
                                reminderModel.date = date
                                reminderModel.seen = is_seen
                                reminderModel.latitude = latitude
                                reminderModel.longitude = longitude
                                reminderModel.time = enteredTime

                                db.collection("reminders")
                                    .document(reminderId)
                                    .set(reminderModel)
                                    .addOnSuccessListener { documentReference ->
                                        Log.d(MainActivity.TAG, "------------------------------------------------------------DocumentSnapshot added with ID: ${reminderModel.userId}")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(MainActivity.TAG, "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++Error adding document", e)
                                    }
                                openDialogBox.value = false
                                navController.navigate(route = Screens.Reminders.route)
                                createNotificationChannel(context = Graph.appContext)
                                createReminderMadeNotification(reminderModel)
                                createNotificationChannel3()
                                scheduleNotification(reminderModel)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 16.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff0b06a6)),

                        ) {
                            Text(
                                text = "Update Reminder",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            openDialogBox.value = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 16.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xffF51720)),
//                    enabled = isEmailValid
                    ) {
                        Text(
                            text = "Close",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

//        ShowTimePicker(con, 0, 0)
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddNewReminder(openDialogBox: MutableState<Boolean>, con: Context, auth: FirebaseAuth, navController: NavController) {

    val db = Firebase.firestore
    val time = remember { mutableStateOf("") }
    val timePickerDialog = TimePickerDialog(
        con,
        {_, hour : Int, minute: Int ->
            time.value = "$hour:$minute"
        }, 0, 0, false
    )

    var description by rememberSaveable { mutableStateOf("") }
    var userId by rememberSaveable { mutableStateOf("") }
    var longitude by rememberSaveable { mutableStateOf("") }
    var latitude by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }
    var enteredTime by rememberSaveable { mutableStateOf("") }
    var created_at by rememberSaveable { mutableStateOf("") }
    var is_seen by rememberSaveable { mutableStateOf(false) }
    val isChecked = remember { mutableStateOf(true) }



    if (openDialogBox.value) {

        Dialog(onDismissRequest = { /*TODO*/ }) {
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
                    OutlinedTextField(value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        placeholder = { Text("It's her birthday") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
//                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    OutlinedTextField(value = date,
                        onValueChange = { date = it },
                        label = { Text("Date") },
                        placeholder = { Text("YYYY-MM-DD") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
//                        onNext = { focusManager.clearFocus() }
                        )
                    )

                    OutlinedTextField(value = enteredTime,
                        onValueChange = { enteredTime = it },
                        label = { Text("Time") },
                        placeholder = { Text("HH:MM") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
//                        onNext = { focusManager.clearFocus() }
                        )
                    )

//                    OutlinedTextField(value = longitude,
//                        onValueChange = { longitude = it },
//                        label = { Text("Longitude") },
//                        placeholder = { Text("-77.0364") },
//                        singleLine = true,
//                        modifier = Modifier.fillMaxWidth(),
//                        keyboardOptions = KeyboardOptions(
//                            keyboardType = KeyboardType.Text,
//                            imeAction = ImeAction.Done
//                        ),
//                        keyboardActions = KeyboardActions(
////                        onNext = { focusManager.clearFocus() }
//                        )
//                    )
//
//                    OutlinedTextField(value = latitude,
//                        onValueChange = { latitude = it },
//                        label = { Text("Latitude") },
//                        placeholder = { Text("-77.0364") },
//                        singleLine = true,
//                        modifier = Modifier.fillMaxWidth(),
//                        keyboardOptions = KeyboardOptions(
//                            keyboardType = KeyboardType.Text,
//                            imeAction = ImeAction.Done
//                        ),
//                        keyboardActions = KeyboardActions(
////                        onNext = { focusManager.clearFocus() }
//                        )
//                    )

                    Row(modifier = Modifier.padding(8.dp)) {


                        Checkbox(
                            checked = isChecked.value,
                            onCheckedChange = { isChecked.value = it },
                            enabled = true,
                            colors = CheckboxDefaults.colors(Color(0xff0b06a6))
                        )
                        Text(text = " Notify me")
                    }

                        Button(
                            onClick = {
                                val reminderModel = ReminderModel()
                                reminderModel.userId = auth.currentUser?.uid.toString()
                                reminderModel.description = description
                                reminderModel.date = date
                                reminderModel.seen = is_seen
                                reminderModel.latitude = latitude
                                reminderModel.longitude = longitude
                                reminderModel.time = enteredTime

                                db.collection("reminders")
                                    .add(reminderModel)
                                    .addOnSuccessListener { documentReference ->
                                        Log.d(MainActivity.TAG, "------------------------------------------------------------DocumentSnapshot added with ID: ${reminderModel.userId}")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(MainActivity.TAG, "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++Error adding document", e)
                                    }
                                openDialogBox.value = false
                                navController.navigate(route = Screens.Reminders.route)
                                createNotificationChannel(context = Graph.appContext)
                                createReminderMadeNotification(reminderModel)

                                if(isChecked.value) {
                                    createNotificationChannel3()
                                    scheduleNotification(reminderModel)
                                }

                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 16.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff0b06a6)),
//                    enabled = isEmailValid
                        ) {
                            Text(
                                text = "Add New Reminder",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }

                }
            }
        }

//        ShowTimePicker(con, 0, 0)
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddLocation(
    openMapDialogBox: MutableState<Boolean>,
    con: Context,
    auth: FirebaseAuth,
    remindList: DocumentSnapshot,
    reminderId: String,
    navController: NavController
) {

    val db = Firebase.firestore
    val time = remember { mutableStateOf("") }
    val timePickerDialog = TimePickerDialog(
        con,
        { _, hour: Int, minute: Int ->
            time.value = "$hour:$minute"
        }, 0, 0, false
    )

    var description by rememberSaveable { mutableStateOf("") }
    var userId by rememberSaveable { mutableStateOf("") }
    var longitude by rememberSaveable { mutableStateOf("") }
    var latitude by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }
    var enteredTime by rememberSaveable { mutableStateOf("") }
    var created_at by rememberSaveable { mutableStateOf("") }
    var is_seen by rememberSaveable { mutableStateOf(false) }
    val isChecked = remember { mutableStateOf(true) }

    description = remindList.get("description").toString()
    userId = remindList.get("userId").toString()
    longitude = remindList.get("longitude").toString()
    latitude = remindList.get("latitude").toString()
    date = remindList.get("date").toString()
    enteredTime = remindList.get("time").toString()
    created_at = remindList.get("created_at").toString()



    if (openMapDialogBox.value) {

        Dialog(onDismissRequest = { /*TODO*/ }) {
            ReminderLocationMap(latitude, longitude, navController)
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
                    Button(
                        onClick = {
                            val latlng = navController
                                .currentBackStackEntry
                                ?.savedStateHandle
                                ?.getLiveData<LatLng>("location_data")
                                ?.value

                            var reminderModel = ReminderModel()
                            reminderModel.userId = auth.currentUser?.uid.toString()
                            reminderModel.description = description
                            reminderModel.date = date
                            reminderModel.seen = is_seen
                            reminderModel.latitude = latlng?.latitude.toString()
                            reminderModel.longitude = latlng?.longitude.toString()
                            reminderModel.time = enteredTime

                            if(reminderModel.latitude != "" && reminderModel.latitude != "null" && reminderModel.latitude != null) {
                                db.collection("reminders")
                                    .document(reminderId)
                                    .set(reminderModel)
                                    .addOnSuccessListener { documentReference ->
                                        Log.d(MainActivity.TAG, "------------------------------------------------------------DocumentSnapshot added with ID: ${reminderModel.userId}")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(MainActivity.TAG, "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++Error adding document", e)
                                    }
                            }
                            openMapDialogBox.value = false
                            navController.navigate(route = Screens.Reminders.route)
//                            createNotificationChannel(context = Graph.appContext)
//                            createReminderMadeNotification(reminderModel)
//                            createNotificationChannel3()
//                            scheduleNotification(reminderModel)

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 16.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff0b06a6)),
//                    enabled = isEmailValid
                    ) {
                        Text(
                            text = "Set Location",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }

                }
            }
        }

//        ShowTimePicker(con, 0, 0)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePicker(onDateSelected: (LocalDate) -> Unit, onDismissRequest: () -> Unit) {
    val selDate = remember { mutableStateOf(LocalDate.now()) }

    //todo - add strings to resource after POC
    Dialog(onDismissRequest = { onDismissRequest() }, properties = DialogProperties()) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(size = 16.dp)
                )
        ) {
            Column(
                Modifier
                    .defaultMinSize(minHeight = 72.dp)
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colors.primary,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select date".toUpperCase(Locale.ENGLISH),
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onPrimary
                )

                Spacer(modifier = Modifier.size(24.dp))

                Text(
                    text = selDate.value.format(DateTimeFormatter.ofPattern("MMM d, YYYY")),
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onPrimary
                )

                Spacer(modifier = Modifier.size(16.dp))
            }

            CustomCalendarView(onDateSelected = {
                selDate.value = it
            })

            Spacer(modifier = Modifier.size(8.dp))

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 16.dp, end = 16.dp)
            ) {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    //TODO - hardcode string
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.button,
                        color = MaterialTheme.colors.onPrimary
                    )
                }

                TextButton(
                    onClick = {
                        onDateSelected(selDate.value)
                        onDismissRequest()
                    }
                ) {
                    //TODO - hardcode string
                    Text(
                        text = "OK",
                        style = MaterialTheme.typography.button,
                        color = MaterialTheme.colors.onPrimary
                    )
                }

            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomCalendarView(onDateSelected: (LocalDate) -> Unit) {
    // Adds view to Compose
    AndroidView(
        modifier = Modifier.wrapContentSize(),
        factory = { context ->
            CalendarView(ContextThemeWrapper(context, R.style.CardView_Light))
        },
        update = { view ->
            view.minDate = 0;//contraints
                view.maxDate = 0;

                view.setOnDateChangeListener { _, year, month, dayOfMonth ->
                    onDateSelected(
                        LocalDate
                            .now()
                            .withMonth(month + 1)
                            .withYear(year)
                            .withDayOfMonth(dayOfMonth)
                    )
                }
        }
    )
}

@Composable
fun ShowTimePicker(context: Context, initHour: Int, initMinute: Int) {
    val time = remember { mutableStateOf("") }
    val timePickerDialog = TimePickerDialog(
        context,
        {_, hour : Int, minute: Int ->
            time.value = "$hour:$minute"
        }, initHour, initMinute, false
    )
    Button(onClick = {
        timePickerDialog.show()
    }) {
        Text(text = "Open Time Picker")
    }
}
