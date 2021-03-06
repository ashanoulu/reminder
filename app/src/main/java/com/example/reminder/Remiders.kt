package com.example.reminder

import android.app.TimePickerDialog
import android.content.Context
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

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
            Column(
                modifier = Modifier
                    .background(color = Color(0xffffffff))
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "My reminders",
                    color = Color(0xff0b06a6),
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )

//                val hashMap:HashMap<Int,String> = HashMap<Int,String>() //define empty hashmap
//                val remindersList1:HashMap<String,ReminderModel> = HashMap<String,ReminderModel>()//: MutableList<ReminderModel> = MutableList<ReminderModel>(10,10)
//
//                    val query = db.collection("reminders").whereEqualTo("userId", auth.currentUser?.uid.toString())
//                    .get().addOnSuccessListener {
//                        documents ->
//                            for (document in documents) {
//                                var remind = ReminderModel()
//                                remind.userId = document.get("userId").toString()
//                                remind.reference = document.reference.toString()
//                                remind.time = document.get("time").toString()
//                                remind.longitude = document.get("longitude").toString()
//                                remind.latitude = document.get("latitude").toString()
//                                remind.date = document.get("date").toString()
//                                remind.description = document.get("description").toString()
//                                remindersList1[document.id] = remind
//                            }
//
//                        }

                var queryResults = db.collection("reminders").whereEqualTo("userId", auth.currentUser?.uid.toString())
                    .get()
                LazyColumn {
                    var remindersList: MutableList<ReminderModel> = mutableListOf()
                    var i : Int =1

//                        .addOnSuccessListener {
//                                documents ->
//                            //var remindersList: MutableList<ReminderModel> = mutableListOf()
//                            for (document in documents) {
//                                var remind = ReminderModel()
//                                remind.userId = document.get("userId").toString()
//                                remind.reference = document.reference.toString()
//                                remind.time = document.get("time").toString()
//                                remind.longitude = document.get("longitude").toString()
//                                remind.latitude = document.get("latitude").toString()
//                                remind.date = document.get("date").toString()
//                                remind.description = document.get("description").toString()
//                                remindersList.add(remind)
//                                i++
//                            }
//
//
//                        }

                    items(queryResults.result.documents.size) { reminder ->
                        ReminderItem(reminder, queryResults.result.documents,
                            auth, con
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

    AddNewReminder(openDialog, con, auth)

}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReminderItem(
    number: Int, remindList: MutableList<DocumentSnapshot>,
    auth: FirebaseAuth, con: Context, ) {
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
                    db.collection("reminders").document("reference")
                        .delete()
                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
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
        }
    }

    AddReminder(openDialog, con, auth, remindList.get(number), remindList.get(number).id)
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddReminder(openDialogBox: MutableState<Boolean>, con: Context, auth: FirebaseAuth, remindList: DocumentSnapshot, reminderId: String) {

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

                    OutlinedTextField(value = longitude,
                        onValueChange = { longitude = it },
                        label = { Text("Longitude") },
                        placeholder = { Text("-77.0364") },
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

                    OutlinedTextField(value = latitude,
                        onValueChange = { latitude = it },
                        label = { Text("Latitude") },
                        placeholder = { Text("-77.0364") },
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
                }
            }
        }

//        ShowTimePicker(con, 0, 0)
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddNewReminder(openDialogBox: MutableState<Boolean>, con: Context, auth: FirebaseAuth) {

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

                    OutlinedTextField(value = longitude,
                        onValueChange = { longitude = it },
                        label = { Text("Longitude") },
                        placeholder = { Text("-77.0364") },
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

                    OutlinedTextField(value = latitude,
                        onValueChange = { latitude = it },
                        label = { Text("Latitude") },
                        placeholder = { Text("-77.0364") },
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
