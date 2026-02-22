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
fun StaffRegistrationScreen(onRegisterSuccess: () -> Unit, onNavigateBack: () -> Unit) {
    val staffPalette = listOf(Color(0xFF1A237E), Color(0xFF3F51B5)) // Professional Deep Blues
    RegistrationFormBase(
        title = "Staff Account",
        subtitle = "Professional portal for camp leaders",
        palette = staffPalette,
        onRegisterSuccess = onRegisterSuccess,
        onNavigateBack = onNavigateBack
    ) {
        // Staff specific fields if any, otherwise standard
        StandardRegistrationFields()
    }
}

@Composable
fun ParentRegistrationScreen(onRegisterSuccess: () -> Unit, onNavigateBack: () -> Unit) {
    val parentPalette = listOf(Color(0xFF006064), Color(0xFF0097A7)) // Warm Teals
    RegistrationFormBase(
        title = "Parent Account",
        subtitle = "Stay connected with your child's journey",
        palette = parentPalette,
        onRegisterSuccess = onRegisterSuccess,
        onNavigateBack = onNavigateBack
    ) {
        StandardRegistrationFields()
    }
}

@Composable
fun StudentRegistrationScreen(onRegisterSuccess: () -> Unit, onNavigateBack: () -> Unit) {
    val studentPalette = listOf(ElectricPurple, BrightCyan)
    RegistrationFormBase(
        title = "Student Account",
        subtitle = "Prepare for your next big adventure",
        palette = studentPalette,
        onRegisterSuccess = onRegisterSuccess,
        onNavigateBack = onNavigateBack
    ) {
        StandardRegistrationFields()
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
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = palette))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                title,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
            
            Spacer(modifier = Modifier.height(48.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    content()
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Button(
                        onClick = onRegisterSuccess,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = palette[0])
                    ) {
                        Text("Finish Registration", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    
                    TextButton(onClick = onNavigateBack) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun StandardRegistrationFields() {
    var id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    OutlinedTextField(
        value = id,
        onValueChange = { id = it },
        label = { Text("ID Number") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Full Name") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        value = contact,
        onValueChange = { contact = it },
        label = { Text("Contact Number") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Password") },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
}
