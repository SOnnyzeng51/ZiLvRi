package com.ziluri.app.ui.common

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import com.ziluri.app.R
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * 自定义庆祝动画视图
 * 显示立体"赞"图标旋转 + 经验粒子飞向"我的"
 */
class CelebrationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val particlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    
    private var thumbRotation = 0f
    private var thumbScale = 0f
    private var thumbAlpha = 0f
    
    private val particles = mutableListOf<Particle>()
    private var particleProgress = 0f
    
    private var targetX = 0f
    private var targetY = 0f
    
    private var isAnimating = false
    
    data class Particle(
        var x: Float,
        var y: Float,
        val startX: Float,
        val startY: Float,
        val endX: Float,
        val endY: Float,
        val size: Float,
        val color: Int,
        val delay: Float
    )
    
    init {
        thumbPaint.style = Paint.Style.FILL
        
        particlePaint.style = Paint.Style.FILL
        
        glowPaint.style = Paint.Style.FILL
        glowPaint.maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.NORMAL)
    }
    
    fun setTargetPosition(x: Float, y: Float) {
        targetX = x
        targetY = y
    }
    
    fun playAnimation() {
        if (isAnimating) return
        isAnimating = true
        
        // 初始化粒子
        initParticles()
        
        // 创建动画
        val thumbAnimator = createThumbAnimator()
        val particleAnimator = createParticleAnimator()
        
        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(thumbAnimator, particleAnimator)
        animatorSet.start()
    }
    
    private fun initParticles() {
        particles.clear()
        val centerX = width / 2f
        val centerY = height / 2f
        
        val colors = listOf(
            ContextCompat.getColor(context, R.color.exp_particle),
            ContextCompat.getColor(context, R.color.primary),
            ContextCompat.getColor(context, R.color.primary_light)
        )
        
        // 创建20个经验粒子
        repeat(20) { i ->
            val angle = Random.nextFloat() * 360
            val distance = Random.nextFloat() * 100 + 50
            val startX = centerX + cos(Math.toRadians(angle.toDouble())).toFloat() * distance
            val startY = centerY + sin(Math.toRadians(angle.toDouble())).toFloat() * distance
            
            particles.add(Particle(
                x = startX,
                y = startY,
                startX = startX,
                startY = startY,
                endX = if (targetX > 0) targetX else width * 0.85f,
                endY = if (targetY > 0) targetY else height.toFloat(),
                size = Random.nextFloat() * 8 + 4,
                color = colors[i % colors.size],
                delay = Random.nextFloat() * 0.3f
            ))
        }
    }
    
    private fun createThumbAnimator(): AnimatorSet {
        val scaleAnimator = ValueAnimator.ofFloat(0f, 1.2f, 1f).apply {
            duration = 600
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                thumbScale = it.animatedValue as Float
                invalidate()
            }
        }
        
        val rotationAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 800
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                thumbRotation = it.animatedValue as Float
                invalidate()
            }
        }
        
        val alphaAnimator = ValueAnimator.ofFloat(0f, 1f, 1f, 0f).apply {
            duration = 1200
            addUpdateListener {
                thumbAlpha = it.animatedValue as Float
                invalidate()
            }
        }
        
        return AnimatorSet().apply {
            playTogether(scaleAnimator, rotationAnimator, alphaAnimator)
        }
    }
    
    private fun createParticleAnimator(): ValueAnimator {
        return ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                particleProgress = it.animatedValue as Float
                updateParticlePositions()
                invalidate()
            }
            addListener(object : android.animation.Animator.AnimatorListener {
                override fun onAnimationStart(animation: android.animation.Animator) {}
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    isAnimating = false
                    visibility = GONE
                }
                override fun onAnimationCancel(animation: android.animation.Animator) {}
                override fun onAnimationRepeat(animation: android.animation.Animator) {}
            })
        }
    }
    
    private fun updateParticlePositions() {
        particles.forEach { particle ->
            val adjustedProgress = ((particleProgress - particle.delay) / (1 - particle.delay)).coerceIn(0f, 1f)
            particle.x = particle.startX + (particle.endX - particle.startX) * adjustedProgress
            particle.y = particle.startY + (particle.endY - particle.startY) * adjustedProgress
        }
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val centerX = width / 2f
        val centerY = height / 2f
        
        // 绘制大拇指图标
        if (thumbAlpha > 0) {
            canvas.save()
            canvas.translate(centerX, centerY)
            canvas.rotate(thumbRotation)
            canvas.scale(thumbScale, thumbScale)
            
            // 绘制3D效果的大拇指
            drawThumbUp(canvas, thumbAlpha)
            
            canvas.restore()
        }
        
        // 绘制经验粒子
        particles.forEach { particle ->
            val adjustedProgress = ((particleProgress - particle.delay) / (1 - particle.delay)).coerceIn(0f, 1f)
            if (adjustedProgress > 0) {
                val alpha = ((1 - adjustedProgress) * 255).toInt()
                
                // 绘制光晕
                glowPaint.color = particle.color
                glowPaint.alpha = (alpha * 0.5f).toInt()
                canvas.drawCircle(particle.x, particle.y, particle.size * 2, glowPaint)
                
                // 绘制粒子
                particlePaint.color = particle.color
                particlePaint.alpha = alpha
                canvas.drawCircle(particle.x, particle.y, particle.size, particlePaint)
            }
        }
    }
    
    private fun drawThumbUp(canvas: Canvas, alpha: Float) {
        val size = 60f
        
        // 底座阴影
        thumbPaint.color = Color.argb((alpha * 100).toInt(), 5, 150, 105)
        canvas.drawRoundRect(-size, -size * 0.3f, size, size, 16f, 16f, thumbPaint)
        
        // 主体
        thumbPaint.color = Color.argb((alpha * 255).toInt(), 16, 185, 129)
        canvas.drawRoundRect(-size * 0.9f, -size * 0.9f, size * 0.9f, size * 0.5f, 12f, 12f, thumbPaint)
        
        // 大拇指
        thumbPaint.color = Color.argb((alpha * 255).toInt(), 255, 255, 255)
        val thumbPath = Path().apply {
            moveTo(-size * 0.3f, -size * 0.2f)
            lineTo(-size * 0.3f, size * 0.3f)
            lineTo(size * 0.5f, size * 0.3f)
            lineTo(size * 0.5f, -size * 0.5f)
            lineTo(size * 0.1f, -size * 0.5f)
            lineTo(size * 0.1f, -size * 0.2f)
            close()
        }
        canvas.drawPath(thumbPath, thumbPaint)
        
        // 手掌部分
        canvas.drawRoundRect(-size * 0.6f, -size * 0.2f, -size * 0.3f, size * 0.3f, 4f, 4f, thumbPaint)
    }
}
