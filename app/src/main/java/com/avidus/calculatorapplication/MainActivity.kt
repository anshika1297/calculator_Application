package com.avidus.calculatorapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.avidus.calculatorapplication.databinding.ActivityMainBinding
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAC.setOnClickListener {
            clearTextViews()
        }

        binding.buttonEmpty.setOnClickListener {
            removeLastCharacter()
        }

        binding.buttonEqual.setOnClickListener {
            evaluateExpressionAndDisplayResult()
        }

        // You can set click listeners for other buttons if needed
        val buttonClickListener = { buttonText: String ->
            binding.Number.append(buttonText)
        }

        val buttons = listOf(
            binding.button0, binding.button1, binding.button2, binding.button3,
            binding.button4, binding.button5, binding.button6,
            binding.button7, binding.button8, binding.button9
        )

        buttons.forEach { button ->
            button.setOnClickListener { buttonClickListener(button.text.toString()) }
        }

        binding.buttonDot.setOnClickListener { buttonClickListener(".") }
        binding.buttonDivide.setOnClickListener { buttonClickListener("/") }
        binding.buttonMultiply.setOnClickListener { buttonClickListener("*") }
        binding.buttonMinus.setOnClickListener { buttonClickListener("-") }
        binding.buttonPlus.setOnClickListener { buttonClickListener("+") }
    }

    private fun clearTextViews() {
        binding.Number.text = ""
        binding.result.text = ""
    }

    private fun removeLastCharacter() {
        val text = binding.Number.text.toString()
        if (text.isNotEmpty()) {
            binding.Number.text = text.dropLast(1)
        }
    }

    private fun evaluateExpressionAndDisplayResult() {
        val expression = binding.Number.text.toString()
        try {
            val result = evaluateExpression(expression)
            binding.result.text = result.toString()
        } catch (e: Exception) {
            binding.result.text = "Error"
        }
    }

    private fun evaluateExpression(expression: String): Double {
        val values = Stack<Double>()
        val operators = Stack<Char>()

        var i = 0
        while (i < expression.length) {
            if (expression[i] == ' ') {
                i++
                continue
            }

            if (expression[i].isDigit() || expression[i] == '.') {
                val sb = StringBuilder()
                while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) {
                    sb.append(expression[i++])
                }
                values.push(sb.toString().toDouble())
                i--
            } else if (expression[i] == '(') {
                operators.push(expression[i])
            } else if (expression[i] == ')') {
                while (operators.peek() != '(') {
                    values.push(applyOp(operators.pop(), values.pop(), values.pop()))
                }
                operators.pop()
            } else if (isOperator(expression[i])) {
                while (!operators.isEmpty() && hasPrecedence(expression[i], operators.peek())) {
                    values.push(applyOp(operators.pop(), values.pop(), values.pop()))
                }
                operators.push(expression[i])
            }
            i++
        }

        while (!operators.isEmpty()) {
            values.push(applyOp(operators.pop(), values.pop(), values.pop()))
        }

        return values.pop()
    }

    private fun isOperator(c: Char): Boolean {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%'
    }

    private fun hasPrecedence(op1: Char, op2: Char): Boolean {
        if (op2 == '(' || op2 == ')') {
            return false
        }
        if ((op1 == '*' || op1 == '/' || op1 == '%') && (op2 == '+' || op2 == '-')) {
            return false
        }
        return true
    }

    private fun applyOp(op: Char, b: Double, a: Double): Double {
        return when (op) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> {
                if (b == 0.0) throw UnsupportedOperationException("Cannot divide by zero")
                a / b
            }
            '%' -> a % b
            else -> throw UnsupportedOperationException("Unknown operator $op")
        }
    }
}
