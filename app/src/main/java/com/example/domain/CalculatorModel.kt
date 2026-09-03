package com.example.domain

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

enum class CalculatorCategory(val title: String, val description: String) {
    FINANCE("Finance", "Investments, loans, interest & retirement"),
    TAX_BUSINESS("Tax & Business", "GST, taxes, profit, discount & salary"),
    CURRENCY("Currency", "Live & offline global exchange rates"),
    GENERAL("General", "Scientific, age, date & mathematical tools"),
    UNIT_CONVERTER("Unit Converter", "Universal conversion across 13 dimensions")
}

data class CalculatorItem(
    val id: String,
    val name: String,
    val description: String,
    val category: CalculatorCategory,
    val icon: ImageVector,
    val route: String,
    val isPopular: Boolean = false,
    val isPro: Boolean = false,
    val keywords: List<String> = emptyList()
)

object CalculatorRegistry {

    val allCalculators: List<CalculatorItem> = listOf(
        // Popular & Finance
        CalculatorItem(
            id = "emi",
            name = "EMI Calculator",
            description = "Calculate loan EMI, interest & amortization schedule",
            category = CalculatorCategory.FINANCE,
            icon = Icons.Default.AccountBalance,
            route = "calc_emi",
            isPopular = true,
            keywords = listOf("loan", "home loan", "car loan", "personal loan", "monthly installment", "interest")
        ),
        CalculatorItem(
            id = "sip",
            name = "SIP Calculator",
            description = "Systematic Investment Plan wealth & returns forecasting",
            category = CalculatorCategory.FINANCE,
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            route = "calc_sip",
            isPopular = true,
            keywords = listOf("sip", "mutual fund", "investment", "wealth", "monthly")
        ),
        CalculatorItem(
            id = "step_up_sip",
            name = "Step-up SIP",
            description = "SIP with yearly increment/step-up percentage",
            category = CalculatorCategory.FINANCE,
            icon = Icons.Default.AutoGraph,
            route = "calc_step_up_sip",
            isPopular = false,
            isPro = true,
            keywords = listOf("step up", "sip increment", "wealth growth")
        ),
        CalculatorItem(
            id = "crypto_stock",
            name = "Stock & Crypto Profit",
            description = "Buy/sell profit, ROI %, capital gains tax & break-even exit price",
            category = CalculatorCategory.FINANCE,
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            route = "calc_crypto_stock",
            isPopular = true,
            isPro = true,
            keywords = listOf("stock", "crypto", "bitcoin", "profit", "capital gains", "roi", "break even", "trading")
        ),
        CalculatorItem(
            id = "lumpsum",
            name = "Lumpsum Calculator",
            description = "One-time mutual fund & stock returns projection",
            category = CalculatorCategory.FINANCE,
            icon = Icons.Default.MonetizationOn,
            route = "calc_lumpsum",
            isPopular = false,
            keywords = listOf("one time", "investment", "compound", "mutual fund")
        ),
        CalculatorItem(
            id = "mutual_fund",
            name = "Mutual Fund Planner",
            description = "Comprehensive planning with SIP, Lumpsum, CAGR & SWP",
            category = CalculatorCategory.FINANCE,
            icon = Icons.Default.Assessment,
            route = "calc_mutual_fund",
            isPopular = true,
            isPro = true,
            keywords = listOf("mf", "portfolio", "returns", "nav", "cagr", "xirr")
        ),
        CalculatorItem(
            id = "swp",
            name = "SWP Calculator",
            description = "Systematic Withdrawal Plan & remaining balance decay",
            category = CalculatorCategory.FINANCE,
            icon = Icons.Default.HourglassBottom,
            route = "calc_swp",
            isPopular = false,
            keywords = listOf("withdrawal", "pension", "monthly income", "corpus")
        ),
        CalculatorItem(
            id = "fd",
            name = "FD Calculator",
            description = "Fixed Deposit maturity & compounded interest",
            category = CalculatorCategory.FINANCE,
            icon = Icons.Default.Savings,
            route = "calc_fd",
            isPopular = true,
            isPro = true,
            keywords = listOf("fixed deposit", "bank", "term deposit", "interest")
        ),
        CalculatorItem(
            id = "rd",
            name = "RD Calculator",
            description = "Recurring Deposit monthly saving maturity",
            category = CalculatorCategory.FINANCE,
            icon = Icons.Default.AccountBalanceWallet,
            route = "calc_rd",
            isPopular = true,
            keywords = listOf("recurring deposit", "bank saving", "monthly deposit")
        ),
        CalculatorItem(
            id = "ppf",
            name = "PPF Calculator",
            description = "Public Provident Fund 15-year tax-free maturity",
            category = CalculatorCategory.FINANCE,
            icon = Icons.Default.ReceiptLong,
            route = "calc_ppf",
            isPopular = false,
            keywords = listOf("ppf", "provident fund", "tax saving", "government scheme")
        ),
        CalculatorItem(
            id = "nps",
            name = "NPS Calculator",
            description = "National Pension Scheme corpus & monthly pension",
            category = CalculatorCategory.FINANCE,
            icon = Icons.Default.Work,
            route = "calc_nps",
            isPopular = false,
            keywords = listOf("nps", "pension", "annuity", "retirement")
        ),
        CalculatorItem(
            id = "cagr",
            name = "CAGR Calculator",
            description = "Compound Annual Growth Rate of investments",
            category = CalculatorCategory.FINANCE,
            icon = Icons.Default.Timeline,
            route = "calc_cagr",
            isPopular = false,
            keywords = listOf("cagr", "annual growth", "stock returns", "cagr formula")
        ),
        CalculatorItem(
            id = "xirr",
            name = "XIRR / ROI Calculator",
            description = "Extended Internal Rate of Return & ROI calculator",
            category = CalculatorCategory.FINANCE,
            icon = Icons.Default.Assessment,
            route = "calc_xirr",
            isPopular = false,
            keywords = listOf("xirr", "irr", "roi", "rate of return")
        ),
        CalculatorItem(
            id = "simple_interest",
            name = "Simple Interest",
            description = "Calculate flat simple interest over time",
            category = CalculatorCategory.FINANCE,
            icon = Icons.Default.Percent,
            route = "calc_simple_interest",
            isPopular = false,
            keywords = listOf("si", "flat interest", "principal rate time")
        ),
        CalculatorItem(
            id = "compound_interest",
            name = "Compound Interest",
            description = "Compound interest with customizable frequencies",
            category = CalculatorCategory.FINANCE,
            icon = Icons.Default.Percent,
            route = "calc_compound_interest",
            isPopular = false,
            keywords = listOf("ci", "compounding", "annual compounding", "growth")
        ),
        CalculatorItem(
            id = "inflation",
            name = "Inflation Calculator",
            description = "Future purchasing power & required cost of living",
            category = CalculatorCategory.FINANCE,
            icon = Icons.Default.PriceChange,
            route = "calc_inflation",
            isPopular = false,
            keywords = listOf("inflation", "purchasing power", "cost of living", "future value")
        ),
        CalculatorItem(
            id = "retirement",
            name = "Retirement Planner",
            description = "Estimate required retirement corpus & monthly savings",
            category = CalculatorCategory.FINANCE,
            icon = Icons.Default.Savings,
            route = "calc_retirement",
            isPopular = false,
            isPro = true,
            keywords = listOf("retirement", "pension", "fire", "corpus shortfall")
        ),
        CalculatorItem(
            id = "loan",
            name = "Loan Calculator",
            description = "Home, Personal, Car & Education loan analyzer",
            category = CalculatorCategory.FINANCE,
            icon = Icons.Default.AccountBalance,
            route = "calc_loan",
            isPopular = true,
            keywords = listOf("home loan", "car loan", "education loan", "personal loan", "emi")
        ),

        // Tax & Business
        CalculatorItem(
            id = "gst",
            name = "GST Calculator",
            description = "Calculate GST Inclusive & Exclusive with all slab rates",
            category = CalculatorCategory.TAX_BUSINESS,
            icon = Icons.Default.Receipt,
            route = "calc_gst",
            isPopular = true,
            keywords = listOf("gst", "tax", "inclusive", "exclusive", "18%", "5%", "12%", "28%")
        ),
        CalculatorItem(
            id = "income_tax",
            name = "Income Tax Calculator",
            description = "Tax calculation under Old and New regimes with deductions",
            category = CalculatorCategory.TAX_BUSINESS,
            icon = Icons.Default.PointOfSale,
            route = "calc_income_tax",
            isPopular = false,
            keywords = listOf("income tax", "tax slabs", "old regime", "new regime", "80c", "deductions")
        ),
        CalculatorItem(
            id = "discount",
            name = "Discount Calculator",
            description = "Sale discounts, consecutive discounts & final price",
            category = CalculatorCategory.TAX_BUSINESS,
            icon = Icons.Default.LocalOffer,
            route = "calc_discount",
            isPopular = false,
            keywords = listOf("sale", "discount", "offer", "savings", "percentage off")
        ),
        CalculatorItem(
            id = "profit_loss",
            name = "Profit & Loss",
            description = "Cost price, selling price, profit/loss & percentage",
            category = CalculatorCategory.TAX_BUSINESS,
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            route = "calc_profit_loss",
            isPopular = false,
            keywords = listOf("profit", "loss", "cost price", "selling price", "margin")
        ),
        CalculatorItem(
            id = "markup",
            name = "Markup Calculator",
            description = "Calculate markup percentage and selling price",
            category = CalculatorCategory.TAX_BUSINESS,
            icon = Icons.Default.PriceChange,
            route = "calc_markup",
            isPopular = false,
            keywords = listOf("markup", "pricing", "cost plus")
        ),
        CalculatorItem(
            id = "margin",
            name = "Margin Calculator",
            description = "Gross margin & profit margin calculation",
            category = CalculatorCategory.TAX_BUSINESS,
            icon = Icons.Default.Scale,
            route = "calc_margin",
            isPopular = false,
            keywords = listOf("margin", "gross profit", "revenue")
        ),
        CalculatorItem(
            id = "salary",
            name = "Salary (In-Hand) Calculator",
            description = "CTC breakdown, Basic, HRA, PF, PT & In-Hand take-home",
            category = CalculatorCategory.TAX_BUSINESS,
            icon = Icons.Default.Money,
            route = "calc_salary",
            isPopular = false,
            keywords = listOf("salary", "ctc", "in hand", "take home", "pf", "tax deduction")
        ),
        CalculatorItem(
            id = "percentage",
            name = "Percentage Calculator",
            description = "% of number, percentage change, increase & decrease",
            category = CalculatorCategory.TAX_BUSINESS,
            icon = Icons.Default.Percent,
            route = "calc_percentage",
            isPopular = false,
            keywords = listOf("percentage", "increase", "decrease", "fraction", "ratio")
        ),

        // Currency
        CalculatorItem(
            id = "currency",
            name = "Currency Converter",
            description = "Live and offline multi-currency exchange conversion",
            category = CalculatorCategory.CURRENCY,
            icon = Icons.Default.CurrencyExchange,
            route = "calc_currency",
            isPopular = true,
            isPro = true,
            keywords = listOf("currency", "usd", "inr", "eur", "forex", "exchange rate", "gbp", "aed")
        ),

        // General
        CalculatorItem(
            id = "scientific",
            name = "Scientific Calculator",
            description = "Trigonometry, logarithms, powers, roots & expressions",
            category = CalculatorCategory.GENERAL,
            icon = Icons.Default.Functions,
            route = "calc_scientific",
            isPopular = true,
            keywords = listOf("scientific", "sin", "cos", "tan", "log", "sqrt", "power", "math")
        ),
        CalculatorItem(
            id = "age",
            name = "Age Calculator",
            description = "Exact age in years, months, days & next birthday countdown",
            category = CalculatorCategory.GENERAL,
            icon = Icons.Default.Cake,
            route = "calc_age",
            isPopular = true,
            keywords = listOf("age", "dob", "birthday", "days old", "next birthday")
        ),
        CalculatorItem(
            id = "date_diff",
            name = "Date Difference",
            description = "Exact duration between two dates in days, weeks & months",
            category = CalculatorCategory.GENERAL,
            icon = Icons.Default.DateRange,
            route = "calc_date_diff",
            isPopular = false,
            keywords = listOf("date", "calendar", "days between", "duration")
        ),
        CalculatorItem(
            id = "time_calc",
            name = "Time Calculator",
            description = "Add, subtract and convert hours, minutes and seconds",
            category = CalculatorCategory.GENERAL,
            icon = Icons.Default.Schedule,
            route = "calc_time",
            isPopular = false,
            keywords = listOf("time", "hours", "minutes", "duration", "clock")
        ),
        CalculatorItem(
            id = "ratio",
            name = "Ratio Calculator",
            description = "Simplify ratios, find missing terms & scale proportions",
            category = CalculatorCategory.GENERAL,
            icon = Icons.Default.Scale,
            route = "calc_ratio",
            isPopular = false,
            keywords = listOf("ratio", "proportion", "simplify", "scale")
        ),
        CalculatorItem(
            id = "average",
            name = "Average & Mean",
            description = "Mean, median, mode, sum and standard statistics",
            category = CalculatorCategory.GENERAL,
            icon = Icons.Default.Calculate,
            route = "calc_average",
            isPopular = false,
            keywords = listOf("average", "mean", "median", "mode", "stats", "sum")
        ),

        // Unit Converter
        CalculatorItem(
            id = "unit_converter",
            name = "Universal Unit Converter",
            description = "Convert Length, Mass, Area, Volume, Temp, Speed, Data & more",
            category = CalculatorCategory.UNIT_CONVERTER,
            icon = Icons.Default.Straighten,
            route = "calc_unit_converter",
            isPopular = true,
            keywords = listOf("unit", "converter", "length", "weight", "kg", "lbs", "km", "miles", "celsius", "fahrenheit", "gb", "mb")
        )
    )

    val popularCalculators: List<CalculatorItem>
        get() = allCalculators.filter { it.isPopular }

    fun getByCategory(category: CalculatorCategory): List<CalculatorItem> =
        allCalculators.filter { it.category == category }

    fun getById(id: String): CalculatorItem? = allCalculators.find { it.id == id }

    fun search(query: String): List<CalculatorItem> {
        if (query.isBlank()) return allCalculators
        val q = query.trim().lowercase()
        return allCalculators.filter { item ->
            item.name.lowercase().contains(q) ||
            item.description.lowercase().contains(q) ||
            item.keywords.any { it.lowercase().contains(q) } ||
            item.category.title.lowercase().contains(q)
        }
    }
}
