package com.example.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

enum class NumberFormatType {
    INDIAN,      // e.g., ₹ 1,00,000.00
    INTERNATIONAL // e.g., $ 100,000.00
}

object AppFormatters {

    fun formatCurrency(
        amount: Double,
        currencySymbol: String = "₹",
        numberFormat: NumberFormatType = NumberFormatType.INDIAN,
        decimals: Int = 0
    ): String {
        if (amount.isNaN() || amount.isInfinite()) return "$currencySymbol 0"
        val formattedNum = when (numberFormat) {
            NumberFormatType.INDIAN -> formatIndianNumber(amount, decimals)
            NumberFormatType.INTERNATIONAL -> formatStandardNumber(amount, decimals)
        }
        return "$currencySymbol $formattedNum"
    }

    fun formatNumber(
        value: Double,
        numberFormat: NumberFormatType = NumberFormatType.INDIAN,
        decimals: Int = 2
    ): String {
        if (value.isNaN() || value.isInfinite()) return "0"
        return when (numberFormat) {
            NumberFormatType.INDIAN -> formatIndianNumber(value, decimals)
            NumberFormatType.INTERNATIONAL -> formatStandardNumber(value, decimals)
        }
    }

    fun formatPercentage(value: Double, decimals: Int = 2): String {
        val pattern = if (decimals == 0) "0" else "0." + "#".repeat(decimals)
        val df = DecimalFormat(pattern, DecimalFormatSymbols(Locale.US))
        return "${df.format(value)}%"
    }

    private fun formatStandardNumber(value: Double, decimals: Int): String {
        val pattern = if (decimals == 0) "#,##0" else "#,##0." + "0".repeat(decimals)
        val df = DecimalFormat(pattern, DecimalFormatSymbols(Locale.US))
        return df.format(value)
    }

    /**
     * Formats according to Indian numbering system (Lakhs & Crores):
     * 1,00,00,000
     */
    private fun formatIndianNumber(value: Double, decimals: Int): String {
        val isNegative = value < 0
        val absValue = kotlin.math.abs(value)
        val longPart = absValue.toLong()
        val fracPart = absValue - longPart

        val strLong = longPart.toString()
        val formattedLong = if (strLong.length <= 3) {
            strLong
        } else {
            val lastThree = strLong.substring(strLong.length - 3)
            val remaining = strLong.substring(0, strLong.length - 3)
            val sb = StringBuilder()
            var count = 0
            for (i in remaining.length - 1 downTo 0) {
                sb.append(remaining[i])
                count++
                if (count == 2 && i != 0) {
                    sb.append(",")
                    count = 0
                }
            }
            sb.reverse().toString() + "," + lastThree
        }

        val result = if (decimals > 0) {
            val fracMultiplier = Math.pow(10.0, decimals.toDouble()).toLong()
            val fracInt = (Math.round(fracPart * fracMultiplier)).toLong()
            val fracStr = fracInt.toString().padStart(decimals, '0').take(decimals)
            "$formattedLong.$fracStr"
        } else {
            formattedLong
        }

        return if (isNegative) "-$result" else result
    }
}
