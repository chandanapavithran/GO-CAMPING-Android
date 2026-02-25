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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleScreenBase(title: String, onBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            content = content
        )
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

@Composable
fun StaffAttendanceContent(dao: com.gocamping.data.AppDao) {
    var students by remember { mutableStateOf<List<com.gocamping.data.User>>(emptyList()) }
    var selectedStudentId by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember { mutableStateOf("Present") }
    val scope = rememberCoroutineScope()
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        scope.launch(kotlinx.coroutines.Dispatchers.IO) {
            students = dao.getAllStudents()
        }
    }
    
    Text("Mark Today's Attendance", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(16.dp))
    
    if (students.isEmpty()) {
        Text("No students found in database", color = Color.Gray)
    } else {
        Text("Select Student:", style = MaterialTheme.typography.bodyMedium)
        LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
            items(students) { student ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(
                            if (selectedStudentId == student.id) CampingGreenHeader.copy(alpha = 0.2f) 
                            else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedStudentId = student.id },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = selectedStudentId == student.id, onClick = { selectedStudentId = student.id })
                    Text("${student.name} (${student.id})", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selectedStatus == "Present", onClick = { selectedStatus = "Present" })
        Text("Present")
        Spacer(modifier = Modifier.width(16.dp))
        RadioButton(selected = selectedStatus == "Absent", onClick = { selectedStatus = "Absent" })
        Text("Absent")
    }
    
    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = { 
            val targetId = selectedStudentId
            if (targetId == null) {
                message = "Please select a student"
                return@Button
            }
            scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    val record = com.gocamping.data.Attendance(
                        date = java.time.LocalDate.now().toString(),
                        studentId = targetId,
                        status = selectedStatus
                    )
                    dao.insertAttendance(record)
                    with(kotlinx.coroutines.Dispatchers.Main) {
                        message = "Attendance marked successfully"
                    }
                } catch (e: Exception) {
                    with(kotlinx.coroutines.Dispatchers.Main) {
                        message = "Error: ${e.message}"
                    }
                }
            }
        }, 
        modifier = Modifier.fillMaxWidth(),
        enabled = selectedStudentId != null
    ) {
        Text("Submit Attendance")
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
            with(kotlinx.coroutines.Dispatchers.Main) {
                records.clear()
                records.addAll(list)
            }
        }
    }

    Text("Attendance Records for $studentId", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(16.dp))
    
    if (records.isEmpty()) {
        Text("No attendance records found.", color = Color.Gray)
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(records) { record ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (record.status == "Present") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(record.date, fontWeight = FontWeight.Medium)
                        Text(
                            record.status, 
                            fontWeight = FontWeight.Bold,
                            color = if (record.status == "Present") Color(0xFF2E7D32) else Color(0xFFC62828)
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
        if (role == "Staff") {
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
    
    Text("Send New Alert", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(16.dp))
    
    OutlinedTextField(
        value = alertText,
        onValueChange = { alertText = it },
        label = { Text("Message") },
        modifier = Modifier.fillMaxWidth().height(120.dp)
    )
    
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Target: ")
        listOf("All", "Student", "Parent").forEach { group ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = targetGroup == group, onClick = { targetGroup = group })
                Text(group, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
    
    Button(
        onClick = { 
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Broadcast Alert")
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

    Text("Current Alerts", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(16.dp))
    
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(alerts) { alert ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = VividOrange.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(alert.type, fontWeight = FontWeight.Bold, color = VividOrange)
                    Text(alert.content)
                    Text(alert.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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
            with(kotlinx.coroutines.Dispatchers.Main) {
                payments.clear()
                payments.addAll(list)
            }
        }
    }

    Text("Make a Payment", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(16.dp))
    
    OutlinedTextField(
        value = studentId,
        onValueChange = { studentId = it },
        label = { Text("Student ID") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = amount,
        onValueChange = { amount = it },
        label = { Text("Amount ($)") },
        modifier = Modifier.fillMaxWidth()
    )
    
    Button(
        onClick = { 
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Pay Now")
    }
    
    Spacer(modifier = Modifier.height(24.dp))
    Text("Payment History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(payments) { payment ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Text("${payment.date}: $${payment.amount} for ${payment.studentId}", modifier = Modifier.padding(16.dp))
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

    Text("Payment Monitoring", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(16.dp))
    
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(records) { record ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Text("User: ${record.parentId}, Std: ${record.studentId}, Amt: $${record.amount}", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

// --- Feedback ---

@Composable
fun FeedbackScreen(role: String, userId: String, studentId: String?, dao: com.gocamping.data.AppDao, onBack: () -> Unit) {
    ModuleScreenBase("Camp Feedback", onBack) {
        if (role.equals("Student", ignoreCase = true) || role.equals("Parent", ignoreCase = true)) {
            SubmitFeedbackContent(dao, studentId ?: userId)
        } else {
            StaffFeedbackContent(dao)
        }
    }
}

@Composable
fun SubmitFeedbackContent(dao: com.gocamping.data.AppDao, userId: String) {
    var feedback by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    
    Text("Give your Feedback", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(16.dp))
    
    OutlinedTextField(
        value = feedback,
        onValueChange = { feedback = it },
        label = { Text("Your thoughts...") },
        modifier = Modifier.fillMaxWidth().height(150.dp)
    )
    
    Button(
        onClick = { 
            scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                val f = com.gocamping.data.Feedback(
                    studentId = userId,
                    content = feedback,
                    date = java.time.LocalDate.now().toString()
                )
                dao.insertFeedback(f)
            }
        }, 
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Submit Feedback")
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

    Text("Camper Feedback", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(16.dp))
    
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(feedbackList) { feedback ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(feedback.content)
                    Text("From: ${feedback.studentId} on ${feedback.date}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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
        Text("Archive Records", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Preserve important data while cleaning up the active view.", style = MaterialTheme.typography.bodyMedium)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { 
                scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                    dao.archiveAllAttendance()
                }
            }, 
            modifier = Modifier.fillMaxWidth(), 
            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
        ) {
            Text("Archive All Attendance")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = { 
                scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                    dao.archiveAllPayments()
                }
            }, 
            modifier = Modifier.fillMaxWidth(), 
            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
        ) {
            Text("Archive Completed Payments")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = { 
                // Alerts archiving not in DFD specifically but in 'Cleanup' requirement
            }, 
            modifier = Modifier.fillMaxWidth(), 
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text("Clear System Cache")
        }
    }
}
