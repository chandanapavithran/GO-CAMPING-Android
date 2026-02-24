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
        onRegisterSuccess = onRegisterSuccess,
        onNavigateBack = onNavigateBack,
        dao = dao,
        role = "Staff",
        roleSpecific1 = department
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
        onRegisterSuccess = onRegisterSuccess,
        onNavigateBack = onNavigateBack,
        dao = dao,
        role = "Parent",
        roleSpecific1 = studentId
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
        onRegisterSuccess = onRegisterSuccess,
        onNavigateBack = onNavigateBack,
        dao = dao,
        role = "Student",
        roleSpecific1 = className,
        roleSpecific2 = address
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

@Composable
fun RegistrationFormBase(
    title: String,
    subtitle: String,
    palette: List<Color>,
    onRegisterSuccess: (String, String) -> Unit,
    onNavigateBack: () -> Unit,
    dao: com.gocamping.data.AppDao,
    role: String,
    roleSpecific1: String? = null,
    roleSpecific2: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = palette))
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.padding(16.dp).align(Alignment.TopStart)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
                .align(Alignment.Center),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = palette.first())
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                OutlinedTextField(value = id, onValueChange = { id = it }, label = { Text("User ID") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("Contact Number") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                
                content()
                
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { 
                        scope.launch(Dispatchers.IO) {
                            val user = com.gocamping.data.User(
                                id = id,
                                name = name,
                                role = role,
                                contactNo = contact,
                                password = password,
                                roleSpecific1 = roleSpecific1,
                                roleSpecific2 = roleSpecific2
                            )
                            dao.insertUser(user)
                            kotlinx.coroutines.withContext(Dispatchers.Main) {
                                onRegisterSuccess(role, id)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = palette.first())
                ) {
                    Text("Register Account", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
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
            shape = RoundedCornerShape(12.dp)
        )
    }

    if (roleSpecific2 != null && onRoleSpecific2Change != null) {
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = roleSpecific2,
            onValueChange = onRoleSpecific2Change,
            label = { Text(label2) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
    }
}
