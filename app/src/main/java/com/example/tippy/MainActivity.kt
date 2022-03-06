package com.example.tippy

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15
class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvTipPercentLabel: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipPercentLabel = findViewById(R.id.tvTipPercentLabel)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)

        // Initial setting of values
        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercentLabel.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)
        seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                Log.i(TAG,"onProgressChanged $p1")
                tvTipPercentLabel.text = "$p1%"
                computeTipAndTotal()
                updateTipDescription(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
        etBaseAmount.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                Log.i(TAG, "afterTextChanged $p0")
                computeTipAndTotal()
            }

        })


    }

    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription = when (tipPercent) {
            in 0..9 -> "Poor"
            in 10..14 -> "Acceptable"
            in 15..19 -> "Good"
            in 20..24 -> "Great"
            else -> "Amazing"
        }
        tvTipDescription.text = tipDescription
        // Update the color based on the tipPercent
        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this, R.color.color_worst_tip),
            ContextCompat.getColor(this,R.color.color_best_tip)
        ) as Int
        tvTipDescription.setTextColor(color)
    }

    private fun computeTipAndTotal() {
        // 1. Get the value of the base and tip percent
        if (etBaseAmount.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return // this if statement is to ensure the rest of the code below is not run if you backspace all the numbers
        }
        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = seekBarTip.progress
        // 2. Compute the tip and total
        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = baseAmount + tipAmount
        // 3. Update the UI
        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)
    }
}

// Extensions idea
// 1. Split the bill by N people
// 2. Round the final amount up/down which should update the tip amount and percent automatically
// 3. Design/color updates
// 4. Replace the text describing the tip ("poor","good",etc) with emojis
// 5. Improve the user interface through styling and coloring, e.g. change the text color, font,
//    optimize the layout for different screens.
// 6. Add pre-defined options for service (e.g poor, acceptable, good, excellenet) which automatically decide the tip percentage
// 7. Show currency symbols, and allow the user to change their currency. Store the currency in SharedPreferences (refer to piazza doc for link)
// 8. Add another screen to the app where you can see tips made in the past. Use intents to add another screen to your app (refer to piazza doc for link)


