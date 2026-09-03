package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.TaxBusinessCalculators
import com.example.ui.components.CalculationActionsBar
import com.example.ui.components.ChartSlice
import com.example.ui.components.DonutPieChart
import com.example.ui.components.FinancialDisclaimerCard
import com.example.ui.components.PrimaryResultCard
import com.example.ui.components.ResultBreakdownItem
import com.example.ui.components.SegmentedOptionsPicker
import com.example.ui.components.SmartNumberInput
import com.example.ui.theme.InterestAmber
import com.example.ui.theme.LossRed
import com.example.ui.theme.PrincipalBlue
import com.example.ui.theme.ProfitGreen
import com.example.ui.viewmodel.SmartCalcViewModel
import com.example.util.AppFormatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxTopBar(title: String, calcId: String, viewModel: SmartCalcViewModel, isFavorite: Boolean, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
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
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
    )
}

// -------------------------------------------------------------
// 1. GST CALCULATOR
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GstCalculatorScreen(viewModel: SmartCalcViewModel, isFavorite: Boolean, onBack: () -> Unit) {
    val context = LocalContext.current
    var amountInput by remember { mutableStateOf("10000") }
    var selectedGstRate by remember { mutableStateOf("18") }
    var isExclusive by remember { mutableStateOf(true) } // true = GST Exclusive (Add GST), false = GST Inclusive (Remove GST)

    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val rate = selectedGstRate.toDoubleOrNull() ?: 18.0

    val gstResult = remember(amount, rate, isExclusive) {
        if (isExclusive) {
            TaxBusinessCalculators.calculateGstExclusive(amount, rate)
        } else {
            TaxBusinessCalculators.calculateGstInclusive(amount, rate)
        }
    }

    val shareText = "SmartCalc – GST Result\n" +
            "Type: ${if (isExclusive) "GST Exclusive (+GST)" else "GST Inclusive (Extract GST)"}\n" +
            "Amount: ₹${AppFormatters.formatNumber(amount)}\n" +
            "GST Rate: $rate%\n" +
            "Net Base: ${AppFormatters.formatCurrency(gstResult.baseAmount)}\n" +
            "GST Amount: ${AppFormatters.formatCurrency(gstResult.gstAmount)} (CGST: ${AppFormatters.formatCurrency(gstResult.cgstAmount)}, SGST: ${AppFormatters.formatCurrency(gstResult.sgstAmount)})\n" +
            "Gross Total: ${AppFormatters.formatCurrency(gstResult.totalAmount)}"

    Scaffold(topBar = { TaxTopBar("GST Calculator", "gst", viewModel, isFavorite, onBack) }) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            SegmentedOptionsPicker(
                options = listOf(true, false),
                selectedOption = isExclusive,
                onOptionSelected = { isExclusive = it },
                labelProvider = { if (it) "GST Exclusive (+)" else "GST Inclusive (-)" }
            )

            Spacer(modifier = Modifier.height(10.dp))

            SmartNumberInput(
                label = if (isExclusive) "Initial Amount" else "Total Amount (with GST)",
                value = amountInput,
                onValueChange = { amountInput = it },
                prefix = "₹",
                sliderRange = 100f..500000f,
                sliderStep = 500f
            )

            Text("Select GST Slab Rate", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp, bottom = 6.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("3", "5", "12", "18", "28").forEach { slab ->
                    val isSelected = selectedGstRate == slab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { selectedGstRate = slab }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "$slab%",
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            SmartNumberInput(
                label = "Custom GST Rate (%)",
                value = selectedGstRate,
                onValueChange = { selectedGstRate = it },
                suffix = "%"
            )

            CalculationActionsBar(
                onCalculate = {},
                onReset = { amountInput = "10000"; selectedGstRate = "18"; isExclusive = true },
                onSave = {
                    viewModel.saveCalculation("gst", "₹${AppFormatters.formatNumber(amount)} @ $rate% (${if (isExclusive) "Excl" else "Incl"})", "Total: ${AppFormatters.formatCurrency(gstResult.totalAmount)}", shareText)
                    Toast.makeText(context, "Saved to history!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            PrimaryResultCard(
                title = "GST Breakdown",
                primaryValue = AppFormatters.formatCurrency(gstResult.totalAmount),
                primaryLabel = "Total Amount",
                breakdownItems = listOf(
                    ResultBreakdownItem("Net Base Amount", AppFormatters.formatCurrency(gstResult.baseAmount), PrincipalBlue),
                    ResultBreakdownItem("Total GST ($rate%)", AppFormatters.formatCurrency(gstResult.gstAmount), InterestAmber),
                    ResultBreakdownItem("CGST (${rate / 2.0}%)", AppFormatters.formatCurrency(gstResult.cgstAmount)),
                    ResultBreakdownItem("SGST (${rate / 2.0}%)", AppFormatters.formatCurrency(gstResult.sgstAmount)),
                    ResultBreakdownItem("Gross Total Amount", AppFormatters.formatCurrency(gstResult.totalAmount), isBold = true)
                ),
                chartContent = {
                    DonutPieChart(
                        slices = listOf(
                            ChartSlice("Base Amount", gstResult.baseAmount, PrincipalBlue),
                            ChartSlice("GST Tax", gstResult.gstAmount, InterestAmber)
                        ),
                        centerTitle = "Total",
                        centerValue = AppFormatters.formatCurrency(gstResult.totalAmount)
                    )
                }
            )

            FinancialDisclaimerCard(customText = "GST calculation provides standard CGST & SGST splits for intra-state transactions. For inter-state supplies, IGST applies.")
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -------------------------------------------------------------
// 2. INCOME TAX CALCULATOR
// -------------------------------------------------------------
@Composable
fun IncomeTaxCalculatorScreen(viewModel: SmartCalcViewModel, isFavorite: Boolean, onBack: () -> Unit) {
    val context = LocalContext.current
    var grossAnnualIncome by remember { mutableStateOf("1200000") }
    var selectedRegime by remember { mutableStateOf("New") } // "New" or "Old"
    var deduction80C by remember { mutableStateOf("150000") }
    var deduction80D by remember { mutableStateOf("25000") }
    var hraExemption by remember { mutableStateOf("50000") }

    val gross = grossAnnualIncome.toDoubleOrNull() ?: 0.0
    val d80C = deduction80C.toDoubleOrNull() ?: 0.0
    val d80D = deduction80D.toDoubleOrNull() ?: 0.0
    val hra = hraExemption.toDoubleOrNull() ?: 0.0

    val taxResult = remember(gross, selectedRegime, d80C, d80D, hra) {
        if (selectedRegime == "New") {
            val standardDeduction = 75000.0
            val taxable = kotlin.math.max(0.0, gross - standardDeduction)
            TaxBusinessCalculators.calculateIncomeTaxNewRegime(taxable)
        } else {
            TaxBusinessCalculators.calculateIncomeTaxOldRegime(
                grossIncome = gross,
                deduction80C = d80C,
                deduction80D = d80D,
                hraExemption = hra,
                standardDeduction = 50000.0
            )
        }
    }

    val shareText = "SmartCalc – Income Tax ($selectedRegime Regime)\n" +
            "Gross Income: ₹${AppFormatters.formatNumber(gross)}\n" +
            "Taxable Income: ${AppFormatters.formatCurrency(taxResult.taxableIncome)}\n" +
            "Tax Payable: ${AppFormatters.formatCurrency(taxResult.taxPayable)}\n" +
            "Health & Edu Cess (4%): ${AppFormatters.formatCurrency(taxResult.cessAmount)}\n" +
            "Total Tax with Cess: ${AppFormatters.formatCurrency(taxResult.totalTaxWithCess)}\n" +
            "Effective Rate: ${AppFormatters.formatPercentage(taxResult.effectiveTaxRate)}"

    Scaffold(topBar = { TaxTopBar("Income Tax Calculator", "income_tax", viewModel, isFavorite, onBack) }) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp)) {
            SegmentedOptionsPicker(
                options = listOf("New", "Old"),
                selectedOption = selectedRegime,
                onOptionSelected = { selectedRegime = it },
                labelProvider = { "$it Tax Regime" }
            )

            Spacer(modifier = Modifier.height(10.dp))

            SmartNumberInput(label = "Gross Annual Income", value = grossAnnualIncome, onValueChange = { grossAnnualIncome = it }, prefix = "₹", sliderRange = 300000f..5000000f, sliderStep = 50000f)

            AnimatedVisibility(visible = selectedRegime == "Old") {
                Column {
                    SmartNumberInput(label = "Section 80C Deductions (Max ₹1.5L)", value = deduction80C, onValueChange = { deduction80C = it }, prefix = "₹", sliderRange = 0f..150000f, sliderStep = 10000f)
                    SmartNumberInput(label = "Section 80D Health Insurance (Max ₹1L)", value = deduction80D, onValueChange = { deduction80D = it }, prefix = "₹", sliderRange = 0f..100000f, sliderStep = 5000f)
                    SmartNumberInput(label = "HRA Exemption Claimed", value = hraExemption, onValueChange = { hraExemption = it }, prefix = "₹", sliderRange = 0f..300000f, sliderStep = 10000f)
                }
            }

            CalculationActionsBar(
                onCalculate = {},
                onReset = { grossAnnualIncome = "1200000"; selectedRegime = "New"; deduction80C = "150000"; deduction80D = "25000"; hraExemption = "50000" },
                onSave = {
                    viewModel.saveCalculation("income_tax", "Gross ₹${AppFormatters.formatNumber(gross)} ($selectedRegime Regime)", "Tax: ${AppFormatters.formatCurrency(taxResult.totalTaxWithCess)}", shareText)
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            PrimaryResultCard(
                title = "Tax Payable Summary",
                primaryValue = AppFormatters.formatCurrency(taxResult.totalTaxWithCess),
                primaryLabel = "Total Tax Liability (inc. Cess)",
                badgeText = "Effective Rate: ${AppFormatters.formatPercentage(taxResult.effectiveTaxRate)}",
                badgeColor = if (taxResult.totalTaxWithCess == 0.0) ProfitGreen else InterestAmber,
                breakdownItems = listOf(
                    ResultBreakdownItem("Gross Annual Income", AppFormatters.formatCurrency(gross)),
                    ResultBreakdownItem("Total Deductions Allowed", AppFormatters.formatCurrency(if (selectedRegime == "New") 75000.0 else taxResult.totalDeductions), ProfitGreen),
                    ResultBreakdownItem("Net Taxable Income", AppFormatters.formatCurrency(taxResult.taxableIncome), PrincipalBlue),
                    ResultBreakdownItem("Basic Income Tax", AppFormatters.formatCurrency(taxResult.taxPayable)),
                    ResultBreakdownItem("Health & Education Cess (4%)", AppFormatters.formatCurrency(taxResult.cessAmount)),
                    ResultBreakdownItem("Total Tax Payable", AppFormatters.formatCurrency(taxResult.totalTaxWithCess), isBold = true)
                )
            )

            FinancialDisclaimerCard(customText = "Calculations reflect official tax slabs for Indian FY 2024-25 / FY 2025-26 including Section 87A rebate and standard deductions.")
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -------------------------------------------------------------
// 3. DISCOUNT CALCULATOR
// -------------------------------------------------------------
@Composable
fun DiscountCalculatorScreen(viewModel: SmartCalcViewModel, isFavorite: Boolean, onBack: () -> Unit) {
    val context = LocalContext.current
    var originalPriceInput by remember { mutableStateOf("2500") }
    var discount1Input by remember { mutableStateOf("20") }
    var discount2Input by remember { mutableStateOf("5") }

    val original = originalPriceInput.toDoubleOrNull() ?: 0.0
    val d1 = discount1Input.toDoubleOrNull() ?: 0.0
    val d2 = discount2Input.toDoubleOrNull() ?: 0.0

    val (totalDiscount, finalPrice) = remember(original, d1, d2) {
        TaxBusinessCalculators.calculateDiscount(original, d1, d2)
    }

    val shareText = "SmartCalc – Discount Result\nOriginal Price: ₹${AppFormatters.formatNumber(original)}\nDiscount: $d1% + $d2%\nYou Save: ${AppFormatters.formatCurrency(totalDiscount)}\nFinal Price: ${AppFormatters.formatCurrency(finalPrice)}"

    Scaffold(topBar = { TaxTopBar("Discount Calculator", "discount", viewModel, isFavorite, onBack) }) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp)) {
            SmartNumberInput(label = "Original Price", value = originalPriceInput, onValueChange = { originalPriceInput = it }, prefix = "₹")
            SmartNumberInput(label = "Primary Discount (%)", value = discount1Input, onValueChange = { discount1Input = it }, suffix = "%", sliderRange = 0f..90f, sliderStep = 1f)
            SmartNumberInput(label = "Additional Stacked Discount (%)", value = discount2Input, onValueChange = { discount2Input = it }, suffix = "%", sliderRange = 0f..50f, sliderStep = 1f)

            CalculationActionsBar(
                onCalculate = {},
                onReset = { originalPriceInput = "2500"; discount1Input = "20"; discount2Input = "0" },
                onSave = {
                    viewModel.saveCalculation("discount", "₹${AppFormatters.formatNumber(original)} with $d1% + $d2% off", "Pay: ${AppFormatters.formatCurrency(finalPrice)} (Saved ${AppFormatters.formatCurrency(totalDiscount)})", shareText)
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            PrimaryResultCard(
                title = "Discount Summary",
                primaryValue = AppFormatters.formatCurrency(finalPrice),
                primaryLabel = "Final Discounted Price",
                badgeText = "You Save ${AppFormatters.formatCurrency(totalDiscount)}",
                badgeColor = ProfitGreen,
                breakdownItems = listOf(
                    ResultBreakdownItem("Original Price", AppFormatters.formatCurrency(original)),
                    ResultBreakdownItem("Total Money Saved", AppFormatters.formatCurrency(totalDiscount), ProfitGreen),
                    ResultBreakdownItem("Final Amount to Pay", AppFormatters.formatCurrency(finalPrice), isBold = true)
                ),
                chartContent = {
                    DonutPieChart(
                        slices = listOf(
                            ChartSlice("Final Price", finalPrice, PrincipalBlue),
                            ChartSlice("Savings", totalDiscount, ProfitGreen)
                        ),
                        centerTitle = "Final Pay",
                        centerValue = AppFormatters.formatCurrency(finalPrice)
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -------------------------------------------------------------
// 4. PROFIT & LOSS / MARKUP / MARGIN
// -------------------------------------------------------------
@Composable
fun ProfitLossCalculatorScreen(viewModel: SmartCalcViewModel, isFavorite: Boolean, onBack: () -> Unit) {
    val context = LocalContext.current
    var costPriceInput by remember { mutableStateOf("1200") }
    var sellingPriceInput by remember { mutableStateOf("1500") }

    val cp = costPriceInput.toDoubleOrNull() ?: 0.0
    val sp = sellingPriceInput.toDoubleOrNull() ?: 0.0

    val (diff, percentage, isProfit) = remember(cp, sp) {
        TaxBusinessCalculators.calculateProfitLoss(cp, sp)
    }

    val shareText = "SmartCalc – Profit & Loss\nCost Price: ₹${AppFormatters.formatNumber(cp)}\nSelling Price: ₹${AppFormatters.formatNumber(sp)}\n${if (isProfit) "Profit" else "Loss"}: ${AppFormatters.formatCurrency(diff)} (${AppFormatters.formatPercentage(percentage)})"

    Scaffold(topBar = { TaxTopBar("Profit & Loss Calculator", "profit_loss", viewModel, isFavorite, onBack) }) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp)) {
            SmartNumberInput(label = "Cost Price (CP)", value = costPriceInput, onValueChange = { costPriceInput = it }, prefix = "₹")
            SmartNumberInput(label = "Selling Price (SP)", value = sellingPriceInput, onValueChange = { sellingPriceInput = it }, prefix = "₹")

            CalculationActionsBar(
                onCalculate = {},
                onReset = { costPriceInput = "1200"; sellingPriceInput = "1500" },
                onSave = {
                    viewModel.saveCalculation("profit_loss", "CP ₹$cp, SP ₹$sp", "${if (isProfit) "Profit" else "Loss"}: ${AppFormatters.formatCurrency(diff)} (${AppFormatters.formatPercentage(percentage)})", shareText)
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            PrimaryResultCard(
                title = if (isProfit) "Profit Analysis" else "Loss Analysis",
                primaryValue = AppFormatters.formatCurrency(diff),
                primaryLabel = if (isProfit) "Total Net Profit" else "Total Net Loss",
                badgeText = "${AppFormatters.formatPercentage(percentage)} ${if (isProfit) "Profit" else "Loss"}",
                badgeColor = if (isProfit) ProfitGreen else LossRed,
                breakdownItems = listOf(
                    ResultBreakdownItem("Cost Price", AppFormatters.formatCurrency(cp)),
                    ResultBreakdownItem("Selling Price", AppFormatters.formatCurrency(sp)),
                    ResultBreakdownItem(if (isProfit) "Net Profit" else "Net Loss", AppFormatters.formatCurrency(diff), if (isProfit) ProfitGreen else LossRed),
                    ResultBreakdownItem("Margin %", AppFormatters.formatPercentage(percentage), isBold = true)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -------------------------------------------------------------
// 5. SALARY (IN-HAND) CALCULATOR
// -------------------------------------------------------------
@Composable
fun SalaryCalculatorScreen(viewModel: SmartCalcViewModel, isFavorite: Boolean, onBack: () -> Unit) {
    val context = LocalContext.current
    var annualCtcInput by remember { mutableStateOf("1200000") }
    var includePf by remember { mutableStateOf(true) }

    val ctc = annualCtcInput.toDoubleOrNull() ?: 0.0

    val salary = remember(ctc, includePf) {
        TaxBusinessCalculators.calculateSalary(annualCtc = ctc, includePf = includePf)
    }

    val shareText = "SmartCalc – In-Hand Salary Breakdown\n" +
            "Annual CTC: ₹${AppFormatters.formatNumber(ctc)}\n" +
            "Gross Monthly: ${AppFormatters.formatCurrency(salary.grossMonthly)}\n" +
            "EPF (Monthly): ${AppFormatters.formatCurrency(salary.pfEmployeeMonthly)}\n" +
            "Prof. Tax: ${AppFormatters.formatCurrency(salary.professionalTaxMonthly)}\n" +
            "Est. Monthly TDS: ${AppFormatters.formatCurrency(salary.estimatedTdsMonthly)}\n" +
            "Net In-Hand Take-Home: ${AppFormatters.formatCurrency(salary.netTakeHomeMonthly)}/month"

    Scaffold(topBar = { TaxTopBar("Salary / In-Hand Calculator", "salary", viewModel, isFavorite, onBack) }) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp)) {
            SmartNumberInput(label = "Annual CTC", value = annualCtcInput, onValueChange = { annualCtcInput = it }, prefix = "₹", sliderRange = 200000f..6000000f, sliderStep = 50000f)

            CalculationActionsBar(
                onCalculate = {},
                onReset = { annualCtcInput = "1200000"; includePf = true },
                onSave = {
                    viewModel.saveCalculation("salary", "CTC ₹${AppFormatters.formatNumber(ctc)}", "Take-home: ${AppFormatters.formatCurrency(salary.netTakeHomeMonthly)}/mo", shareText)
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            PrimaryResultCard(
                title = "Monthly Take-Home Pay",
                primaryValue = AppFormatters.formatCurrency(salary.netTakeHomeMonthly),
                primaryLabel = "Estimated Monthly In-Hand Salary",
                badgeText = "Annual In-Hand: ${AppFormatters.formatCurrency(salary.netTakeHomeMonthly * 12)}",
                badgeColor = ProfitGreen,
                breakdownItems = listOf(
                    ResultBreakdownItem("Gross Monthly Salary", AppFormatters.formatCurrency(salary.grossMonthly), isBold = true),
                    ResultBreakdownItem("Basic Salary (50%)", AppFormatters.formatCurrency(salary.basicMonthly)),
                    ResultBreakdownItem("HRA (40% of Basic)", AppFormatters.formatCurrency(salary.hraMonthly)),
                    ResultBreakdownItem("Special Allowances", AppFormatters.formatCurrency(salary.specialAllowanceMonthly)),
                    ResultBreakdownItem("Employee PF Deduction", AppFormatters.formatCurrency(salary.pfEmployeeMonthly), LossRed),
                    ResultBreakdownItem("Professional Tax", AppFormatters.formatCurrency(salary.professionalTaxMonthly), LossRed),
                    ResultBreakdownItem("Estimated Income Tax (TDS)", AppFormatters.formatCurrency(salary.estimatedTdsMonthly), InterestAmber),
                    ResultBreakdownItem("Total Monthly Deductions", AppFormatters.formatCurrency(salary.totalDeductionsMonthly), LossRed),
                    ResultBreakdownItem("Net In-Hand Salary", AppFormatters.formatCurrency(salary.netTakeHomeMonthly), ProfitGreen, isBold = true)
                )
            )

            FinancialDisclaimerCard(customText = "Estimated take-home includes standard components (Basic, HRA, PF, PT, TDS under New Regime). Specific company policies may vary.")
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -------------------------------------------------------------
// 6. PERCENTAGE CALCULATOR (5 modes)
// -------------------------------------------------------------
@Composable
fun PercentageCalculatorScreen(viewModel: SmartCalcViewModel, isFavorite: Boolean, onBack: () -> Unit) {
    val context = LocalContext.current
    var mode by remember { mutableStateOf("X% of Y") } // "X% of Y", "X is ?% of Y", "% Change", "Fraction to %"

    var inputX by remember { mutableStateOf("15") }
    var inputY by remember { mutableStateOf("2500") }

    val x = inputX.toDoubleOrNull() ?: 0.0
    val y = inputY.toDoubleOrNull() ?: 0.0

    val (resultValue, resultLabel) = remember(mode, x, y) {
        when (mode) {
            "X% of Y" -> {
                val res = (x / 100.0) * y
                Pair(AppFormatters.formatNumber(res), "$x% of $y is")
            }
            "X is ?% of Y" -> {
                val res = if (y != 0.0) (x / y) * 100.0 else 0.0
                Pair(AppFormatters.formatPercentage(res), "$x is what % of $y")
            }
            "% Change" -> {
                val diff = y - x
                val pct = if (x != 0.0) (diff / x) * 100.0 else 0.0
                Pair("${if (pct >= 0) "+" else ""}${AppFormatters.formatPercentage(pct)}", "Change from $x to $y")
            }
            else -> {
                val res = if (y != 0.0) (x / y) * 100.0 else 0.0
                Pair(AppFormatters.formatPercentage(res), "Fraction $x / $y as %")
            }
        }
    }

    val shareText = "SmartCalc – Percentage Result\nMode: $mode\nInputs: $x, $y\nResult: $resultValue ($resultLabel)"

    Scaffold(topBar = { TaxTopBar("Percentage Calculator", "percentage", viewModel, isFavorite, onBack) }) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp)) {
            SegmentedOptionsPicker(
                options = listOf("X% of Y", "X is ?% of Y", "% Change", "Fraction"),
                selectedOption = mode,
                onOptionSelected = { mode = it },
                labelProvider = { it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            when (mode) {
                "X% of Y" -> {
                    SmartNumberInput(label = "Percentage (X)", value = inputX, onValueChange = { inputX = it }, suffix = "%")
                    SmartNumberInput(label = "Total Number (Y)", value = inputY, onValueChange = { inputY = it })
                }
                "X is ?% of Y" -> {
                    SmartNumberInput(label = "Value (X)", value = inputX, onValueChange = { inputX = it })
                    SmartNumberInput(label = "Total (Y)", value = inputY, onValueChange = { inputY = it })
                }
                "% Change" -> {
                    SmartNumberInput(label = "Initial Value (X)", value = inputX, onValueChange = { inputX = it })
                    SmartNumberInput(label = "Final Value (Y)", value = inputY, onValueChange = { inputY = it })
                }
                else -> {
                    SmartNumberInput(label = "Numerator (X)", value = inputX, onValueChange = { inputX = it })
                    SmartNumberInput(label = "Denominator (Y)", value = inputY, onValueChange = { inputY = it })
                }
            }

            CalculationActionsBar(
                onCalculate = {},
                onReset = { inputX = "15"; inputY = "2500" },
                onSave = {
                    viewModel.saveCalculation("percentage", "$mode ($x, $y)", resultValue, shareText)
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            PrimaryResultCard(
                title = "Percentage Calculation",
                primaryValue = resultValue,
                primaryLabel = resultLabel
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
