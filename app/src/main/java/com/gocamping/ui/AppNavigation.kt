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
            StudentRegistrationScreen(
                onRegisterSuccess = { navController.navigate(Screen.Login.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.RegisterStaff.route) {
            StaffRegistrationScreen(
                onRegisterSuccess = { navController.navigate(Screen.Login.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.RegisterParent.route) {
            ParentRegistrationScreen(
                onRegisterSuccess = { navController.navigate(Screen.Login.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Functional Dashboards
        composable(Screen.StudentDashboard.route) { 
            StudentDashboardScreen(onLogout = { 
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.StudentDashboard.route) { inclusive = true }
                }
            }) 
        }
        composable(Screen.StaffDashboard.route) { 
            StaffDashboardScreen(onLogout = { 
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.StaffDashboard.route) { inclusive = true }
                }
            }) 
        }
        composable(Screen.ParentDashboard.route) { 
            ParentDashboardScreen(onLogout = { 
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.ParentDashboard.route) { inclusive = true }
                }
            }) 
        }
    }
}
