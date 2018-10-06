package com.example.android.homecinema

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.transition.Explode
import android.transition.Slide
import android.transition.TransitionManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.bluetooth_popup.*
import kotlinx.android.synthetic.main.help_popup.*
import kotlinx.android.synthetic.main.view_popup.*

class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setSupportActionBar(settingsbar)

        backButton.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.transition.slide_lefttoright, R.transition.hold)

        }

        bluetoothButton.setOnClickListener {
            bluetoothPopup()
        }

        // VIEW FIELD OF SETTING ACTIVITY
        viewButton.setOnClickListener {
            viewPopup()
        }

        helpButton.setOnClickListener {
            helpPopup()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Set toolbar title
        settingsbar_text.text = "Settings"
        return super.onCreateOptionsMenu(menu)
    }

    private fun bluetoothPopup() {
        // Initialize a new layout inflater instance
        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.bluetooth_popup, null)

        //Get screen size
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var width = displayMetrics.widthPixels
        // Initialize a new instance of popup window
        val popupWindow = PopupWindow(
                view, // Custom view to show in popup window, set popup size
                width - 50, 1000
        )
        popupWindow.isOutsideTouchable = true
        // If API level 23 or higher then execute the code
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Create a new slide animation for popup window enter transition
            val slideUp = Slide()
            slideUp.slideEdge = Gravity.BOTTOM
            popupWindow.enterTransition = slideUp
        }

        // Get the widgets reference from custom view
        val bluetoothClose = view.findViewById<ImageView>(R.id.bluetooth_close)
        // Set a click listener for popup's button widget
        bluetoothClose.setOnClickListener {
            // Dismiss the popup window
            popupWindow.dismiss()
        }


        // Finally, show the popup window on app
        TransitionManager.beginDelayedTransition(root_layout)
        popupWindow.showAtLocation(
                root_layout, // Location to display popup window
                Gravity.CENTER, // Exact position of layout to display popup
                0, // X offset
                0 // Y offset
        )
    }

    private fun viewPopup() {
        // Initialize a new layout inflater instance
        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.view_popup, null)

        //Get screen size
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var width = displayMetrics.widthPixels
        // Initialize a new instance of popup window
        val popupWindow = PopupWindow(
                view, // Custom view to show in popup window, set popup size
                width - 50, 1000
        )
        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.update()


        // If API level 23 or higher then execute the code
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Create a new slide animation for popup window enter transition
            val slideUp = Slide()
            slideUp.slideEdge = Gravity.BOTTOM
            popupWindow.enterTransition = slideUp
        }

        // Get the widgets reference from custom view
        val viewClose = view.findViewById<ImageView>(R.id.view_close)
        // Set a click listener for popup's button widget
        viewClose.setOnClickListener {
            // Dismiss the popup window & show what the user added as new dimensions for the AR object in settingsView
            val inputHeight = view.findViewById<EditText>(R.id.view_height).text
            val inputWidth = view.findViewById<EditText>(R.id.view_width).text
            view_details.text = "$inputHeight" + "x" + "$inputWidth"
            popupWindow.dismiss()
        }

        // Finally, show the popup window on app
        TransitionManager.beginDelayedTransition(root_layout)
        popupWindow.showAtLocation(
                root_layout, // Location to display popup window
                Gravity.CENTER, // Exact position of layout to display popup
                0, // X offset
                0 // Y offset
        )
    }

    private fun helpPopup() {
        // Initialize a new layout inflater instance
        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.help_popup, null)

        //Get screen size
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var width = displayMetrics.widthPixels
        // Initialize a new instance of popup window
        val popupWindow = PopupWindow(
                view, // Custom view to show in popup window, set popup size
                width - 50, 1000
        )

        popupWindow.isOutsideTouchable = true
        // If API level 23 or higher then execute the code
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Create a new slide animation for popup window enter transition
            val slideUp = Slide()
            slideUp.slideEdge = Gravity.BOTTOM
            popupWindow.enterTransition = slideUp
        }

        // Get the widgets reference from custom view
        val helpClose = view.findViewById<ImageView>(R.id.help_close)
        // Set a click listener for popup's button widget
        helpClose.setOnClickListener {
            // Dismiss the popup window
            popupWindow.dismiss()
        }

        // Finally, show the popup window on app
        TransitionManager.beginDelayedTransition(root_layout)
        popupWindow.showAtLocation(
                root_layout, // Location to display popup window
                Gravity.CENTER, // Exact position of layout to display popup
                0, // X offset
                0 // Y offset
        )
    }

}
