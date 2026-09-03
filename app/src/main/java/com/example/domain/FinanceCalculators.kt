package com.example.domain

import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToLong

data class EmiResult(
    val monthlyEmi: Double,
    val totalPrincipal: Double,
    val totalInterest: Double,
    val totalPayable: Double,
    val schedule: List<AmortizationItem>
)

data class AmortizationItem(
    val period: Int, // Year or Month
    val principalPaid: Double,
    val interestPaid: Double,
    val totalPaid: Double,
    val remainingBalance: Double
)

data class SipResult(
    val totalInvested: Double,
    val estimatedReturns: Double,
    val maturityValue: Double,
    val yearlyBreakdown: List<YearlyGrowthItem>
)

data class YearlyGrowthItem(
    val year: Int,
    val invested: Double,
    val returns: Double,
    val balance: Double
)

data class SwpResult(
    val totalWithdrawn: Double,
    val remainingCorpus: Double,
    val estimatedGrowthEarned: Double,
    val yearlyBreakdown: List<YearlyGrowthItem>
)

data class FdResult(
    val principal: Double,
    val interestEarned: Double,
    val maturityAmount: Double
)

data class RdResult(
    val totalDeposit: Double,
    val interestEarned: Double,
    val maturityAmount: Double
)

data class PpfResult(
    val totalInvested: Double,
    val totalInterest: Double,
    val maturityAmount: Double,
    val yearlyBreakdown: List<YearlyGrowthItem>
)

data class NpsResult(
    val totalInvested: Double,
    val totalCorpus: Double,
    val lumpSumAmount: Double,
    val annuityAmount: Double,
    val estimatedMonthlyPension: Double
)

data class RetirementResult(
    val requiredCorpus: Double,
    val projectedCorpus: Double,
    val shortfallOrSurplus: Double, // positive = surplus, negative = shortfall
    val requiredMonthlySavings: Double,
    val futureMonthlyExpense: Double
)

object FinanceCalculators {

    fun calculateEmi(
        principal: Double,
        annualInterestRate: Double,
        tenureMonths: Int
    ): EmiResult {
        if (principal <= 0 || tenureMonths <= 0) {
            return EmiResult(0.0, principal, 0.0, principal, emptyList())
        }
        if (annualInterestRate <= 0.0) {
            val emi = principal / tenureMonths
            return EmiResult(emi, principal, 0.0, principal, emptyList())
        }

        val monthlyRate = (annualInterestRate / 100.0) / 12.0
        val factor = (1.0 + monthlyRate).pow(tenureMonths.toDouble())
        val monthlyEmi = principal * monthlyRate * factor / (factor - 1.0)
        val totalPayable = monthlyEmi * tenureMonths
        val totalInterest = totalPayable - principal

        // Generate yearly schedule
        val schedule = mutableListOf<AmortizationItem>()
        var balance = principal
        val totalYears = (tenureMonths + 11) / 12

        var currentMonth = 1
        for (year in 1..totalYears) {
            var yearlyPrincipal = 0.0
            var yearlyInterest = 0.0
            val monthsInThisYear = kotlin.math.min(12, tenureMonths - (year - 1) * 12)

            for (m in 1..monthsInThisYear) {
                val interestForMonth = balance * monthlyRate
                val principalForMonth = monthlyEmi - interestForMonth
                yearlyInterest += interestForMonth
                yearlyPrincipal += principalForMonth
                balance = max(0.0, balance - principalForMonth)
                currentMonth++
            }

            schedule.add(
                AmortizationItem(
                    period = year,
                    principalPaid = yearlyPrincipal,
                    interestPaid = yearlyInterest,
                    totalPaid = yearlyPrincipal + yearlyInterest,
                    remainingBalance = balance
                )
            )
        }

        return EmiResult(
            monthlyEmi = monthlyEmi,
            totalPrincipal = principal,
            totalInterest = totalInterest,
            totalPayable = totalPayable,
            schedule = schedule
        )
    }

