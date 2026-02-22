package com.gocamping.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gocamping.ui.screens.*

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToDashboard = { role ->
                    when (role) {
                        "Student" -> navController.navigate(Screen.StudentDashboard.route)
                        "Staff" -> navController.navigate(Screen.StaffDashboard.route)
                        "Parent" -> navController.navigate(Screen.ParentDashboard.route)
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.RegisterType.route)
                }
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
            StudentRegistrationScreen(onRegisterSuccess = { navController.navigate(Screen.Login.route) })
        }
        // Staff and Parent reg placeholders
        composable(Screen.RegisterStaff.route) { Text("Staff Registration Coming Soon") }
        composable(Screen.RegisterParent.route) { Text("Parent Registration Coming Soon") }
        
        // Dashboard placeholders
        composable(Screen.StudentDashboard.route) { Text("Student Dashboard Coming Soon") }
        composable(Screen.StaffDashboard.route) { 
            StaffDashboardScreen(onLogout = { navController.navigate(Screen.Login.route) })
        }
        composable(Screen.ParentDashboard.route) { Text("Parent Dashboard Coming Soon") }
    }
}
