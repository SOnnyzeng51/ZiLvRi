package com.ziluri.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.ziluri.app.R
import com.ziluri.app.ZiLuRiApplication
import com.ziluri.app.data.model.CalendarDay
import com.ziluri.app.data.model.TodoItem
import com.ziluri.app.util.DateUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var tvCurrentMonth: TextView
    private lateinit var tvTodayInfo: TextView
    private lateinit var tvSelectedDate: TextView
    private lateinit var tvTodoCount: TextView
    private lateinit var btnPrevMonth: ImageButton
    private lateinit var btnNextMonth: ImageButton
    private lateinit var btnToday: TextView
    private lateinit var chipGroupView: ChipGroup
    private lateinit var rvCalendar: RecyclerView
    private lateinit var rvTodos: RecyclerView
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var layoutWeekHeader: LinearLayout
    
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var todoAdapter: TodoAdapter
    
    private var currentCalendar = Calendar.getInstance()
    private var selectedDate = Calendar.getInstance()
    private var currentViewType = ViewType.MONTH
    
    enum class ViewType { YEAR, MONTH, WEEK, DAY }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupCalendar()
        setupTodoList()
        setupViewTypeChips()
        loadData()
    }

    private fun initViews(view: View) {
        tvCurrentMonth = view.findViewById(R.id.tv_current_month)
        tvTodayInfo = view.findViewById(R.id.tv_today_info)
        tvSelectedDate = view.findViewById(R.id.tv_selected_date)
        tvTodoCount = view.findViewById(R.id.tv_todo_count)
        btnPrevMonth = view.findViewById(R.id.btn_prev_month)
        btnNextMonth = view.findViewById(R.id.btn_next_month)
        btnToday = view.findViewById(R.id.btn_today)
        chipGroupView = view.findViewById(R.id.chip_group_view)
        rvCalendar = view.findViewById(R.id.rv_calendar)
        rvTodos = view.findViewById(R.id.rv_todos)
        layoutEmpty = view.findViewById(R.id.layout_empty)
        layoutWeekHeader = view.findViewById(R.id.layout_week_header)
        
        btnPrevMonth.setOnClickListener { navigateMonth(-1) }
        btnNextMonth.setOnClickListener { navigateMonth(1) }
        btnToday.setOnClickListener { goToToday() }
        
        updateTodayInfo()
    }

    private fun setupViewTypeChips() {
        val chipYear = chipGroupView.findViewById<Chip>(R.id.chip_year)
        val chipMonth = chipGroupView.findViewById<Chip>(R.id.chip_month)
        val chipWeek = chipGroupView.findViewById<Chip>(R.id.chip_week)
        val chipDay = chipGroupView.findViewById<Chip>(R.id.chip_day)
        
        chipYear.setOnClickListener { switchViewType(ViewType.YEAR) }
        chipMonth.setOnClickListener { switchViewType(ViewType.MONTH) }
        chipWeek.setOnClickListener { switchViewType(ViewType.WEEK) }
        chipDay.setOnClickListener { switchViewType(ViewType.DAY) }
        
        // 默认选中月视图
        chipMonth.isChecked = true
    }

    private fun switchViewType(type: ViewType) {
        currentViewType = type
        when (type) {
            ViewType.YEAR -> showYearView()
            ViewType.MONTH -> showMonthView()
            ViewType.WEEK -> showWeekView()
            ViewType.DAY -> showDayView()
        }
    }

    private fun showYearView() {
        layoutWeekHeader.visibility = View.GONE
        // 年视图：显示12个月份的缩略图
        rvCalendar.layoutManager = GridLayoutManager(requireContext(), 3)
        generateYearCalendar()
    }

    private fun showMonthView() {
        layoutWeekHeader.visibility = View.VISIBLE
        rvCalendar.layoutManager = GridLayoutManager(requireContext(), 7)
        generateMonthCalendar()
    }

    private fun showWeekView() {
        layoutWeekHeader.visibility = View.VISIBLE
        rvCalendar.layoutManager = GridLayoutManager(requireContext(), 7)
        generateWeekCalendar()
    }

    private fun showDayView() {
        layoutWeekHeader.visibility = View.GONE
        rvCalendar.visibility = View.GONE
        // 日视图只显示待办列表
    }

    private fun setupCalendar() {
        calendarAdapter = CalendarAdapter { day ->
            onDateSelected(day)
        }
        
        rvCalendar.apply {
            layoutManager = GridLayoutManager(requireContext(), 7)
            adapter = calendarAdapter
            itemAnimator = null
        }
        
        generateMonthCalendar()
    }

    private fun setupTodoList() {
        todoAdapter = TodoAdapter(
            onItemClick = { todo -> /* 编辑待办 */ },
            onCheckedChange = { todo, isChecked -> 
                updateTodoStatus(todo, isChecked)
            }
        )
        
        rvTodos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = todoAdapter
        }
        
        // 设置拖拽排序
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition
                todoAdapter.moveItem(fromPos, toPos)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
            
            override fun isLongPressDragEnabled() = false
        })
        
        itemTouchHelper.attachToRecyclerView(rvTodos)
        todoAdapter.setItemTouchHelper(itemTouchHelper)
    }

    private fun generateMonthCalendar() {
        val days = mutableListOf<CalendarDay>()
        val calendar = currentCalendar.clone() as Calendar
        
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        // 上个月的日期
        calendar.add(Calendar.MONTH, -1)
        val daysInPrevMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in firstDayOfWeek - 1 downTo 0) {
            val day = daysInPrevMonth - i
            days.add(CalendarDay(
                date = getDateForPrevMonth(day),
                dayOfMonth = day,
                isCurrentMonth = false,
                isToday = false,
                isSelected = false,
                isWeekend = false
            ))
        }
        
        // 当月日期
        calendar.add(Calendar.MONTH, 1)
        val today = Calendar.getInstance()
        for (day in 1..daysInMonth) {
            calendar.set(Calendar.DAY_OF_MONTH, day)
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val isToday = DateUtils.isSameDay(calendar.timeInMillis, today.timeInMillis)
            val isSelected = DateUtils.isSameDay(calendar.timeInMillis, selectedDate.timeInMillis)
            
            days.add(CalendarDay(
                date = calendar.timeInMillis,
                dayOfMonth = day,
                isCurrentMonth = true,
                isToday = isToday,
                isSelected = isSelected,
                isWeekend = dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
            ))
        }
        
        // 下个月日期（补齐6行）
        val remainingDays = 42 - days.size
        for (day in 1..remainingDays) {
            days.add(CalendarDay(
                date = getDateForNextMonth(day),
                dayOfMonth = day,
                isCurrentMonth = false,
                isToday = false,
                isSelected = false,
                isWeekend = false
            ))
        }
        
        calendarAdapter.submitList(days)
        updateMonthTitle()
    }

    private fun generateWeekCalendar() {
        val days = mutableListOf<CalendarDay>()
        val calendar = selectedDate.clone() as Calendar
        
        // 找到本周第一天（周日）
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        
        val today = Calendar.getInstance()
        for (i in 0..6) {
            val isToday = DateUtils.isSameDay(calendar.timeInMillis, today.timeInMillis)
            val isSelected = DateUtils.isSameDay(calendar.timeInMillis, selectedDate.timeInMillis)
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            
            days.add(CalendarDay(
                date = calendar.timeInMillis,
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH),
                isCurrentMonth = true,
                isToday = isToday,
                isSelected = isSelected,
                isWeekend = dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
            ))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        calendarAdapter.submitList(days)
        updateMonthTitle()
    }

    private fun generateYearCalendar() {
        // 年视图暂时简化处理
        showMonthView()
    }

    private fun getDateForPrevMonth(day: Int): Long {
        val cal = currentCalendar.clone() as Calendar
        cal.add(Calendar.MONTH, -1)
        cal.set(Calendar.DAY_OF_MONTH, day)
        return cal.timeInMillis
    }

    private fun getDateForNextMonth(day: Int): Long {
        val cal = currentCalendar.clone() as Calendar
        cal.add(Calendar.MONTH, 1)
        cal.set(Calendar.DAY_OF_MONTH, day)
        return cal.timeInMillis
    }

    private fun onDateSelected(day: CalendarDay) {
        selectedDate.timeInMillis = day.date
        
        if (!day.isCurrentMonth) {
            if (day.dayOfMonth > 15) {
                currentCalendar.add(Calendar.MONTH, -1)
            } else {
                currentCalendar.add(Calendar.MONTH, 1)
            }
        }
        
        when (currentViewType) {
            ViewType.MONTH -> generateMonthCalendar()
            ViewType.WEEK -> generateWeekCalendar()
            else -> {}
        }
        
        updateSelectedDateTitle()
        loadTodosForDate(day.date)
        updateTodayButtonVisibility()
    }

    private fun navigateMonth(offset: Int) {
        currentCalendar.add(Calendar.MONTH, offset)
        generateMonthCalendar()
        updateTodayButtonVisibility()
    }

    private fun goToToday() {
        currentCalendar = Calendar.getInstance()
        selectedDate = Calendar.getInstance()
        generateMonthCalendar()
        updateSelectedDateTitle()
        loadTodosForDate(selectedDate.timeInMillis)
        btnToday.visibility = View.GONE
    }

    private fun updateMonthTitle() {
        val year = currentCalendar.get(Calendar.YEAR)
        val month = currentCalendar.get(Calendar.MONTH) + 1
        tvCurrentMonth.text = "${year}年${month}月"
    }

    private fun updateTodayInfo() {
        val today = Calendar.getInstance()
        val dayOfWeek = when (today.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "周日"
            Calendar.MONDAY -> "周一"
            Calendar.TUESDAY -> "周二"
            Calendar.WEDNESDAY -> "周三"
            Calendar.THURSDAY -> "周四"
            Calendar.FRIDAY -> "周五"
            Calendar.SATURDAY -> "周六"
            else -> ""
        }
        tvTodayInfo.text = "今天是$dayOfWeek"
    }

    private fun updateSelectedDateTitle() {
        val isToday = DateUtils.isSameDay(selectedDate.timeInMillis, System.currentTimeMillis())
        tvSelectedDate.text = if (isToday) {
            "今天的待办"
        } else {
            val month = selectedDate.get(Calendar.MONTH) + 1
            val day = selectedDate.get(Calendar.DAY_OF_MONTH)
            "${month}月${day}日的待办"
        }
    }

    private fun updateTodayButtonVisibility() {
        val today = Calendar.getInstance()
        val isSameMonth = currentCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                currentCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH)
        btnToday.visibility = if (isSameMonth) View.GONE else View.VISIBLE
    }

    private fun loadData() {
        loadTodosForDate(selectedDate.timeInMillis)
    }

    private fun loadTodosForDate(date: Long) {
        val app = requireActivity().application as ZiLuRiApplication
        val repository = app.repository
        
        viewLifecycleOwner.lifecycleScope.launch {
            repository.getTodoItemsByDate(DateUtils.getDayStart(date), DateUtils.getDayEnd(date))
                .collectLatest { todos ->
                    todoAdapter.submitList(todos)
                    tvTodoCount.text = "${todos.size} 项"
                    layoutEmpty.visibility = if (todos.isEmpty()) View.VISIBLE else View.GONE
                    rvTodos.visibility = if (todos.isEmpty()) View.GONE else View.VISIBLE
                }
        }
    }

    private fun updateTodoStatus(todo: TodoItem, isChecked: Boolean) {
        val app = requireActivity().application as ZiLuRiApplication
        val repository = app.repository
        
        viewLifecycleOwner.lifecycleScope.launch {
            val updatedTodo = todo.copy(isCompleted = isChecked)
            repository.updateTodoItem(updatedTodo)
        }
    }
}
