package com.example.android.homecinema

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.Slide
import android.transition.TransitionManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.widget.*
import kotlinx.android.synthetic.main.activity_settings.*
import java.lang.Compiler.disable
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import kotlinx.android.synthetic.main.bluetooth_popup.*


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

        //Check if bluetooth is already on and change details text to On
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter.state == BluetoothAdapter.STATE_ON){
            bluetooth_details.text = "On | "
        }
        //Check if bluetooth is connected to an audio device and set text accordingly
        if(mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP) == BluetoothAdapter.STATE_CONNECTED){
            bluetoothconnection_details.text = "Connected"
        } else if (mBluetoothAdapter.state == BluetoothAdapter.STATE_ON) {
            bluetoothconnection_details.text = "Disconnected"
        }

        // BLUETOOTH
        bluetoothButton.setOnClickListener {
            //open bluetooth popup
            bluetoothPopup()
        }

        // VIEW
        viewButton.setOnClickListener {
            //open view popup
            viewPopup()
        }

        // HELP
        helpButton.setOnClickListener {
            //open help popup
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

        val width = displayMetrics.widthPixels
        // Initialize a new instance of popup window
        val popupWindow = PopupWindow(
                view, // Custom view to show in popup window, set popup size
                width - 50, 1000
        )
        // If API level 23 or higher then execute the code
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Create a new slide animation for popup window enter transition
            val slideUp = Slide()
            slideUp.slideEdge = Gravity.BOTTOM
            popupWindow.enterTransition = slideUp
        }

        // Get the widgets reference from custom view
        val bluetoothClose = view.findViewById<ImageView>(R.id.bluetooth_close)

        // Get bluetooth adapter
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // Set a click listener for popup's button widget
        bluetoothClose.setOnClickListener {
            // Dismiss the popup window & check bluetooth connection state
            if(mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP) == BluetoothAdapter.STATE_CONNECTED) {
                bluetoothconnection_details.text = "Connected"
            } else if (bluetooth_details.text != "Off"){
                bluetoothconnection_details.text = "Disconnected"
            }
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


        val switchButton = view.findViewById<Switch>(R.id.switch_button)
        switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // The switch is enabled/checked
                val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                if (!mBluetoothAdapter.isEnabled) {
                    mBluetoothAdapter.enable()
                    bluetooth_details.text = "On | "
                    //open Android bluetooth connection menu
                    startActivity( Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS))
                }
            } else {
                // The switch is disabled
                val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                mBluetoothAdapter.disable()
                bluetooth_details.text = "Off"
                //set connection state textview to empty
                bluetoothconnection_details.text = ""
            }
        }
        if (mBluetoothAdapter.state == BluetoothAdapter.STATE_ON){
            switchButton.isChecked = true
        }
    }

    private fun viewPopup() {
        // Initialize a new layout inflater instance
        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.view_popup, null)

        //Get screen size
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        // Initialize a new instance of popup window
        val popupWindow = PopupWindow(
                view, // Custom view to show in popup window, set popup size
                width - 50, 1000
        )
        popupWindow.isFocusable = true
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
            popupWindow.dismiss()
            val customDimensionsHeight = view.findViewById<TextView>(R.id.customDimensionHeight).text
            val customDimensionsWidth = view.findViewById<TextView>(R.id.customDimensionWidth).text

            view_details.text = "$customDimensionsHeight" + "x" + "$customDimensionsWidth"
        }

        // Finally, show the popup window on app
        TransitionManager.beginDelayedTransition(root_layout)
        popupWindow.showAtLocation(
                root_layout, // Location to display popup window
                Gravity.CENTER, // Exact position of layout to display popup
                0, // X offset
                0 // Y offset
        )

        //values for popup edit fields
        val inputHeight = view.findViewById<EditText>(R.id.view_height)
        val inputWidth = view.findViewById<EditText>(R.id.view_width)

        //Height edit field change listener that changes customDimensions textview to display what the user input was
        inputHeight.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                val customDimensionsHeight = view.findViewById<TextView>(R.id.customDimensionHeight)
                customDimensionsHeight.setText(s)
            }
        })

        //Width edit field change listener that changes customDimensions textview to display what the user input was
        inputWidth.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                val customDimensionsWidth = view.findViewById<TextView>(R.id.customDimensionWidth)
                customDimensionsWidth.setText(s)
            }
        })
    }

    private fun helpPopup() {
        // Initialize a new layout inflater instance
        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.help_popup, null)

        //Get screen size
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        // Initialize a new instance of popup window
        val popupWindow = PopupWindow(
                view, // Custom view to show in popup window, set popup size
                width - 50, 1000
        )

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
