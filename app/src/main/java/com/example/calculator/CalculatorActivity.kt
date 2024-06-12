package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.math.BigDecimal
import java.math.RoundingMode

class CalculatorActivity : AppCompatActivity() {

    private lateinit var inputField: TextView
    private lateinit var resultField: TextView
    private var oldNumber = "0"
    private var operator = ""
    private var isNew = true
    private lateinit var pressedButton: Button
    private var operatorButtonPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        bindViews()
    }

    private fun bindViews() {
        inputField = findViewById(R.id.inputField)
        resultField = findViewById(R.id.result)

        val buttons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6,
            R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot, R.id.btnAdd, R.id.btnSubtract,
            R.id.btnMultiply, R.id.btnDivide, R.id.btnEquals, R.id.btnClear, R.id.btnSign,
            R.id.btnPercent
        )

        buttons.forEach { id ->
            findViewById<Button>(id).setOnClickListener { onButtonClick(it as Button) }
        }
    }

    private fun onButtonClick(button: Button) {
        if (isNew) inputField.text = ""
        isNew = false
        val value = button.text.toString()
        when (value) {
            "C" -> {
                clearFields()
            }
            "+/−" -> {
                changeSign()
            }
            "%" -> {
                percentage()
            }
            "+", "−", "×", "÷" -> {
                handleOperator(button, value)
            }
            "=" -> {
                calculateResult()
            }
            "." -> {
                handleDecimalPoint(value)
            }
            "0" -> {
                handleZero()
            }
            else -> {
                handleDigit(value)
            }
        }
    }

    private fun clearFields() {
        if (operatorButtonPressed) {
            pressedButton.background = getDrawable(R.drawable.rounded_button)
            operatorButtonPressed = false
        }
        inputField.text = "0"
        resultField.text = ""
        operator = ""
        isNew = true
    }

    private fun changeSign() {
        val value = inputField.text.toString()
        if (value != "0") {
            if (value.contains('-')) {
                val newText = inputField.text.substring(1)
                inputField.text = newText
            } else {
                val newText = "-${inputField.text}"
                inputField.text = newText
            }
        }
    }

    private fun percentage() {
        if (operator.isEmpty()) {
            val result: Double = inputField.text.toString().toDouble() / 100
            resultField.text = result.toString()
        } else {
            val newNumber = inputField.text.toString()
            var result = 0.0
            when(operator) {
                "+" -> result = oldNumber.toDouble() + oldNumber.toDouble() * newNumber.toDouble() / 100
                "−" -> result = oldNumber.toDouble() - oldNumber.toDouble() * newNumber.toDouble() / 100
                "×" -> result = oldNumber.toDouble() * newNumber.toDouble() / 100
                "÷" -> result = oldNumber.toDouble() / newNumber.toDouble() * 100
            }
            resultField.text = result.toString()
            operator = ""
            oldNumber = result.toString()
        }
    }

    private fun handleOperator(button: Button, value: String) {
        if (operatorButtonPressed) {
            pressedButton.background = getDrawable(R.drawable.rounded_button)
        }
        button.background = getDrawable(R.drawable.pressed_button)
        pressedButton = button
        operatorButtonPressed = true
        operation(value)
    }

    private fun operation(value: String) {
        isNew = true
        if (operator.isNotEmpty()) {
            calculate()
        }
        operator = value
        oldNumber = if (resultField.text.isNotEmpty()) {
            resultField.text.toString()
        } else {
            inputField.text.toString()
        }
        isNew = true
    }

    private fun calculateResult() {
        if (operatorButtonPressed) {
            pressedButton.background = getDrawable(R.drawable.rounded_button)
            operatorButtonPressed = false
        }
        calculate()
        operator = ""
    }

    private fun calculate() {
        if (operator.isEmpty()) return

        val newNumber = inputField.text.toString().toDoubleOrNull()
        if (newNumber == null) {
            clearFields()
            return
        }

        var result = 0.0
        when (operator) {
            "+" -> result = oldNumber.toDouble() + newNumber
            "−" -> result = oldNumber.toDouble() - newNumber
            "×" -> result = oldNumber.toDouble() * newNumber
            "÷" -> if (newNumber != 0.0) {
                result = oldNumber.toDouble() / newNumber
            } else {
                inputField.text = getString(R.string.error)
                return
            }
        }
        val formattedResult = BigDecimal(result).setScale(5, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString().replace(",", ".")
        resultField.text = formattedResult
        inputField.text = formattedResult
        oldNumber = formattedResult
    }

    private fun handleDecimalPoint(value: String) {
        if (zeroIsFirst()) {
            inputField.text = "0."
        }
        if (!inputField.text.contains('.')) inputField.append(value)
    }

    private fun handleZero() {
        if (zeroIsFirst() && inputField.text.length == 1) {
            inputField.text = "0"
        } else {
            inputField.append("0")
        }
    }

    private fun zeroIsFirst(): Boolean {
        val number = inputField.text.toString()
        if (number.isEmpty()) return true
        return number.startsWith("0")
    }

    private fun handleDigit(value: String) {
        if (operatorButtonPressed) {
            pressedButton.background = getDrawable(R.drawable.rounded_button)
            operatorButtonPressed = false
        }
        val inputText = inputField.text.toString()
        if (inputText.startsWith("0") && !inputText.contains(".") || inputText == getString(R.string.error)) inputField.text = ""
        inputField.append(value)
    }
}