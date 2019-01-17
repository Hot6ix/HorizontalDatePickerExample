package com.j.simples.horizontaldatepicker

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.time.YearMonth
import java.util.*


class DateRecyclerViewAdapter(private var context: Context): RecyclerView.Adapter<DateRecyclerViewAdapter.ViewHolder>() {

    private lateinit var mRecyclerView: RecyclerView

    private var mDayList = ArrayList<Calendar>()
    private var mCurrentCal = Calendar.getInstance()
    private var mPreviousCal = mCurrentCal.clone() as Calendar
    private var mNextCal = mCurrentCal.clone() as Calendar

    // Attrs
    var enableWeekendHighlight = false
    var listItemTextColor: Int = Color.GRAY

    init {
        init()
    }

    private fun init() {
        mPreviousCal.apply {
            time = mCurrentCal.time
            add(Calendar.MONTH , -1)
        }

        mNextCal.apply {
            time = mCurrentCal.time
            add(Calendar.MONTH, 1)
        }

        mDayList.clear()

        addMonth(mPreviousCal, DIRECTION_START)
        addMonth(mCurrentCal, DIRECTION_END)
        addMonth(mNextCal, DIRECTION_END)
        printCalendarInfo()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        mRecyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ViewHolder {
        val layout = LayoutInflater.from(context).inflate(R.layout.day_item, parent, false)
        return ViewHolder(layout)
    }

    override fun getItemCount(): Int = mDayList.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val calendar = mDayList[position]
        viewHolder.day.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
        viewHolder.dayText.text = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
        viewHolder.itemView.setOnClickListener {
            moveTo(calendar, true)
        }

        if(enableWeekendHighlight) {
            when {
                calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY -> {
                    viewHolder.day.setTextColor(ContextCompat.getColor(context, R.color.saturday_highlight))
                    viewHolder.dayText.setTextColor(ContextCompat.getColor(context, R.color.saturday_highlight))
                }
                calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY -> {
                    viewHolder.day.setTextColor(ContextCompat.getColor(context, R.color.sunday_highlight))
                    viewHolder.dayText.setTextColor(ContextCompat.getColor(context, R.color.sunday_highlight))
                }
                else -> {
                    viewHolder.day.setTextColor(listItemTextColor)
                    viewHolder.dayText.setTextColor(listItemTextColor)
                }
            }
        }

//        if(position % 2 == 0) {
//            viewHolder.itemView.setBackgroundColor(Color.parseColor("#dddddd"))
//        }
//        else {
//            viewHolder.itemView.setBackgroundColor(Color.parseColor("#ffffff"))
//        }
    }

    fun getItemByPosition(index: Int): Calendar = mDayList[index]

    private fun addMonth(cal: Calendar, direction: Int = DIRECTION_END) {
        // get number of days of specific month
        val numberOfDays =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val yearMonthObj = YearMonth.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
                yearMonthObj.lengthOfMonth()
            } else {
                cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            }

        val list = ArrayList<Calendar>()
        for(i in 1..numberOfDays) {
            Calendar.getInstance().apply {
                set(Calendar.YEAR, cal.get(Calendar.YEAR))
                set(Calendar.MONTH, cal.get(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, i)
            }.let {
                list.add(it)
            }
        }

        if(direction == DIRECTION_START) {
            // start
            mDayList.addAll(0, list)
            notifyItemRangeInserted(0, numberOfDays)
        }
        else {
            // end
            val lastSize = mDayList.size
            mDayList.addAll(list)
            notifyItemRangeInserted(lastSize, numberOfDays)
        }
    }

    private fun removeMonth(direction: Int) {
        val month: Int
        if(direction == DIRECTION_START) {
            // remove previous month of current
            month = mPreviousCal.get(Calendar.MONTH)
            val count = mDayList.count { it.get(Calendar.MONTH) == month }
            mDayList.removeAll { it.get(Calendar.MONTH) == month }
            notifyItemRangeRemoved(0, count)
        }
        else {
            // remove next month of current
            month = mNextCal.get(Calendar.MONTH)
            val count = mDayList.count { it.get(Calendar.MONTH) == month }
            val lastSize = mDayList.size
            mDayList.removeAll { it.get(Calendar.MONTH) == month }
            notifyItemRangeRemoved(lastSize - count, count)
        }
    }

    fun loadNextMonth(direction: Int) {
        if(direction == DIRECTION_START) {
            removeMonth(DIRECTION_END)

            mNextCal.time = mCurrentCal.time
            mCurrentCal.time = mPreviousCal.time
            mPreviousCal.apply {
                add(Calendar.MONTH, -1)
            }


            addMonth(mPreviousCal, direction)
        }
        else {
            removeMonth(DIRECTION_START)

            mPreviousCal.time = mCurrentCal.time
            mCurrentCal.time = mNextCal.time
            mNextCal.apply {
                add(Calendar.MONTH, 1)
            }

            addMonth(mNextCal, direction)
        }
//        printCalendarInfo()
    }

    fun setDate(cal: Calendar) {
        mCurrentCal.time = cal.time
    }

    fun isDateExist(calendar: Calendar): Boolean {
        val result = mDayList.find { it.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && it.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && it.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) }
        return result != null
    }

    fun moveTo(calendar: Calendar, animated: Boolean) {
        val target = mDayList.find { it.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && it.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) }
        if(target != null) {
            // Move if target date exists
            if(animated) {
                mRecyclerView.smoothScrollToPosition(mDayList.indexOf(target))
            }
            else {
                val recyclerWidth = mRecyclerView.width
                val offset = recyclerWidth / 2 - (recyclerWidth / 7 / 2)
                (mRecyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(mDayList.indexOf(target), offset)
            }
        }
    }

    fun updateList() {
        init()
        notifyDataSetChanged()
    }

    private fun printCalendarInfo() {
        val group = mDayList.groupBy { it.get(Calendar.MONTH) }
        val months = group.keys
        val counts = group.values
        Log.d("DateRecyclerViewAdapter", "${months.joinToString { (it + 1).toString() }} - ${counts.joinToString { it.size.toString() }}")
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var dayText: TextView = view.findViewById(R.id.dayText)
        var day: TextView = view.findViewById(R.id.day)

        init {
            val width = mRecyclerView.measuredWidth / 7
            dayText.width = width
            day.width = width
        }
    }

    companion object {
        const val DIRECTION_START = 0
        const val DIRECTION_END = 1
    }
}