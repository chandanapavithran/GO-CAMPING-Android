package com.gocamping.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable
import com.gocamping.ui.theme.*
import com.gocamping.ui.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(onLogout: () -> Unit, onNavigateToModule: (String) -> Unit) {
    DashboardBase("Student Portal", onLogout) {
        item { HeaderSection("Welcome Camper!", "Track your journey and stay updated") }
        
        item {
            DashboardCard(
                "My Attendance",
                Icons.Default.CheckCircle,
                "Check your daily presence records",
                SuccessGreen
            ) { onNavigateToModule(Screen.Attendance.route) }
        }
        item {
            DashboardCard(
                "Camp Alerts",
                Icons.Default.Notifications,
                "Stay updated with latest announcements",
                VividOrange
            ) { onNavigateToModule(Screen.Alert.route) }
        }
        item {
            DashboardCard(
                "Give Feedback",
                Icons.Default.Email,
                "Share your experience with us",
                ElectricPurple
            ) { onNavigateToModule(Screen.Feedback.route) }
        }
    }
}

@Composable
fun StaffDashboardScreen(onLogout: () -> Unit, onNavigateToModule: (String) -> Unit) {
    DashboardBase("Staff Command Center", onLogout) {
        item { HeaderSection("Camp Leader Dashboard", "Manage operations and safety") }
        
        item {
            DashboardCard(
                "Mark Attendance",
                Icons.Default.Edit,
                "Record daily student presence",
                BrightCyan
            ) { onNavigateToModule(Screen.Attendance.route) }
        }
        item {
            DashboardCard(
                "Monitor Payments",
                Icons.Default.ShoppingCart,
                "Track student and parent transactions",
                SuccessGreen
            ) { onNavigateToModule(Screen.Payment.route) }
        }
        item {
            DashboardCard(
                "Send Alerts",
                Icons.Default.Warning,
                "Broadcast important messages",
                ErrorRed
            ) { onNavigateToModule(Screen.Alert.route) }
        }
        item {
            DashboardCard(
                "Review Feedback",
                Icons.Default.Email,
                "Analyze camper thoughts and suggestions",
                ElectricPurple
            ) { onNavigateToModule(Screen.Feedback.route) }
        }
        item {
            DashboardCard(
                "Data Archiving",
                Icons.Default.CloudSync,
                "Manage historical records and cleanup",
                Color.Gray
            ) { onNavigateToModule(Screen.Archiving.route) }
        }
    }
}

@Composable
fun ParentDashboardScreen(onLogout: () -> Unit, onNavigateToModule: (String) -> Unit) {
    DashboardBase("Parent Portal", onLogout) {
        item { HeaderSection("Stay Connected", "Monitor your child's camp activities") }
        
        item {
            DashboardCard(
                "Attendance Tracker",
                Icons.Default.CheckCircle,
                "View child's attendance records",
                SuccessGreen
            ) { onNavigateToModule(Screen.Attendance.route) }
        }
        item {
            DashboardCard(
                "Camp Payments",
                Icons.Default.Payments,
                "Make and track camp-related payments",
                BrightCyan
            ) { onNavigateToModule(Screen.Payment.route) }
        }
        item {
            DashboardCard(
                "Safety Alerts",
                Icons.Default.Notifications,
                "Receive urgent updates and reminders",
                VividOrange
            ) { onNavigateToModule(Screen.Alert.route) }
        }
        item {
            DashboardCard(
                "Contact/Feedback",
                Icons.Default.Email,
                "Reach out to camp organizers",
                ElectricPurple
            ) { onNavigateToModule(Screen.Feedback.route) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBase(title: String, onLogout: () -> Unit, content: LazyListScope.() -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        // Using Default.ExitToApp to match the existing code style and avoid import issues
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content
        )
    }
}

@Composable
fun HeaderSection(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun DashboardCard(title: String, icon: ImageVector, description: String, iconColor: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = iconColor.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
