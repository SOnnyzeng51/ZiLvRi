package com.ziluri.app.ui.home

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ziluri.app.R
import com.ziluri.app.data.model.CalendarDay

class CalendarAdapter(
    private val onDateClick: (CalendarDay) -> Unit
) : ListAdapter<CalendarDay, CalendarAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val layoutDay: LinearLayout = itemView.findViewById(R.id.layout_day)
        private val tvDay: TextView = itemView.findViewById(R.id.tv_day)
        private val indicatorDot: View = itemView.findViewById(R.id.indicator_dot)
        private val indicatorCheck: ImageView = itemView.findViewById(R.id.indicator_check)

        fun bind(day: CalendarDay) {
            tvDay.text = day.dayOfMonth.toString()
            
            // 设置文字颜色
            val textColor = when {
                day.isToday -> ContextCompat.getColor(itemView.context, android.R.color.white)
                !day.isCurrentMonth -> ContextCompat.getColor(itemView.context, R.color.text_hint)
                day.isWeekend -> ContextCompat.getColor(itemView.context, R.color.calendar_weekend)
                else -> ContextCompat.getColor(itemView.context, R.color.text_primary)
            }
            tvDay.setTextColor(textColor)
            
            // 设置背景
            val bgDrawable = tvDay.background as? GradientDrawable ?: GradientDrawable()
            bgDrawable.shape = GradientDrawable.OVAL
            
            when {
                day.isToday -> {
                    bgDrawable.setColor(ContextCompat.getColor(itemView.context, R.color.primary))
                    tvDay.background = bgDrawable
                }
                day.isSelected && day.isCurrentMonth -> {
                    bgDrawable.setColor(ContextCompat.getColor(itemView.context, R.color.calendar_selected_bg))
                    tvDay.background = bgDrawable
                }
                else -> {
                    tvDay.background = null
                }
            }
            
            // 设置指示器
            indicatorDot.visibility = View.GONE
            indicatorCheck.visibility = View.GONE
            
            if (day.hasTodos && day.isCurrentMonth) {
                if (day.allCompleted) {
                    indicatorCheck.visibility = View.VISIBLE
                } else {
                    indicatorDot.visibility = View.VISIBLE
                    val dotColor = if (day.hasInProgressTodos) {
                        R.color.primary
                    } else {
                        R.color.text_hint
                    }
                    (indicatorDot.background as? GradientDrawable)?.setColor(
                        ContextCompat.getColor(itemView.context, dotColor)
                    )
                }
            }
            
            // 点击事件
            layoutDay.setOnClickListener {
                onDateClick(day)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CalendarDay>() {
        override fun areItemsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
            return oldItem == newItem
        }
    }
}
