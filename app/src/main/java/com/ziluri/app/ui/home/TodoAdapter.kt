package com.ziluri.app.ui.home

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.ziluri.app.R
import com.ziluri.app.data.model.Priority
import com.ziluri.app.data.model.TodoItem
import java.util.*

class TodoAdapter(
    private val onItemClick: (TodoItem) -> Unit,
    private val onCheckedChange: (TodoItem, Boolean) -> Unit
) : ListAdapter<TodoItem, TodoAdapter.ViewHolder>(DiffCallback()) {

    private var itemTouchHelper: ItemTouchHelper? = null
    private val items = mutableListOf<TodoItem>()

    fun setItemTouchHelper(helper: ItemTouchHelper) {
        itemTouchHelper = helper
    }

    override fun submitList(list: List<TodoItem>?) {
        items.clear()
        list?.let { items.addAll(it) }
        super.submitList(list?.toList())
    }

    fun moveItem(from: Int, to: Int) {
        if (from < to) {
            for (i in from until to) {
                Collections.swap(items, i, i + 1)
            }
        } else {
            for (i in from downTo to + 1) {
                Collections.swap(items, i, i - 1)
            }
        }
        notifyItemMoved(from, to)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkbox: MaterialCheckBox = itemView.findViewById(R.id.checkbox)
        private val tvContent: TextView = itemView.findViewById(R.id.tv_content)
        private val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        private val tvRepeat: TextView = itemView.findViewById(R.id.tv_repeat)
        private val layoutMeta: LinearLayout = itemView.findViewById(R.id.layout_meta)
        private val viewPriority: View = itemView.findViewById(R.id.view_priority)
        private val ivDragHandle: ImageView = itemView.findViewById(R.id.iv_drag_handle)

        init {
            // 拖拽手柄
            ivDragHandle.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    itemTouchHelper?.startDrag(this)
                }
                false
            }
        }

        fun bind(todo: TodoItem) {
            tvContent.text = todo.content
            
            // 设置完成状态
            checkbox.isChecked = todo.isCompleted
            if (todo.isCompleted) {
                tvContent.paintFlags = tvContent.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvContent.alpha = 0.5f
            } else {
                tvContent.paintFlags = tvContent.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                tvContent.alpha = 1f
            }
            
            // 设置优先级颜色
            val priorityColor = when (todo.priority) {
                Priority.LOW -> R.color.priority_low
                Priority.MEDIUM -> R.color.priority_medium
                Priority.HIGH -> R.color.priority_high
                Priority.URGENT -> R.color.priority_urgent
            }
            viewPriority.setBackgroundColor(ContextCompat.getColor(itemView.context, priorityColor))
            
            // 设置时间和重复信息
            var showMeta = false
            if (todo.reminderTime != null) {
                tvTime.visibility = View.VISIBLE
                tvTime.text = formatTime(todo.reminderTime)
                showMeta = true
            } else {
                tvTime.visibility = View.GONE
            }
            
            if (todo.requiredCompletions > 1) {
                tvRepeat.visibility = View.VISIBLE
                tvRepeat.text = "${todo.currentCompletions}/${todo.requiredCompletions}"
                showMeta = true
            } else {
                tvRepeat.visibility = View.GONE
            }
            
            layoutMeta.visibility = if (showMeta) View.VISIBLE else View.GONE
            
            // 点击事件
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChange(todo, isChecked)
            }
            
            itemView.setOnClickListener {
                onItemClick(todo)
            }
        }

        private fun formatTime(timestamp: Long): String {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            return String.format("%02d:%02d", hour, minute)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TodoItem>() {
        override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
            return oldItem == newItem
        }
    }
}
