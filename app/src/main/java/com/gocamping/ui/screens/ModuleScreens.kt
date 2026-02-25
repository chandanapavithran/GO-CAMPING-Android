package com.gocamping.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gocamping.data.*
import com.gocamping.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.gocamping.R
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.gocamping.ui.theme.CampingGreenHeader
import com.gocamping.ui.theme.CampingTextDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleScreenBase(title: String, onBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        // Bottom Wave
        Image(
            painter = painterResource(id = R.drawable.bg_wave),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillWidth
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Refined Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CampingGreenHeader)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        "GO CAMPING",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                Box(modifier = Modifier.size(48.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = CampingTextDark
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                content()
            }
        }
    }
}

// --- Attendance ---

@Composable
fun AttendanceScreen(role: String, userId: String, studentId: String?, dao: com.gocamping.data.AppDao, onBack: () -> Unit) {
    ModuleScreenBase("Attendance", onBack) {
        if (role.equals("Staff", ignoreCase = true)) {
            StaffAttendanceContent(dao)
        } else {
            // For parents and students, studentId is what matters
            ViewAttendanceContent(dao, studentId ?: userId, role)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffAttendanceContent(dao: com.gocamping.data.AppDao) {
    var students by remember { mutableStateOf<List<com.gocamping.data.User>>(emptyList()) }
    var attendanceMap by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var selectedDate by remember { mutableStateOf(java.time.LocalDate.now().toString()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        scope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val list = dao.getAllStudents()
            students = list
            // Initialize all as Present by default as per common flow
            attendanceMap = list.associate { it.id to "Present" }
        }
    }
    
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val instant = java.time.Instant.ofEpochMilli(it)
                        selectedDate = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate().toString()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Text("Attendance Marking", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CampingTextDark)
    Spacer(modifier = Modifier.height(16.dp))

    // Manual Date Entry
    OutlinedTextField(
        value = "Date: $selectedDate",
        onValueChange = { },
        readOnly = true,
        modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
        trailingIcon = { 
            IconButton(onClick = { showDatePicker = true }) {
                Icon(Icons.Default.DateRange, contentDescription = "Select Date") 
            }
        },
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.LightGray,
            unfocusedBorderColor = Color.LightGray
        ),
        enabled = false // Make the field clickable but not typeable
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    if (students.isEmpty()) {
        Text("No students found in database", color = Color.Gray)
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 400.dp)) {
            items(students) { student ->
                val currentStatus = attendanceMap[student.id] ?: "Present"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(20.dp),
                                color = Color.LightGray.copy(alpha = 0.3f)
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(8.dp), tint = Color.Gray)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(student.name, fontWeight = FontWeight.Bold, color = CampingTextDark)
                                Text("(${student.id})", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                        
                        // Custom Present/Absent Toggle
                        Row(
                            modifier = Modifier
                                .background(Color(0xFFEEEEEE), RoundedCornerShape(20.dp))
                                .padding(2.dp)
                        ) {
                            val presentColor = if (currentStatus == "Present") CampingGreenHeader else Color.Transparent
                            val presentTextColor = if (currentStatus == "Present") Color.White else Color.Gray
                            
                            val absentColor = if (currentStatus == "Absent") Color(0xFFC62828) else Color.Transparent
                            val absentTextColor = if (currentStatus == "Absent") Color.White else Color.Gray

                            Box(
                                modifier = Modifier
                                    .background(presentColor, RoundedCornerShape(20.dp))
                                    .clickable { attendanceMap = attendanceMap + (student.id to "Present") }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (currentStatus == "Present") {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text("Present", color = presentTextColor, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            
                            Box(
                                modifier = Modifier
                                    .background(absentColor, RoundedCornerShape(20.dp))
                                    .clickable { attendanceMap = attendanceMap + (student.id to "Absent") }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Absent", color = absentTextColor, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Button(
        onClick = { 
            attendanceMap = students.associate { it.id to "Present" }
        },
        modifier = Modifier.fillMaxWidth().height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text("Mark All Present", color = CampingTextDark)
    }

    Spacer(modifier = Modifier.height(12.dp))

    Button(
        onClick = { 
            scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    students.forEach { student ->
                        val record = com.gocamping.data.Attendance(
                            date = selectedDate,
                            studentId = student.id,
                            status = attendanceMap[student.id] ?: "Present"
                        )
                        dao.insertAttendance(record)
                    }
                    withContext(kotlinx.coroutines.Dispatchers.Main) {
                        message = "Attendance marked successfully"
                    }
                } catch (e: Exception) {
                    withContext(kotlinx.coroutines.Dispatchers.Main) {
                        message = "Error: ${e.message}"
                    }
                }
            }
        }, 
        modifier = Modifier.fillMaxWidth().height(56.dp),
        enabled = students.isNotEmpty(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CampingGreenHeader)
    ) {
        Text("Mark Attendance", fontWeight = FontWeight.Bold)
    }

    if (message != null) {
        Text(message!!, modifier = Modifier.padding(top = 8.dp), color = CampingGreenHeader)
    }
}

@Composable
fun ViewAttendanceContent(dao: com.gocamping.data.AppDao, studentId: String, role: String) {
    val records = remember { mutableStateListOf<com.gocamping.data.Attendance>() }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(studentId) {
        scope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val list = dao.getAttendanceForStudent(studentId)
            withContext(kotlinx.coroutines.Dispatchers.Main) {
                records.clear()
                records.addAll(list)
            }
        }
    }

    Text("Attendance Records for $studentId", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CampingTextDark)
    Spacer(modifier = Modifier.height(16.dp))
    
    if (records.isEmpty()) {
        Text("No attendance records found.", color = Color.Gray)
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.heightIn(max = 500.dp)) {
            items(records) { record ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (record.status == "Present") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(record.date, fontWeight = FontWeight.Medium, color = CampingTextDark)
                            Text("Status", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                        Text(
                            record.status, 
                            fontWeight = FontWeight.Bold,
                            color = if (record.status == "Present") Color(0xFF2E7D32) else Color(0xFFC62828),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

// --- Alerts ---

@Composable
fun AlertScreen(role: String, userId: String, dao: com.gocamping.data.AppDao, onBack: () -> Unit) {
    ModuleScreenBase("Camp Alerts", onBack) {
        if (role.equals("Staff", ignoreCase = true)) {
            StaffAlertContent(dao)
        } else {
            ViewAlertContent(dao, role)
        }
    }
}

@Composable
fun StaffAlertContent(dao: com.gocamping.data.AppDao) {
    var alertText by remember { mutableStateOf("") }
    var alertType by remember { mutableStateOf("Info") }
    var targetGroup by remember { mutableStateOf("All") }
    val scope = rememberCoroutineScope()
    
    Text("Send New Alert", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CampingTextDark)
    Spacer(modifier = Modifier.height(16.dp))
    
    OutlinedTextField(
        value = alertText,
        onValueChange = { alertText = it },
        placeholder = { Text("Message") },
        modifier = Modifier.fillMaxWidth().height(120.dp),
        shape = RoundedCornerShape(8.dp)
    )
    
    Spacer(modifier = Modifier.height(16.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Target: ", color = CampingTextDark, fontWeight = FontWeight.Medium)
        listOf("All", "Student", "Parent").forEach { group ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = targetGroup == group, 
                    onClick = { targetGroup = group },
                    colors = RadioButtonDefaults.colors(selectedColor = CampingGreenHeader)
                )
                Text(group, style = MaterialTheme.typography.bodySmall, color = CampingTextDark)
            }
        }
    }
    
    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = { 
            if (alertText.isBlank()) return@Button
            scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                val alert = com.gocamping.data.Alert(
                    date = java.time.LocalDate.now().toString(),
                    type = alertType,
                    content = alertText,
                    targetGroup = targetGroup
                )
                dao.insertAlert(alert)
            }
        }, 
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CampingGreenHeader)
    ) {
        Text("Broadcast Alert", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ViewAlertContent(dao: com.gocamping.data.AppDao, role: String) {
    val alerts = remember { mutableStateListOf<com.gocamping.data.Alert>() }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.Dispatchers.IO.let { io ->
            val list = dao.getAlertsForRole(role)
            alerts.clear()
            alerts.addAll(list)
        }
    }

    Text("Current Alerts", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CampingTextDark)
    Spacer(modifier = Modifier.height(16.dp))
    
    if (alerts.isEmpty()) {
        Text("No active alerts.", color = Color.Gray)
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.heightIn(max = 500.dp)) {
            items(alerts) { alert ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = VividOrange.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(alert.type, fontWeight = FontWeight.Bold, color = VividOrange)
                        Text(alert.content, color = CampingTextDark)
                        Text(alert.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
        }
    }
}

// --- Payments ---

@Composable
fun PaymentScreen(role: String, userId: String, studentId: String?, dao: com.gocamping.data.AppDao, onBack: () -> Unit) {
    ModuleScreenBase("Payments", onBack) {
        if (role.equals("Parent", ignoreCase = true)) {
            ParentPaymentContent(dao, userId, studentId)
        } else if (role.equals("Staff", ignoreCase = true)) {
            StaffPaymentContent(dao)
        }
    }
}

@Composable
fun ParentPaymentContent(dao: com.gocamping.data.AppDao, userId: String, initialStudentId: String?) {
    var amount by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf(initialStudentId ?: "") }
    val scope = rememberCoroutineScope()
    val payments = remember { mutableStateListOf<com.gocamping.data.Payment>() }
    
    LaunchedEffect(Unit) {
        scope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val list = dao.getPaymentsForParent(userId)
            withContext(kotlinx.coroutines.Dispatchers.Main) {
                payments.clear()
                payments.addAll(list)
            }
        }
    }

    Text("Make a Payment", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CampingTextDark)
    Spacer(modifier = Modifier.height(16.dp))
    
    OutlinedTextField(
        value = studentId,
        onValueChange = { studentId = it },
        placeholder = { Text("Student ID") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    )
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        value = amount,
        onValueChange = { amount = it },
        placeholder = { Text("Amount ($)") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    )
    
    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = { 
            if (amount.isBlank() || studentId.isBlank()) return@Button
            scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                val p = com.gocamping.data.Payment(
                    date = java.time.LocalDate.now().toString(),
                    studentId = studentId,
                    parentId = userId,
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    status = "Completed"
                )
                dao.insertPayment(p)
            }
        }, 
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CampingGreenHeader)
    ) {
        Text("Pay Now", fontWeight = FontWeight.Bold)
    }
    
    Spacer(modifier = Modifier.height(32.dp))
    Text("Payment History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CampingTextDark)
    Spacer(modifier = Modifier.height(8.dp))
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 300.dp)) {
        items(payments) { payment ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Amount: $${payment.amount}", fontWeight = FontWeight.Bold, color = CampingGreenHeader)
                        Text("For: ${payment.studentId}", style = MaterialTheme.typography.bodySmall)
                    }
                    Text(payment.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun StaffPaymentContent(dao: com.gocamping.data.AppDao) {
    val records = remember { mutableStateListOf<com.gocamping.data.Payment>() }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.Dispatchers.IO.let { 
            val list = dao.getAllActivePayments()
            records.clear()
            records.addAll(list)
        }
    }

    Text("Payment Monitoring", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CampingTextDark)
    Spacer(modifier = Modifier.height(16.dp))
    
    if (records.isEmpty()) {
        Text("No payments found.", color = Color.Gray)
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.heightIn(max = 500.dp)) {
            items(records) { record ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Student: ${record.studentId}", fontWeight = FontWeight.Bold, color = CampingTextDark)
                            Text("$${record.amount}", fontWeight = FontWeight.Bold, color = CampingGreenHeader)
                        }
                        Text("Payer (Parent ID): ${record.parentId}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Text("Date: ${record.date}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
        }
    }
}

// --- Feedback ---

@Composable
fun FeedbackScreen(role: String, userId: String, studentId: String?, dao: com.gocamping.data.AppDao, onBack: () -> Unit) {
    ModuleScreenBase("Camp Feedback", onBack) {
        if (role.equals("Student", ignoreCase = true)) {
            SubmitFeedbackContent(dao, userId)
        } else if (role.equals("Staff", ignoreCase = true)) {
            StaffFeedbackContent(dao)
        }
    }
}

@Composable
fun SubmitFeedbackContent(dao: com.gocamping.data.AppDao, userId: String) {
    var feedback by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Success") },
            text = { Text("you hv submitted feedback") },
            confirmButton = {
                Button(onClick = { showSuccessDialog = false; feedback = "" }, colors = ButtonDefaults.buttonColors(containerColor = CampingGreenHeader)) {
                    Text("OK")
                }
            }
        )
    }

    Text("Give your Feedback", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CampingTextDark)
    Spacer(modifier = Modifier.height(16.dp))
    
    OutlinedTextField(
        value = feedback,
        onValueChange = { feedback = it },
        placeholder = { Text("Your thoughts...") },
        modifier = Modifier.fillMaxWidth().height(150.dp),
        shape = RoundedCornerShape(8.dp)
    )
    
    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = { 
            if (feedback.isBlank()) return@Button
            scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                val f = com.gocamping.data.Feedback(
                    studentId = userId,
                    content = feedback,
                    date = java.time.LocalDate.now().toString()
                )
                dao.insertFeedback(f)
                withContext(kotlinx.coroutines.Dispatchers.Main) {
                    showSuccessDialog = true
                }
            }
        }, 
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CampingGreenHeader)
    ) {
        Text("Submit Feedback", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StaffFeedbackContent(dao: com.gocamping.data.AppDao) {
    val feedbackList = remember { mutableStateListOf<com.gocamping.data.Feedback>() }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.Dispatchers.IO.let { 
            val list = dao.getAllActiveFeedback()
            feedbackList.clear()
            feedbackList.addAll(list)
        }
    }

    Text("Camper Feedback", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CampingTextDark)
    Spacer(modifier = Modifier.height(16.dp))
    
    if (feedbackList.isEmpty()) {
        Text("No feedback received yet.", color = Color.Gray)
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.heightIn(max = 500.dp)) {
            items(feedbackList) { feedback ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(feedback.content, style = MaterialTheme.typography.bodyLarge, color = CampingTextDark)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp), tint = CampingGreenHeader)
                            Text(" Student ID: ${feedback.studentId}", style = MaterialTheme.typography.bodySmall, color = CampingGreenHeader, fontWeight = FontWeight.Bold)
                        }
                        Text(feedback.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
        }
    }
}

// --- Archiving ---

@Composable
fun ArchivingScreen(dao: com.gocamping.data.AppDao, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    ModuleScreenBase("Data Archiving", onBack) {
        Text("Archive Records", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CampingTextDark)
        Text("Preserve important data while cleaning up the active view.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = { 
                scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                    dao.archiveAllAttendance()
                }
            }, 
            modifier = Modifier.fillMaxWidth().height(56.dp), 
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
        ) {
            Text("Archive All Attendance", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { 
                scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                    dao.archiveAllPayments()
                }
            }, 
            modifier = Modifier.fillMaxWidth().height(56.dp), 
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
        ) {
            Text("Archive Completed Payments", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { 
                // Alerts archiving not in DFD specifically but in 'Cleanup' requirement
            }, 
            modifier = Modifier.fillMaxWidth().height(56.dp), 
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text("Clear System Cache", fontWeight = FontWeight.Bold)
        }
    }
}
