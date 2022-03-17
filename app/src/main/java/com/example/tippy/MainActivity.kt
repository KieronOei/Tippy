package com.example.tippy

import android.animation.ArgbEvaluator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.lang.Math.ceil
import java.lang.Math.floor
import java.text.DecimalFormat
import java.math.RoundingMode


private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15

class MainActivity : AppCompatActivity() {
    private lateinit var btnDown: Button
    private lateinit var btnUp: Button
    private lateinit var etBaseAmount: EditText
    private lateinit var etSplitNumber: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvTipPercentLabel: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView
    private lateinit var tvIndividualAmount: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnDown = findViewById(R.id.btnDown)
        btnUp = findViewById(R.id.btnUp)
        etBaseAmount = findViewById(R.id.etBaseAmount)
        etSplitNumber = findViewById(R.id.etSplitNumber)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipPercentLabel = findViewById(R.id.tvTipPercentLabel)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)
        tvIndividualAmount = findViewById(R.id.tvIndividualAmount)

        // Initial setting of values
        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercentLabel.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)
        seekBarTip.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                Log.i(TAG, "onProgressChanged $p1")
                tvTipPercentLabel.text = "$p1%"
                computeTipAndTotal()
                updateTipDescription(p1)
                computeIndividual()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
        etBaseAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                Log.i(TAG, "afterTextChanged_etBaseAmount $p0")
                computeTipAndTotal()
            }

        })

        etSplitNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                Log.i(TAG, "afterTextChanged_etSplitNumber $p0")
                computeIndividual()
            }

        })

        btnDown.setOnClickListener{
            // 1. Calculate values
            val totalAmount = tvTotalAmount.text.toString().toDouble()
            val baseAmount = etBaseAmount.text.toString().toDouble()
            val newAmount = floor(totalAmount)
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            var newTipAmount = newAmount - baseAmount
            newTipAmount = df.format(newTipAmount).toDouble()
            Log.d(TAG, "newAmount" + newAmount.toString())
            Log.d(TAG, "baseAmount" + baseAmount.toString())
            Log.d(TAG, "newTipAmount" + newTipAmount.toString())
            val newPercentage = newTipAmount / baseAmount * 100

            // 2. Update values on screen
            tvTotalAmount.text = newAmount.toString()
            tvTipAmount.text = newTipAmount.toString()
            seekBarTip.progress = newPercentage.toInt()
            tvTipPercentLabel.text = "%.2f".format(newPercentage)

            TODO("ROUNDING ERROR CAUSING ME A HEADACHE LINES 106-116")
        }

        btnUp.setOnClickListener{
            // 1. Calculate values
            val totalAmount = tvTotalAmount.text.toString().toDouble()
            val baseAmount = etBaseAmount.text.toString().toDouble()
            val newAmount = ceil(totalAmount)

            val newTipAmount = newAmount - baseAmount
            val newPercentage = newTipAmount / baseAmount * 100

            // 2. Update values on screen
            tvTotalAmount.text = newAmount.toString()
            tvTipAmount.text = newTipAmount.toString()
            seekBarTip.progress = newPercentage.toInt()
            tvTipPercentLabel.text = "%.2f".format(newPercentage)
            TODO("ERROR FROM BTNDOWN SO CANNOT CONTINUE")
        }

    }

    private fun computeIndividual() {
        if (etSplitNumber.text.isEmpty()) {
            tvIndividualAmount.text = ""
            return // this if statement is to ensure the rest of the code below is not run if you backspace all the numbers
        }
        val totalAmount = tvTotalAmount.text.toString().toDouble()
        val numPeople = etSplitNumber.text.toString().toInt()
        val individualAmount = totalAmount / numPeople
        tvIndividualAmount.text = "%.2f".format(individualAmount)
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
            ContextCompat.getColor(this, R.color.color_best_tip)
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
// 1. Split the bill by N people [DONE]
// 2. Round the final amount up/down which should update the tip amount and percent automatically [INCOMPLETE]
// 3. Design/color updates
// 4. Replace the text describing the tip ("poor","good",etc) with emojis
// 5. Improve the user interface through styling and coloring, e.g. change the text color, font,
//    optimize the layout for different screens.
// 6. Add pre-defined options for service (e.g poor, acceptable, good, excellent)/**/ which automatically decide the tip percentage
// 7. Show currency symbols, and allow the user to change their currency. Store the currency in SharedPreferences (refer to piazza doc for link)
// 8. Add another screen to the app where you can see tips made in the past. Use intents to add another screen to your app (refer to piazza doc for link)