    fun calculateSip(
        monthlyInvestment: Double,
        annualReturnRate: Double,
        tenureYears: Double
    ): SipResult {
        val totalMonths = (tenureYears * 12).toInt()
        if (monthlyInvestment <= 0 || totalMonths <= 0) {
            return SipResult(0.0, 0.0, 0.0, emptyList())
        }

        val monthlyRate = (annualReturnRate / 100.0) / 12.0
        val totalInvested = monthlyInvestment * totalMonths

        val maturityValue = if (monthlyRate > 0) {
            monthlyInvestment * (( (1.0 + monthlyRate).pow(totalMonths.toDouble()) - 1.0) / monthlyRate) * (1.0 + monthlyRate)
        } else {
            totalInvested
        }

        val estimatedReturns = maturityValue - totalInvested

        // Yearly breakdown
        val breakdown = mutableListOf<YearlyGrowthItem>()
        val yearsCount = max(1, tenureYears.toInt())
        for (y in 1..yearsCount) {
            val months = y * 12
            val investedSoFar = monthlyInvestment * months
            val balanceSoFar = if (monthlyRate > 0) {
                monthlyInvestment * (( (1.0 + monthlyRate).pow(months.toDouble()) - 1.0) / monthlyRate) * (1.0 + monthlyRate)
            } else {
                investedSoFar
            }
            breakdown.add(
                YearlyGrowthItem(
                    year = y,
                    invested = investedSoFar,
                    returns = max(0.0, balanceSoFar - investedSoFar),
                    balance = balanceSoFar
                )
            )
        }

        return SipResult(
            totalInvested = totalInvested,
            estimatedReturns = max(0.0, estimatedReturns),
            maturityValue = maturityValue,
            yearlyBreakdown = breakdown
        )
    }

    fun calculateStepUpSip(
        initialMonthlySip: Double,
        annualReturnRate: Double,
        tenureYears: Int,
        annualStepUpPercent: Double
    ): SipResult {
        if (initialMonthlySip <= 0 || tenureYears <= 0) {
            return SipResult(0.0, 0.0, 0.0, emptyList())
        }

        val monthlyRate = (annualReturnRate / 100.0) / 12.0
        var corpus = 0.0
        var totalInvested = 0.0
        var currentMonthlySip = initialMonthlySip
        val breakdown = mutableListOf<YearlyGrowthItem>()

        for (year in 1..tenureYears) {
            for (month in 1..12) {
                corpus = (corpus + currentMonthlySip) * (1.0 + monthlyRate)
                totalInvested += currentMonthlySip
            }
            breakdown.add(
                YearlyGrowthItem(
                    year = year,
                    invested = totalInvested,
                    returns = max(0.0, corpus - totalInvested),
                    balance = corpus
                )
            )
            currentMonthlySip *= (1.0 + annualStepUpPercent / 100.0)
        }

        return SipResult(
            totalInvested = totalInvested,
            estimatedReturns = max(0.0, corpus - totalInvested),
            maturityValue = corpus,
            yearlyBreakdown = breakdown
        )
    }

    fun calculateLumpsum(
        investmentAmount: Double,
        annualReturnRate: Double,
        tenureYears: Double
    ): SipResult {
        if (investmentAmount <= 0 || tenureYears <= 0) {
            return SipResult(investmentAmount, 0.0, investmentAmount, emptyList())
        }

        val r = annualReturnRate / 100.0
        val maturityValue = investmentAmount * (1.0 + r).pow(tenureYears)
        val estimatedReturns = maturityValue - investmentAmount

        val breakdown = mutableListOf<YearlyGrowthItem>()
        val yearsCount = max(1, tenureYears.toInt())
        for (y in 1..yearsCount) {
            val balance = investmentAmount * (1.0 + r).pow(y.toDouble())
            breakdown.add(
                YearlyGrowthItem(
                    year = y,
                    invested = investmentAmount,
                    returns = max(0.0, balance - investmentAmount),
                    balance = balance
                )
            )
        }

        return SipResult(
            totalInvested = investmentAmount,
            estimatedReturns = max(0.0, estimatedReturns),
            maturityValue = maturityValue,
            yearlyBreakdown = breakdown
        )
    }

    fun calculateSwp(
        initialInvestment: Double,
        monthlyWithdrawal: Double,
        annualReturnRate: Double,
        tenureYears: Int
    ): SwpResult {
        if (initialInvestment <= 0 || tenureYears <= 0) {
            return SwpResult(0.0, 0.0, 0.0, emptyList())
        }

        val monthlyRate = (annualReturnRate / 100.0) / 12.0
        var currentBalance = initialInvestment
        var totalWithdrawn = 0.0
        var totalInterestGenerated = 0.0
        val breakdown = mutableListOf<YearlyGrowthItem>()

        for (year in 1..tenureYears) {
            for (m in 1..12) {
                if (currentBalance <= 0) {
                    currentBalance = 0.0
                    break
                }
                val growth = currentBalance * monthlyRate
                totalInterestGenerated += growth
                currentBalance += growth
                val actualWithdraw = kotlin.math.min(monthlyWithdrawal, currentBalance)
                currentBalance -= actualWithdraw
                totalWithdrawn += actualWithdraw
            }
            breakdown.add(
                YearlyGrowthItem(
                    year = year,
                    invested = initialInvestment,
                    returns = totalWithdrawn,
                    balance = currentBalance
                )
            )
        }

        return SwpResult(
            totalWithdrawn = totalWithdrawn,
            remainingCorpus = currentBalance,
            estimatedGrowthEarned = totalInterestGenerated,
            yearlyBreakdown = breakdown
        )
    }

