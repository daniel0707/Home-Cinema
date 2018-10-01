package com.example.android.homecinema

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.view.ViewGroup

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {

        val mRootView: ViewGroup = findViewById(R.id.homeScreen)
        val mFade: Fade = Fade(Fade.IN)
        TransitionManager.beginDelayedTransition(mRootView, mFade)

        super.onStart()
    }
}
