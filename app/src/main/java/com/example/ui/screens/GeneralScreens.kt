package com.example.ui.screens

import android.app.DatePickerDialog
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.domain.GeneralCalculators
import com.example.ui.components.CalculationActionsBar
import com.example.ui.components.PrimaryResultCard
import com.example.ui.components.ResultBreakdownItem
import com.example.ui.components.SmartNumberInput
import com.example.ui.theme.InterestAmber
import com.example.ui.theme.PrincipalBlue
import com.example.ui.theme.ProfitGreen
import com.example.ui.viewmodel.SmartCalcViewModel
import com.example.util.AppFormatters
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralTopBar(title: String, calcId: String, viewModel: SmartCalcViewModel, isFavorite: Boolean, onBack: () -> Unit) {
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
// 1. AGE CALCULATOR
// -------------------------------------------------------------
@Composable
fun AgeCalculatorScreen(viewModel: SmartCalcViewModel, isFavorite: Boolean, onBack: () -> Unit) {
    val context = LocalContext.current
    val today = Calendar.getInstance()

    var birthYear by remember { mutableStateOf(2000) }
    var birthMonth by remember { mutableStateOf(0) }
    var birthDay by remember { mutableStateOf(1) }

    var targetYear by remember { mutableStateOf(today.get(Calendar.YEAR)) }
    var targetMonth by remember { mutableStateOf(today.get(Calendar.MONTH)) }
    var targetDay by remember { mutableStateOf(today.get(Calendar.DAY_OF_MONTH)) }

    val ageDetail = remember(birthYear, birthMonth, birthDay, targetYear, targetMonth, targetDay) {
        GeneralCalculators.calculateAge(
            birthYear = birthYear,
            birthMonth = birthMonth,
            birthDay = birthDay,
            targetYear = targetYear,
            targetMonth = targetMonth,
            targetDay = targetDay
        )
    }

    val birthDatePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            birthYear = year
            birthMonth = month
            birthDay = dayOfMonth
        },
        birthYear,
        birthMonth,
        birthDay
    )

    val targetDatePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            targetYear = year
            targetMonth = month
            targetDay = dayOfMonth
        },
        targetYear,
        targetMonth,
        targetDay
    )

    val birthDateStr = String.format("%02d/%02d/%04d", birthDay, birthMonth + 1, birthYear)
    val targetDateStr = String.format("%02d/%02d/%04d", targetDay, targetMonth + 1, targetYear)
    val shareText = "SmartCalc – Age Result\nDate of Birth: $birthDateStr\nAge as of $targetDateStr: ${ageDetail.years} Years, ${ageDetail.months} Months, ${ageDetail.days} Days\nTotal Days: ${ageDetail.totalDays}\nNext Birthday: In ${ageDetail.nextBirthdayDays} days on a ${ageDetail.nextBirthdayWeekday}"

    val westernZodiac = remember(birthDay, birthMonth) {
        when (birthMonth) {
            0 -> if (birthDay < 20) "Capricorn ♑ (Earth)" else "Aquarius ♒ (Air)"
            1 -> if (birthDay < 19) "Aquarius ♒ (Air)" else "Pisces ♓ (Water)"
            2 -> if (birthDay < 21) "Pisces ♓ (Water)" else "Aries ♈ (Fire)"
            3 -> if (birthDay < 20) "Aries ♈ (Fire)" else "Taurus ♉ (Earth)"
            4 -> if (birthDay < 21) "Taurus ♉ (Earth)" else "Gemini ♊ (Air)"
            5 -> if (birthDay < 21) "Gemini ♊ (Air)" else "Cancer ♋ (Water)"
            6 -> if (birthDay < 23) "Cancer ♋ (Water)" else "Leo ♌ (Fire)"
            7 -> if (birthDay < 23) "Leo ♌ (Fire)" else "Virgo ♍ (Earth)"
            8 -> if (birthDay < 23) "Virgo ♍ (Earth)" else "Libra ♎ (Air)"
            9 -> if (birthDay < 23) "Libra ♎ (Air)" else "Scorpio ♏ (Water)"
            10 -> if (birthDay < 22) "Scorpio ♏ (Water)" else "Sagittarius ♐ (Fire)"
            11 -> if (birthDay < 22) "Sagittarius ♐ (Fire)" else "Capricorn ♑ (Earth)"
            else -> "Capricorn ♑ (Earth)"
        }
    }

    val chineseZodiac = remember(birthYear) {
        val animals = listOf("Rat 🐀", "Ox 🐂", "Tiger 🐅", "Rabbit 🐇", "Dragon 🐉", "Snake 🐍", "Horse 🐎", "Goat 🐐", "Monkey 🐒", "Rooster 🐓", "Dog 🐕", "Pig 🐖")
        val idx = (birthYear - 4) % 12
        if (idx >= 0) animals[idx] else animals[(idx + 12) % 12]
    }

    val upcomingBirthdays = remember(birthDay, birthMonth, targetYear) {
        val list = mutableListOf<Pair<Int, String>>()
        val dayNames = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        for (i in 1..4) {
            val y = targetYear + i
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, y)
                set(Calendar.MONTH, birthMonth)
                set(Calendar.DAY_OF_MONTH, birthDay)
            }
            val dayOfWeek = dayNames[cal.get(Calendar.DAY_OF_WEEK) - 1]
            list.add(y to dayOfWeek)
        }
        list
    }

    Scaffold(topBar = { GeneralTopBar("Age Calculator", "age", viewModel, isFavorite, onBack) }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Inputs: Date of Birth & Target Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // DOB
                Column(modifier = Modifier.weight(1f)) {
                    Text("Date of Birth", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = { birthDatePicker.show() },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(birthDateStr, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.CalendarMonth, contentDescription = "DOB", modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Age At Date
                Column(modifier = Modifier.weight(1f)) {
                    Text("Age as of Date", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = { targetDatePicker.show() },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(targetDateStr, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Target Date", modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick DOB Presets Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "2000" to 2000,
                    "1995" to 1995,
                    "1990" to 1990,
                    "18 Yrs Ago" to (today.get(Calendar.YEAR) - 18)
                ).forEach { (label, yr) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (birthYear == yr) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            )
                            .clickable {
                                birthYear = yr
                                birthMonth = 0
                                birthDay = 1
                            }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (birthYear == yr) FontWeight.Bold else FontWeight.Medium,
                            color = if (birthYear == yr) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            CalculationActionsBar(
                onCalculate = {},
                onReset = {
                    birthYear = 2000
                    birthMonth = 0
                    birthDay = 1
                    targetYear = today.get(Calendar.YEAR)
                    targetMonth = today.get(Calendar.MONTH)
                    targetDay = today.get(Calendar.DAY_OF_MONTH)
                },
                onSave = {
                    viewModel.saveCalculation("age", "DOB: $birthDateStr", "${ageDetail.years} yrs, ${ageDetail.months} mos, ${ageDetail.days} days", shareText)
                    Toast.makeText(context, "Age Calculation Saved to History!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            // Hero Age Display Card
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "EXACT CURRENT AGE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${ageDetail.years} Years",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${ageDetail.months} Months | ${ageDetail.days} Days",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Next Birthday Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Next Birthday in ${ageDetail.nextBirthdayDays} Days (${ageDetail.nextBirthdayWeekday})",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            // Life Statistics Detailed Grid
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(
                        text = "Life Summary & Total Time Lived",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatMiniBox("Total Months", "${ageDetail.totalMonths}", Modifier.weight(1f))
                        StatMiniBox("Total Weeks", "${ageDetail.totalWeeks}", Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatMiniBox("Total Days", "${ageDetail.totalDays}", Modifier.weight(1f))
                        StatMiniBox("Total Hours", "~${ageDetail.totalDays * 24L}", Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatMiniBox("Heartbeats (~72 bpm)", "~${(ageDetail.totalDays * 24L * 60L * 72L) / 1000000}M", Modifier.weight(1f))
                        StatMiniBox("Breaths (~16 bpm)", "~${(ageDetail.totalDays * 24L * 60L * 16L) / 1000000}M", Modifier.weight(1f))
                    }
                }
            }

            // Astrology & Zodiac Signs
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Western Zodiac", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(westernZodiac, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    }
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text("Chinese Zodiac", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(chineseZodiac, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Upcoming Birthday Milestones
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(
                        text = "Upcoming Birthday Days",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    upcomingBirthdays.forEach { (year, dayName) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("$birthDay/${birthMonth + 1}/$year", style = MaterialTheme.typography.bodyMedium)
                            Text(dayName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatMiniBox(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

// -------------------------------------------------------------
// 2. DATE DIFFERENCE CALCULATOR
// -------------------------------------------------------------
@Composable
fun DateDifferenceCalculatorScreen(viewModel: SmartCalcViewModel, isFavorite: Boolean, onBack: () -> Unit) {
    val context = LocalContext.current
    val today = Calendar.getInstance()

    var startYear by remember { mutableStateOf(today.get(Calendar.YEAR)) }
    var startMonth by remember { mutableStateOf(today.get(Calendar.MONTH)) }
    var startDay by remember { mutableStateOf(today.get(Calendar.DAY_OF_MONTH)) }

    var endYear by remember { mutableStateOf(today.get(Calendar.YEAR) + 1) }
    var endMonth by remember { mutableStateOf(today.get(Calendar.MONTH)) }
    var endDay by remember { mutableStateOf(today.get(Calendar.DAY_OF_MONTH)) }

    val diffResult = remember(startYear, startMonth, startDay, endYear, endMonth, endDay) {
        GeneralCalculators.calculateDateDifference(startYear, startMonth, startDay, endYear, endMonth, endDay)
    }

    val startDatePicker = DatePickerDialog(context, { _, y, m, d -> startYear = y; startMonth = m; startDay = d }, startYear, startMonth, startDay)
    val endDatePicker = DatePickerDialog(context, { _, y, m, d -> endYear = y; endMonth = m; endDay = d }, endYear, endMonth, endDay)

    val startStr = String.format("%02d/%02d/%04d", startDay, startMonth + 1, startYear)
    val endStr = String.format("%02d/%02d/%04d", endDay, endMonth + 1, endYear)
    val shareText = "SmartCalc – Date Difference\nStart: $startStr\nEnd: $endStr\nDiff: ${diffResult.years} Yrs, ${diffResult.months} Mos, ${diffResult.days} Days (${diffResult.totalDays} total days)"

    Scaffold(topBar = { GeneralTopBar("Date Difference", "date_diff", viewModel, isFavorite, onBack) }) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text("Start Date", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedButton(onClick = { startDatePicker.show() }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp)) {
                Text(startStr, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("End Date", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedButton(onClick = { endDatePicker.show() }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp)) {
                Text(endStr, fontWeight = FontWeight.Bold)
            }

            CalculationActionsBar(
                onCalculate = {},
                onReset = {
                    startYear = today.get(Calendar.YEAR); startMonth = today.get(Calendar.MONTH); startDay = today.get(Calendar.DAY_OF_MONTH)
                    endYear = today.get(Calendar.YEAR) + 1; endMonth = today.get(Calendar.MONTH); endDay = today.get(Calendar.DAY_OF_MONTH)
                },
                onSave = {
                    viewModel.saveCalculation("date_diff", "$startStr to $endStr", "${diffResult.totalDays} days", shareText)
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            PrimaryResultCard(
                title = "Difference Duration",
                primaryValue = "${diffResult.totalDays} Days",
                primaryLabel = "${diffResult.years} Years, ${diffResult.months} Months, ${diffResult.days} Days",
                breakdownItems = listOf(
                    ResultBreakdownItem("Total Weeks", "${diffResult.totalWeeks} weeks"),
                    ResultBreakdownItem("Total Hours", "${diffResult.totalHours} hours", PrincipalBlue, isBold = true)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -------------------------------------------------------------
// 3. RATIO & AVERAGE/STATISTICS CALCULATORS
// -------------------------------------------------------------
@Composable
fun RatioCalculatorScreen(viewModel: SmartCalcViewModel, isFavorite: Boolean, onBack: () -> Unit) {
    val context = LocalContext.current
    var inputA by remember { mutableStateOf("1920") }
    var inputB by remember { mutableStateOf("1080") }

    val valA = inputA.toDoubleOrNull() ?: 0.0
    val valB = inputB.toDoubleOrNull() ?: 0.0

    val (sA, sB) = remember(valA, valB) {
        GeneralCalculators.simplifyRatio(valA, valB)
    }

    val shareText = "SmartCalc – Ratio\nValues: $valA : $valB\nSimplified Ratio: $sA : $sB"

    Scaffold(topBar = { GeneralTopBar("Ratio Calculator", "ratio", viewModel, isFavorite, onBack) }) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmartNumberInput(label = "First Term (A)", value = inputA, onValueChange = { inputA = it }, modifier = Modifier.weight(1f))
                SmartNumberInput(label = "Second Term (B)", value = inputB, onValueChange = { inputB = it }, modifier = Modifier.weight(1f))
            }

            CalculationActionsBar(
                onCalculate = {},
                onReset = { inputA = "1920"; inputB = "1080" },
                onSave = {
                    viewModel.saveCalculation("ratio", "$valA : $valB", "$sA : $sB", shareText)
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            PrimaryResultCard(
                title = "Simplified Ratio",
                primaryValue = "$sA : $sB",
                primaryLabel = "Lowest Integer Proportion",
                breakdownItems = listOf(
                    ResultBreakdownItem("Decimal Value (A / B)", String.format("%.4f", if (valB != 0.0) valA / valB else 0.0)),
                    ResultBreakdownItem("Percentage (A of Total)", AppFormatters.formatPercentage(if (valA + valB > 0) (valA / (valA + valB)) * 100 else 0.0))
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StatisticsCalculatorScreen(viewModel: SmartCalcViewModel, isFavorite: Boolean, onBack: () -> Unit) {
    val context = LocalContext.current
    var rawInput by remember { mutableStateOf("12, 18, 25, 30, 18, 42, 50") }

    val numbers = remember(rawInput) {
        rawInput.split(",", " ", "\n", ";")
            .mapNotNull { it.trim().toDoubleOrNull() }
    }

    val stats = remember(numbers) {
        GeneralCalculators.calculateStatistics(numbers)
    }

    val shareText = "SmartCalc – Statistics\nValues: $rawInput\nCount: ${stats?.count ?: 0}\nMean: ${stats?.mean ?: 0.0}\nMedian: ${stats?.median ?: 0.0}"

    Scaffold(topBar = { GeneralTopBar("Average & Statistics", "average", viewModel, isFavorite, onBack) }) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp)) {
            SmartNumberInput(label = "Enter comma-separated numbers", value = rawInput, onValueChange = { rawInput = it }, helperText = "e.g. 10, 20, 30")

            CalculationActionsBar(
                onCalculate = {},
                onReset = { rawInput = "12, 18, 25, 30, 18, 42, 50" },
                onSave = {
                    viewModel.saveCalculation("average", "Data (${numbers.size} items)", "Mean: ${stats?.mean ?: 0.0}", shareText)
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                },
                shareText = shareText
            )

            if (stats != null) {
                PrimaryResultCard(
                    title = "Descriptive Statistics",
                    primaryValue = AppFormatters.formatNumber(stats.mean),
                    primaryLabel = "Mean / Average",
                    breakdownItems = listOf(
                        ResultBreakdownItem("Total Count (N)", "${stats.count}"),
                        ResultBreakdownItem("Sum Total", AppFormatters.formatNumber(stats.sum)),
                        ResultBreakdownItem("Median", AppFormatters.formatNumber(stats.median), PrincipalBlue),
                        ResultBreakdownItem("Mode", if (stats.mode.isNotEmpty()) stats.mode.joinToString(", ") { AppFormatters.formatNumber(it) } else "No unique mode"),
                        ResultBreakdownItem("Minimum", AppFormatters.formatNumber(stats.min)),
                        ResultBreakdownItem("Maximum", AppFormatters.formatNumber(stats.max)),
                        ResultBreakdownItem("Range", AppFormatters.formatNumber(stats.range), isBold = true)
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
