package com.ziluri.app.ui.common

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import com.ziluri.app.R

/**
 * iOS风格的圆形复选框
 */
class CircleCheckBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    
    private val checkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        color = Color.WHITE
    }
    
    private var animationProgress = 0f
    private var _isChecked = false
    
    var isChecked: Boolean
        get() = _isChecked
        set(value) {
            if (_isChecked != value) {
                _isChecked = value
                animateCheck(value)
                onCheckedChangeListener?.invoke(value)
            }
        }
    
    var onCheckedChangeListener: ((Boolean) -> Unit)? = null
    
    private val uncheckedColor = ContextCompat.getColor(context, R.color.checkbox_unchecked)
    private val checkedColor = ContextCompat.getColor(context, R.color.checkbox_checked)
    
    init {
        isClickable = true
        setOnClickListener {
            isChecked = !isChecked
        }
    }
    
    private fun animateCheck(checked: Boolean) {
        val animator = ValueAnimator.ofFloat(
            if (checked) 0f else 1f,
            if (checked) 1f else 0f
        )
        animator.duration = 300
        animator.interpolator = OvershootInterpolator(2f)
        animator.addUpdateListener {
            animationProgress = it.animatedValue as Float
            invalidate()
        }
        animator.start()
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = minOf(width, height) / 2f - 4f
        
        // 绘制边框
        borderPaint.color = if (animationProgress > 0) {
            blendColors(uncheckedColor, checkedColor, animationProgress)
        } else {
            uncheckedColor
        }
        canvas.drawCircle(centerX, centerY, radius, borderPaint)
        
        // 绘制填充
        if (animationProgress > 0) {
            fillPaint.color = checkedColor
            fillPaint.alpha = (animationProgress * 255).toInt()
            canvas.drawCircle(centerX, centerY, radius * animationProgress, fillPaint)
        }
        
        // 绘制勾选
        if (animationProgress > 0.3f) {
            val checkProgress = ((animationProgress - 0.3f) / 0.7f).coerceIn(0f, 1f)
            drawCheck(canvas, centerX, centerY, radius * 0.5f, checkProgress)
        }
    }
    
    private fun drawCheck(canvas: Canvas, cx: Float, cy: Float, size: Float, progress: Float) {
        val path = Path()
        
        // 勾选的三个点
        val startX = cx - size * 0.5f
        val startY = cy
        val midX = cx - size * 0.1f
        val midY = cy + size * 0.4f
        val endX = cx + size * 0.5f
        val endY = cy - size * 0.3f
        
        path.moveTo(startX, startY)
        
        if (progress <= 0.5f) {
            // 画第一段
            val p = progress * 2
            path.lineTo(
                startX + (midX - startX) * p,
                startY + (midY - startY) * p
            )
        } else {
            // 画完整的勾
            path.lineTo(midX, midY)
            val p = (progress - 0.5f) * 2
            path.lineTo(
                midX + (endX - midX) * p,
                midY + (endY - midY) * p
            )
        }
        
        checkPaint.alpha = (progress * 255).toInt()
        canvas.drawPath(path, checkPaint)
    }
    
    private fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
        val inverseRatio = 1 - ratio
        val r = Color.red(color1) * inverseRatio + Color.red(color2) * ratio
        val g = Color.green(color1) * inverseRatio + Color.green(color2) * ratio
        val b = Color.blue(color1) * inverseRatio + Color.blue(color2) * ratio
        return Color.rgb(r.toInt(), g.toInt(), b.toInt())
    }
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredSize = 48
        val width = resolveSize(desiredSize, widthMeasureSpec)
        val height = resolveSize(desiredSize, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }
    
    fun setCheckedWithoutAnimation(checked: Boolean) {
        _isChecked = checked
        animationProgress = if (checked) 1f else 0f
        invalidate()
    }
}
