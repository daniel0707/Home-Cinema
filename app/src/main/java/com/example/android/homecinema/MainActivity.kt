package com.example.android.homecinema

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup

class MainActivity : AppCompatActivity() {

    val videos: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            val reqID = 1
            val intent = Intent(this,VideoActivity::class.java)
            startActivityForResult(intent,reqID)
        }
        super.onCreate(savedInstanceState)

        addVideos()

        rv_video_list.layoutManager = LinearLayoutManager(this)
        rv_video_list.adapter = VideoAdapter(videos, this)

        // Set the toolbar as support action bar
        setSupportActionBar(toolbar)

        // Now get the support action bar
        val actionBar = supportActionBar

        // Set toolbar title/app title
        actionBar!!.title = "HomeCinema"


        // Set action bar elevation
        actionBar.elevation = 4.0F

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.homecinemamenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu item
        when (item.itemId) {
            R.id.action_settings -> {
                Log.i("DBG", "CLICKED")
                //TODO open settings activity
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
