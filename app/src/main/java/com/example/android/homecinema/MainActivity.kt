package com.example.android.homecinema

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*
import android.transition.Fade
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.widget.*


class MainActivity : AppCompatActivity() {

    val videos: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)

        /* button.setOnClickListener {

        //TODO implement this functionality to recyclerView element clicks
            val reqID = 1
            val intent = Intent(this,VideoActivity::class.java)
            startActivityForResult(intent,reqID)
        }*/

        super.onCreate(savedInstanceState)

        addVideos()

        rv_video_list.layoutManager = LinearLayoutManager(this)
        rv_video_list.adapter = VideoAdapter(videos, this)

        // Set the toolbar as support action bar
        setSupportActionBar(toolbar)

        // Now get the support action bar
        val actionBar = supportActionBar

        // Set action bar elevation
        actionBar!!.elevation = 4.0F
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.homecinemamenu, menu)
        // Set toolbar title
        toolbar_text.text = "HomeCinema"
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu item
        when (item.itemId) {
            R.id.action_settings -> {
                Log.i("DBG", "CLICKED")
                val intent = Intent(applicationContext, SettingsActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.transition.slide_righttoleft, R.transition.hold)

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {

        val mRootView: ViewGroup = findViewById(R.id.homeScreen)
        val mFade: Fade = Fade(Fade.IN)
        TransitionManager.beginDelayedTransition(mRootView, mFade)

        super.onStart()
    }

    fun addVideos() {
        //TODO GET VIDEOS HERE
        videos.add("example.mp4")
        videos.add("test.mp4")

        videos.add("test.mp4")
        videos.add("test.mp4")
        videos.add("test.mp4")
        videos.add("test.mp4")
        videos.add("test.mp4")
        videos.add("test.mp4")
        videos.add("test.mp4")
        videos.add("test.mp4")

        videos.add("test.mp4")
    }
}
