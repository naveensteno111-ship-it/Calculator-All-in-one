package com.example.domain

import kotlin.math.abs
import kotlin.math.max

data class GstResult(
    val baseAmount: Double,
    val gstRate: Double,
    val gstAmount: Double,
    val cgstAmount: Double,
    val sgstAmount: Double,
    val totalAmount: Double
)

data class IncomeTaxResult(
    val grossIncome: Double,
    val totalDeductions: Double,
    val taxableIncome: Double,
    val taxPayable: Double,
    val cessAmount: Double,
    val totalTaxWithCess: Double,
    val effectiveTaxRate: Double
)

data class SalaryBreakdown(
    val ctcAnnual: Double,
    val grossMonthly: Double,
    val basicMonthly: Double,
    val hraMonthly: Double,
    val specialAllowanceMonthly: Double,
    val pfEmployeeMonthly: Double,
    val professionalTaxMonthly: Double,
    val estimatedTdsMonthly: Double,
    val totalDeductionsMonthly: Double,
    val netTakeHomeMonthly: Double
)

object TaxBusinessCalculators {

    fun calculateGstExclusive(originalAmount: Double, gstRate: Double): GstResult {
        val gstAmount = (originalAmount * gstRate) / 100.0
        val total = originalAmount + gstAmount
        return GstResult(
            baseAmount = originalAmount,
            gstRate = gstRate,
            gstAmount = gstAmount,
            cgstAmount = gstAmount / 2.0,
            sgstAmount = gstAmount / 2.0,
            totalAmount = total
        )
    }

    fun calculateGstInclusive(totalAmount: Double, gstRate: Double): GstResult {
        val baseAmount = totalAmount / (1.0 + gstRate / 100.0)
        val gstAmount = totalAmount - baseAmount
        return GstResult(
            baseAmount = baseAmount,
            gstRate = gstRate,
            gstAmount = gstAmount,
            cgstAmount = gstAmount / 2.0,
            sgstAmount = gstAmount / 2.0,
            totalAmount = totalAmount
        )
    }

    fun calculateDiscount(
        originalPrice: Double,
        discountPercent1: Double,
        discountPercent2: Double = 0.0
    ): Pair<Double, Double> {
        val priceAfterFirst = originalPrice * (1.0 - discountPercent1 / 100.0)
        val finalPrice = priceAfterFirst * (1.0 - discountPercent2 / 100.0)
        val totalDiscount = originalPrice - finalPrice
        return Pair(max(0.0, totalDiscount), max(0.0, finalPrice))
    }

    fun calculateProfitLoss(costPrice: Double, sellingPrice: Double): Triple<Double, Double, Boolean> {
        val diff = sellingPrice - costPrice
        val isProfit = diff >= 0
        val percentage = if (costPrice > 0) (abs(diff) / costPrice) * 100.0 else 0.0
        return Triple(abs(diff), percentage, isProfit)
    }

    fun calculateMarkup(costPrice: Double, markupPercent: Double): Pair<Double, Double> {
        val profit = costPrice * (markupPercent / 100.0)
        val sellingPrice = costPrice + profit
        return Pair(profit, sellingPrice)
    }

    fun calculateMargin(costPrice: Double, sellingPrice: Double): Pair<Double, Double> {
        val profit = sellingPrice - costPrice
        val marginPercent = if (sellingPrice > 0) (profit / sellingPrice) * 100.0 else 0.0
        return Pair(profit, marginPercent)
    }

