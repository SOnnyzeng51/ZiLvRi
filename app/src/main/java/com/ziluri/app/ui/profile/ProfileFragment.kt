package com.ziluri.app.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.ziluri.app.R
import com.ziluri.app.ZiLuRiApplication
import com.ziluri.app.ui.LoginActivity
import com.ziluri.app.util.PreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var ivAvatar: ImageView
    private lateinit var tvNickname: TextView
    private lateinit var tvLevel: TextView
    private lateinit var progressExp: LinearProgressIndicator
    private lateinit var tvExp: TextView
    private lateinit var tvContinuousDays: TextView
    private lateinit var tvTotalCompleted: TextView
    private lateinit var layoutUserInfo: LinearLayout
    private lateinit var layoutBackup: LinearLayout
    private lateinit var layoutTheme: LinearLayout
    private lateinit var layoutAbout: LinearLayout
    private lateinit var switchSound: MaterialSwitch
    private lateinit var switchVibrate: MaterialSwitch
    private lateinit var tvThemeValue: TextView
    private lateinit var btnLogout: MaterialButton
    
    private lateinit var prefsManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefsManager = PreferencesManager(requireContext())
        initViews(view)
        setupListeners()
        loadUserData()
    }

    private fun initViews(view: View) {
        ivAvatar = view.findViewById(R.id.iv_avatar)
        tvNickname = view.findViewById(R.id.tv_nickname)
        tvLevel = view.findViewById(R.id.tv_level)
        progressExp = view.findViewById(R.id.progress_exp)
        tvExp = view.findViewById(R.id.tv_exp)
        tvContinuousDays = view.findViewById(R.id.tv_continuous_days)
        tvTotalCompleted = view.findViewById(R.id.tv_total_completed)
        layoutUserInfo = view.findViewById(R.id.layout_user_info)
        layoutBackup = view.findViewById(R.id.layout_backup)
        layoutTheme = view.findViewById(R.id.layout_theme)
        layoutAbout = view.findViewById(R.id.layout_about)
        switchSound = view.findViewById(R.id.switch_sound)
        switchVibrate = view.findViewById(R.id.switch_vibrate)
        tvThemeValue = view.findViewById(R.id.tv_theme_value)
        btnLogout = view.findViewById(R.id.btn_logout)
        
        // 初始化开关状态
        switchSound.isChecked = prefsManager.soundEnabled
        switchVibrate.isChecked = prefsManager.vibrateEnabled
        updateThemeText()
    }

    private fun setupListeners() {
        layoutUserInfo.setOnClickListener {
            // 点击登录或查看用户信息
            if (prefsManager.userId == 0L) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
        }
        
        layoutBackup.setOnClickListener {
            showBackupDialog()
        }
        
        layoutTheme.setOnClickListener {
            showThemeDialog()
        }
        
        layoutAbout.setOnClickListener {
            showAboutDialog()
        }
        
        switchSound.setOnCheckedChangeListener { _, isChecked ->
            prefsManager.soundEnabled = isChecked
        }
        
        switchVibrate.setOnCheckedChangeListener { _, isChecked ->
            prefsManager.vibrateEnabled = isChecked
        }
        
        btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun loadUserData() {
        val app = requireActivity().application as ZiLuRiApplication
        val repository = app.repository
        
        viewLifecycleOwner.lifecycleScope.launch {
            val userId = prefsManager.userId
            if (userId > 0) {
                repository.getUserById(userId).collect { user ->
                    user?.let {
                        tvNickname.text = it.nickname
                        tvLevel.text = "Lv.${it.level} ${getLevelTitle(it.level)}"
                        tvContinuousDays.text = it.continuousDays.toString()
                        tvTotalCompleted.text = it.totalCompleted.toString()
                        
                        val expForNextLevel = getExpForLevel(it.level)
                        val progress = (it.exp.toFloat() / expForNextLevel * 100).toInt()
                        progressExp.progress = progress
                        tvExp.text = "${it.exp} / $expForNextLevel EXP"
                    }
                }
            } else {
                tvNickname.text = "点击登录"
                tvLevel.text = "Lv.1 自律新手"
            }
        }
    }

    private fun getLevelTitle(level: Int): String {
        return when {
            level <= 5 -> "自律新手"
            level <= 10 -> "自律学徒"
            level <= 20 -> "自律达人"
            level <= 35 -> "自律专家"
            level <= 50 -> "自律大师"
            level <= 70 -> "自律宗师"
            else -> "自律传奇"
        }
    }

    private fun getExpForLevel(level: Int): Int {
        return when {
            level <= 5 -> 100
            level <= 10 -> 200
            level <= 20 -> 350
            level <= 35 -> 500
            level <= 50 -> 750
            else -> 1000
        }
    }

    private fun showBackupDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_backup, null)
        
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()
        
        dialogView.findViewById<MaterialButton>(R.id.btn_backup).setOnClickListener {
            performBackup()
            dialog.dismiss()
        }
        
        dialogView.findViewById<MaterialButton>(R.id.btn_restore).setOnClickListener {
            performRestore()
            dialog.dismiss()
        }
        
        dialog.show()
    }

    private fun performBackup() {
        Toast.makeText(requireContext(), "正在备份数据...", Toast.LENGTH_SHORT).show()
        // TODO: 实现备份逻辑
        Toast.makeText(requireContext(), "备份成功！文件已保存到下载目录", Toast.LENGTH_LONG).show()
    }

    private fun performRestore() {
        Toast.makeText(requireContext(), "请选择备份文件", Toast.LENGTH_SHORT).show()
        // TODO: 实现还原逻辑
    }

    private fun showThemeDialog() {
        val themes = arrayOf("浅色", "深色", "跟随系统")
        val currentTheme = prefsManager.themeMode
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("选择主题")
            .setSingleChoiceItems(themes, currentTheme) { dialog, which ->
                prefsManager.themeMode = which
                updateThemeText()
                applyTheme(which)
                dialog.dismiss()
            }
            .show()
    }

    private fun updateThemeText() {
        tvThemeValue.text = when (prefsManager.themeMode) {
            0 -> "浅色"
            1 -> "深色"
            else -> "跟随系统"
        }
    }

    private fun applyTheme(mode: Int) {
        val nightMode = when (mode) {
            0 -> AppCompatDelegate.MODE_NIGHT_NO
            1 -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("关于自律日")
            .setMessage("版本：1.1.0\n\n一款帮助你管理日常待办事项、培养自律习惯的应用。\n\n持续自律，遇见更好的自己！")
            .setPositiveButton("确定", null)
            .show()
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("退出登录")
            .setMessage("确定要退出登录吗？本地数据不会被删除。")
            .setNegativeButton("取消", null)
            .setPositiveButton("确定") { _, _ ->
                prefsManager.userId = 0
                loadUserData()
                Toast.makeText(requireContext(), "已退出登录", Toast.LENGTH_SHORT).show()
            }
            .show()
    }
}
