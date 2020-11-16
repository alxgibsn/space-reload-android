package com.rive.spacereload

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import app.rive.runtime.kotlin.*
import com.liaoinstan.springview.container.BaseSimpleHeader
import com.liaoinstan.springview.widget.SpringView

class AnimatedHeader(context: Context, file: File) : BaseSimpleHeader() {

    private val context: Context
    private var freshTime: Long = 0
    private var file: File? = null
    private var riveView: AnimationView? = null

    init {
        type = SpringView.Type.OVERLAP
        movePara = 2.0f
        this.context = context
        this.file = file
    }

    override fun getView(inflater: LayoutInflater, viewGroup: ViewGroup): View {
        val view = inflater.inflate(R.layout.header, viewGroup, false)
        val headerView  = view.findViewById<LinearLayout>(R.id.root)

        var renderer = Renderer()
        val artboard = file!!.artboard()
        val riveView = AnimationView(renderer, artboard, context)
        this.riveView = riveView

        headerView.addView(riveView)
        return view
    }

    override fun getDragLimitHeight(rootView: View?): Int {
        return 350
    }

    override fun getEndingAnimHeight(rootView: View?): Int {
        return 350
    }

    override fun getDragSpringHeight(rootView: View?): Int {
        return 350
    }

    override fun onPreDrag(rootView: View) {}

    override fun onDropAnim(rootView: View, dy: Int) {
        riveView?.pullExtent = dy
    }

    override fun onLimitDes(rootView: View, upORdown: Boolean) {
        riveView?.isRefreshing = true
    }

    override fun onStartAnim() {
        freshTime = System.currentTimeMillis()
    }

    override fun onFinishAnim() {}

    override fun onResetAnim() {
        riveView?.reset()
    }
}