    fun calculateFd(
        principal: Double,
        annualRate: Double,
        tenureYears: Double,
        compoundingFrequency: Int = 4 // 12=monthly, 4=quarterly, 2=half-yearly, 1=yearly
    ): FdResult {
        if (principal <= 0 || tenureYears <= 0) return FdResult(principal, 0.0, principal)
        val n = max(1, compoundingFrequency).toDouble()
        val r = (annualRate / 100.0) / n
        val totalPeriods = n * tenureYears
        val maturity = principal * (1.0 + r).pow(totalPeriods)
        val interest = maturity - principal
        return FdResult(principal, max(0.0, interest), maturity)
    }

    fun calculateRd(
        monthlyDeposit: Double,
        annualRate: Double,
        tenureMonths: Int
    ): RdResult {
        if (monthlyDeposit <= 0 || tenureMonths <= 0) return RdResult(0.0, 0.0, 0.0)
        val totalDeposit = monthlyDeposit * tenureMonths
        val i = (annualRate / 100.0) / 12.0
        // Standard RD maturity with monthly compounding
        val maturity = if (i > 0) {
            monthlyDeposit * (( (1.0 + i).pow(tenureMonths.toDouble()) - 1.0) / i) * (1.0 + i)
        } else {
            totalDeposit
        }
        val interest = maturity - totalDeposit
        return RdResult(totalDeposit, max(0.0, interest), maturity)
    }

    fun calculatePpf(
        annualInvestment: Double,
        annualRate: Double = 7.1,
        tenureYears: Int = 15
    ): PpfResult {
        if (annualInvestment <= 0 || tenureYears <= 0) return PpfResult(0.0, 0.0, 0.0, emptyList())
        val r = annualRate / 100.0
        var balance = 0.0
        var totalInvested = 0.0
        val breakdown = mutableListOf<YearlyGrowthItem>()

        for (y in 1..tenureYears) {
            totalInvested += annualInvestment
            val interestForYear = (balance + annualInvestment) * r
            balance = balance + annualInvestment + interestForYear
            breakdown.add(
                YearlyGrowthItem(
                    year = y,
                    invested = totalInvested,
                    returns = balance - totalInvested,
                    balance = balance
                )
            )
        }

        return PpfResult(
            totalInvested = totalInvested,
            totalInterest = balance - totalInvested,
            maturityAmount = balance,
            yearlyBreakdown = breakdown
        )
    }

    fun calculateNps(
        currentAge: Int,
        retirementAge: Int,
        monthlyContribution: Double,
        expectedReturnRate: Double,
        annuityPercent: Double = 40.0,
        expectedAnnuityRate: Double = 6.0
    ): NpsResult {
        val years = max(1, retirementAge - currentAge)
        val sipResult = calculateSip(monthlyContribution, expectedReturnRate, years.toDouble())
        val totalCorpus = sipResult.maturityValue
        val annuityRatio = (annuityPercent.coerceIn(40.0, 100.0)) / 100.0
        val annuityAmount = totalCorpus * annuityRatio
        val lumpSumAmount = totalCorpus * (1.0 - annuityRatio)
        val monthlyPension = (annuityAmount * (expectedAnnuityRate / 100.0)) / 12.0

        return NpsResult(
            totalInvested = sipResult.totalInvested,
            totalCorpus = totalCorpus,
            lumpSumAmount = lumpSumAmount,
            annuityAmount = annuityAmount,
            estimatedMonthlyPension = monthlyPension
        )
    }

    fun calculateCagr(
        initialValue: Double,
        finalValue: Double,
        years: Double
    ): Double {
        if (initialValue <= 0 || finalValue <= 0 || years <= 0) return 0.0
        return ((finalValue / initialValue).pow(1.0 / years) - 1.0) * 100.0
    }

    fun calculateSimpleInterest(
        principal: Double,
        annualRate: Double,
        timeYears: Double
    ): Pair<Double, Double> {
        val interest = (principal * annualRate * timeYears) / 100.0
        return Pair(interest, principal + interest)
    }

    fun calculateCompoundInterest(
        principal: Double,
        annualRate: Double,
        timeYears: Double,
        compoundingTimesPerYear: Int = 1
    ): Pair<Double, Double> {
        val n = max(1, compoundingTimesPerYear).toDouble()
        val amount = principal * (1.0 + (annualRate / 100.0) / n).pow(n * timeYears)
        val interest = amount - principal
        return Pair(max(0.0, interest), amount)
    }

    fun calculateInflation(
        currentAmount: Double,
        inflationRate: Double,
        years: Double
    ): Pair<Double, Double> {
        val futureValue = currentAmount * (1.0 + inflationRate / 100.0).pow(years)
        val purchasingPower = currentAmount / (1.0 + inflationRate / 100.0).pow(years)
        return Pair(futureValue, purchasingPower)
    }