    fun calculateSalary(
        annualCtc: Double,
        basicPercentOfGross: Double = 50.0,
        hraPercentOfBasic: Double = 40.0,
        includePf: Boolean = true,
        professionalTaxMonthly: Double = 200.0
    ): SalaryBreakdown {
        val grossMonthly = annualCtc / 12.0
        val basicMonthly = grossMonthly * (basicPercentOfGross / 100.0)
        val hraMonthly = basicMonthly * (hraPercentOfBasic / 100.0)
        val specialAllowanceMonthly = max(0.0, grossMonthly - basicMonthly - hraMonthly)

        val pfMonthly = if (includePf) minOf(basicMonthly * 0.12, 1800.0) else 0.0

        // Quick annual taxable estimate
        val annualGross = grossMonthly * 12.0
        val stdDeduction = 75000.0
        val taxable = max(0.0, annualGross - stdDeduction - (pfMonthly * 12.0) - (professionalTaxMonthly * 12.0))
        val annualTax = calculateIncomeTaxNewRegime(taxable).totalTaxWithCess
        val monthlyTds = annualTax / 12.0

        val totalDeductions = pfMonthly + professionalTaxMonthly + monthlyTds
        val netTakeHome = max(0.0, grossMonthly - totalDeductions)

        return SalaryBreakdown(
            ctcAnnual = annualCtc,
            grossMonthly = grossMonthly,
            basicMonthly = basicMonthly,
            hraMonthly = hraMonthly,
            specialAllowanceMonthly = specialAllowanceMonthly,
            pfEmployeeMonthly = pfMonthly,
            professionalTaxMonthly = professionalTaxMonthly,
            estimatedTdsMonthly = monthlyTds,
            totalDeductionsMonthly = totalDeductions,
            netTakeHomeMonthly = netTakeHome
        )
    }

    fun calculateIncomeTaxNewRegime(taxableIncome: Double): IncomeTaxResult {
        // Indian New Tax Regime (FY 2024-25 / 2025-26):
        // 0 - 3L: 0%
        // 3L - 7L: 5%
        // 7L - 10L: 10%
        // 10L - 12L: 15%
        // 12L - 15L: 20%
        // Above 15L: 30%
        // Full rebate u/s 87A if taxable income <= 7,00,000
        var tax = 0.0
        val income = max(0.0, taxableIncome)

        if (income > 300000) {
            tax += minOf(income - 300000, 400000.0) * 0.05
        }
        if (income > 700000) {
            tax += minOf(income - 700000, 300000.0) * 0.10
        }
        if (income > 1000000) {
            tax += minOf(income - 1000000, 200000.0) * 0.15
        }
        if (income > 1200000) {
            tax += minOf(income - 1200000, 300000.0) * 0.20
        }
        if (income > 1500000) {
            tax += (income - 1500000) * 0.30
        }

        // Rebate u/s 87A
        if (income <= 700000) {
            tax = 0.0
        }

        val cess = tax * 0.04
        val totalTax = tax + cess
        val effectiveRate = if (income > 0) (totalTax / income) * 100.0 else 0.0

        return IncomeTaxResult(
            grossIncome = income,
            totalDeductions = 0.0,
            taxableIncome = income,
            taxPayable = tax,
            cessAmount = cess,
            totalTaxWithCess = totalTax,
            effectiveTaxRate = effectiveRate
        )
    }

    fun calculateIncomeTaxOldRegime(
        grossIncome: Double,
        deduction80C: Double = 0.0,
        deduction80D: Double = 0.0,
        hraExemption: Double = 0.0,
        standardDeduction: Double = 50000.0
    ): IncomeTaxResult {
        val totalDeductions = minOf(deduction80C, 150000.0) + minOf(deduction80D, 100000.0) + hraExemption + standardDeduction
        val taxableIncome = max(0.0, grossIncome - totalDeductions)

        // Old Regime slabs:
        // 0 - 2.5L: 0%
        // 2.5L - 5L: 5%
        // 5L - 10L: 20%
        // > 10L: 30%
        // Rebate u/s 87A if taxable <= 5L
        var tax = 0.0
        if (taxableIncome > 250000) {
            tax += minOf(taxableIncome - 250000, 250000.0) * 0.05
        }
        if (taxableIncome > 500000) {
            tax += minOf(taxableIncome - 500000, 500000.0) * 0.20
        }
        if (taxableIncome > 1000000) {
            tax += (taxableIncome - 1000000) * 0.30
        }

        if (taxableIncome <= 500000) {
            tax = 0.0
        }

        val cess = tax * 0.04
        val totalTax = tax + cess
        val effectiveRate = if (grossIncome > 0) (totalTax / grossIncome) * 100.0 else 0.0

        return IncomeTaxResult(
            grossIncome = grossIncome,
            totalDeductions = totalDeductions,
            taxableIncome = taxableIncome,
            taxPayable = tax,
            cessAmount = cess,
            totalTaxWithCess = totalTax,
            effectiveTaxRate = effectiveRate
        )
    }
}
