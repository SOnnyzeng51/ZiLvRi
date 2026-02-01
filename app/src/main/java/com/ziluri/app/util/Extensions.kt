package com.ziluri.app.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 * View 扩展函数
 */
fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.isVisible(): Boolean = visibility == View.VISIBLE

fun View.toggleVisibility() {
    visibility = if (isVisible()) View.GONE else View.VISIBLE
}

fun View.showWithAnimation(duration: Long = 300) {
    AnimationUtils.fadeIn(this, duration)
}

fun View.hideWithAnimation(duration: Long = 300) {
    AnimationUtils.fadeOut(this, duration)
}

/**
 * 隐藏软键盘
 */
fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

/**
 * 显示软键盘
 */
fun View.showKeyboard() {
    requestFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

/**
 * 防止重复点击
 */
fun View.setOnSingleClickListener(interval: Long = 500, onClick: (View) -> Unit) {
    var lastClickTime = 0L
    setOnClickListener { view ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= interval) {
            lastClickTime = currentTime
            onClick(view)
        }
    }
}

/**
 * Context 扩展函数
 */
fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.toastLong(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

/**
 * Fragment 扩展函数
 */
fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    context?.toast(message, duration)
}

fun Fragment.toastLong(message: String) {
    context?.toastLong(message)
}

fun Fragment.hideKeyboard() {
    view?.hideKeyboard()
}

/**
 * dp 转 px
 */
fun Context.dpToPx(dp: Float): Int {
    return (dp * resources.displayMetrics.density + 0.5f).toInt()
}

fun Context.dpToPx(dp: Int): Int {
    return dpToPx(dp.toFloat())
}

/**
 * px 转 dp
 */
fun Context.pxToDp(px: Int): Float {
    return px / resources.displayMetrics.density
}

/**
 * sp 转 px
 */
fun Context.spToPx(sp: Float): Int {
    return (sp * resources.displayMetrics.scaledDensity + 0.5f).toInt()
}

/**
 * 获取屏幕宽度
 */
fun Context.screenWidth(): Int {
    return resources.displayMetrics.widthPixels
}

/**
 * 获取屏幕高度
 */
fun Context.screenHeight(): Int {
    return resources.displayMetrics.heightPixels
}

/**
 * 字符串扩展
 */
fun String?.orDefault(default: String = ""): String {
    return this ?: default
}

fun String?.isNotNullOrBlank(): Boolean {
    return !this.isNullOrBlank()
}

/**
 * Long 时间戳扩展
 */
fun Long.toDateString(pattern: String = "yyyy-MM-dd"): String {
    return DateUtils.formatDate(this, pattern)
}

fun Long.toFullDateString(): String {
    return DateUtils.formatFullDate(this)
}

fun Long.isToday(): Boolean {
    return DateUtils.isToday(this)
}

/**
 * 集合扩展
 */
fun <T> List<T>.safeGet(index: Int): T? {
    return if (index in indices) this[index] else null
}
