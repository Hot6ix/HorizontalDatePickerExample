package com.j.simples.horizontaldatepicker

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.DisplayMetrics
import java.util.*

class DateRecyclerView: RecyclerView {

    private lateinit var mAdapter: DateRecyclerViewAdapter
    private lateinit var mSnapHelper: LinearSnapHelper
    private lateinit var mLayoutManager: LinearLayoutManager

    private var mIsUpdated = false
    private var mPrevSelected: Calendar? = null
    private var mLastSelected: Calendar? = null
    private var mScrolledListener: OnScrolledListener? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        post {
            mAdapter = DateRecyclerViewAdapter(context)
            mLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            mSnapHelper = LinearSnapHelper()
            mSnapHelper.attachToRecyclerView(this)

            layoutManager = mLayoutManager
            adapter = mAdapter
        }
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)

        // prevent to call by scrollToPosition()
        if(dx != 0) {
            val currentView = mSnapHelper.findSnapView(layoutManager)
            val currentPosition = mLayoutManager.getPosition(currentView!!)

            val currentItem = mAdapter.getItemByPosition(currentPosition)

            // set previous and last selected item for update next month
            if(mPrevSelected?.get(Calendar.DAY_OF_MONTH) != mLastSelected?.get(Calendar.DAY_OF_MONTH) && mLastSelected?.get(Calendar.DAY_OF_MONTH) != currentItem.get(Calendar.DAY_OF_MONTH)) {
                mPrevSelected = mLastSelected

                mIsUpdated = true
            }

            mLastSelected = currentItem
            mScrolledListener?.onItemScrolled(currentItem)

            // Load next month when current item moved from month to another month
            if(mPrevSelected != null && mLastSelected != null) {
                if(mIsUpdated && mPrevSelected?.get(Calendar.MONTH) != mLastSelected?.get(Calendar.MONTH)) {
                    if(mPrevSelected!!.before(mLastSelected!!)) {
                        // moved from current month to next month
                        post {
                            mAdapter.loadNextMonth(DateRecyclerViewAdapter.DIRECTION_END)
                        }
                    }
                    else {
                        // moved from current month to previous month
                        post {
                            mAdapter.loadNextMonth(DateRecyclerViewAdapter.DIRECTION_START)
                        }
                    }
                    mIsUpdated = false
                }
            }
        }
    }

    override fun smoothScrollToPosition(position: Int) {
        CenterSmoothScroller(context).let {
            it.targetPosition = position
            mLayoutManager.startSmoothScroll(it)
        }
    }

    fun setOnScrolledListener(listener: OnScrolledListener) {
        this.mScrolledListener = listener
    }

    fun setDate(calendar: Calendar) {
        post {
            stopScroll()
            if(mAdapter.isDateExist(calendar)) {
                // smooth scroll occurs infinite scrolling
                mAdapter.moveTo(calendar, false)
            }
            else {
                mPrevSelected = null
                mLastSelected = calendar

                mAdapter.setDate(calendar)
                mAdapter.updateList()
                mAdapter.moveTo(calendar, false)
            }
        }
    }

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        val customVelocityY = velocityY * 0.5
        return super.fling(velocityX, customVelocityY.toInt())
    }

    private class CenterSmoothScroller(context: Context): LinearSmoothScroller(context) {
        override fun calculateDtToFit(
            viewStart: Int,
            viewEnd: Int,
            boxStart: Int,
            boxEnd: Int,
            snapPreference: Int
        ): Int {
            return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
        }

        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            return SPEED / displayMetrics.densityDpi
        }

        companion object {
            const val SPEED = 100f // default is 25
        }
    }

}