    fun calculateRetirement(
        currentAge: Int,
        retirementAge: Int,
        lifeExpectancy: Int = 85,
        currentMonthlyExpense: Double,
        inflationRate: Double = 6.0,
        currentSavings: Double = 0.0,
        monthlyInvestment: Double = 0.0,
        expectedReturnPreRetirement: Double = 12.0,
        expectedReturnPostRetirement: Double = 8.0
    ): RetirementResult {
        val yearsToRetire = max(1, retirementAge - currentAge)
        val yearsInRetirement = max(1, lifeExpectancy - retirementAge)

        // Expense at retirement year
        val futureMonthlyExpense = currentMonthlyExpense * (1.0 + inflationRate / 100.0).pow(yearsToRetire.toDouble())
        val futureAnnualExpense = futureMonthlyExpense * 12.0

        // Real rate of return post-retirement
        val inf = inflationRate / 100.0
        val postRet = expectedReturnPostRetirement / 100.0
        val realRate = if (postRet != inf) ((1.0 + postRet) / (1.0 + inf)) - 1.0 else 0.0001

        // Required corpus at retirement: Present value of annuity of future annual expenses
        val requiredCorpus = if (realRate > 0) {
            futureAnnualExpense * ((1.0 - (1.0 + realRate).pow(-yearsInRetirement.toDouble())) / realRate) * (1.0 + realRate)
        } else {
            futureAnnualExpense * yearsInRetirement
        }

        // Projected corpus from existing savings + current monthly investment
        val preRetRate = expectedReturnPreRetirement / 100.0
        val savingsGrowth = currentSavings * (1.0 + preRetRate).pow(yearsToRetire.toDouble())
        val sipGrowth = if (monthlyInvestment > 0) {
            calculateSip(monthlyInvestment, expectedReturnPreRetirement, yearsToRetire.toDouble()).maturityValue
        } else 0.0
        val projectedCorpus = savingsGrowth + sipGrowth

        val shortfallOrSurplus = projectedCorpus - requiredCorpus

        // Required monthly savings if there is shortfall
        val neededCorpus = max(0.0, requiredCorpus - savingsGrowth)
        val monthlyRate = preRetRate / 12.0
        val months = yearsToRetire * 12
        val requiredMonthlySavings = if (neededCorpus > 0 && monthlyRate > 0) {
            neededCorpus * monthlyRate / (((1.0 + monthlyRate).pow(months.toDouble()) - 1.0) * (1.0 + monthlyRate))
        } else 0.0

        return RetirementResult(
            requiredCorpus = requiredCorpus,
            projectedCorpus = projectedCorpus,
            shortfallOrSurplus = shortfallOrSurplus,
            requiredMonthlySavings = max(0.0, requiredMonthlySavings),
            futureMonthlyExpense = futureMonthlyExpense
        )
    }

    data class StockCryptoProfitResult(
        val totalInvestment: Double,
        val grossExitValue: Double,
        val totalFees: Double,
        val capitalGainsTax: Double,
        val netProfit: Double,
        val netRoiPercent: Double,
        val breakEvenPrice: Double,
        val isProfit: Boolean
    )

    fun calculateStockCryptoProfit(
        buyPrice: Double,
        sellPrice: Double,
        quantity: Double,
        buyFeePercent: Double = 0.1,
        sellFeePercent: Double = 0.1,
        taxPercent: Double = 15.0
    ): StockCryptoProfitResult {
        val qty = max(0.0000001, quantity)
        val rawBuyCost = buyPrice * qty
        val buyFee = rawBuyCost * (buyFeePercent / 100.0)
        val totalInvestment = rawBuyCost + buyFee

        val rawSellRevenue = sellPrice * qty
        val sellFee = rawSellRevenue * (sellFeePercent / 100.0)
        val totalFees = buyFee + sellFee

        val grossProfit = rawSellRevenue - totalInvestment - sellFee
        val tax = if (grossProfit > 0) grossProfit * (taxPercent / 100.0) else 0.0
        val netProfit = grossProfit - tax

        val netRoi = if (totalInvestment > 0) (netProfit / totalInvestment) * 100.0 else 0.0

        val feeFactor = max(0.001, 1.0 - sellFeePercent / 100.0)
        val breakEven = (totalInvestment / feeFactor) / qty

        return StockCryptoProfitResult(
            totalInvestment = totalInvestment,
            grossExitValue = rawSellRevenue,
            totalFees = totalFees,
            capitalGainsTax = tax,
            netProfit = netProfit,
            netRoiPercent = netRoi,
            breakEvenPrice = breakEven,
            isProfit = netProfit >= 0
        )
    }
}
