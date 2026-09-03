package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.FinanceCalculators
import com.example.ui.components.AmortizationScheduleView
import com.example.ui.components.CalculationActionsBar
import com.example.ui.components.ChartSlice
import com.example.ui.components.DonutPieChart
import com.example.ui.components.FinancialDisclaimerCard
import com.example.ui.components.PrimaryResultCard
import com.example.ui.components.ResultBreakdownItem
import com.example.ui.components.SegmentedOptionsPicker
import com.example.ui.components.SmartNumberInput
import com.example.ui.components.YearlyGrowthBarChart
import com.example.ui.components.YearlyInvestmentTableView
import com.example.ui.theme.InterestAmber
import com.example.ui.theme.InvestedPurple
import com.example.ui.theme.PrincipalBlue
import com.example.ui.theme.ProfitGreen
import com.example.ui.viewmodel.SmartCalcViewModel
import com.example.util.AppFormatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceCalculatorTopBar(
    title: String,
    calcId: String,
    viewModel: SmartCalcViewModel,
    isFavorite: Boolean,
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = { viewModel.toggleFavorite(calcId) }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color(0xFFFBBF24) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

// -------------------------------------------------------------
// 1. EMI CALCULATOR
// -------------------------------------------------------------
@Composable
fun EmiCalculatorScreen(
    viewModel: SmartCalcViewModel,
    isFavorite: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var loanAmount by remember { mutableStateOf("1000000") }
    var interestRate by remember { mutableStateOf("8.5") }
    var tenure by remember { mutableStateOf("5") }
    var tenureUnit by remember { mutableStateOf("Years") } // "Years" or "Months"

    val principal = loanAmount.toDoubleOrNull() ?: 0.0
    val rate = interestRate.toDoubleOrNull() ?: 0.0
    val tenureVal = tenure.toDoubleOrNull() ?: 0.0
    val tenureMonths = if (tenureUnit == "Years") (tenureVal * 12).toInt() else tenureVal.toInt()

    val emiResult = remember(principal, rate, tenureMonths) {
        FinanceCalculators.calculateEmi(principal, rate, tenureMonths)
    }

    val shareText = "SmartCalc – EMI Result\n" +
            "Loan Amount: ₹${AppFormatters.formatNumber(principal)}\n" +
            "Interest Rate: $rate%\n" +
            "Tenure: $tenure $tenureUnit\n" +
            "Monthly EMI: ${AppFormatters.formatCurrency(emiResult.monthlyEmi)}\n" +
            "Total Interest: ${AppFormatters.formatCurrency(emiResult.totalInterest)}\n" +
            "Total Payable: ${AppFormatters.formatCurrency(emiResult.totalPayable)}"

    Scaffold(
        topBar = {
            FinanceCalculatorTopBar(
                title = "EMI Calculator",
                calcId = "emi",
                viewModel = viewModel,
                isFavorite = isFavorite,
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Inputs
            SmartNumberInput(
                label = "Loan Amount",
                value = loanAmount,
                onValueChange = { loanAmount = it },
                prefix = "₹",
                sliderRange = 10000f..10000000f,
                sliderStep = 10000f,
                testTag = "input_loan_amount"
            )

            SmartNumberInput(
                label = "Interest Rate (p.a.)",
                value = interestRate,
                onValueChange = { interestRate = it },
                suffix = "%",
                sliderRange = 1f..30f,
                sliderStep = 0.1f,
                testTag = "input_interest_rate"
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmartNumberInput(
                    label = "Loan Tenure",
                    value = tenure,
                    onValueChange = { tenure = it },
                    modifier = Modifier.weight(1.5f),
                    sliderRange = 1f..30f,
                    sliderStep = 1f,
                    testTag = "input_tenure"
                )
                Column(modifier = Modifier.weight(1f).padding(top = 28.dp)) {
                    SegmentedOptionsPicker(
                        options = listOf("Years", "Months"),
                        selectedOption = tenureUnit,
                        onOptionSelected = { tenureUnit = it },
                        labelProvider = { it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Actions
            CalculationActionsBar(
                onCalculate = { /* Auto updates */ },
                onReset = {
                    loanAmount = "1000000"
                    interestRate = "8.5"
                    tenure = "5"
                    tenureUnit = "Years"
                },
                onSave = {
                    viewModel.saveCalculation(
                        calculatorId = "emi",
                        inputSummary = "₹${AppFormatters.formatNumber(principal)} @ $rate% for $tenure $tenureUnit",
                        resultSummary = "EMI: ${AppFormatters.formatCurrency(emiResult.monthlyEmi)}",
                        detailedText = shareText
                    )
                    Toast.makeText(context, "Calculation saved to history!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            // Result
            PrimaryResultCard(
                title = "Payment Summary",
                primaryValue = AppFormatters.formatCurrency(emiResult.monthlyEmi),
                primaryLabel = "Monthly EMI Amount",
                breakdownItems = listOf(
                    ResultBreakdownItem("Principal Amount", AppFormatters.formatCurrency(emiResult.totalPrincipal), PrincipalBlue),
                    ResultBreakdownItem("Total Interest Payable", AppFormatters.formatCurrency(emiResult.totalInterest), InterestAmber),
                    ResultBreakdownItem("Total Amount Payable", AppFormatters.formatCurrency(emiResult.totalPayable), isBold = true)
                ),
                chartContent = {
                    val slices = listOf(
                        ChartSlice("Principal", emiResult.totalPrincipal, PrincipalBlue),
                        ChartSlice("Interest", emiResult.totalInterest, InterestAmber)
                    )
                    DonutPieChart(
                        slices = slices,
                        centerTitle = "Total Payable",
                        centerValue = AppFormatters.formatCurrency(emiResult.totalPayable)
                    )
                }
            )

            // Amortization Schedule Table
            if (emiResult.schedule.isNotEmpty()) {
                AmortizationScheduleView(schedule = emiResult.schedule)
            }

            FinancialDisclaimerCard()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -------------------------------------------------------------
// 2. SIP CALCULATOR & STEP-UP SIP
// -------------------------------------------------------------
@Composable
fun SipCalculatorScreen(
    viewModel: SmartCalcViewModel,
    isFavorite: Boolean,
    isStepUp: Boolean = false,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var monthlyInvestment by remember { mutableStateOf("10000") }
    var expectedReturn by remember { mutableStateOf("12.0") }
    var durationYears by remember { mutableStateOf("10") }
    var stepUpPercent by remember { mutableStateOf("10.0") }

    val monthly = monthlyInvestment.toDoubleOrNull() ?: 0.0
    val rate = expectedReturn.toDoubleOrNull() ?: 0.0
    val years = durationYears.toDoubleOrNull() ?: 0.0
    val stepUp = stepUpPercent.toDoubleOrNull() ?: 0.0

    val sipResult = remember(monthly, rate, years, stepUp, isStepUp) {
        if (isStepUp) {
            FinanceCalculators.calculateStepUpSip(monthly, rate, years.toInt(), stepUp)
        } else {
            FinanceCalculators.calculateSip(monthly, rate, years)
        }
    }

    val title = if (isStepUp) "Step-up SIP Calculator" else "SIP Calculator"
    val shareText = "SmartCalc – $title Result\n" +
            "Monthly Investment: ₹${AppFormatters.formatNumber(monthly)}\n" +
            (if (isStepUp) "Annual Step-up: $stepUp%\n" else "") +
            "Expected Return: $rate% p.a.\n" +
            "Duration: $years Years\n" +
            "Total Invested: ${AppFormatters.formatCurrency(sipResult.totalInvested)}\n" +
            "Estimated Returns: ${AppFormatters.formatCurrency(sipResult.estimatedReturns)}\n" +
            "Total Maturity Value: ${AppFormatters.formatCurrency(sipResult.maturityValue)}"

    Scaffold(
        topBar = {
            FinanceCalculatorTopBar(
                title = title,
                calcId = if (isStepUp) "step_up_sip" else "sip",
                viewModel = viewModel,
                isFavorite = isFavorite,
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            SmartNumberInput(
                label = "Monthly Investment",
                value = monthlyInvestment,
                onValueChange = { monthlyInvestment = it },
                prefix = "₹",
                sliderRange = 500f..200000f,
                sliderStep = 500f
            )

            if (isStepUp) {
                SmartNumberInput(
                    label = "Annual Step-up (%)",
                    value = stepUpPercent,
                    onValueChange = { stepUpPercent = it },
                    suffix = "%",
                    sliderRange = 1f..50f,
                    sliderStep = 1f
                )
            }

            SmartNumberInput(
                label = "Expected Return Rate (p.a.)",
                value = expectedReturn,
                onValueChange = { expectedReturn = it },
                suffix = "%",
                sliderRange = 1f..30f,
                sliderStep = 0.5f
            )

            SmartNumberInput(
                label = "Investment Time Period",
                value = durationYears,
                onValueChange = { durationYears = it },
                suffix = "Years",
                sliderRange = 1f..40f,
                sliderStep = 1f
            )

            CalculationActionsBar(
                onCalculate = { },
                onReset = {
                    monthlyInvestment = "10000"
                    expectedReturn = "12.0"
                    durationYears = "10"
                    stepUpPercent = "10.0"
                },
                onSave = {
                    viewModel.saveCalculation(
                        calculatorId = if (isStepUp) "step_up_sip" else "sip",
                        inputSummary = "₹${AppFormatters.formatNumber(monthly)}/mo @ $rate% for $years yrs",
                        resultSummary = "Corpus: ${AppFormatters.formatCurrency(sipResult.maturityValue)}",
                        detailedText = shareText
                    )
                    Toast.makeText(context, "Saved to history!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            PrimaryResultCard(
                title = "Wealth Projection",
                primaryValue = AppFormatters.formatCurrency(sipResult.maturityValue),
                primaryLabel = "Total Maturity Value",
                breakdownItems = listOf(
                    ResultBreakdownItem("Invested Amount", AppFormatters.formatCurrency(sipResult.totalInvested), InvestedPurple),
                    ResultBreakdownItem("Estimated Returns", AppFormatters.formatCurrency(sipResult.estimatedReturns), ProfitGreen),
                    ResultBreakdownItem("Total Value", AppFormatters.formatCurrency(sipResult.maturityValue), isBold = true)
                ),
                chartContent = {
                    val slices = listOf(
                        ChartSlice("Invested", sipResult.totalInvested, InvestedPurple),
                        ChartSlice("Returns", sipResult.estimatedReturns, ProfitGreen)
                    )
                    DonutPieChart(
                        slices = slices,
                        centerTitle = "Total Value",
                        centerValue = AppFormatters.formatCurrency(sipResult.maturityValue)
                    )
                }
            )

            if (sipResult.yearlyBreakdown.isNotEmpty()) {
                YearlyGrowthBarChart(items = sipResult.yearlyBreakdown)
                YearlyInvestmentTableView(breakdown = sipResult.yearlyBreakdown)
            }

            FinancialDisclaimerCard(customText = "Calculated returns are estimates based on expected return rate. Mutual fund investments are subject to market risks.")
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -------------------------------------------------------------
// 3. LUMPSUM CALCULATOR
// -------------------------------------------------------------
@Composable
fun LumpsumCalculatorScreen(
    viewModel: SmartCalcViewModel,
    isFavorite: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var investmentAmount by remember { mutableStateOf("100000") }
    var expectedReturn by remember { mutableStateOf("12.0") }
    var tenureYears by remember { mutableStateOf("5") }

    val amount = investmentAmount.toDoubleOrNull() ?: 0.0
    val rate = expectedReturn.toDoubleOrNull() ?: 0.0
    val years = tenureYears.toDoubleOrNull() ?: 0.0

    val result = remember(amount, rate, years) {
        FinanceCalculators.calculateLumpsum(amount, rate, years)
    }

    val shareText = "SmartCalc – Lumpsum Result\n" +
            "Initial Investment: ₹${AppFormatters.formatNumber(amount)}\n" +
            "Expected Return: $rate% p.a.\n" +
            "Tenure: $years Years\n" +
            "Invested Amount: ${AppFormatters.formatCurrency(result.totalInvested)}\n" +
            "Est. Returns: ${AppFormatters.formatCurrency(result.estimatedReturns)}\n" +
            "Total Value: ${AppFormatters.formatCurrency(result.maturityValue)}"

    Scaffold(
        topBar = {
            FinanceCalculatorTopBar(title = "Lumpsum Calculator", calcId = "lumpsum", viewModel = viewModel, isFavorite = isFavorite, onBack = onBack)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            SmartNumberInput(label = "Investment Amount", value = investmentAmount, onValueChange = { investmentAmount = it }, prefix = "₹", sliderRange = 1000f..5000000f, sliderStep = 5000f)
            SmartNumberInput(label = "Expected Return Rate (p.a.)", value = expectedReturn, onValueChange = { expectedReturn = it }, suffix = "%", sliderRange = 1f..30f, sliderStep = 0.5f)
            SmartNumberInput(label = "Time Period", value = tenureYears, onValueChange = { tenureYears = it }, suffix = "Years", sliderRange = 1f..30f, sliderStep = 1f)

            CalculationActionsBar(
                onCalculate = {},
                onReset = { investmentAmount = "100000"; expectedReturn = "12.0"; tenureYears = "5" },
                onSave = {
                    viewModel.saveCalculation("lumpsum", "₹${AppFormatters.formatNumber(amount)} @ $rate% for $years yrs", "Final: ${AppFormatters.formatCurrency(result.maturityValue)}", shareText)
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            PrimaryResultCard(
                title = "Lumpsum Returns",
                primaryValue = AppFormatters.formatCurrency(result.maturityValue),
                primaryLabel = "Total Maturity Value",
                breakdownItems = listOf(
                    ResultBreakdownItem("Invested Amount", AppFormatters.formatCurrency(result.totalInvested), InvestedPurple),
                    ResultBreakdownItem("Estimated Returns", AppFormatters.formatCurrency(result.estimatedReturns), ProfitGreen),
                    ResultBreakdownItem("Total Value", AppFormatters.formatCurrency(result.maturityValue), isBold = true)
                ),
                chartContent = {
                    DonutPieChart(
                        slices = listOf(
                            ChartSlice("Invested", result.totalInvested, InvestedPurple),
                            ChartSlice("Returns", result.estimatedReturns, ProfitGreen)
                        ),
                        centerTitle = "Total Value",
                        centerValue = AppFormatters.formatCurrency(result.maturityValue)
                    )
                }
            )

            if (result.yearlyBreakdown.isNotEmpty()) {
                YearlyInvestmentTableView(breakdown = result.yearlyBreakdown)
            }

            FinancialDisclaimerCard()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -------------------------------------------------------------
// 4. MUTUAL FUND PLANNER (Multi-mode: SIP, Lumpsum, Step-up, SWP, CAGR)
// -------------------------------------------------------------
@Composable
fun MutualFundPlannerScreen(
    viewModel: SmartCalcViewModel,
    isFavorite: Boolean,
    onBack: () -> Unit
) {
    var mode by remember { mutableStateOf("SIP") } // SIP, Lumpsum, Step-Up, SWP

    Scaffold(
        topBar = {
            FinanceCalculatorTopBar(title = "Mutual Fund Planner", calcId = "mutual_fund", viewModel = viewModel, isFavorite = isFavorite, onBack = onBack)
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            SegmentedOptionsPicker(
                options = listOf("SIP", "Lumpsum", "Step-Up", "SWP"),
                selectedOption = mode,
                onOptionSelected = { mode = it },
                labelProvider = { it },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            when (mode) {
                "SIP" -> SipCalculatorScreen(viewModel = viewModel, isFavorite = isFavorite, isStepUp = false, onBack = onBack)
                "Lumpsum" -> LumpsumCalculatorScreen(viewModel = viewModel, isFavorite = isFavorite, onBack = onBack)
                "Step-Up" -> SipCalculatorScreen(viewModel = viewModel, isFavorite = isFavorite, isStepUp = true, onBack = onBack)
                "SWP" -> SwpCalculatorScreen(viewModel = viewModel, isFavorite = isFavorite, onBack = onBack)
            }
        }
    }
}

// -------------------------------------------------------------
// 5. SWP CALCULATOR
// -------------------------------------------------------------
@Composable
fun SwpCalculatorScreen(
    viewModel: SmartCalcViewModel,
    isFavorite: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var initialCorpus by remember { mutableStateOf("1000000") }
    var monthlyWithdrawal by remember { mutableStateOf("8000") }
    var expectedReturn by remember { mutableStateOf("8.5") }
    var durationYears by remember { mutableStateOf("10") }

    val initial = initialCorpus.toDoubleOrNull() ?: 0.0
    val withdrawal = monthlyWithdrawal.toDoubleOrNull() ?: 0.0
    val rate = expectedReturn.toDoubleOrNull() ?: 0.0
    val years = durationYears.toIntOrNull() ?: 0

    val swpResult = remember(initial, withdrawal, rate, years) {
        FinanceCalculators.calculateSwp(initial, withdrawal, rate, years)
    }

    val shareText = "SmartCalc – SWP Result\n" +
            "Initial Investment: ₹${AppFormatters.formatNumber(initial)}\n" +
            "Monthly Withdrawal: ₹${AppFormatters.formatNumber(withdrawal)}\n" +
            "Expected Return: $rate%\n" +
            "Duration: $years Years\n" +
            "Total Withdrawn: ${AppFormatters.formatCurrency(swpResult.totalWithdrawn)}\n" +
            "Remaining Corpus: ${AppFormatters.formatCurrency(swpResult.remainingCorpus)}"

    Scaffold(
        topBar = {
            FinanceCalculatorTopBar(title = "SWP Calculator", calcId = "swp", viewModel = viewModel, isFavorite = isFavorite, onBack = onBack)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            SmartNumberInput(label = "Initial Investment Corpus", value = initialCorpus, onValueChange = { initialCorpus = it }, prefix = "₹", sliderRange = 50000f..10000000f, sliderStep = 50000f)
            SmartNumberInput(label = "Monthly Withdrawal", value = monthlyWithdrawal, onValueChange = { monthlyWithdrawal = it }, prefix = "₹", sliderRange = 1000f..200000f, sliderStep = 1000f)
            SmartNumberInput(label = "Expected Return Rate (p.a.)", value = expectedReturn, onValueChange = { expectedReturn = it }, suffix = "%", sliderRange = 1f..25f, sliderStep = 0.5f)
            SmartNumberInput(label = "Duration (Years)", value = durationYears, onValueChange = { durationYears = it }, suffix = "Years", sliderRange = 1f..30f, sliderStep = 1f)

            CalculationActionsBar(
                onCalculate = {},
                onReset = { initialCorpus = "1000000"; monthlyWithdrawal = "8000"; expectedReturn = "8.5"; durationYears = "10" },
                onSave = {
                    viewModel.saveCalculation("swp", "₹${AppFormatters.formatNumber(initial)} corpus, ₹$withdrawal/mo", "Remaining: ${AppFormatters.formatCurrency(swpResult.remainingCorpus)}", shareText)
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            PrimaryResultCard(
                title = "SWP Summary",
                primaryValue = AppFormatters.formatCurrency(swpResult.remainingCorpus),
                primaryLabel = "Remaining Balance Corpus",
                breakdownItems = listOf(
                    ResultBreakdownItem("Total Investment", AppFormatters.formatCurrency(initial), InvestedPurple),
                    ResultBreakdownItem("Total Withdrawn Amount", AppFormatters.formatCurrency(swpResult.totalWithdrawn), InterestAmber),
                    ResultBreakdownItem("Total Growth Earned", AppFormatters.formatCurrency(swpResult.estimatedGrowthEarned), ProfitGreen),
                    ResultBreakdownItem("Remaining Balance", AppFormatters.formatCurrency(swpResult.remainingCorpus), isBold = true)
                )
            )

            if (swpResult.yearlyBreakdown.isNotEmpty()) {
                YearlyInvestmentTableView(breakdown = swpResult.yearlyBreakdown)
            }

            FinancialDisclaimerCard()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -------------------------------------------------------------
// 6. FD & RD CALCULATORS
// -------------------------------------------------------------
@Composable
fun FdCalculatorScreen(
    viewModel: SmartCalcViewModel,
    isFavorite: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var principalInput by remember { mutableStateOf("100000") }
    var rateInput by remember { mutableStateOf("7.0") }
    var tenureInput by remember { mutableStateOf("3") }
    var compFreq by remember { mutableStateOf("Quarterly") } // Monthly, Quarterly, Half-Yearly, Yearly

    val freqTimes = when (compFreq) {
        "Monthly" -> 12
        "Quarterly" -> 4
        "Half-Yearly" -> 2
        else -> 1
    }

    val principal = principalInput.toDoubleOrNull() ?: 0.0
    val rate = rateInput.toDoubleOrNull() ?: 0.0
    val tenure = tenureInput.toDoubleOrNull() ?: 0.0

    val result = remember(principal, rate, tenure, freqTimes) {
        FinanceCalculators.calculateFd(principal, rate, tenure, freqTimes)
    }

    val shareText = "SmartCalc – FD Result\nPrincipal: ₹${AppFormatters.formatNumber(principal)}\nInterest: $rate% ($compFreq)\nTenure: $tenure Yrs\nMaturity: ${AppFormatters.formatCurrency(result.maturityAmount)}"

    Scaffold(
        topBar = { FinanceCalculatorTopBar("FD Calculator", "fd", viewModel, isFavorite, onBack) }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp)) {
            SmartNumberInput(label = "Total Investment", value = principalInput, onValueChange = { principalInput = it }, prefix = "₹", sliderRange = 5000f..5000000f, sliderStep = 5000f)
            SmartNumberInput(label = "Rate of Interest (p.a.)", value = rateInput, onValueChange = { rateInput = it }, suffix = "%", sliderRange = 1f..15f, sliderStep = 0.1f)
            SmartNumberInput(label = "Tenure (Years)", value = tenureInput, onValueChange = { tenureInput = it }, suffix = "Years", sliderRange = 1f..15f, sliderStep = 1f)

            Text("Compounding Frequency", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
            SegmentedOptionsPicker(
                options = listOf("Monthly", "Quarterly", "Half-Yearly", "Yearly"),
                selectedOption = compFreq,
                onOptionSelected = { compFreq = it },
                labelProvider = { it }
            )

            CalculationActionsBar(
                onCalculate = {},
                onReset = { principalInput = "100000"; rateInput = "7.0"; tenureInput = "3"; compFreq = "Quarterly" },
                onSave = {
                    viewModel.saveCalculation("fd", "₹${AppFormatters.formatNumber(principal)} @ $rate% for $tenure yrs", "Maturity: ${AppFormatters.formatCurrency(result.maturityAmount)}", shareText)
                    Toast.makeText(context, "Saved to history!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            PrimaryResultCard(
                title = "FD Maturity Summary",
                primaryValue = AppFormatters.formatCurrency(result.maturityAmount),
                primaryLabel = "Total Maturity Amount",
                breakdownItems = listOf(
                    ResultBreakdownItem("Principal Amount", AppFormatters.formatCurrency(result.principal), PrincipalBlue),
                    ResultBreakdownItem("Total Interest Earned", AppFormatters.formatCurrency(result.interestEarned), ProfitGreen),
                    ResultBreakdownItem("Maturity Value", AppFormatters.formatCurrency(result.maturityAmount), isBold = true)
                ),
                chartContent = {
                    DonutPieChart(
                        slices = listOf(
                            ChartSlice("Principal", result.principal, PrincipalBlue),
                            ChartSlice("Interest", result.interestEarned, ProfitGreen)
                        ),
                        centerTitle = "Maturity",
                        centerValue = AppFormatters.formatCurrency(result.maturityAmount)
                    )
                }
            )

            FinancialDisclaimerCard()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun RdCalculatorScreen(
    viewModel: SmartCalcViewModel,
    isFavorite: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var monthlyInput by remember { mutableStateOf("5000") }
    var rateInput by remember { mutableStateOf("6.8") }
    var tenureMonthsInput by remember { mutableStateOf("36") }

    val monthly = monthlyInput.toDoubleOrNull() ?: 0.0
    val rate = rateInput.toDoubleOrNull() ?: 0.0
    val months = tenureMonthsInput.toIntOrNull() ?: 0

    val result = remember(monthly, rate, months) {
        FinanceCalculators.calculateRd(monthly, rate, months)
    }

    val shareText = "SmartCalc – RD Result\nMonthly Deposit: ₹${AppFormatters.formatNumber(monthly)}\nInterest Rate: $rate%\nTenure: $months Months\nMaturity: ${AppFormatters.formatCurrency(result.maturityAmount)}"

    Scaffold(topBar = { FinanceCalculatorTopBar("RD Calculator", "rd", viewModel, isFavorite, onBack) }) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp)) {
            SmartNumberInput(label = "Monthly Deposit Amount", value = monthlyInput, onValueChange = { monthlyInput = it }, prefix = "₹", sliderRange = 500f..200000f, sliderStep = 500f)
            SmartNumberInput(label = "Interest Rate (p.a.)", value = rateInput, onValueChange = { rateInput = it }, suffix = "%", sliderRange = 1f..15f, sliderStep = 0.1f)
            SmartNumberInput(label = "Tenure (Months)", value = tenureMonthsInput, onValueChange = { tenureMonthsInput = it }, suffix = "Months", sliderRange = 6f..120f, sliderStep = 6f)

            CalculationActionsBar(
                onCalculate = {},
                onReset = { monthlyInput = "5000"; rateInput = "6.8"; tenureMonthsInput = "36" },
                onSave = {
                    viewModel.saveCalculation("rd", "₹${AppFormatters.formatNumber(monthly)}/mo for $months mos", "Maturity: ${AppFormatters.formatCurrency(result.maturityAmount)}", shareText)
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            PrimaryResultCard(
                title = "RD Maturity Summary",
                primaryValue = AppFormatters.formatCurrency(result.maturityAmount),
                primaryLabel = "Total Maturity Amount",
                breakdownItems = listOf(
                    ResultBreakdownItem("Total Invested Amount", AppFormatters.formatCurrency(result.totalDeposit), InvestedPurple),
                    ResultBreakdownItem("Total Interest Earned", AppFormatters.formatCurrency(result.interestEarned), ProfitGreen),
                    ResultBreakdownItem("Maturity Value", AppFormatters.formatCurrency(result.maturityAmount), isBold = true)
                )
            )

            FinancialDisclaimerCard()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -------------------------------------------------------------
// 7. PPF & NPS CALCULATORS
// -------------------------------------------------------------
@Composable
fun PpfCalculatorScreen(
    viewModel: SmartCalcViewModel,
    isFavorite: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var yearlyDeposit by remember { mutableStateOf("150000") }
    var rateInput by remember { mutableStateOf("7.1") }
    var tenureYears by remember { mutableStateOf("15") }

    val yearly = yearlyDeposit.toDoubleOrNull() ?: 0.0
    val rate = rateInput.toDoubleOrNull() ?: 7.1
    val years = tenureYears.toIntOrNull() ?: 15

    val result = remember(yearly, rate, years) {
        FinanceCalculators.calculatePpf(yearly, rate, years)
    }

    val shareText = "SmartCalc – PPF Result\nYearly Investment: ₹${AppFormatters.formatNumber(yearly)}\nRate: $rate%\nTenure: $years Years\nMaturity: ${AppFormatters.formatCurrency(result.maturityAmount)}"

    Scaffold(topBar = { FinanceCalculatorTopBar("PPF Calculator", "ppf", viewModel, isFavorite, onBack) }) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp)) {
            SmartNumberInput(label = "Yearly Investment (Max ₹1.5L/yr)", value = yearlyDeposit, onValueChange = { yearlyDeposit = it }, prefix = "₹", sliderRange = 500f..150000f, sliderStep = 500f)
            SmartNumberInput(label = "Current PPF Interest Rate", value = rateInput, onValueChange = { rateInput = it }, suffix = "%", sliderRange = 5f..10f, sliderStep = 0.1f)
            SmartNumberInput(label = "Tenure (15 Yrs Standard)", value = tenureYears, onValueChange = { tenureYears = it }, suffix = "Years", sliderRange = 15f..30f, sliderStep = 5f)

            CalculationActionsBar(
                onCalculate = {},
                onReset = { yearlyDeposit = "150000"; rateInput = "7.1"; tenureYears = "15" },
                onSave = {
                    viewModel.saveCalculation("ppf", "₹${AppFormatters.formatNumber(yearly)}/yr for $years yrs", "Maturity: ${AppFormatters.formatCurrency(result.maturityAmount)}", shareText)
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            PrimaryResultCard(
                title = "PPF Maturity",
                primaryValue = AppFormatters.formatCurrency(result.maturityAmount),
                primaryLabel = "Tax-Free Maturity Value",
                breakdownItems = listOf(
                    ResultBreakdownItem("Total Investment", AppFormatters.formatCurrency(result.totalInvested), InvestedPurple),
                    ResultBreakdownItem("Total Interest Earned", AppFormatters.formatCurrency(result.totalInterest), ProfitGreen),
                    ResultBreakdownItem("Maturity Amount", AppFormatters.formatCurrency(result.maturityAmount), isBold = true)
                ),
                chartContent = {
                    DonutPieChart(
                        slices = listOf(
                            ChartSlice("Invested", result.totalInvested, InvestedPurple),
                            ChartSlice("Interest", result.totalInterest, ProfitGreen)
                        ),
                        centerTitle = "Maturity",
                        centerValue = AppFormatters.formatCurrency(result.maturityAmount)
                    )
                }
            )

            if (result.yearlyBreakdown.isNotEmpty()) {
                YearlyInvestmentTableView(breakdown = result.yearlyBreakdown)
            }

            FinancialDisclaimerCard()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun NpsCalculatorScreen(
    viewModel: SmartCalcViewModel,
    isFavorite: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var currentAgeInput by remember { mutableStateOf("25") }
    var retirementAgeInput by remember { mutableStateOf("60") }
    var monthlyInput by remember { mutableStateOf("5000") }
    var returnRateInput by remember { mutableStateOf("10.0") }
    var annuityPercentInput by remember { mutableStateOf("40") }

    val age = currentAgeInput.toIntOrNull() ?: 25
    val retAge = retirementAgeInput.toIntOrNull() ?: 60
    val monthly = monthlyInput.toDoubleOrNull() ?: 0.0
    val rate = returnRateInput.toDoubleOrNull() ?: 10.0
    val annuity = annuityPercentInput.toDoubleOrNull() ?: 40.0

    val npsResult = remember(age, retAge, monthly, rate, annuity) {
        FinanceCalculators.calculateNps(age, retAge, monthly, rate, annuity)
    }

    val shareText = "SmartCalc – NPS Result\nMonthly: ₹${AppFormatters.formatNumber(monthly)}\nTotal Corpus: ${AppFormatters.formatCurrency(npsResult.totalCorpus)}\nLump sum (60%): ${AppFormatters.formatCurrency(npsResult.lumpSumAmount)}\nMonthly Pension: ${AppFormatters.formatCurrency(npsResult.estimatedMonthlyPension)}"

    Scaffold(topBar = { FinanceCalculatorTopBar("NPS Calculator", "nps", viewModel, isFavorite, onBack) }) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmartNumberInput(label = "Current Age", value = currentAgeInput, onValueChange = { currentAgeInput = it }, modifier = Modifier.weight(1f), sliderRange = 18f..65f, sliderStep = 1f)
                SmartNumberInput(label = "Retirement Age", value = retirementAgeInput, onValueChange = { retirementAgeInput = it }, modifier = Modifier.weight(1f), sliderRange = 50f..75f, sliderStep = 1f)
            }
            SmartNumberInput(label = "Monthly Contribution", value = monthlyInput, onValueChange = { monthlyInput = it }, prefix = "₹", sliderRange = 500f..100000f, sliderStep = 500f)
            SmartNumberInput(label = "Expected Return Rate (p.a.)", value = returnRateInput, onValueChange = { returnRateInput = it }, suffix = "%", sliderRange = 5f..20f, sliderStep = 0.5f)
            SmartNumberInput(label = "Annuity Reinvestment (%) (Min 40%)", value = annuityPercentInput, onValueChange = { annuityPercentInput = it }, suffix = "%", sliderRange = 40f..100f, sliderStep = 5f)

            CalculationActionsBar(
                onCalculate = {},
                onReset = { currentAgeInput = "25"; retirementAgeInput = "60"; monthlyInput = "5000"; returnRateInput = "10.0"; annuityPercentInput = "40" },
                onSave = {
                    viewModel.saveCalculation("nps", "₹${AppFormatters.formatNumber(monthly)}/mo from age $age to $retAge", "Pension: ${AppFormatters.formatCurrency(npsResult.estimatedMonthlyPension)}/mo", shareText)
                    Toast.makeText(context, "Saved to history!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            PrimaryResultCard(
                title = "NPS Pension & Corpus",
                primaryValue = AppFormatters.formatCurrency(npsResult.estimatedMonthlyPension),
                primaryLabel = "Estimated Monthly Pension",
                breakdownItems = listOf(
                    ResultBreakdownItem("Total Investment", AppFormatters.formatCurrency(npsResult.totalInvested), InvestedPurple),
                    ResultBreakdownItem("Total Retirement Corpus", AppFormatters.formatCurrency(npsResult.totalCorpus), isBold = true),
                    ResultBreakdownItem("Lump sum Withdrawal", AppFormatters.formatCurrency(npsResult.lumpSumAmount), ProfitGreen),
                    ResultBreakdownItem("Annuity Corpus (${annuity.toInt()}%)", AppFormatters.formatCurrency(npsResult.annuityAmount), PrincipalBlue)
                )
            )

            FinancialDisclaimerCard(customText = "NPS returns and annuity rates are subject to market conditions. Results are indicative projections.")
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -------------------------------------------------------------
// 8. CAGR & XIRR CALCULATORS
// -------------------------------------------------------------
@Composable
fun CagrCalculatorScreen(
    viewModel: SmartCalcViewModel,
    isFavorite: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var initialInput by remember { mutableStateOf("50000") }
    var finalInput by remember { mutableStateOf("120000") }
    var yearsInput by remember { mutableStateOf("5") }

    val initial = initialInput.toDoubleOrNull() ?: 0.0
    val finalVal = finalInput.toDoubleOrNull() ?: 0.0
    val years = yearsInput.toDoubleOrNull() ?: 0.0

    val cagrPercent = remember(initial, finalVal, years) {
        FinanceCalculators.calculateCagr(initial, finalVal, years)
    }

    val shareText = "SmartCalc – CAGR Result\nInitial Value: ₹${AppFormatters.formatNumber(initial)}\nFinal Value: ₹${AppFormatters.formatNumber(finalVal)}\nPeriod: $years Years\nCAGR: ${AppFormatters.formatPercentage(cagrPercent)}"

    Scaffold(topBar = { FinanceCalculatorTopBar("CAGR Calculator", "cagr", viewModel, isFavorite, onBack) }) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp)) {
            SmartNumberInput(label = "Initial Investment Value", value = initialInput, onValueChange = { initialInput = it }, prefix = "₹")
            SmartNumberInput(label = "Final / Current Value", value = finalInput, onValueChange = { finalInput = it }, prefix = "₹")
            SmartNumberInput(label = "Investment Duration (Years)", value = yearsInput, onValueChange = { yearsInput = it }, suffix = "Years", sliderRange = 1f..30f, sliderStep = 0.5f)

            CalculationActionsBar(
                onCalculate = {},
                onReset = { initialInput = "50000"; finalInput = "120000"; yearsInput = "5" },
                onSave = {
                    viewModel.saveCalculation("cagr", "₹${AppFormatters.formatNumber(initial)} to ₹${AppFormatters.formatNumber(finalVal)} in $years yrs", "CAGR: ${AppFormatters.formatPercentage(cagrPercent)}", shareText)
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            PrimaryResultCard(
                title = "CAGR Analysis",
                primaryValue = AppFormatters.formatPercentage(cagrPercent),
                primaryLabel = "Compound Annual Growth Rate",
                breakdownItems = listOf(
                    ResultBreakdownItem("Absolute Return", AppFormatters.formatPercentage(if (initial > 0) ((finalVal - initial) / initial) * 100 else 0.0), ProfitGreen),
                    ResultBreakdownItem("Total Gain / Profit", AppFormatters.formatCurrency(finalVal - initial), ProfitGreen),
                    ResultBreakdownItem("CAGR (Annualized)", AppFormatters.formatPercentage(cagrPercent), isBold = true)
                )
            )

            FinancialDisclaimerCard()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -------------------------------------------------------------
// 9. SIMPLE & COMPOUND INTEREST
// -------------------------------------------------------------
@Composable
fun SimpleInterestScreen(
    viewModel: SmartCalcViewModel,
    isFavorite: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var principalInput by remember { mutableStateOf("100000") }
    var rateInput by remember { mutableStateOf("6.0") }
    var timeInput by remember { mutableStateOf("3") }

    val p = principalInput.toDoubleOrNull() ?: 0.0
    val r = rateInput.toDoubleOrNull() ?: 0.0
    val t = timeInput.toDoubleOrNull() ?: 0.0

    val (interest, total) = remember(p, r, t) {
        FinanceCalculators.calculateSimpleInterest(p, r, t)
    }

    val shareText = "SmartCalc – Simple Interest\nPrincipal: ₹${AppFormatters.formatNumber(p)}\nRate: $r%\nTime: $t Yrs\nInterest: ${AppFormatters.formatCurrency(interest)}\nTotal: ${AppFormatters.formatCurrency(total)}"

    Scaffold(topBar = { FinanceCalculatorTopBar("Simple Interest", "simple_interest", viewModel, isFavorite, onBack) }) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp)) {
            SmartNumberInput(label = "Principal Amount", value = principalInput, onValueChange = { principalInput = it }, prefix = "₹")
            SmartNumberInput(label = "Annual Interest Rate", value = rateInput, onValueChange = { rateInput = it }, suffix = "%")
            SmartNumberInput(label = "Time Period (Years)", value = timeInput, onValueChange = { timeInput = it }, suffix = "Years")

            CalculationActionsBar(
                onCalculate = {},
                onReset = { principalInput = "100000"; rateInput = "6.0"; timeInput = "3" },
                onSave = {
                    viewModel.saveCalculation("simple_interest", "₹${AppFormatters.formatNumber(p)} @ $r% for $t yrs", "Total: ${AppFormatters.formatCurrency(total)}", shareText)
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            PrimaryResultCard(
                title = "Simple Interest Calculation",
                primaryValue = AppFormatters.formatCurrency(interest),
                primaryLabel = "Total Interest Earned",
                breakdownItems = listOf(
                    ResultBreakdownItem("Principal Amount", AppFormatters.formatCurrency(p), PrincipalBlue),
                    ResultBreakdownItem("Simple Interest", AppFormatters.formatCurrency(interest), ProfitGreen),
                    ResultBreakdownItem("Total Amount Payable", AppFormatters.formatCurrency(total), isBold = true)
                )
            )

            FinancialDisclaimerCard()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CompoundInterestScreen(
    viewModel: SmartCalcViewModel,
    isFavorite: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var principalInput by remember { mutableStateOf("100000") }
    var rateInput by remember { mutableStateOf("8.0") }
    var timeInput by remember { mutableStateOf("5") }
    var freq by remember { mutableStateOf("Annually") }

    val p = principalInput.toDoubleOrNull() ?: 0.0
    val r = rateInput.toDoubleOrNull() ?: 0.0
    val t = timeInput.toDoubleOrNull() ?: 0.0
    val timesPerYear = when (freq) {
        "Monthly" -> 12
        "Quarterly" -> 4
        "Half-Yearly" -> 2
        else -> 1
    }

    val (interest, total) = remember(p, r, t, timesPerYear) {
        FinanceCalculators.calculateCompoundInterest(p, r, t, timesPerYear)
    }

    val shareText = "SmartCalc – Compound Interest\nPrincipal: ₹${AppFormatters.formatNumber(p)}\nRate: $r%\nTime: $t Yrs ($freq)\nCI: ${AppFormatters.formatCurrency(interest)}\nTotal: ${AppFormatters.formatCurrency(total)}"

    Scaffold(topBar = { FinanceCalculatorTopBar("Compound Interest", "compound_interest", viewModel, isFavorite, onBack) }) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp)) {
            SmartNumberInput(label = "Principal Amount", value = principalInput, onValueChange = { principalInput = it }, prefix = "₹")
            SmartNumberInput(label = "Annual Interest Rate", value = rateInput, onValueChange = { rateInput = it }, suffix = "%")
            SmartNumberInput(label = "Time Period (Years)", value = timeInput, onValueChange = { timeInput = it }, suffix = "Years")

            Text("Compounding Frequency", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
            SegmentedOptionsPicker(
                options = listOf("Annually", "Half-Yearly", "Quarterly", "Monthly"),
                selectedOption = freq,
                onOptionSelected = { freq = it },
                labelProvider = { it }
            )

            CalculationActionsBar(
                onCalculate = {},
                onReset = { principalInput = "100000"; rateInput = "8.0"; timeInput = "5"; freq = "Annually" },
                onSave = {
                    viewModel.saveCalculation("compound_interest", "₹${AppFormatters.formatNumber(p)} @ $r% for $t yrs", "Total: ${AppFormatters.formatCurrency(total)}", shareText)
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            PrimaryResultCard(
                title = "Compound Interest Calculation",
                primaryValue = AppFormatters.formatCurrency(total),
                primaryLabel = "Total Maturity Amount",
                breakdownItems = listOf(
                    ResultBreakdownItem("Principal Amount", AppFormatters.formatCurrency(p), PrincipalBlue),
                    ResultBreakdownItem("Compound Interest", AppFormatters.formatCurrency(interest), ProfitGreen),
                    ResultBreakdownItem("Final Maturity Value", AppFormatters.formatCurrency(total), isBold = true)
                ),
                chartContent = {
                    DonutPieChart(
                        slices = listOf(
                            ChartSlice("Principal", p, PrincipalBlue),
                            ChartSlice("Compound Interest", interest, ProfitGreen)
                        ),
                        centerTitle = "Total Value",
                        centerValue = AppFormatters.formatCurrency(total)
                    )
                }
            )

            FinancialDisclaimerCard()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -------------------------------------------------------------
// 10. RETIREMENT & INFLATION CALCULATORS
// -------------------------------------------------------------
@Composable
fun RetirementPlannerScreen(
    viewModel: SmartCalcViewModel,
    isFavorite: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var currentAgeInput by remember { mutableStateOf("30") }
    var retirementAgeInput by remember { mutableStateOf("60") }
    var monthlyExpenseInput by remember { mutableStateOf("40000") }
    var inflationRateInput by remember { mutableStateOf("6.0") }
    var currentSavingsInput by remember { mutableStateOf("500000") }
    var monthlyInvestmentInput by remember { mutableStateOf("15000") }

    val age = currentAgeInput.toIntOrNull() ?: 30
    val retAge = retirementAgeInput.toIntOrNull() ?: 60
    val expense = monthlyExpenseInput.toDoubleOrNull() ?: 40000.0
    val inflation = inflationRateInput.toDoubleOrNull() ?: 6.0
    val savings = currentSavingsInput.toDoubleOrNull() ?: 0.0
    val monthlyInv = monthlyInvestmentInput.toDoubleOrNull() ?: 0.0

    val result = remember(age, retAge, expense, inflation, savings, monthlyInv) {
        FinanceCalculators.calculateRetirement(
            currentAge = age,
            retirementAge = retAge,
            currentMonthlyExpense = expense,
            inflationRate = inflation,
            currentSavings = savings,
            monthlyInvestment = monthlyInv
        )
    }

    val isSurplus = result.shortfallOrSurplus >= 0
    val shareText = "SmartCalc – Retirement Plan\nRequired Corpus: ${AppFormatters.formatCurrency(result.requiredCorpus)}\nProjected Corpus: ${AppFormatters.formatCurrency(result.projectedCorpus)}\n${if (isSurplus) "Surplus" else "Shortfall"}: ${AppFormatters.formatCurrency(kotlin.math.abs(result.shortfallOrSurplus))}\nReq. Monthly Saving: ${AppFormatters.formatCurrency(result.requiredMonthlySavings)}"

    Scaffold(topBar = { FinanceCalculatorTopBar("Retirement Planner", "retirement", viewModel, isFavorite, onBack) }) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmartNumberInput(label = "Current Age", value = currentAgeInput, onValueChange = { currentAgeInput = it }, modifier = Modifier.weight(1f), sliderRange = 18f..65f, sliderStep = 1f)
                SmartNumberInput(label = "Retirement Age", value = retirementAgeInput, onValueChange = { retirementAgeInput = it }, modifier = Modifier.weight(1f), sliderRange = 45f..75f, sliderStep = 1f)
            }
            SmartNumberInput(label = "Current Monthly Expenses", value = monthlyExpenseInput, onValueChange = { monthlyExpenseInput = it }, prefix = "₹", sliderRange = 5000f..300000f, sliderStep = 5000f)
            SmartNumberInput(label = "Expected Inflation Rate", value = inflationRateInput, onValueChange = { inflationRateInput = it }, suffix = "%", sliderRange = 3f..12f, sliderStep = 0.5f)
            SmartNumberInput(label = "Current Existing Savings", value = currentSavingsInput, onValueChange = { currentSavingsInput = it }, prefix = "₹")
            SmartNumberInput(label = "Current Monthly Investment", value = monthlyInvestmentInput, onValueChange = { monthlyInvestmentInput = it }, prefix = "₹")

            CalculationActionsBar(
                onCalculate = {},
                onReset = { currentAgeInput = "30"; retirementAgeInput = "60"; monthlyExpenseInput = "40000"; inflationRateInput = "6.0"; currentSavingsInput = "500000"; monthlyInvestmentInput = "15000" },
                onSave = {
                    viewModel.saveCalculation("retirement", "Retire at $retAge, exp ₹$expense/mo", "Req Corpus: ${AppFormatters.formatCurrency(result.requiredCorpus)}", shareText)
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            PrimaryResultCard(
                title = "Retirement Analysis",
                primaryValue = AppFormatters.formatCurrency(result.requiredCorpus),
                primaryLabel = "Required Retirement Corpus",
                badgeText = if (isSurplus) "Surplus On Track" else "Corpus Shortfall",
                badgeColor = if (isSurplus) ProfitGreen else MaterialTheme.colorScheme.error,
                breakdownItems = listOf(
                    ResultBreakdownItem("Future Monthly Expense (at age $retAge)", AppFormatters.formatCurrency(result.futureMonthlyExpense), InterestAmber),
                    ResultBreakdownItem("Projected Corpus with Current Plan", AppFormatters.formatCurrency(result.projectedCorpus), PrincipalBlue),
                    ResultBreakdownItem(if (isSurplus) "Surplus Amount" else "Shortfall Amount", AppFormatters.formatCurrency(kotlin.math.abs(result.shortfallOrSurplus)), if (isSurplus) ProfitGreen else MaterialTheme.colorScheme.error),
                    ResultBreakdownItem("Recommended Monthly SIP", AppFormatters.formatCurrency(result.requiredMonthlySavings), isBold = true)
                )
            )

            FinancialDisclaimerCard()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun InflationCalculatorScreen(
    viewModel: SmartCalcViewModel,
    isFavorite: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var currentAmountInput by remember { mutableStateOf("100000") }
    var inflationRateInput by remember { mutableStateOf("6.0") }
    var yearsInput by remember { mutableStateOf("10") }

    val amount = currentAmountInput.toDoubleOrNull() ?: 0.0
    val rate = inflationRateInput.toDoubleOrNull() ?: 6.0
    val years = yearsInput.toDoubleOrNull() ?: 10.0

    val (futureCost, purchasingPower) = remember(amount, rate, years) {
        FinanceCalculators.calculateInflation(amount, rate, years)
    }

    val shareText = "SmartCalc – Inflation Result\nCurrent: ₹${AppFormatters.formatNumber(amount)}\nRate: $rate%\nYears: $years\nFuture Cost: ${AppFormatters.formatCurrency(futureCost)}\nPurchasing Power: ${AppFormatters.formatCurrency(purchasingPower)}"

    Scaffold(topBar = { FinanceCalculatorTopBar("Inflation Calculator", "inflation", viewModel, isFavorite, onBack) }) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp)) {
            SmartNumberInput(label = "Current Amount / Cost", value = currentAmountInput, onValueChange = { currentAmountInput = it }, prefix = "₹", sliderRange = 1000f..5000000f, sliderStep = 5000f)
            SmartNumberInput(label = "Annual Inflation Rate", value = inflationRateInput, onValueChange = { inflationRateInput = it }, suffix = "%", sliderRange = 1f..15f, sliderStep = 0.5f)
            SmartNumberInput(label = "Time Period (Years)", value = yearsInput, onValueChange = { yearsInput = it }, suffix = "Years", sliderRange = 1f..40f, sliderStep = 1f)

            CalculationActionsBar(
                onCalculate = {},
                onReset = { currentAmountInput = "100000"; inflationRateInput = "6.0"; yearsInput = "10" },
                onSave = {
                    viewModel.saveCalculation("inflation", "₹${AppFormatters.formatNumber(amount)} @ $rate% for $years yrs", "Future: ${AppFormatters.formatCurrency(futureCost)}", shareText)
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            PrimaryResultCard(
                title = "Inflation Impact",
                primaryValue = AppFormatters.formatCurrency(futureCost),
                primaryLabel = "Future Cost in ${years.toInt()} Years",
                breakdownItems = listOf(
                    ResultBreakdownItem("Current Cost Today", AppFormatters.formatCurrency(amount)),
                    ResultBreakdownItem("Future Cost Needed", AppFormatters.formatCurrency(futureCost), InterestAmber),
                    ResultBreakdownItem("Purchasing Power in ${years.toInt()} Yrs", AppFormatters.formatCurrency(purchasingPower), isBold = true)
                )
            )

            FinancialDisclaimerCard()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
