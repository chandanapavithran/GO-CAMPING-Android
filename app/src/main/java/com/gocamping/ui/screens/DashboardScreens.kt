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
import com.gocamping.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(onLogout: () -> Unit) {
    DashboardBase("Student Portal", onLogout) {
        item {
            HeaderSection("Welcome back, Explorer!", "You have 2 active camps this week.")
        }
        item {
            DashboardCard("My Camps", Icons.Default.Terrain, "View your registered camps", ElectricPurple)
        }
        item {
            DashboardCard("Activities", Icons.Default.Flag, "Check today's schedule", BrightCyan)
        }
        item {
            DashboardCard("My Badges", Icons.Default.Star, "You earned 5 badges!", VividOrange)
        }
        item {
            DashboardCard("Safety Tips", Icons.Default.Info, "Camping essentials", SuccessGreen)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDashboardScreen(onLogout: () -> Unit) {
    DashboardBase("Staff Dashboard", onLogout) {
        item {
            HeaderSection("Staff Overview", "Manage attendance and alerts for your groups.")
        }
        item { DashboardCard("Attendance", Icons.Default.CheckCircle, "Mark student presence", SuccessGreen) }
        item { DashboardCard("Emergency Alerts", Icons.Default.Warning, "Broadcast message", ErrorRed) }
        item { DashboardCard("Group Management", Icons.Default.Groups, "View assigned students", BrightCyan) }
        item { DashboardCard("Logistics", Icons.Default.LocalShipping, "Check equipment status", ElectricPurple) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboardScreen(onLogout: () -> Unit) {
    DashboardBase("Parent Portal", onLogout) {
        item {
            HeaderSection("Child Monitoring", "Track your child's camping adventure.")
        }
        item { DashboardCard("Safety Tracker", Icons.Default.LocationOn, "Real-time location pulse", SuccessGreen) }
        item { DashboardCard("Payment History", Icons.Default.Payments, "Manage fees and invoices", VividOrange) }
        item { DashboardCard("Camp Media", Icons.Default.PhotoLibrary, "View photos of your child", SoftPink) }
        item { DashboardCard("Direct Contact", Icons.Default.ContactSupport, "Message camp lead", BrightCyan) }
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
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content
        )
    }
}

@Composable
fun HeaderSection(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(subtitle, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun DashboardCard(title: String, icon: ImageVector, description: String, iconColor: androidx.compose.ui.graphics.Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
