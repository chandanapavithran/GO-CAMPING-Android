package com.gocamping.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RegisterTypeScreen(onNavigateToRegister: (String) -> Unit, onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Join GO CAMPING", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Text("Select your role to register", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { onNavigateToRegister("Student") }, modifier = Modifier.fillMaxWidth()) {
            Text("Student Registration")
        }
        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { onNavigateToRegister("Staff") }, modifier = Modifier.fillMaxWidth()) {
            Text("Staff Registration")
        }
        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { onNavigateToRegister("Parent") }, modifier = Modifier.fillMaxWidth()) {
            Text("Parent Registration")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(onClick = onNavigateBack) {
            Text("Back to Login")
        }
    }
}

@Composable
fun StudentRegistrationScreen(onRegisterSuccess: () -> Unit) {
    var studentId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Student Registration", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = studentId, onValueChange = { studentId = it }, label = { Text("Student ID") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("Contact No") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = className, onValueChange = { className = it }, label = { Text("Class") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRegisterSuccess, modifier = Modifier.fillMaxWidth()) {
            Text("Register")
        }
    }
}
