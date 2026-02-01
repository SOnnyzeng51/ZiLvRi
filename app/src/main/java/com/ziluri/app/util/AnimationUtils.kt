package com.ziluri.app.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator

/**
 * 动画工具类
 */
object AnimationUtils {

    /**
     * 弹跳缩放动画
     */
    fun bounceScale(view: View, duration: Long = 300) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.2f, 0.9f, 1.1f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.2f, 0.9f, 1.1f, 1f)
        
        ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY).apply {
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }
    
    /**
     * 淡入动画
     */
    fun fadeIn(view: View, duration: Long = 300, onEnd: (() -> Unit)? = null) {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        
        ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f).apply {
            this.duration = duration
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onEnd?.invoke()
                }
            })
            start()
        }
    }
    
    /**
     * 淡出动画
     */
    fun fadeOut(view: View, duration: Long = 300, onEnd: (() -> Unit)? = null) {
        ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f).apply {
            this.duration = duration
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                    onEnd?.invoke()
                }
            })
            start()
        }
    }
    
    /**
     * 弹入动画
     */
    fun popIn(view: View, duration: Long = 400, onEnd: (() -> Unit)? = null) {
        view.scaleX = 0f
        view.scaleY = 0f
        view.alpha = 0f
        view.visibility = View.VISIBLE
        
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 1f)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
        
        ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY, alpha).apply {
            this.duration = duration
            interpolator = OvershootInterpolator(1.5f)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onEnd?.invoke()
                }
            })
            start()
        }
    }
    
    /**
     * 弹出动画
     */
    fun popOut(view: View, duration: Long = 300, onEnd: (() -> Unit)? = null) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0f)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f)
        
        ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY, alpha).apply {
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                    view.scaleX = 1f
                    view.scaleY = 1f
                    view.alpha = 1f
                    onEnd?.invoke()
                }
            })
            start()
        }
    }
    
    /**
     * 抖动动画（用于错误提示）
     */
    fun shake(view: View, duration: Long = 500) {
        ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0f, -10f, 10f, -10f, 10f, -5f, 5f, 0f).apply {
            this.duration = duration
            start()
        }
    }
    
    /**
     * 心跳动画
     */
    fun heartbeat(view: View, duration: Long = 1000) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.1f, 1f, 1.1f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.1f, 1f, 1.1f, 1f)
        
        ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY).apply {
            this.duration = duration
            repeatCount = ObjectAnimator.INFINITE
            start()
        }
    }
    
    /**
     * 旋转动画
     */
    fun rotate(view: View, fromDegrees: Float = 0f, toDegrees: Float = 360f, duration: Long = 500) {
        ObjectAnimator.ofFloat(view, View.ROTATION, fromDegrees, toDegrees).apply {
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }
    
    /**
     * 滑入动画（从底部）
     */
    fun slideInFromBottom(view: View, duration: Long = 300, onEnd: (() -> Unit)? = null) {
        view.translationY = view.height.toFloat()
        view.alpha = 0f
        view.visibility = View.VISIBLE
        
        val translateY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, view.height.toFloat(), 0f)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
        
        ObjectAnimator.ofPropertyValuesHolder(view, translateY, alpha).apply {
            this.duration = duration
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onEnd?.invoke()
                }
            })
            start()
        }
    }
    
    /**
     * 滑出动画（向底部）
     */
    fun slideOutToBottom(view: View, duration: Long = 300, onEnd: (() -> Unit)? = null) {
        val translateY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0f, view.height.toFloat())
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f)
        
        ObjectAnimator.ofPropertyValuesHolder(view, translateY, alpha).apply {
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                    view.translationY = 0f
                    view.alpha = 1f
                    onEnd?.invoke()
                }
            })
            start()
        }
    }
}
