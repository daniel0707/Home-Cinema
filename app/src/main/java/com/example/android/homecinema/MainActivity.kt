package com.example.android.homecinema

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.transition.Fade
import android.transition.TransitionManager
import android.view.ViewGroup

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            val reqID = 1
            val intent = Intent(this,VideoActivity::class.java)
            startActivityForResult(intent,reqID)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {

        val mRootView: ViewGroup = findViewById(R.id.homeScreen)
        val mFade: Fade = Fade(Fade.IN)
        TransitionManager.beginDelayedTransition(mRootView, mFade)

        super.onStart()
    }
}
