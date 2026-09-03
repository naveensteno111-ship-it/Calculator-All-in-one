package com.example.domain

enum class UnitCategory(val title: String) {
    LENGTH("Length"),
    WEIGHT("Weight / Mass"),
    AREA("Area"),
    VOLUME("Volume"),
    TEMPERATURE("Temperature"),
    SPEED("Speed"),
    TIME("Time"),
    DATA("Data Storage"),
    ENERGY("Energy"),
    PRESSURE("Pressure"),
    POWER("Power"),
    FREQUENCY("Frequency"),
    ANGLE("Angle")
}

data class UnitItem(
    val name: String,
    val symbol: String,
    val toBaseMultiplier: Double // multiplier to convert 1 unit to standard SI base unit
)

object UnitConverterEngine {

    val categories: Map<UnitCategory, List<UnitItem>> = mapOf(
        UnitCategory.LENGTH to listOf(
            UnitItem("Meter", "m", 1.0),
            UnitItem("Kilometer", "km", 1000.0),
            UnitItem("Centimeter", "cm", 0.01),
            UnitItem("Millimeter", "mm", 0.001),
            UnitItem("Mile", "mi", 1609.344),
            UnitItem("Yard", "yd", 0.9144),
            UnitItem("Foot", "ft", 0.3048),
            UnitItem("Inch", "in", 0.0254),
            UnitItem("Nautical Mile", "NM", 1852.0)
        ),
        UnitCategory.WEIGHT to listOf(
            UnitItem("Kilogram", "kg", 1.0),
            UnitItem("Gram", "g", 0.001),
            UnitItem("Milligram", "mg", 0.000001),
            UnitItem("Metric Ton", "t", 1000.0),
            UnitItem("Pound", "lb", 0.45359237),
            UnitItem("Ounce", "oz", 0.0283495231),
            UnitItem("Carat", "ct", 0.0002)
        ),
        UnitCategory.AREA to listOf(
            UnitItem("Square Meter", "m²", 1.0),
            UnitItem("Square Kilometer", "km²", 1_000_000.0),
            UnitItem("Square Foot", "ft²", 0.092903),
            UnitItem("Square Yard", "yd²", 0.836127),
            UnitItem("Acre", "ac", 4046.86),
            UnitItem("Hectare", "ha", 10000.0),
            UnitItem("Square Mile", "mi²", 2589988.11)
        ),
        UnitCategory.VOLUME to listOf(
            UnitItem("Liter", "L", 1.0),
            UnitItem("Milliliter", "mL", 0.001),
            UnitItem("Cubic Meter", "m³", 1000.0),
            UnitItem("Gallon (US)", "gal", 3.78541),
            UnitItem("Quart (US)", "qt", 0.946353),
            UnitItem("Pint (US)", "pt", 0.473176),
            UnitItem("Fluid Ounce (US)", "fl oz", 0.0295735),
            UnitItem("Cup (US)", "cup", 0.236588)
        ),
        UnitCategory.TEMPERATURE to listOf(
            UnitItem("Celsius", "°C", 1.0),
            UnitItem("Fahrenheit", "°F", 1.0),
            UnitItem("Kelvin", "K", 1.0)
        ),
        UnitCategory.SPEED to listOf(
            UnitItem("Kilometers per hour", "km/h", 0.277778),
            UnitItem("Miles per hour", "mph", 0.44704),
            UnitItem("Meters per second", "m/s", 1.0),
            UnitItem("Knots", "kn", 0.514444),
            UnitItem("Feet per second", "ft/s", 0.3048)
        ),
        UnitCategory.TIME to listOf(
            UnitItem("Second", "s", 1.0),
            UnitItem("Millisecond", "ms", 0.001),
            UnitItem("Minute", "min", 60.0),
            UnitItem("Hour", "hr", 3600.0),
            UnitItem("Day", "d", 86400.0),
            UnitItem("Week", "wk", 604800.0),
            UnitItem("Month (30d)", "mo", 2592000.0),
            UnitItem("Year (365d)", "yr", 31536000.0)
        ),
        UnitCategory.DATA to listOf(
            UnitItem("Byte", "B", 1.0),
            UnitItem("Kilobyte", "KB", 1024.0),
            UnitItem("Megabyte", "MB", 1048576.0),
            UnitItem("Gigabyte", "GB", 1073741824.0),
            UnitItem("Terabyte", "TB", 1099511627776.0),
            UnitItem("Petabyte", "PB", 1125899906842624.0),
            UnitItem("Bit", "b", 0.125)
        ),
        UnitCategory.ENERGY to listOf(
            UnitItem("Joule", "J", 1.0),
            UnitItem("Kilojoule", "kJ", 1000.0),
            UnitItem("Calorie", "cal", 4.184),
            UnitItem("Kilocalorie", "kcal", 4184.0),
            UnitItem("Watt-hour", "Wh", 3600.0),
            UnitItem("Kilowatt-hour", "kWh", 3600000.0),
            UnitItem("BTU", "BTU", 1055.06)
        ),
        UnitCategory.PRESSURE to listOf(
            UnitItem("Pascal", "Pa", 1.0),
            UnitItem("Kilopascal", "kPa", 1000.0),
            UnitItem("Bar", "bar", 100000.0),
            UnitItem("PSI", "psi", 6894.76),
            UnitItem("Atmosphere", "atm", 101325.0),
            UnitItem("mmHg (Torr)", "mmHg", 133.322)
        ),
        UnitCategory.POWER to listOf(
            UnitItem("Watt", "W", 1.0),
            UnitItem("Kilowatt", "kW", 1000.0),
            UnitItem("Megawatt", "MW", 1000000.0),
            UnitItem("Horsepower (hp)", "hp", 745.7),
            UnitItem("Metric Horsepower", "PS", 735.499)
        ),
        UnitCategory.FREQUENCY to listOf(
            UnitItem("Hertz", "Hz", 1.0),
            UnitItem("Kilohertz", "kHz", 1000.0),
            UnitItem("Megahertz", "MHz", 1000000.0),
            UnitItem("Gigahertz", "GHz", 1000000000.0),
            UnitItem("RPM", "rpm", 1.0 / 60.0)
        ),
        UnitCategory.ANGLE to listOf(
            UnitItem("Degree", "°", 1.0),
            UnitItem("Radian", "rad", 180.0 / Math.PI),
            UnitItem("Gradian", "grad", 0.9)
        )
    )

    fun convert(
        category: UnitCategory,
        fromUnit: UnitItem,
        toUnit: UnitItem,
        value: Double
    ): Double {
        if (category == UnitCategory.TEMPERATURE) {
            return convertTemperature(fromUnit.symbol, toUnit.symbol, value)
        }
        val baseValue = value * fromUnit.toBaseMultiplier
        return baseValue / toUnit.toBaseMultiplier
    }

    private fun convertTemperature(fromSymbol: String, toSymbol: String, value: Double): Double {
        if (fromSymbol == toSymbol) return value
        // Convert to Celsius first
        val celsius = when (fromSymbol) {
            "°C" -> value
            "°F" -> (value - 32.0) * (5.0 / 9.0)
            "K" -> value - 273.15
            else -> value
        }
        // Convert from Celsius to target
        return when (toSymbol) {
            "°C" -> celsius
            "°F" -> (celsius * (9.0 / 5.0)) + 32.0
            "K" -> celsius + 273.15
            else -> celsius
        }
    }
}
