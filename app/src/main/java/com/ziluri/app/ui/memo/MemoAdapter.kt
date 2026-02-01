package com.ziluri.app.ui.memo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ziluri.app.data.model.Memo
import com.ziluri.app.databinding.ItemMemoBinding
import com.ziluri.app.util.DateUtils

class MemoAdapter(
    private val onMemoClick: (Memo) -> Unit,
    private val onMemoLongClick: (Memo) -> Unit
) : ListAdapter<Memo, MemoAdapter.MemoViewHolder>(MemoDiffCallback()) {
    
    // 预定义的备忘录颜色
    private val memoColors = listOf(
        0xFFFFF9C4.toInt(), // 浅黄
        0xFFFFCCBC.toInt(), // 浅橙
        0xFFC8E6C9.toInt(), // 浅绿
        0xFFB3E5FC.toInt(), // 浅蓝
        0xFFE1BEE7.toInt(), // 浅紫
        0xFFFFFFFF.toInt()  // 白色
    )
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val binding = ItemMemoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MemoViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class MemoViewHolder(
        private val binding: ItemMemoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onMemoClick(getItem(position))
                }
            }
            
            binding.root.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onMemoLongClick(getItem(position))
                }
                true
            }
        }
        
        fun bind(memo: Memo) {
            binding.tvTitle.text = memo.title
            binding.tvContent.text = memo.content
            binding.tvDate.text = DateUtils.formatDate(memo.updatedAt, "MM/dd HH:mm")
            
            // 设置置顶图标
            binding.ivPin.visibility = if (memo.isPinned) View.VISIBLE else View.GONE
            
            // 设置背景颜色
            val colorIndex = (memo.id % memoColors.size).toInt()
            binding.cardMemo.setCardBackgroundColor(memoColors[colorIndex])
            
            // 限制内容显示行数
            binding.tvContent.maxLines = 6
        }
    }
    
    class MemoDiffCallback : DiffUtil.ItemCallback<Memo>() {
        override fun areItemsTheSame(oldItem: Memo, newItem: Memo): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Memo, newItem: Memo): Boolean {
            return oldItem == newItem
        }
    }
}
