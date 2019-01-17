package com.j.simples.horizontaldatepickerexample

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.j.simples.horizontaldatepicker.OnDateChangedListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        horizontalDatePicker.addDateChangedListener(object : OnDateChangedListener {
            override fun onDateChanged(year: Int, month: Int, day: Int) {
                textView.text = "$year / $month / $day"
            }

        })
    }
}
