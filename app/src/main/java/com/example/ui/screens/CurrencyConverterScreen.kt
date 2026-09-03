package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CalculationActionsBar
import com.example.ui.components.FinancialDisclaimerCard
import com.example.ui.components.PrimaryResultCard
import com.example.ui.components.ResultBreakdownItem
import com.example.ui.components.SmartNumberInput
import com.example.ui.theme.PrincipalBlue
import com.example.ui.theme.ProfitGreen
import com.example.ui.viewmodel.SmartCalcViewModel
import com.example.util.AppFormatters

data class CurrencyInfo(
    val code: String,
    val name: String,
    val symbol: String,
    val flag: String
)

val availableCurrencies = listOf(
    CurrencyInfo("USD", "US Dollar", "$", "🇺🇸"),
    CurrencyInfo("INR", "Indian Rupee", "₹", "🇮🇳"),
    CurrencyInfo("EUR", "Euro", "€", "🇪🇺"),
    CurrencyInfo("GBP", "British Pound", "£", "🇬🇧"),
    CurrencyInfo("AED", "UAE Dirham", "د.إ", "🇦🇪"),
    CurrencyInfo("CAD", "Canadian Dollar", "C$", "🇨🇦"),
    CurrencyInfo("AUD", "Australian Dollar", "A$", "🇦🇺"),
    CurrencyInfo("JPY", "Japanese Yen", "¥", "🇯🇵"),
    CurrencyInfo("CNY", "Chinese Yuan", "¥", "🇨🇳"),
    CurrencyInfo("SAR", "Saudi Riyal", "﷼", "🇸🇦"),
    CurrencyInfo("SGD", "Singapore Dollar", "S$", "🇸🇬"),
    CurrencyInfo("CHF", "Swiss Franc", "CHF", "🇨🇭"),
    CurrencyInfo("NZD", "New Zealand Dollar", "NZ$", "🇳🇿"),
    CurrencyInfo("KWD", "Kuwaiti Dinar", "KD", "🇰🇼"),
    CurrencyInfo("BHD", "Bahraini Dinar", "BD", "🇧🇭"),
    CurrencyInfo("OMR", "Omani Rial", "OMR", "🇴🇲"),
    CurrencyInfo("QAR", "Qatari Riyal", "QR", "🇶🇦"),
    CurrencyInfo("THB", "Thai Baht", "฿", "🇹🇭"),
    CurrencyInfo("MYR", "Malaysian Ringgit", "RM", "🇲🇾"),
    CurrencyInfo("KRW", "South Korean Won", "₩", "🇰🇷"),
    CurrencyInfo("BRL", "Brazilian Real", "R$", "🇧🇷"),
    CurrencyInfo("RUB", "Russian Ruble", "₽", "🇷🇺")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterScreen(
    viewModel: SmartCalcViewModel,
    isFavorite: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currencyState by viewModel.currencyState.collectAsState()

    var amountInput by remember { mutableStateOf("100") }
    var fromCode by remember { mutableStateOf("USD") }
    var toCode by remember { mutableStateOf("INR") }

    val amount = amountInput.toDoubleOrNull() ?: 0.0

    val fromRate = currencyState.rates[fromCode] ?: 1.0
    val toRate = currencyState.rates[toCode] ?: 86.85

    // Cross exchange rate calculation (Base is USD)
    val convertedAmount = if (fromRate > 0) (amount / fromRate) * toRate else 0.0
    val singleUnitRate = if (fromRate > 0) (1.0 / fromRate) * toRate else 0.0

    val fromInfo = availableCurrencies.find { it.code == fromCode } ?: CurrencyInfo(fromCode, fromCode, "", "")
    val toInfo = availableCurrencies.find { it.code == toCode } ?: CurrencyInfo(toCode, toCode, "", "")

    val shareText = "SmartCalc – Currency Converter\n" +
            "Amount: $amount ${fromInfo.code} (${fromInfo.name})\n" +
            "Converted: ${AppFormatters.formatNumber(convertedAmount)} ${toInfo.code} (${toInfo.name})\n" +
            "Exchange Rate: 1 ${fromInfo.code} = ${AppFormatters.formatNumber(singleUnitRate)} ${toInfo.code}\n" +
            "Status: ${if (currencyState.isLive) "Live Rates" else "Offline Cached Rates"}"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Currency Converter", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.fetchCurrencyRates("USD") }) {
                        if (currencyState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh live rates")
                        }
                    }
                    IconButton(onClick = { viewModel.toggleFavorite("currency") }) {
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
            // Live / Offline Status Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (currencyState.isLive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (currencyState.isLive) Icons.Default.CloudDone else Icons.Default.CloudOff,
                        contentDescription = "Status",
                        tint = if (currencyState.isLive) ProfitGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (currencyState.isLive) "Live Exchange Rates" else "Offline Cached Rates",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Tap 🔄 to update",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Amount Input
            SmartNumberInput(
                label = "Amount",
                value = amountInput,
                onValueChange = { amountInput = it },
                prefix = fromInfo.symbol
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Source & Target Selectors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CurrencySelectorDropdown(
                    label = "From Currency",
                    selectedCode = fromCode,
                    onSelected = { fromCode = it },
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        val temp = fromCode
                        fromCode = toCode
                        toCode = temp
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Swap Currency",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                CurrencySelectorDropdown(
                    label = "To Currency",
                    selectedCode = toCode,
                    onSelected = { toCode = it },
                    modifier = Modifier.weight(1f)
                )
            }

            // Actions
            CalculationActionsBar(
                onCalculate = {},
                onReset = { amountInput = "100" },
                onSave = {
                    viewModel.saveCalculation(
                        calculatorId = "currency",
                        inputSummary = "$amount $fromCode to $toCode",
                        resultSummary = "${AppFormatters.formatNumber(convertedAmount)} $toCode",
                        detailedText = shareText
                    )
                    Toast.makeText(context, "Saved to history!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            // Result Card
            PrimaryResultCard(
                title = "Converted Amount",
                primaryValue = "${toInfo.symbol} ${AppFormatters.formatNumber(convertedAmount)}",
                primaryLabel = "${toInfo.flag} ${toInfo.name} ($toCode)",
                badgeText = "1 $fromCode = ${AppFormatters.formatNumber(singleUnitRate)} $toCode",
                badgeColor = ProfitGreen,
                breakdownItems = listOf(
                    ResultBreakdownItem("Source Amount", "${fromInfo.flag} $amount $fromCode (${fromInfo.name})"),
                    ResultBreakdownItem("Exchange Rate", "1 $fromCode = ${AppFormatters.formatNumber(singleUnitRate)} $toCode", PrincipalBlue),
                    ResultBreakdownItem("Inverse Rate", "1 $toCode = ${AppFormatters.formatNumber(if (singleUnitRate > 0) 1.0 / singleUnitRate else 0.0)} $fromCode"),
                    ResultBreakdownItem("Total Converted", "${toInfo.symbol} ${AppFormatters.formatNumber(convertedAmount)} $toCode", ProfitGreen, isBold = true)
                )
            )

            // Multi-Currency Live Conversion Matrix
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Global Currencies for $amount $fromCode",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val topCurrencies = availableCurrencies.take(8)
                    topCurrencies.forEachIndexed { index, curr ->
                        val rate = currencyState.rates[curr.code] ?: 1.0
                        val valConverted = if (fromRate > 0) (amount / fromRate) * rate else 0.0

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(curr.flag, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = curr.code,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = curr.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = "${curr.symbol} ${AppFormatters.formatNumber(valConverted)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (index < topCurrencies.size - 1) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        }
                    }
                }
            }

            FinancialDisclaimerCard(customText = "Exchange rates are sourced from open exchange data APIs. Real-time bank transaction rates and foreign exchange fees may differ.")
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelectorDropdown(
    label: String,
    selectedCode: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val info = availableCurrencies.find { it.code == selectedCode } ?: CurrencyInfo(selectedCode, selectedCode, "", "")

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = "${info.flag} ${info.code}",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.bodyMedium
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                availableCurrencies.forEach { curr ->
                    DropdownMenuItem(
                        text = { Text("${curr.flag} ${curr.code} - ${curr.name}") },
                        onClick = {
                            onSelected(curr.code)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
