package com.ziluri.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ziluri.app.R
import com.ziluri.app.ui.home.HomeFragment
import com.ziluri.app.ui.memo.MemoFragment
import com.ziluri.app.ui.profile.ProfileFragment
import com.ziluri.app.ui.stats.StatsFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var homeFragment: HomeFragment
    private lateinit var memoFragment: MemoFragment
    private lateinit var statsFragment: StatsFragment
    private lateinit var profileFragment: ProfileFragment
    
    private lateinit var ivHome: ImageView
    private lateinit var ivMemo: ImageView
    private lateinit var ivStats: ImageView
    private lateinit var ivProfile: ImageView
    
    private var currentFragment: Fragment? = null
    private var currentNavIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initFragments()
        initViews()
        setupNavigation()
        
        // 默认显示首页
        showFragment(0)
    }
    
    private fun initFragments() {
        homeFragment = HomeFragment()
        memoFragment = MemoFragment()
        statsFragment = StatsFragment()
        profileFragment = ProfileFragment()
    }
    
    private fun initViews() {
        ivHome = findViewById(R.id.iv_home)
        ivMemo = findViewById(R.id.iv_memo)
        ivStats = findViewById(R.id.iv_stats)
        ivProfile = findViewById(R.id.iv_profile)
        
        val fabAdd = findViewById<FloatingActionButton>(R.id.fab_add)
        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddTodoActivity::class.java))
        }
    }
    
    private fun setupNavigation() {
        findViewById<android.view.View>(R.id.nav_home).setOnClickListener {
            showFragment(0)
        }
        
        findViewById<android.view.View>(R.id.nav_memo).setOnClickListener {
            showFragment(1)
        }
        
        findViewById<android.view.View>(R.id.nav_stats).setOnClickListener {
            showFragment(2)
        }
        
        findViewById<android.view.View>(R.id.nav_profile).setOnClickListener {
            showFragment(3)
        }
    }
    
    private fun showFragment(index: Int) {
        if (currentNavIndex == index && currentFragment != null) return
        
        currentNavIndex = index
        
        val fragment = when (index) {
            0 -> homeFragment
            1 -> memoFragment
            2 -> statsFragment
            3 -> profileFragment
            else -> homeFragment
        }
        
        supportFragmentManager.beginTransaction().apply {
            currentFragment?.let { hide(it) }
            
            if (fragment.isAdded) {
                show(fragment)
            } else {
                add(R.id.fragment_container, fragment)
            }
            
            commit()
        }
        
        currentFragment = fragment
        updateNavIcons(index)
    }
    
    private fun updateNavIcons(selectedIndex: Int) {
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        val hintColor = ContextCompat.getColor(this, R.color.text_hint)
        
        ivHome.setColorFilter(if (selectedIndex == 0) primaryColor else hintColor)
        ivMemo.setColorFilter(if (selectedIndex == 1) primaryColor else hintColor)
        ivStats.setColorFilter(if (selectedIndex == 2) primaryColor else hintColor)
        ivProfile.setColorFilter(if (selectedIndex == 3) primaryColor else hintColor)
    }
}
