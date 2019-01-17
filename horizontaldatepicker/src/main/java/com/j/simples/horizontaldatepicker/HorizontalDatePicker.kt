package com.j.simples.horizontaldatepicker

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.widget.Button
import android.widget.TextView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class HorizontalDatePicker: ConstraintLayout, OnScrolledListener {

    // Views
    lateinit var dateText: TextView
    lateinit var recyclerView: DateRecyclerView
    lateinit var selector: Button

    // Attributes
    private var mDateTextColor: Int = 0
    private var mDateTextSize: Float = 0f
    private var mListItemTextColor: Int = 0
    private var mHighlightStrokeColor: Int = 0
    private var mHighlightStrokeWidth: Int = 0
    private var mEnableWeekendHighlight: Boolean = false

    // Etc
    private var mCalendar = Calendar.getInstance()
    private var mDateFormat: DateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.DEFAULT)

    private var mDateChangedListener: OnDateChangedListener? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet? = null) {
        if(isInEditMode) return

        inflate(context, R.layout.horizontal_datepicker_layout, this)

        // get attrs from xml
        context.theme.obtainStyledAttributes(attrs, R.styleable.HorizontalDatePicker, 0, 0).apply {
            try{
                mDateTextColor = getColor(R.styleable.HorizontalDatePicker_dateTextColor, Color.GRAY)
                mDateTextSize = getDimension(R.styleable.HorizontalDatePicker_dateTextSize, 18f)
                mListItemTextColor = getColor(R.styleable.HorizontalDatePicker_listItemTextColor, Color.GRAY)
                mHighlightStrokeColor = getColor(R.styleable.HorizontalDatePicker_highlightStrokeColor, Color.GRAY)
                mHighlightStrokeWidth = getDimensionPixelSize(R.styleable.HorizontalDatePicker_highlightStrokeWidth, 5)
                mEnableWeekendHighlight = getBoolean(R.styleable.HorizontalDatePicker_enableWeekendHighlight, false)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                recycle()
            }
        }

        // init date text
        dateText = findViewById(R.id.dateText)
        dateText.apply {
            text = mDateFormat.format(mCalendar.time)
            textSize = mDateTextSize
            setTextColor(mDateTextColor)
        }

        // init highlighter
        selector = findViewById(R.id.selector)
        (selector.background as GradientDrawable).apply {
            setStroke(mHighlightStrokeWidth, mHighlightStrokeColor)
        }

        // init recyclerview
        recyclerView = findViewById(R.id.dateRecyclerView)
        recyclerView.setOnScrolledListener(this)

        recyclerView.post {
            selector.apply {
                width = recyclerView.width / 7
            }

            (recyclerView.adapter as DateRecyclerViewAdapter).apply {
                enableWeekendHighlight = mEnableWeekendHighlight
                listItemTextColor = mListItemTextColor
                moveTo(Calendar.getInstance(), false)
            }
        }
    }

    override fun onItemScrolled(calendar: Calendar) {
        mCalendar.time = calendar.time
        dateText.text = mDateFormat.format(calendar.time)
        mDateChangedListener?.onDateChanged(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(
            Calendar.DAY_OF_MONTH))
    }

    fun addDateChangedListener(listener: OnDateChangedListener) {
        this.mDateChangedListener = listener
    }

    fun setDateFormat(template: String) {
        mDateFormat = SimpleDateFormat(template, Locale.getDefault())
        dateText.text = mDateFormat.format(mCalendar.time)
    }

    fun setDate(calendar: Calendar) {
        mCalendar.time = calendar.time
        recyclerView.setDate(calendar)
        dateText.text = mDateFormat.format(calendar.time)
    }

    fun enableWeekendHighlight(bool: Boolean) {
        recyclerView.post {
            (recyclerView.adapter as DateRecyclerViewAdapter).apply {
                enableWeekendHighlight = bool
            }
        }
    }

    fun setHighlighterColor(color: Int) {
        mHighlightStrokeColor = color
        applyHighlighter()
    }

    fun setHighlighterWidth(size: Int) {
        mHighlightStrokeWidth = size
        applyHighlighter()
    }

    private fun applyHighlighter() {
        (selector.background as GradientDrawable).apply {
            setStroke(mHighlightStrokeWidth, mHighlightStrokeColor)
        }
    }

}