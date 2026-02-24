package com.gocamping.ui

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object RegisterType : Screen("register_type")
    object RegisterStudent : Screen("register_student")
    object RegisterStaff : Screen("register_staff")
    object RegisterParent : Screen("register_parent")
    object StudentDashboard : Screen("student_dashboard")
    object StaffDashboard : Screen("staff_dashboard")
    object ParentDashboard : Screen("parent_dashboard")
    object Attendance : Screen("attendance")
    object Alert : Screen("alert")
    object Payment : Screen("payment")
    object Feedback : Screen("feedback")
    object Archiving : Screen("archiving")
}
