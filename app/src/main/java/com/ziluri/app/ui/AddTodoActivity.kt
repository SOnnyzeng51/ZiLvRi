package com.ziluri.app.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ziluri.app.R
import com.ziluri.app.ZiLuRiApplication
import com.ziluri.app.data.model.Priority
import com.ziluri.app.data.model.TodoGroup
import com.ziluri.app.data.model.TodoItem
import com.ziluri.app.databinding.ActivityAddTodoBinding
import com.ziluri.app.util.DateUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

class AddTodoActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAddTodoBinding
    private val app by lazy { application as ZiLuRiApplication }
    
    private var selectedDate: Long = DateUtils.getTodayStart()
    private var selectedEndDate: Long? = null
    private var selectedPriority: Priority = Priority.MEDIUM
    private var selectedGroupId: Long? = null
    private var isMultiDay: Boolean = false
    private var requiredCompletions: Int = 1
    
    private var isAddingGroup: Boolean = false
    private var groups: List<TodoGroup> = emptyList()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 获取传入的日期
        intent.getLongExtra("date", -1).let {
            if (it > 0) selectedDate = it
        }
        
        setupToolbar()
        setupDatePicker()
        setupPrioritySelector()
        setupGroupSelector()
        setupMultiDaySwitch()
        setupCompletionTimes()
        setupAddTypeSwitch()
        setupSaveButton()
        
        loadGroups()
        updateDateDisplay()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }
    
    private fun setupDatePicker() {
        binding.layoutDate.setOnClickListener {
            showDatePicker { date ->
                selectedDate = date
                updateDateDisplay()
            }
        }
        
        binding.layoutEndDate.setOnClickListener {
            showDatePicker { date ->
                selectedEndDate = date
                updateEndDateDisplay()
            }
        }
    }
    
    private fun showDatePicker(onDateSelected: (Long) -> Unit) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDate
        
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(DateUtils.getDayStart(calendar.timeInMillis))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    
    private fun updateDateDisplay() {
        binding.tvDate.text = DateUtils.formatFullDate(selectedDate)
    }
    
    private fun updateEndDateDisplay() {
        selectedEndDate?.let {
            binding.tvEndDate.text = DateUtils.formatFullDate(it)
        }
    }
    
    private fun setupPrioritySelector() {
        binding.chipGroupPriority.setOnCheckedStateChangeListener { group, checkedIds ->
            selectedPriority = when {
                checkedIds.contains(R.id.chip_low) -> Priority.LOW
                checkedIds.contains(R.id.chip_medium) -> Priority.MEDIUM
                checkedIds.contains(R.id.chip_high) -> Priority.HIGH
                checkedIds.contains(R.id.chip_urgent) -> Priority.URGENT
                else -> Priority.MEDIUM
            }
        }
        // 默认选中中等优先级
        binding.chipMedium.isChecked = true
    }
    
    private fun setupGroupSelector() {
        binding.spinnerGroup.setOnClickListener {
            showGroupSelector()
        }
    }
    
    private fun showGroupSelector() {
        val groupNames = groups.map { it.name }.toMutableList()
        groupNames.add(0, "创建新分组...")
        
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("选择分组")
            .setItems(groupNames.toTypedArray()) { _, which ->
                if (which == 0) {
                    showCreateGroupDialog()
                } else {
                    selectedGroupId = groups[which - 1].id
                    binding.tvGroupName.text = groups[which - 1].name
                }
            }
            .create()
        dialog.show()
    }
    
    private fun showCreateGroupDialog() {
        val input = android.widget.EditText(this)
        input.hint = "分组名称"
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("创建新分组")
            .setView(input)
            .setPositiveButton("创建") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    createNewGroup(name)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun createNewGroup(name: String) {
        lifecycleScope.launch {
            val group = TodoGroup(name = name)
            val id = app.repository.insertGroup(group)
            selectedGroupId = id
            binding.tvGroupName.text = name
            loadGroups()
        }
    }
    
    private fun loadGroups() {
        lifecycleScope.launch {
            groups = app.repository.getAllGroups().first()
            if (groups.isNotEmpty() && selectedGroupId == null) {
                selectedGroupId = groups.first().id
                binding.tvGroupName.text = groups.first().name
            }
        }
    }
    
    private fun setupMultiDaySwitch() {
        binding.switchMultiDay.setOnCheckedChangeListener { _, isChecked ->
            isMultiDay = isChecked
            binding.layoutEndDate.visibility = if (isChecked) View.VISIBLE else View.GONE
            binding.layoutCompletionTimes.visibility = if (isChecked) View.VISIBLE else View.GONE
            
            if (isChecked && selectedEndDate == null) {
                selectedEndDate = DateUtils.addDays(selectedDate, 1)
                updateEndDateDisplay()
            }
        }
    }
    
    private fun setupCompletionTimes() {
        binding.btnMinus.setOnClickListener {
            if (requiredCompletions > 1) {
                requiredCompletions--
                binding.tvCompletionTimes.text = requiredCompletions.toString()
            }
        }
        
        binding.btnPlus.setOnClickListener {
            requiredCompletions++
            binding.tvCompletionTimes.text = requiredCompletions.toString()
        }
    }
    
    private fun setupAddTypeSwitch() {
        binding.chipAddItem.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isAddingGroup = false
                binding.layoutItemOptions.visibility = View.VISIBLE
                binding.layoutGroupOptions.visibility = View.GONE
            }
        }
        
        binding.chipAddGroup.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isAddingGroup = true
                binding.layoutItemOptions.visibility = View.GONE
                binding.layoutGroupOptions.visibility = View.VISIBLE
            }
        }
    }
    
    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            if (isAddingGroup) {
                saveGroup()
            } else {
                saveItem()
            }
        }
    }
    
    private fun saveGroup() {
        val name = binding.etGroupName.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "请输入分组名称", Toast.LENGTH_SHORT).show()
            return
        }
        
        lifecycleScope.launch {
            val group = TodoGroup(name = name)
            app.repository.insertGroup(group)
            Toast.makeText(this@AddTodoActivity, "分组创建成功", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun saveItem() {
        val content = binding.etContent.text.toString().trim()
        if (content.isEmpty()) {
            Toast.makeText(this, "请输入待办内容", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (selectedGroupId == null) {
            // 如果没有分组，先创建默认分组
            lifecycleScope.launch {
                val defaultGroup = TodoGroup(name = "默认分组")
                selectedGroupId = app.repository.insertGroup(defaultGroup)
                saveItemWithGroup(content)
            }
        } else {
            lifecycleScope.launch {
                saveItemWithGroup(content)
            }
        }
    }
    
    private suspend fun saveItemWithGroup(content: String) {
        val item = TodoItem(
            groupId = selectedGroupId!!,
            content = content,
            priority = selectedPriority,
            date = selectedDate,
            startDate = if (isMultiDay) selectedDate else null,
            endDate = if (isMultiDay) selectedEndDate else null,
            requiredCompletions = if (isMultiDay) requiredCompletions else 1
        )
        
        app.repository.insertItem(item)
        Toast.makeText(this@AddTodoActivity, "添加成功", Toast.LENGTH_SHORT).show()
        finish()
    }
}
