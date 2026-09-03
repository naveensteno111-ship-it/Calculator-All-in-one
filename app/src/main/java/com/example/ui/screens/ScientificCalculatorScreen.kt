package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ScientificEngine
import com.example.ui.viewmodel.SmartCalcViewModel
import com.example.util.AppFormatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScientificCalculatorScreen(
    viewModel: SmartCalcViewModel,
    isFavorite: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var expression by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("0") }
    var isRadian by remember { mutableStateOf(false) }
    var memoryValue by remember { mutableStateOf(0.0) }

    val engine = remember { ScientificEngine() }
    engine.isRadianMode = isRadian
    engine.memoryValue = memoryValue

    fun evaluateLive(expr: String) {
        if (expr.isBlank()) {
            resultText = "0"
            return
        }
        val res = engine.evaluate(expr)
        res.onSuccess {
            resultText = AppFormatters.formatNumber(it)
        }.onFailure {
            // Keep current result or show syntax status
        }
    }

    fun onKeyClick(key: String) {
        when (key) {
            "AC" -> {
                expression = ""
                resultText = "0"
            }
            "DEL" -> {
                if (expression.isNotEmpty()) {
                    expression = expression.dropLast(1)
                    evaluateLive(expression)
                }
            }
            "=" -> {
                val res = engine.evaluate(expression)
                res.onSuccess {
                    val finalFormatted = AppFormatters.formatNumber(it)
                    viewModel.saveCalculation(
                        calculatorId = "scientific",
                        inputSummary = expression,
                        resultSummary = finalFormatted,
                        detailedText = "$expression = $finalFormatted"
                    )
                    expression = finalFormatted.replace(",", "")
                    resultText = finalFormatted
                }.onFailure {
                    resultText = "Error"
                    Toast.makeText(context, "Invalid Math Expression", Toast.LENGTH_SHORT).show()
                }
            }
            "MC" -> { memoryValue = 0.0; Toast.makeText(context, "Memory Cleared", Toast.LENGTH_SHORT).show() }
            "MR" -> {
                expression += AppFormatters.formatNumber(memoryValue).replace(",", "")
                evaluateLive(expression)
            }
            "M+" -> {
                val current = resultText.toDoubleOrNull() ?: 0.0
                memoryValue += current
                Toast.makeText(context, "M+ $current", Toast.LENGTH_SHORT).show()
            }
            "M-" -> {
                val current = resultText.toDoubleOrNull() ?: 0.0
                memoryValue -= current
                Toast.makeText(context, "M- $current", Toast.LENGTH_SHORT).show()
            }
            "RAD/DEG" -> {
                isRadian = !isRadian
                evaluateLive(expression)
            }
            else -> {
                expression += key
                evaluateLive(expression)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scientific Calculator", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite("scientific") }) {
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
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Display Screen Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isRadian) "RAD" else "DEG",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (memoryValue != 0.0) {
                            Text(
                                text = "M (${AppFormatters.formatNumber(memoryValue)})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }

                    // Expression
                    Text(
                        text = if (expression.isEmpty()) "0" else expression,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        textAlign = TextAlign.End
                    )

                    // Result Preview
                    Text(
                        text = resultText,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        textAlign = TextAlign.End
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Keypad Grid
            val keys = listOf(
                listOf("RAD/DEG", "sin", "cos", "tan", "AC"),
                listOf("ln", "log", "sqrt", "^", "DEL"),
                listOf("MC", "MR", "M+", "M-", "÷"),
                listOf("π", "7", "8", "9", "×"),
                listOf("e", "4", "5", "6", "−"),
                listOf("(", "1", "2", "3", "+"),
                listOf(")", "0", ".", "%", "=")
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                keys.forEach { rowKeys ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowKeys.forEach { key ->
                            val isAction = key in listOf("AC", "DEL")
                            val isEquals = key == "="
                            val isOperator = key in listOf("+", "−", "×", "÷", "%", "^")
                            val isFunction = key in listOf("sin", "cos", "tan", "log", "ln", "sqrt", "π", "e", "RAD/DEG", "MC", "MR", "M+", "M-")

                            val bgColor = when {
                                isEquals -> MaterialTheme.colorScheme.primary
                                isAction -> MaterialTheme.colorScheme.error.copy(alpha = 0.85f)
                                isOperator -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                isFunction -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                                else -> MaterialTheme.colorScheme.surface
                            }

                            val textColor = when {
                                isEquals -> MaterialTheme.colorScheme.onPrimary
                                isAction -> MaterialTheme.colorScheme.onError
                                isOperator -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurface
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(bgColor)
                                    .clickable { onKeyClick(key) }
                                    .testTag("key_$key"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (key == "DEL") {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                                        contentDescription = "Backspace",
                                        tint = textColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Text(
                                        text = if (key == "RAD/DEG") (if (isRadian) "RAD" else "DEG") else key,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
