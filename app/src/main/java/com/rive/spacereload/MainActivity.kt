package com.rive.spacereload

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import app.rive.runtime.kotlin.File
import com.liaoinstan.springview.widget.SpringView
import com.liaoinstan.springview.widget.SpringView.OnFreshListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var file = File(resources.openRawResource(R.raw.space_reload).readBytes())
        val springView = findViewById<SpringView>(R.id.springview)
        var header = AnimatedHeader(this, file)
        springView?.header = header
        springView?.setListener(object : OnFreshListener {
            override fun onRefresh() {
                Handler()
                    .postDelayed({ springView?.onFinishFreshAndLoad() }, 3000)
            }
            override fun onLoadmore() {
                Handler()
                    .postDelayed({ springView?.onFinishFreshAndLoad() }, 3000)
            }
        })
    }
}