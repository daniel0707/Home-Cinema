package com.example.android.homecinema

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        button.setOnClickListener {
            val reqID = 1
            val intent = Intent(this,VideoActivity::class.java)
            startActivityForResult(intent,reqID)
        }
    }
}
