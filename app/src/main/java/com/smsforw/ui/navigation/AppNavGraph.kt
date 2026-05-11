package com.smsforw.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smsforw.ui.screens.home.HomeRoute
import com.smsforw.ui.screens.rules.EditRuleRoute
import com.smsforw.ui.screens.rules.RulesRoute
import com.smsforw.ui.screens.settings.SettingsRoute

object Routes {
    const val HOME = "home"
    const val RULES = "rules"
    const val EDIT_RULE = "edit_rule"
    const val EDIT_RULE_WITH_ID = "edit_rule/{ruleId}"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeRoute(
                onNavigateToRules = { navController.navigate(Routes.RULES) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.RULES) {
            RulesRoute(
                onNavigateToAddRule = { navController.navigate(Routes.EDIT_RULE) },
                onNavigateToEditRule = { ruleId ->
                    navController.navigate("edit_rule/$ruleId")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.EDIT_RULE_WITH_ID,
            arguments = listOf(navArgument("ruleId") { type = NavType.LongType })
        ) {
            EditRuleRoute(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.EDIT_RULE) {
            EditRuleRoute(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsRoute(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
