package com.rive.spacereload

import android.content.Context
import android.graphics.Canvas
import android.util.Log
import android.view.View
import app.rive.runtime.kotlin.*
import kotlin.properties.Delegates


class AnimationView : View {
    private var lastTime: Long = 0

    private val renderer: Renderer
    private val artboard: Artboard

    private val idleInstance: LinearAnimationInstance
    private val pullInstance: LinearAnimationInstance
    private val triggerInstance: LinearAnimationInstance
    private val loadingInstance: LinearAnimationInstance

    var isRefreshing: Boolean = false
    var pullExtent: Int by Delegates.observable(0) { _, _, new ->
        targetBounds = AABB(width.toFloat(), new.toFloat())
        isPlaying = new > 1
    }

    lateinit var targetBounds: AABB
    var isPlaying = false
        set(value) {
            if (value != field) {
                field = value
                if (value) {
                    lastTime = System.currentTimeMillis()
                    invalidate()
                }
            }
        }

    constructor(_renderer: Renderer, _artboard: Artboard, context: Context) : super(context) {
        renderer = _renderer
        artboard = _artboard
        idleInstance = LinearAnimationInstance(_artboard.animation(0))
        pullInstance = LinearAnimationInstance(_artboard.animation(1))
        triggerInstance = LinearAnimationInstance(_artboard.animation(2))
        loadingInstance = LinearAnimationInstance(_artboard.animation(3))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        lastTime = System.currentTimeMillis()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        redraw(canvas)
    }

    private fun redraw(canvas: Canvas) {
        val currentTime = System.currentTimeMillis()
        val elapsed = (currentTime - lastTime) / 1000f
        lastTime = currentTime
        renderer.canvas = canvas
        renderer.align(Fit.COVER, Alignment.CENTER, targetBounds, artboard.bounds())

        idleInstance.advance(elapsed)
        idleInstance.apply(artboard, 1f)

        if (!isRefreshing) {
            val pos = pullExtent.toFloat() / 300.toFloat()
            val frames = if (pullInstance.animation.workEnd > 0) {
                pullInstance.animation.workEnd - pullInstance.animation.workStart
            } else {
                pullInstance.animation.duration
            }
            val time = frames.toFloat() / pullInstance.animation.fps.toFloat()
            pullInstance.time(time * pos)
            pullInstance.apply(artboard, 1f)
            pullInstance.advance(elapsed)
        } else {
            triggerInstance.apply(artboard, 1f)
            triggerInstance.advance(elapsed)
            val triggerTime = triggerInstance.time()
            val triggerAnimation = triggerInstance.animation
            if (triggerTime >= triggerAnimation.workEnd / triggerAnimation.fps) {
                loadingInstance.apply(artboard, 1f)
                loadingInstance.advance(elapsed)
            }
        }

        canvas.save()
        artboard.advance(elapsed)
        artboard.draw(renderer)
        canvas.restore()

        if (isPlaying) {
            // Paint again.
            invalidate()
        }
    }

    fun reset() {
        isRefreshing = false
        triggerInstance.time(0f)
        triggerInstance.apply(artboard, 1f)
        loadingInstance.time(0f)
        loadingInstance.apply(artboard, 1f)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh);
        targetBounds = AABB(w.toFloat(), h.toFloat());
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        renderer.cleanup()
    }
}