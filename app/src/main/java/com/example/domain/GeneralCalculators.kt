package com.example.domain

import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong

data class AgeDetail(
    val years: Int,
    val months: Int,
    val days: Int,
    val totalMonths: Long,
    val totalWeeks: Long,
    val totalDays: Long,
    val nextBirthdayDays: Long,
    val nextBirthdayWeekday: String
)

data class DateDifferenceDetail(
    val years: Int,
    val months: Int,
    val days: Int,
    val totalDays: Long,
    val totalWeeks: Long,
    val totalHours: Long
)

data class StatisticsResult(
    val count: Int,
    val sum: Double,
    val mean: Double,
    val median: Double,
    val mode: List<Double>,
    val min: Double,
    val max: Double,
    val range: Double
)

object GeneralCalculators {

    fun calculateAge(
        birthYear: Int, birthMonth: Int, birthDay: Int,
        targetYear: Int, targetMonth: Int, targetDay: Int
    ): AgeDetail {
        val birthCal = Calendar.getInstance().apply {
            set(birthYear, birthMonth, birthDay, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val targetCal = Calendar.getInstance().apply {
            set(targetYear, targetMonth, targetDay, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        var years = targetYear - birthYear
        var months = targetMonth - birthMonth
        var days = targetDay - birthDay

        if (days < 0) {
            val tempCal = Calendar.getInstance().apply {
                set(targetYear, targetMonth - 1, 1)
            }
            days += tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
            months--
        }

        if (months < 0) {
            months += 12
            years--
        }

        val diffMillis = targetCal.timeInMillis - birthCal.timeInMillis
        val totalDays = TimeUnit.MILLISECONDS.toDays(diffMillis).coerceAtLeast(0)
        val totalWeeks = totalDays / 7
        val totalMonths = (years * 12L + months)

        // Next Birthday
        val nextBdayCal = Calendar.getInstance().apply {
            set(targetYear, birthMonth, birthDay, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (nextBdayCal.before(targetCal) || nextBdayCal == targetCal) {
            nextBdayCal.set(Calendar.YEAR, targetYear + 1)
        }
        val nextBdayDiff = TimeUnit.MILLISECONDS.toDays(nextBdayCal.timeInMillis - targetCal.timeInMillis)
        val weekdays = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val nextWeekday = weekdays[nextBdayCal.get(Calendar.DAY_OF_WEEK) - 1]

        return AgeDetail(
            years = maxOf(0, years),
            months = maxOf(0, months),
            days = maxOf(0, days),
            totalMonths = totalMonths,
            totalWeeks = totalWeeks,
            totalDays = totalDays,
            nextBirthdayDays = nextBdayDiff,
            nextBirthdayWeekday = nextWeekday
        )
    }

    fun calculateDateDifference(
        startYear: Int, startMonth: Int, startDay: Int,
        endYear: Int, endMonth: Int, endDay: Int
    ): DateDifferenceDetail {
        val startCal = Calendar.getInstance().apply {
            set(startYear, startMonth, startDay, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endCal = Calendar.getInstance().apply {
            set(endYear, endMonth, endDay, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val (first, second) = if (startCal.before(endCal)) Pair(startCal, endCal) else Pair(endCal, startCal)

        var years = second.get(Calendar.YEAR) - first.get(Calendar.YEAR)
        var months = second.get(Calendar.MONTH) - first.get(Calendar.MONTH)
        var days = second.get(Calendar.DAY_OF_MONTH) - first.get(Calendar.DAY_OF_MONTH)

        if (days < 0) {
            val tempCal = Calendar.getInstance().apply {
                set(second.get(Calendar.YEAR), second.get(Calendar.MONTH) - 1, 1)
            }
            days += tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
            months--
        }

        if (months < 0) {
            months += 12
            years--
        }

        val diffMillis = kotlin.math.abs(endCal.timeInMillis - startCal.timeInMillis)
        val totalDays = TimeUnit.MILLISECONDS.toDays(diffMillis)
        val totalWeeks = totalDays / 7
        val totalHours = totalDays * 24

        return DateDifferenceDetail(
            years = years,
            months = months,
            days = days,
            totalDays = totalDays,
            totalWeeks = totalWeeks,
            totalHours = totalHours
        )
    }

    fun calculateStatistics(numbers: List<Double>): StatisticsResult? {
        if (numbers.isEmpty()) return null
        val sorted = numbers.sorted()
        val count = sorted.size
        val sum = sorted.sum()
        val mean = sum / count

        val median = if (count % 2 == 1) {
            sorted[count / 2]
        } else {
            (sorted[count / 2 - 1] + sorted[count / 2]) / 2.0
        }

        val freq = numbers.groupingBy { it }.eachCount()
        val maxFreq = freq.values.maxOrNull() ?: 1
        val mode = if (maxFreq > 1) freq.filter { it.value == maxFreq }.keys.toList() else emptyList()

        val min = sorted.first()
        val max = sorted.last()
        val range = max - min

        return StatisticsResult(
            count = count,
            sum = sum,
            mean = mean,
            median = median,
            mode = mode,
            min = min,
            max = max,
            range = range
        )
    }

    fun simplifyRatio(a: Double, b: Double): Pair<Long, Long> {
        if (a <= 0 || b <= 0) return Pair(0L, 0L)
        val multiplier = 1000.0
        val lA = (a * multiplier).roundToLong()
        val lB = (b * multiplier).roundToLong()
        val g = gcd(lA, lB)
        return Pair(lA / g, lB / g)
    }

    private fun gcd(a: Long, b: Long): Long {
        var n1 = a
        var n2 = b
        while (n2 != 0L) {
            val temp = n2
            n2 = n1 % n2
            n1 = temp
        }
        return n1
    }
}
