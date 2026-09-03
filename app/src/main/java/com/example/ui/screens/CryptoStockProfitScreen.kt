package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.FinanceCalculators
import com.example.ui.components.CalculationActionsBar
import com.example.ui.components.ChartSlice
import com.example.ui.components.DonutPieChart
import com.example.ui.components.FinancialDisclaimerCard
import com.example.ui.components.PrimaryResultCard
import com.example.ui.components.ProSubscriptionDialog
import com.example.ui.components.ResultBreakdownItem
import com.example.ui.components.SegmentedOptionsPicker
import com.example.ui.components.SmartNumberInput
import com.example.ui.theme.InterestAmber
import com.example.ui.theme.InvestedPurple
import com.example.ui.theme.PrincipalBlue
import com.example.ui.theme.ProfitGreen
import com.example.ui.viewmodel.SmartCalcViewModel
import com.example.util.AppFormatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoStockProfitScreen(
    viewModel: SmartCalcViewModel,
    isFavorite: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val isProActive by viewModel.isProActive.collectAsState()
    var showProDialog by remember { mutableStateOf(false) }

    var buyPriceInput by remember { mutableStateOf("1500") }
    var sellPriceInput by remember { mutableStateOf("2100") }
    var quantityInput by remember { mutableStateOf("50") }
    var buyFeeInput by remember { mutableStateOf("0.1") }
    var sellFeeInput by remember { mutableStateOf("0.1") }
    var taxPresetIndex by remember { mutableStateOf(1) } // 0: 0%, 1: 15% (STCG), 2: 20% (LTCG), 3: 30% (Crypto/VDA)

    val taxRates = listOf(0.0, 15.0, 20.0, 30.0)
    val taxLabels = listOf("0%", "15% STCG", "20% LTCG", "30% Crypto")

    val buyPrice = buyPriceInput.toDoubleOrNull() ?: 0.0
    val sellPrice = sellPriceInput.toDoubleOrNull() ?: 0.0
    val quantity = quantityInput.toDoubleOrNull() ?: 0.0
    val buyFee = buyFeeInput.toDoubleOrNull() ?: 0.0
    val sellFee = sellFeeInput.toDoubleOrNull() ?: 0.0
    val selectedTaxRate = taxRates.getOrElse(taxPresetIndex) { 15.0 }

    val result = remember(buyPrice, sellPrice, quantity, buyFee, sellFee, selectedTaxRate) {
        FinanceCalculators.calculateStockCryptoProfit(
            buyPrice = buyPrice,
            sellPrice = sellPrice,
            quantity = quantity,
            buyFeePercent = buyFee,
            sellFeePercent = sellFee,
            taxPercent = selectedTaxRate
        )
    }

    val shareText = "SmartCalc – Stock & Crypto Profit Result\n" +
            "Buy Price: ₹${AppFormatters.formatNumber(buyPrice)} × $quantity units\n" +
            "Sell Price: ₹${AppFormatters.formatNumber(sellPrice)}\n" +
            "Total Investment: ${AppFormatters.formatCurrency(result.totalInvestment)}\n" +
            "Gross Exit Value: ${AppFormatters.formatCurrency(result.grossExitValue)}\n" +
            "Total Fees: ${AppFormatters.formatCurrency(result.totalFees)}\n" +
            "Capital Gains Tax (${selectedTaxRate}%): ${AppFormatters.formatCurrency(result.capitalGainsTax)}\n" +
            "Net Profit: ${AppFormatters.formatCurrency(result.netProfit)} (${String.format("%.2f", result.netRoiPercent)}% ROI)\n" +
            "Break-Even Price: ${AppFormatters.formatCurrency(result.breakEvenPrice)}"

    if (showProDialog) {
        ProSubscriptionDialog(
            viewModel = viewModel,
            onDismiss = { showProDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Stock & Crypto Profit",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFBBF24))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "PRO",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite("crypto_stock") }) {
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Pro Status Banner if not subscribed
            if (!isProActive) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("crypto_pro_banner"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "✨ SmartCalc PRO Tool",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Subscribe for ₹199 / Year to unlock unlimited exports & premium features.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        androidx.compose.material3.Button(
                            onClick = { showProDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFBBF24),
                                contentColor = Color(0xFF0F172A)
                            )
                        ) {
                            Text("₹199 / Yr", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Input fields
            SmartNumberInput(
                label = "Buy Price per Unit / Coin",
                value = buyPriceInput,
                onValueChange = { buyPriceInput = it },
                prefix = "₹",
                sliderRange = 1f..100000f,
                sliderStep = 50f
            )

            SmartNumberInput(
                label = "Target Sell / Exit Price",
                value = sellPriceInput,
                onValueChange = { sellPriceInput = it },
                prefix = "₹",
                sliderRange = 1f..150000f,
                sliderStep = 50f
            )

            SmartNumberInput(
                label = "Quantity / Number of Shares / Coins",
                value = quantityInput,
                onValueChange = { quantityInput = it },
                suffix = "units",
                sliderRange = 1f..1000f,
                sliderStep = 1f
            )

            Text(
                text = "Tax Slab Category",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
            )

            SegmentedOptionsPicker(
                options = taxLabels,
                selectedOption = taxLabels[taxPresetIndex],
                onOptionSelected = { taxPresetIndex = taxLabels.indexOf(it).coerceAtLeast(0) },
                labelProvider = { it }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SmartNumberInput(
                        label = "Buy Fee (%)",
                        value = buyFeeInput,
                        onValueChange = { buyFeeInput = it },
                        suffix = "%"
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    SmartNumberInput(
                        label = "Sell Fee (%)",
                        value = sellFeeInput,
                        onValueChange = { sellFeeInput = it },
                        suffix = "%"
                    )
                }
            }

            // Action Buttons
            CalculationActionsBar(
                onCalculate = {},
                onReset = {
                    buyPriceInput = "1500"
                    sellPriceInput = "2100"
                    quantityInput = "50"
                    buyFeeInput = "0.1"
                    sellFeeInput = "0.1"
                    taxPresetIndex = 1
                },
                onSave = {
                    viewModel.saveCalculation(
                        calculatorId = "crypto_stock",
                        inputSummary = "Buy ₹$buyPrice, Sell ₹$sellPrice ($quantity qty)",
                        resultSummary = "Net Profit: ${AppFormatters.formatCurrency(result.netProfit)} (${String.format("%.1f", result.netRoiPercent)}%)",
                        detailedText = shareText
                    )
                    Toast.makeText(context, "Saved to history!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            // Result Summary Card
            PrimaryResultCard(
                title = "Trade & Profit Outcome",
                primaryValue = (if (result.netProfit >= 0) "+" else "") + AppFormatters.formatCurrency(result.netProfit),
                primaryLabel = "Net Profit / Loss (After Taxes & Fees)",
                badgeText = if (result.netProfit >= 0) "PROFIT" else "LOSS",
                badgeColor = if (result.netProfit >= 0) ProfitGreen else Color(0xFFEF4444),
                breakdownItems = listOf(
                    ResultBreakdownItem("Total Investment (Buy Cost + Fee)", AppFormatters.formatCurrency(result.totalInvestment), PrincipalBlue),
                    ResultBreakdownItem("Gross Exit Value", AppFormatters.formatCurrency(result.grossExitValue), InvestedPurple),
                    ResultBreakdownItem("Total Brokerage & Platform Fees", AppFormatters.formatCurrency(result.totalFees), InterestAmber),
                    ResultBreakdownItem("Capital Gains Tax (${selectedTaxRate}%)", AppFormatters.formatCurrency(result.capitalGainsTax), Color(0xFFEF4444)),
                    ResultBreakdownItem("Net Return on Investment (ROI)", String.format("%.2f%%", result.netRoiPercent), if (result.netRoiPercent >= 0) ProfitGreen else Color(0xFFEF4444), isBold = true),
                    ResultBreakdownItem("Break-Even Exit Price", AppFormatters.formatCurrency(result.breakEvenPrice), isBold = true)
                )
            )

            // Visual Breakdown Donut Chart
            if (result.totalInvestment > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                val chartSlices = listOf(
                    ChartSlice("Investment", result.totalInvestment, PrincipalBlue),
                    ChartSlice("Net Profit", result.netProfit.coerceAtLeast(0.0), ProfitGreen),
                    ChartSlice("Taxes & Fees", result.totalFees + result.capitalGainsTax, InterestAmber)
                )
                DonutPieChart(
                    slices = chartSlices,
                    centerTitle = "Net ROI",
                    centerValue = String.format("%.1f%%", result.netRoiPercent)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            FinancialDisclaimerCard(
                customText = "Taxation rules depend on your jurisdiction (e.g. Section 115BBH for VDAs/Crypto in India, STCG under Section 111A). Please consult a certified financial advisor."
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
