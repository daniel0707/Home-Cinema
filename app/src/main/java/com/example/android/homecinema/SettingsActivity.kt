package com.example.android.homecinema

import android.annotation.SuppressLint
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
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.widget.*
import kotlinx.android.synthetic.main.activity_settings.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile

class SettingsActivity : AppCompatActivity() {

    lateinit var customHeight : Any
    lateinit var customWidth : Any

    @SuppressLint("InflateParams", "SetTextI18n")
    //this view cannot have a parent, text needs to be concatenated
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setSupportActionBar(settingsbar)

        //initial text value setup
        fetchPreferences()

        val inflater : LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.view_popup,null)
        val customDimensionsHeight = view.findViewById<TextView>(R.id.customDimensionHeight)
        val customDimensionsWidth = view.findViewById<TextView>(R.id.customDimensionWidth)
        customDimensionsHeight.text = customHeight.toString()
        customDimensionsWidth.text = customWidth.toString()
        view_details.text = "$customWidth x $customHeight"


        backButton.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.transition.slide_righttoleft, R.transition.hold)
        }

        //Check if bluetooth is already on and change details text to On
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter.state == BluetoothAdapter.STATE_ON){
            bluetooth_details.text = getString(R.string.Bluetooth_On)
        }
        //Check if bluetooth is connected to an audio device and set text accordingly
        if(mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP) == BluetoothAdapter.STATE_CONNECTED){
            bluetoothconnection_details.text = getString(R.string.Bluetooth_connected)
        } else if (mBluetoothAdapter.state == BluetoothAdapter.STATE_ON) {
            bluetoothconnection_details.text = getString(R.string.Bluetooth_disconnected)
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
        settingsbar_text.text = getString(R.string.settingsbar_text)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("InflateParams", "ObsoleteSdkInt")
    //this view cannot have a parent, sdk supports legacy
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
                bluetoothconnection_details.text = getString(R.string.Bluetooth_connected)
            } else if (bluetooth_details.text != "Off"){
                bluetoothconnection_details.text = getString(R.string.Bluetooth_disconnected)
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
                if (!mBluetoothAdapter.isEnabled) {
                    mBluetoothAdapter.enable()
                    bluetooth_details.text = getString(R.string.Bluetooth_On)
                    //open Android bluetooth connection menu
                    startActivity( Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS))
                }
            } else {
                // The switch is disabled
                mBluetoothAdapter.disable()
                bluetooth_details.text = getString(R.string.Bluetooth_off)
                //set connection state textview to empty
                bluetoothconnection_details.text = ""
            }
        }
        if (mBluetoothAdapter.state == BluetoothAdapter.STATE_ON){
            switchButton.isChecked = true
        }
    }

    @SuppressLint("InflateParams", "ObsoleteSdkInt", "SetTextI18n", "ApplySharedPref")
    //Layout needs null as parent, SDK supports legacy, Text has to be concatenated, SharedPref need to be sync
    private fun viewPopup() {
        // Initialize a new layout inflater instance
        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.view_popup, null)

        fetchPreferences()

        view.findViewById<TextView>(R.id.customDimensionHeight).text = customHeight.toString()
        view.findViewById<TextView>(R.id.customDimensionWidth).text = customWidth.toString()

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

            val sharedPref = this.getSharedPreferences(getString(R.string.shared_preference_key),Context.MODE_PRIVATE)
            with (sharedPref.edit()) {
                putInt(getString(R.string.width_preference_key), customDimensionsWidth.toString().toInt())
                putInt(getString(R.string.height_preference_key), customDimensionsHeight.toString().toInt())
                commit()
            }

            view_details.text = "$customDimensionsWidth x $customDimensionsHeight"
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
                customDimensionsHeight.text = s

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
                customDimensionsWidth.text = s
            }
        })
    }

    @SuppressLint("InflateParams", "ObsoleteSdkInt")
    //Layout needs null as parent, SDK checks for legacy situation
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

    private fun fetchPreferences(){
        val sharedPref = this.getSharedPreferences(getString(R.string.shared_preference_key),Context.MODE_PRIVATE)?: return
        val defaultHeight = resources.getInteger(R.integer.default_height)
        val defaultWidth = resources.getInteger(R.integer.default_width)
        customHeight = sharedPref.getInt(getString(R.string.height_preference_key), defaultHeight)
        customWidth = sharedPref.getInt(getString(R.string.width_preference_key), defaultWidth)
    }

}
