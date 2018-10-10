package com.example.android.homecinema

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.view.Gravity
import android.widget.Toast

class BluetoothService : Service() {

    lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate() {
        super.onCreate()
        // receives brodcasts for BluetoothState changes
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent?.action


                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED))
                {
                    context?: applicationContext
                    val toast = Toast.makeText(context,"", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    val state = intent?.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                    when (state) {
                        BluetoothAdapter.STATE_OFF -> {
                            toast.setText("Bluetooth is off")
                            toast.show()
                        }
                        BluetoothAdapter.STATE_TURNING_OFF -> {
                            toast.setText("Bluetooth is turning off")
                            toast.show()
                        }
                        BluetoothAdapter.STATE_ON -> {
                            toast.setText("Bluetooth is on")
                            toast.show()
                        }
                        BluetoothAdapter.STATE_TURNING_ON -> {
                            toast.setText("Bluetooth is turning on")
                            toast.show()
                        }
                    }
                }
            }
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction(android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(broadcastReceiver,intentFilter)
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
}

