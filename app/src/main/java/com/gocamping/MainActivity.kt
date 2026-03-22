package com.gocamping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gocamping.ui.AppNavigation
import com.gocamping.ui.theme.CampingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = androidx.compose.ui.platform.LocalContext.current
            androidx.compose.runtime.LaunchedEffect(Unit) {
                val db = com.gocamping.data.AppDatabase.getDatabase(context)
                val dao = db.appDao()
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    // Seed a student user if not exists
                    if (dao.getUserById("STUDENT001") == null) {
                        dao.insertUser(com.gocamping.data.User(
                            id = "STUDENT001",
                            name = "Test Student",
                            role = "Student",
                            contactNo = "1234567890",
                            password = "password",
                            roleSpecific1 = "Class 10",
                            roleSpecific2 = "Main St"
                        ))
                    }
                    // Seed a staff user if not exists
                    if (dao.getUserById("STAFF001") == null) {
                        dao.insertUser(com.gocamping.data.User(
                            id = "STAFF001",
                            name = "Test Staff",
                            role = "Staff",
                            contactNo = "0987654321",
                            password = "password",
                            roleSpecific1 = "Management"
                        ))
                    }
                }
            }
            CampingTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = androidx.compose.ui.graphics.Color.White
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding).fillMaxSize().androidx.compose.foundation.background(androidx.compose.ui.graphics.Color.White)) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Welcome to $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CampingTheme {
        Greeting("Go Camping")
    }
}
