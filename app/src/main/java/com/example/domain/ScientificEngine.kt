package com.example.domain

import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

class ScientificEngine {

    var isRadianMode: Boolean = false
    var memoryValue: Double = 0.0

    fun evaluate(expression: String): Result<Double> {
        return try {
            val sanitized = expression
                .replace("×", "*")
                .replace("÷", "/")
                .replace("−", "-")
                .replace("π", Math.PI.toString())
                .replace("e", Math.E.toString())

            val parser = ExpressionParser(sanitized, isRadianMode)
            val result = parser.parse()
            if (result.isNaN() || result.isInfinite()) {
                Result.failure(IllegalArgumentException("Math Error"))
            } else {
                Result.success(result)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private class ExpressionParser(private val src: String, private val isRad: Boolean) {
        private var pos = -1
        private var ch = ' '

        private fun nextChar() {
            pos++
            ch = if (pos < src.length) src[pos] else '\u0000'
        }

        private fun eat(charToEat: Char): Boolean {
            while (ch == ' ') nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < src.length) throw IllegalArgumentException("Unexpected: '$ch'")
            return x
        }

        // Grammar:
        // Expression = Term | Expression `+` Term | Expression `-` Term
        // Term = Factor | Term `*` Factor | Term `/` Factor | Term `%` Factor
        // Factor = `+` Factor | `-` Factor | ( Expression ) | Number | FunctionName Factor | Factor `^` Factor | Factor `!`

        private fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                when {
                    eat('+') -> x += parseTerm()
                    eat('-') -> x -= parseTerm()
                    else -> return x
                }
            }
        }

        private fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                when {
                    eat('*') -> x *= parseFactor()
                    eat('/') -> {
                        val divisor = parseFactor()
                        if (divisor == 0.0) throw ArithmeticException("Division by zero")
                        x /= divisor
                    }
                    eat('%') -> x %= parseFactor()
                    else -> return x
                }
            }
        }

        private fun parseFactor(): Double {
            if (eat('+')) return +parseFactor()
            if (eat('-')) return -parseFactor()

            var x: Double
            val startPos = pos

            if (eat('(')) {
                x = parseExpression()
                eat(')')
            } else if ((ch in '0'..'9') || ch == '.') {
                while ((ch in '0'..'9') || ch == '.') nextChar()
                x = src.substring(startPos, pos).toDouble()
            } else if (ch in 'a'..'z' || ch in 'A'..'Z') {
                while (ch in 'a'..'z' || ch in 'A'..'Z') nextChar()
                val func = src.substring(startPos, pos).lowercase()
                x = parseFactor()
                x = when (func) {
                    "sqrt" -> sqrt(x)
                    "sin" -> if (isRad) sin(x) else sin(Math.toRadians(x))
                    "cos" -> if (isRad) cos(x) else cos(Math.toRadians(x))
                    "tan" -> if (isRad) tan(x) else tan(Math.toRadians(x))
                    "log" -> log10(x)
                    "ln" -> ln(x)
                    "abs" -> kotlin.math.abs(x)
                    "exp" -> Math.E.pow(x)
                    else -> throw IllegalArgumentException("Unknown function: $func")
                }
            } else {
                throw IllegalArgumentException("Unexpected character: '$ch'")
            }

            if (eat('^')) x = x.pow(parseFactor())
            if (eat('!')) x = factorial(x)

            return x
        }

        private fun factorial(n: Double): Double {
            val i = n.toInt()
            if (i < 0 || n != i.toDouble()) throw IllegalArgumentException("Factorial of non-positive integer")
            if (i > 170) return Double.POSITIVE_INFINITY
            var res = 1.0
            for (j in 2..i) res *= j
            return res
        }
    }
}
