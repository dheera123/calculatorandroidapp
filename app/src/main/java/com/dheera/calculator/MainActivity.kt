package com.dheera.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dheera.calculator.ui.theme.CalculatorAndroidAppTheme
import java.text.DecimalFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorAndroidAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    CalculatorScreen()
                }
            }
        }
    }
}

@Composable
private fun CalculatorScreen() {
    var expression by rememberSaveable { mutableStateOf("") }
    var result by rememberSaveable { mutableStateOf("0") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (expression.isBlank()) "0" else expression,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            fontSize = 36.sp,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2
        )

        Text(
            text = result,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(24.dp))

        val buttonRows = listOf(
            listOf("C", "DEL", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "=", "")
        )

        buttonRows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { label ->
                    if (label.isBlank()) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        CalculatorButton(
                            label = label,
                            weight = if (label == "0") 2f else 1f,
                            onClick = {
                                when (label) {
                                    "C" -> {
                                        expression = ""
                                        result = "0"
                                    }
                                    "DEL" -> {
                                        if (expression.isNotEmpty()) {
                                            expression = expression.dropLast(1)
                                        }
                                    }
                                    "=" -> {
                                        result = evaluateExpression(expression)
                                    }
                                    else -> {
                                        expression += label
                                    }
                                }
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun CalculatorButton(
    label: String,
    weight: Float = 1f,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .weight(weight)
            .height(72.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (label in listOf("=", "+", "-", "×", "÷")) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface,
            contentColor = if (label in listOf("=", "+", "-", "×", "÷")) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(text = label, fontSize = 24.sp)
    }
}

private fun evaluateExpression(input: String): String {
    if (input.isBlank()) return "0"
    return try {
        val expression = input
            .replace('×', '*')
            .replace('÷', '/')
            .replace("%", "/100")
        val result = ExpressionEvaluator().evaluate(expression)
        DecimalFormat("0.######").format(result)
    } catch (exception: Exception) {
        "Error"
    }
}

private class ExpressionEvaluator {
    private lateinit var expression: String
    private var index = 0
    private var currentChar: Char = '\u0000'

    fun evaluate(expression: String): Double {
        this.expression = expression
        index = 0
        nextChar()
        val value = parseExpression()
        if (currentChar != '\u0000') {
            throw IllegalArgumentException("Unexpected character: $currentChar")
        }
        return value
    }

    private fun nextChar() {
        currentChar = if (index < expression.length) expression[index++] else '\u0000'
    }

    private fun eat(charToEat: Char): Boolean {
        while (currentChar == ' ') nextChar()
        if (currentChar == charToEat) {
            nextChar()
            return true
        }
        return false
    }

    private fun parseExpression(): Double {
        var value = parseTerm()
        while (true) {
            when {
                eat('+') -> value += parseTerm()
                eat('-') -> value -= parseTerm()
                else -> return value
            }
        }
    }

    private fun parseTerm(): Double {
        var value = parseFactor()
        while (true) {
            when {
                eat('*') -> value *= parseFactor()
                eat('/') -> value /= parseFactor()
                else -> return value
            }
        }
    }

    private fun parseFactor(): Double {
        if (eat('+')) return parseFactor()
        if (eat('-')) return -parseFactor()

        val start = index - 1
        if (currentChar.isDigit() || currentChar == '.') {
            while (currentChar.isDigit() || currentChar == '.') {
                nextChar()
            }
            return expression.substring(start, index - 1).toDouble()
        }

        throw IllegalArgumentException("Unexpected character: $currentChar")
    }
}
