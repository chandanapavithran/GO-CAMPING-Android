package com.gocamping.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.gocamping.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.gocamping.R
import androidx.compose.ui.unit.sp
import com.gocamping.ui.theme.CampingGreenHeader
import com.gocamping.ui.theme.CampingTextDark
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterTypeScreen(onNavigateToRegister: (String) -> Unit, onNavigateBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.Center),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Join the Adventure",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Select your role to get started",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))

                RoleRegistrationButton("Student", BrightCyan, onNavigateToRegister)
                Spacer(modifier = Modifier.height(16.dp))
                RoleRegistrationButton("Staff", ElectricPurple, onNavigateToRegister)
                Spacer(modifier = Modifier.height(16.dp))
                RoleRegistrationButton("Parent", VividOrange, onNavigateToRegister)

                Spacer(modifier = Modifier.height(32.dp))
                TextButton(onClick = onNavigateBack) {
                    Text("Back to Login", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}

@Composable
fun RoleRegistrationButton(role: String, color: Color, onClick: (String) -> Unit) {
    Button(
        onClick = { onClick(role) },
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text("$role Registration", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
fun StaffRegistrationScreen(onRegisterSuccess: (String, String) -> Unit, onNavigateBack: () -> Unit, dao: com.gocamping.data.AppDao) {
    val staffPalette = listOf(Color(0xFF1A237E), Color(0xFF3F51B5))
    var department by remember { mutableStateOf("") }
    
    RegistrationFormBase(
        title = "Staff Account",
        subtitle = "Professional portal for camp leaders",
        palette = staffPalette,
        onRegisterSuccess = { onRegisterSuccess("Staff", "") }, // Adjusting to new signature
        onNavigateBack = onNavigateBack,
        dao = dao,
        role = "Staff",
        roleSpecific1 = department,
        validateSpecific = {
            if (department.isBlank()) {
                "Department cannot be empty"
            } else {
                null
            }
        }
    ) {
        StandardRegistrationFields(role = "Staff", roleSpecific1 = department, onRoleSpecific1Change = { department = it })
    }
}

@Composable
fun ParentRegistrationScreen(onRegisterSuccess: (String, String) -> Unit, onNavigateBack: () -> Unit, dao: com.gocamping.data.AppDao) {
    val parentPalette = listOf(Color(0xFF006064), Color(0xFF0097A7))
    var studentId by remember { mutableStateOf("") }
    
    RegistrationFormBase(
        title = "Parent Account",
        subtitle = "Stay connected with your child's journey",
        palette = parentPalette,
        onRegisterSuccess = { onRegisterSuccess("Parent", "") }, // Adjusting to new signature
        onNavigateBack = onNavigateBack,
        dao = dao,
        role = "Parent",
        roleSpecific1 = studentId,
        validateSpecific = {
            if (studentId.isBlank()) {
                "Child's Student ID cannot be empty"
            } else {
                val student = dao.getStudentById(studentId)
                if (student == null) {
                    "Child with ID $studentId does not exist"
                } else {
                    null
                }
            }
        }
    ) {
        StandardRegistrationFields(role = "Parent", roleSpecific1 = studentId, onRoleSpecific1Change = { studentId = it })
    }
}

@Composable
fun StudentRegistrationScreen(onRegisterSuccess: (String, String) -> Unit, onNavigateBack: () -> Unit, dao: com.gocamping.data.AppDao) {
    val studentPalette = listOf(ElectricPurple, BrightCyan)
    var className by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    
    RegistrationFormBase(
        title = "Student Account",
        subtitle = "Prepare for your next big adventure",
        palette = studentPalette,
        onRegisterSuccess = { onRegisterSuccess("Student", "") }, // Adjusting to new signature
        onNavigateBack = onNavigateBack,
        dao = dao,
        role = "Student",
        roleSpecific1 = className,
        roleSpecific2 = address,
        validateSpecific = {
            if (className.isBlank()) {
                "Class cannot be empty"
            } else if (address.isBlank()) {
                "Address cannot be empty"
            } else {
                null
            }
        }
    ) {
        StandardRegistrationFields(
            role = "Student", 
            roleSpecific1 = className, 
            onRoleSpecific1Change = { className = it },
            roleSpecific2 = address,
            onRoleSpecific2Change = { address = it }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationFormBase(
    title: String,
    subtitle: String,
    palette: List<Color>,
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    dao: com.gocamping.data.AppDao,
    role: String,
    roleSpecific1: String? = null,
    roleSpecific2: String? = null,
    validateSpecific: (suspend () -> String?)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { /* Don't dismiss by clicking outside */ },
            title = { Text("Registration Successful") },
            text = { Text("you hv created the account") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onRegisterSuccess()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Bottom Wave (Moved to background)
        Image(
            painter = painterResource(id = R.drawable.bg_wave),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillWidth
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CampingGreenHeader)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
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
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = CampingTextDark
                    )
                )
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = CampingTextDark.copy(alpha = 0.7f))
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                OutlinedTextField(
                    value = id,
                    onValueChange = { id = it; errorMessage = null },
                    label = { Text("User ID") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = null },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it; errorMessage = null },
                    label = { Text("Contact Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                
                content()
                
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { 
                        val trimmedId = id.trim()
                        if (trimmedId.isBlank() || name.isBlank() || contact.isBlank() || password.isBlank()) {
                            errorMessage = "All fields are required"
                            return@Button
                        }
                        
                        scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                            try {
                                // Check if user exists
                                val existingUser = dao.getUserById(trimmedId)
                                if (existingUser != null) {
                                    with(kotlinx.coroutines.Dispatchers.Main) {
                                        errorMessage = "Account with this ID already exists"
                                    }
                                    return@launch
                                }

                                // Role specific validation
                                val specificError = validateSpecific?.invoke()
                                if (specificError != null) {
                                    with(kotlinx.coroutines.Dispatchers.Main) {
                                        errorMessage = specificError
                                    }
                                    return@launch
                                }

                                val user = com.gocamping.data.User(
                                    id = trimmedId,
                                    name = name,
                                    role = role,
                                    contactNo = contact,
                                    password = password,
                                    roleSpecific1 = roleSpecific1,
                                    roleSpecific2 = roleSpecific2
                                )
                                dao.insertUser(user)
                                with(kotlinx.coroutines.Dispatchers.Main) {
                                    showSuccessDialog = true
                                }
                            } catch (e: Exception) {
                                with(kotlinx.coroutines.Dispatchers.Main) {
                                    errorMessage = "Error: ${e.message}"
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CampingGreenHeader)
                ) {
                    Text("Register Account", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onNavigateBack) {
                    Text("Already have an account? Login", color = CampingTextDark.copy(alpha = 0.7f))
                }
                Spacer(modifier = Modifier.height(100.dp)) // Padding for wave
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardRegistrationFields(
    role: String,
    roleSpecific1: String?,
    onRoleSpecific1Change: (String) -> Unit,
    roleSpecific2: String? = null,
    onRoleSpecific2Change: ((String) -> Unit)? = null
) {
    val label1 = when(role) {
        "Student" -> "Class"
        "Staff" -> "Department"
        "Parent" -> "Child\'s Student ID"
        else -> ""
    }
    
    val label2 = if (role == "Student") "Address" else ""

    Spacer(modifier = Modifier.height(12.dp))
    if(roleSpecific1 != null) {
        OutlinedTextField(
            value = roleSpecific1,
            onValueChange = onRoleSpecific1Change,
            label = { Text(label1) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray
            )
        )
    }

    if (roleSpecific2 != null && onRoleSpecific2Change != null) {
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = roleSpecific2,
            onValueChange = onRoleSpecific2Change,
            label = { Text(label2) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray
            )
        )
    }
}
