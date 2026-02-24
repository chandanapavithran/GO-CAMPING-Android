package com.gocamping.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gocamping.ui.screens.*
import com.gocamping.data.AppDatabase
import com.gocamping.data.*

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val dao = database.appDao()
    
    var currentUserRole by remember { mutableStateOf("Guest") }
    var currentUserId by remember { mutableStateOf("") }

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onTimeout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToDashboard = { role, id ->
                    currentUserRole = role
                    currentUserId = id
                    when (role) {
                        "Student" -> navController.navigate(Screen.StudentDashboard.route)
                        "Staff" -> navController.navigate(Screen.StaffDashboard.route)
                        "Parent" -> navController.navigate(Screen.ParentDashboard.route)
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.RegisterType.route)
                },
                dao = dao
            )
        }
        composable(Screen.RegisterType.route) {
            RegisterTypeScreen(
                onNavigateToRegister = { role ->
                    when (role) {
                        "Student" -> navController.navigate(Screen.RegisterStudent.route)
                        "Staff" -> navController.navigate(Screen.RegisterStaff.route)
                        "Parent" -> navController.navigate(Screen.RegisterParent.route)
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.RegisterStudent.route) {
            StudentRegistrationScreen(
                onRegisterSuccess = { role, id ->
                    currentUserRole = role
                    currentUserId = id
                    navController.navigate(Screen.StudentDashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() },
                dao = dao
            )
        }
        composable(Screen.RegisterStaff.route) {
            StaffRegistrationScreen(
                onRegisterSuccess = { role, id ->
                    currentUserRole = role
                    currentUserId = id
                    navController.navigate(Screen.StaffDashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() },
                dao = dao
            )
        }
        composable(Screen.RegisterParent.route) {
            ParentRegistrationScreen(
                onRegisterSuccess = { role, id ->
                    currentUserRole = role
                    currentUserId = id
                    navController.navigate(Screen.ParentDashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() },
                dao = dao
            )
        }
        
        // Functional Dashboards
        composable(Screen.StudentDashboard.route) { 
            StudentDashboardScreen(
                onLogout = { 
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.StudentDashboard.route) { inclusive = true }
                    }
                },
                onNavigateToModule = { module ->
                    navController.navigate(module)
                }
            ) 
        }
        composable(Screen.StaffDashboard.route) { 
            StaffDashboardScreen(
                onLogout = { 
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.StaffDashboard.route) { inclusive = true }
                    }
                },
                onNavigateToModule = { module ->
                    navController.navigate(module)
                }
            ) 
        }
        composable(Screen.ParentDashboard.route) { 
            ParentDashboardScreen(
                onLogout = { 
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.ParentDashboard.route) { inclusive = true }
                    }
                },
                onNavigateToModule = { module ->
                    navController.navigate(module)
                }
            ) 
        }

        // Module Screens
        composable(Screen.Attendance.route) {
            AttendanceScreen(role = currentUserRole, userId = currentUserId, dao = dao, onBack = { navController.popBackStack() })
        }
        composable(Screen.Alert.route) {
            AlertScreen(role = currentUserRole, userId = currentUserId, dao = dao, onBack = { navController.popBackStack() })
        }
        composable(Screen.Payment.route) {
            PaymentScreen(role = currentUserRole, userId = currentUserId, dao = dao, onBack = { navController.popBackStack() })
        }
        composable(Screen.Feedback.route) {
            FeedbackScreen(role = currentUserRole, userId = currentUserId, dao = dao, onBack = { navController.popBackStack() })
        }
        composable(Screen.Archiving.route) {
            ArchivingScreen(dao = dao, onBack = { navController.popBackStack() })
        }
    }
}
