package com.ziluri.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ziluri.app.ZiLuRiApplication
import com.ziluri.app.data.model.LoginType
import com.ziluri.app.data.model.User
import com.ziluri.app.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private val app by lazy { application as ZiLuRiApplication }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        // QQ登录
        binding.btnQqLogin.setOnClickListener {
            // TODO: 实际接入QQ SDK
            Toast.makeText(this, "QQ登录功能需要接入QQ SDK", Toast.LENGTH_SHORT).show()
            simulateLogin(LoginType.QQ, "QQ用户")
        }
        
        // 微信登录
        binding.btnWechatLogin.setOnClickListener {
            // TODO: 实际接入微信SDK
            Toast.makeText(this, "微信登录功能需要接入微信SDK", Toast.LENGTH_SHORT).show()
            simulateLogin(LoginType.WECHAT, "微信用户")
        }
        
        // 跳过登录（游客模式）
        binding.btnSkip.setOnClickListener {
            loginAsGuest()
        }
    }
    
    private fun simulateLogin(loginType: LoginType, nickname: String) {
        lifecycleScope.launch {
            val user = User(
                id = "${loginType.name.lowercase()}_${System.currentTimeMillis()}",
                nickname = nickname,
                loginType = loginType
            )
            
            app.repository.insertUser(user)
            app.preferencesManager.userId = user.id
            app.preferencesManager.isFirstLaunch = false
            
            navigateToMain()
        }
    }
    
    private fun loginAsGuest() {
        lifecycleScope.launch {
            val user = app.repository.createGuestUser()
            app.preferencesManager.userId = user.id
            app.preferencesManager.isFirstLaunch = false
            
            navigateToMain()
        }
    }
    
    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
