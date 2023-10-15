package com.zareshahi.myreport.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zareshahi.myreport.navigation.Routes
import com.zareshahi.myreport.screen.AddNewReport
import com.zareshahi.myreport.screen.HomeScreen
import com.zareshahi.myreport.ui.theme.MyReportTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyReportTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        NavHost(navController = navController, startDestination = "home") {
                            composable(Routes.HOME.route) {
                                HomeScreen(navController = navController)
                            }
                            composable("${Routes.ADD_REPORT.route}?id={id}"){navBackStackEntry->
                                val id = navBackStackEntry.arguments?.getString("id")?.toLongOrNull()
                                AddNewReport(id =id,navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting() {

}
