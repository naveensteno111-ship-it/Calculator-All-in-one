package com.example.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ui.screens.AgeCalculatorScreen
import com.example.ui.screens.CagrCalculatorScreen
import com.example.ui.screens.CompoundInterestScreen
import com.example.ui.screens.CryptoStockProfitScreen
import com.example.ui.screens.CurrencyConverterScreen
import com.example.ui.screens.DateDifferenceCalculatorScreen
import com.example.ui.screens.DiscountCalculatorScreen
import com.example.ui.screens.EmiCalculatorScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.FdCalculatorScreen
import com.example.ui.screens.GstCalculatorScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.IncomeTaxCalculatorScreen
import com.example.ui.screens.InflationCalculatorScreen
import com.example.ui.screens.LumpsumCalculatorScreen
import com.example.ui.screens.MutualFundPlannerScreen
import com.example.ui.screens.NpsCalculatorScreen
import com.example.ui.screens.PercentageCalculatorScreen
import com.example.ui.screens.PpfCalculatorScreen
import com.example.ui.screens.ProfitLossCalculatorScreen
import com.example.ui.screens.RatioCalculatorScreen
import com.example.ui.screens.RdCalculatorScreen
import com.example.ui.screens.RetirementPlannerScreen
import com.example.ui.screens.SalaryCalculatorScreen
import com.example.ui.screens.ScientificCalculatorScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SimpleInterestScreen
import com.example.ui.screens.SipCalculatorScreen
import com.example.ui.screens.StatisticsCalculatorScreen
import com.example.ui.screens.SwpCalculatorScreen
import com.example.ui.screens.UnitConverterScreen
import com.example.ui.viewmodel.SmartCalcViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: SmartCalcViewModel,
    modifier: Modifier = Modifier
) {
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(250)) },
        exitTransition = { fadeOut(animationSpec = tween(250)) }
    ) {
        composable(NavRoutes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToCalculator = { calcId -> navController.navigate(calcId) },
                onNavigateToSettings = { navController.navigate(NavRoutes.SETTINGS) },
                onNavigateToHistory = { navController.navigate(NavRoutes.HISTORY) },
                onNavigateToFavorites = { navController.navigate(NavRoutes.FAVORITES) }
            )
        }

        composable(NavRoutes.FAVORITES) {
            FavoritesScreen(
                viewModel = viewModel,
                onNavigateToCalculator = { calcId -> navController.navigate(calcId) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.HISTORY) {
            HistoryScreen(
                viewModel = viewModel,
                onNavigateToCalculator = { calcId -> navController.navigate(calcId) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.SETTINGS) {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // Finance Screens
        composable(NavRoutes.EMI) {
            EmiCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.EMI),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.LOAN) {
            EmiCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.LOAN),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.SIP) {
            SipCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.SIP),
                isStepUp = false,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.STEP_UP_SIP) {
            SipCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.STEP_UP_SIP),
                isStepUp = true,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.CRYPTO_STOCK) {
            CryptoStockProfitScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.CRYPTO_STOCK),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.LUMPSUM) {
            LumpsumCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.LUMPSUM),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.MUTUAL_FUND) {
            MutualFundPlannerScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.MUTUAL_FUND),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.SWP) {
            SwpCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.SWP),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.FD) {
            FdCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.FD),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.RD) {
            RdCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.RD),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.PPF) {
            PpfCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.PPF),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.NPS) {
            NpsCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.NPS),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.CAGR) {
            CagrCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.CAGR),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.XIRR) {
            CagrCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.XIRR),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.SIMPLE_INTEREST) {
            SimpleInterestScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.SIMPLE_INTEREST),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.COMPOUND_INTEREST) {
            CompoundInterestScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.COMPOUND_INTEREST),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.RETIREMENT) {
            RetirementPlannerScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.RETIREMENT),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.INFLATION) {
            InflationCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.INFLATION),
                onBack = { navController.popBackStack() }
            )
        }

        // Tax & Business Screens
        composable(NavRoutes.GST) {
            GstCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.GST),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.INCOME_TAX) {
            IncomeTaxCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.INCOME_TAX),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.DISCOUNT) {
            DiscountCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.DISCOUNT),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.PROFIT_LOSS) {
            ProfitLossCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.PROFIT_LOSS),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.MARKUP) {
            ProfitLossCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.MARKUP),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.MARGIN) {
            ProfitLossCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.MARGIN),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.SALARY) {
            SalaryCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.SALARY),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.PERCENTAGE) {
            PercentageCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.PERCENTAGE),
                onBack = { navController.popBackStack() }
            )
        }

        // General & Tools
        composable(NavRoutes.AGE) {
            AgeCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.AGE),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.DATE_DIFF) {
            DateDifferenceCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.DATE_DIFF),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TIME_DURATION) {
            DateDifferenceCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.TIME_DURATION),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.RATIO) {
            RatioCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.RATIO),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.AVERAGE) {
            StatisticsCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.AVERAGE),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.SCIENTIFIC) {
            ScientificCalculatorScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.SCIENTIFIC),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.UNIT_CONVERTER) {
            UnitConverterScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.UNIT_CONVERTER),
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.CURRENCY) {
            CurrencyConverterScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(NavRoutes.CURRENCY),
                onBack = { navController.popBackStack() }
            )
        }
    }
}
