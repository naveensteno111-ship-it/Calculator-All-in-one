package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.domain.UnitCategory
import com.example.domain.UnitConverterEngine
import com.example.domain.UnitItem
import com.example.ui.components.CalculationActionsBar
import com.example.ui.components.PrimaryResultCard
import com.example.ui.components.ResultBreakdownItem
import com.example.ui.components.SmartNumberInput
import com.example.ui.theme.PrincipalBlue
import com.example.ui.theme.ProfitGreen
import com.example.ui.viewmodel.SmartCalcViewModel
import com.example.util.AppFormatters

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun UnitConverterScreen(
    viewModel: SmartCalcViewModel,
    isFavorite: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf(UnitCategory.LENGTH) }
    var inputValue by remember { mutableStateOf("1") }

    val currentUnits = UnitConverterEngine.categories[selectedCategory] ?: emptyList()
    var fromUnit by remember(selectedCategory) { mutableStateOf(currentUnits.firstOrNull() ?: UnitItem("Meter", "m", 1.0)) }
    var toUnit by remember(selectedCategory) { mutableStateOf(currentUnits.getOrNull(1) ?: currentUnits.firstOrNull() ?: UnitItem("Kilometer", "km", 1000.0)) }

    val numericValue = inputValue.toDoubleOrNull() ?: 0.0
    val convertedResult = remember(selectedCategory, fromUnit, toUnit, numericValue) {
        UnitConverterEngine.convert(selectedCategory, fromUnit, toUnit, numericValue)
    }

    val shareText = "SmartCalc – Unit Converter\n" +
            "Category: ${selectedCategory.title}\n" +
            "From: $numericValue ${fromUnit.symbol} (${fromUnit.name})\n" +
            "To: ${AppFormatters.formatNumber(convertedResult)} ${toUnit.symbol} (${toUnit.name})"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Unit Converter", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite("unit_converter") }) {
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
            // Category Chips Flow
            Text(
                text = "Select Category",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                UnitCategory.values().forEach { cat ->
                    val isSelected = cat == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            )
                            .clickable {
                                selectedCategory = cat
                                val units = UnitConverterEngine.categories[cat] ?: emptyList()
                                fromUnit = units.firstOrNull() ?: fromUnit
                                toUnit = units.getOrNull(1) ?: fromUnit
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = cat.title,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Value Input
            SmartNumberInput(
                label = "Value to Convert",
                value = inputValue,
                onValueChange = { inputValue = it },
                suffix = fromUnit.symbol
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Source Unit Dropdown & Target Unit Dropdown with Swap Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // From Dropdown
                UnitDropdownPicker(
                    label = "From",
                    selectedUnit = fromUnit,
                    units = currentUnits,
                    onUnitSelected = { fromUnit = it },
                    modifier = Modifier.weight(1f)
                )

                // Swap
                IconButton(
                    onClick = {
                        val temp = fromUnit
                        fromUnit = toUnit
                        toUnit = temp
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Swap Units",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // To Dropdown
                UnitDropdownPicker(
                    label = "To",
                    selectedUnit = toUnit,
                    units = currentUnits,
                    onUnitSelected = { toUnit = it },
                    modifier = Modifier.weight(1f)
                )
            }

            // Actions
            CalculationActionsBar(
                onCalculate = {},
                onReset = { inputValue = "1" },
                onSave = {
                    viewModel.saveCalculation(
                        calculatorId = "unit_converter",
                        inputSummary = "$numericValue ${fromUnit.symbol} (${selectedCategory.title})",
                        resultSummary = "${AppFormatters.formatNumber(convertedResult)} ${toUnit.symbol}",
                        detailedText = shareText
                    )
                    Toast.makeText(context, "Saved to history!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            // Result
            PrimaryResultCard(
                title = "${selectedCategory.title} Conversion",
                primaryValue = "${AppFormatters.formatNumber(convertedResult)} ${toUnit.symbol}",
                primaryLabel = toUnit.name,
                badgeText = "1 ${fromUnit.symbol} = ${AppFormatters.formatNumber(UnitConverterEngine.convert(selectedCategory, fromUnit, toUnit, 1.0))} ${toUnit.symbol}",
                badgeColor = ProfitGreen,
                breakdownItems = listOf(
                    ResultBreakdownItem("Source Input", "$numericValue ${fromUnit.symbol} (${fromUnit.name})"),
                    ResultBreakdownItem("Converted Output", "${AppFormatters.formatNumber(convertedResult)} ${toUnit.symbol} (${toUnit.name})", PrincipalBlue, isBold = true)
                )
            )

            // All Units in Category Quick-Matrix Card
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
                        text = "All ${selectedCategory.title} Units for $numericValue ${fromUnit.symbol}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    currentUnits.forEachIndexed { index, unit ->
                        val converted = UnitConverterEngine.convert(selectedCategory, fromUnit, unit, numericValue)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = unit.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${AppFormatters.formatNumber(converted)} ${unit.symbol}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (index < currentUnits.size - 1) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDropdownPicker(
    label: String,
    selectedUnit: UnitItem,
    units: List<UnitItem>,
    onUnitSelected: (UnitItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

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
                value = "${selectedUnit.symbol} - ${selectedUnit.name}",
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
                units.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text("${unit.symbol} (${unit.name})") },
                        onClick = {
                            onUnitSelected(unit)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
