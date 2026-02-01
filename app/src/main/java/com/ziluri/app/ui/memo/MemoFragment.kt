package com.ziluri.app.ui.memo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ziluri.app.ZiLuRiApplication
import com.ziluri.app.data.model.Memo
import com.ziluri.app.databinding.FragmentMemoBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MemoFragment : Fragment() {
    
    private var _binding: FragmentMemoBinding? = null
    private val binding get() = _binding!!
    
    private val app by lazy { requireActivity().application as ZiLuRiApplication }
    private lateinit var memoAdapter: MemoAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMemoBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupFab()
        setupSearch()
        loadMemos()
    }
    
    private fun setupRecyclerView() {
        memoAdapter = MemoAdapter(
            onMemoClick = { memo ->
                showEditMemoDialog(memo)
            },
            onMemoLongClick = { memo ->
                showMemoOptionsDialog(memo)
            }
        )
        
        binding.rvMemos.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = memoAdapter
        }
    }
    
    private fun setupFab() {
        binding.fabAddMemo.setOnClickListener {
            showAddMemoDialog()
        }
    }
    
    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                val query = s?.toString() ?: ""
                searchMemos(query)
            }
        })
    }
    
    private fun loadMemos() {
        viewLifecycleOwner.lifecycleScope.launch {
            app.repository.getAllMemos().collect { memos ->
                memoAdapter.submitList(memos)
                updateEmptyState(memos.isEmpty())
            }
        }
    }
    
    private fun searchMemos(query: String) {
        if (query.isBlank()) {
            loadMemos()
            return
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            app.repository.searchMemos(query).collect { memos ->
                memoAdapter.submitList(memos)
                updateEmptyState(memos.isEmpty())
            }
        }
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvMemos.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
    
    private fun showAddMemoDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(com.ziluri.app.R.layout.dialog_edit_memo, null)
        
        val etTitle = dialogView.findViewById<EditText>(com.ziluri.app.R.id.et_title)
        val etContent = dialogView.findViewById<EditText>(com.ziluri.app.R.id.et_content)
        
        AlertDialog.Builder(requireContext())
            .setTitle("新建备忘录")
            .setView(dialogView)
            .setPositiveButton("保存") { _, _ ->
                val title = etTitle.text.toString().trim()
                val content = etContent.text.toString().trim()
                
                if (title.isNotEmpty() || content.isNotEmpty()) {
                    createMemo(title.ifEmpty { "无标题" }, content)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun showEditMemoDialog(memo: Memo) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(com.ziluri.app.R.layout.dialog_edit_memo, null)
        
        val etTitle = dialogView.findViewById<EditText>(com.ziluri.app.R.id.et_title)
        val etContent = dialogView.findViewById<EditText>(com.ziluri.app.R.id.et_content)
        
        etTitle.setText(memo.title)
        etContent.setText(memo.content)
        
        AlertDialog.Builder(requireContext())
            .setTitle("编辑备忘录")
            .setView(dialogView)
            .setPositiveButton("保存") { _, _ ->
                val title = etTitle.text.toString().trim()
                val content = etContent.text.toString().trim()
                
                updateMemo(memo.copy(
                    title = title.ifEmpty { "无标题" },
                    content = content
                ))
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun showMemoOptionsDialog(memo: Memo) {
        val options = arrayOf(
            if (memo.isPinned) "取消置顶" else "置顶",
            "删除"
        )
        
        AlertDialog.Builder(requireContext())
            .setTitle(memo.title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> togglePin(memo)
                    1 -> deleteMemo(memo)
                }
            }
            .show()
    }
    
    private fun createMemo(title: String, content: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val memo = Memo(title = title, content = content)
            app.repository.insertMemo(memo)
        }
    }
    
    private fun updateMemo(memo: Memo) {
        viewLifecycleOwner.lifecycleScope.launch {
            app.repository.updateMemo(memo)
        }
    }
    
    private fun togglePin(memo: Memo) {
        viewLifecycleOwner.lifecycleScope.launch {
            app.repository.updateMemo(memo.copy(isPinned = !memo.isPinned))
        }
    }
    
    private fun deleteMemo(memo: Memo) {
        AlertDialog.Builder(requireContext())
            .setTitle("删除备忘录")
            .setMessage("确定要删除「${memo.title}」吗？")
            .setPositiveButton("删除") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    app.repository.deleteMemo(memo)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
