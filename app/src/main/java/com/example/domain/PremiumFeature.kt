package com.example.domain

/**
 * Enum defining all premium calculator tools and capabilities
 * that require an active subscription to access.
 */
enum class PremiumFeature(val featureId: String, val displayName: String, val description: String) {
    FD_CALCULATOR("fd", "Fixed Deposit Pro", "Fixed deposit maturity and quarterly compounding calculator"),
    SWP_CALCULATOR("swp", "Mutual Fund SWP", "Systematic withdrawal planner for regular income"),
    CAGR_CALCULATOR("cagr", "CAGR Calculator", "Compound annual growth rate calculation"),
    STOCK_CRYPTO_PRO("crypto_stock", "Stock & Crypto Profit Pro", "Tax slab calculations and net returns after brokerage"),
    AMORTIZATION_EXPORT("amortization", "Year-wise Amortization", "Full payment schedules and PDF breakdown tables"),
    STEP_UP_SIP("step_up_sip", "Step-Up SIP Planner", "Annual increment SIP wealth forecasting"),
    RETIREMENT_PLANNER("retirement", "Retirement & FIRE Planner", "Financial independence and early retirement calculator"),
    LIVE_CURRENCY("currency_live", "Live Global Currency", "Real-time exchange rates for 160+ international currencies"),
    ALL_PREMIUM("all", "All Pro Features", "Unrestricted access to all 20+ smart calculators");

    companion object {
        fun fromCalculatorId(calcId: String): PremiumFeature? {
            return when (calcId) {
                "fd" -> FD_CALCULATOR
                "swp" -> SWP_CALCULATOR
                "cagr" -> CAGR_CALCULATOR
                "crypto_stock" -> STOCK_CRYPTO_PRO
                "amortization" -> AMORTIZATION_EXPORT
                "step_up_sip" -> STEP_UP_SIP
                "retirement" -> RETIREMENT_PLANNER
                "currency" -> LIVE_CURRENCY
                else -> null
            }
        }
    }
}
