package com.example.android.homecinema

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setSupportActionBar(settingsbar)

        // Now get the support action bar
        val actionBar = supportActionBar

        // Set action bar elevation
        actionBar!!.elevation = 4.0F

        backButton.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.transition.slide_lefttoright, R.transition.hold)

        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Set toolbar title
        settingsbar_text.text = "Settings"
        return super.onCreateOptionsMenu(menu)
    }

}
