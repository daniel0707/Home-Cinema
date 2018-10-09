package com.example.android.homecinema


import android.content.Intent
import android.content.pm.PackageManager
import android.database.CursorJoiner
import android.database.MatrixCursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener



class MainActivity : AppCompatActivity() {
    private var permissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()
        setupListView(permissionGranted)
        //Deprecated for ListView instead
        /*
        rv_video_list.layoutManager = LinearLayoutManager(this)
        rv_video_list.adapter = VideoAdapter(videos, this)
        */
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
                val intent = Intent(applicationContext, SettingsActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.transition.slide_lefttoright, R.transition.hold)

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {

        val mRootView: ViewGroup = findViewById(R.id.homeScreen)
        val mFade = Fade(Fade.IN)
        TransitionManager.beginDelayedTransition(mRootView, mFade)

        super.onStart()
    }
    private fun checkPermissions(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),123)
        } else {
            // Permission has already been granted
            permissionGranted = true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            123 -> {permissionGranted = (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            setupListView(permissionGranted)}
        }
            return
    }

    private fun setupListView(permission : Boolean) {
        if (permission) {
            val externalMediaContentURI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            val externalMediaThumbnailURI = MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI
            val projVideo = arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.HEIGHT, MediaStore.Video.Media.WIDTH)
            val myVideoCursor = contentResolver.query(externalMediaContentURI, projVideo, null, null, null)
            val projThumbnail = arrayOf(MediaStore.Video.Thumbnails.VIDEO_ID,MediaStore.Video.Thumbnails.DATA)
            val myThumbnailCursor = contentResolver.query(externalMediaThumbnailURI, projThumbnail, null, null, null)

            val cursorJoiner = CursorJoiner(myVideoCursor, arrayOf(MediaStore.Video.Media._ID), myThumbnailCursor, arrayOf(MediaStore.Video.Thumbnails.VIDEO_ID))

            val finalCursor = MatrixCursor(arrayOf(*projVideo, *projThumbnail))

            for (joinerResult in cursorJoiner) {
                when (joinerResult) {
                    // handle case where a row in cursorA is unique
                    CursorJoiner.Result.LEFT -> {
                        finalCursor.addRow(arrayOf(
                                myVideoCursor.getString(myVideoCursor.getColumnIndex(MediaStore.Video.Media._ID)),
                                myVideoCursor.getString(myVideoCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)),
                                myVideoCursor.getLong(myVideoCursor.getColumnIndex(MediaStore.Video.Media.DURATION)),
                                myVideoCursor.getString(myVideoCursor.getColumnIndex(MediaStore.Video.Media.DATA)),
                                myVideoCursor.getString(myVideoCursor.getColumnIndex(MediaStore.Video.Media.HEIGHT)),
                                myVideoCursor.getString(myVideoCursor.getColumnIndex(MediaStore.Video.Media.WIDTH)),
                                //missing thumbnail case
                                null,
                                null
                        ))

                    }
                    // handle case where a row in cursorB is unique
                    CursorJoiner.Result.RIGHT -> {
                    }
                    // handle case where a row with the same key is in both cursors
                    CursorJoiner.Result.BOTH -> {
                        finalCursor.addRow(arrayOf(
                                myVideoCursor.getString(myVideoCursor.getColumnIndex(MediaStore.Video.Media._ID)),
                                myVideoCursor.getString(myVideoCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)),
                                myVideoCursor.getLong(myVideoCursor.getColumnIndex(MediaStore.Video.Media.DURATION)),
                                myVideoCursor.getString(myVideoCursor.getColumnIndex(MediaStore.Video.Media.DATA)),
                                myVideoCursor.getString(myVideoCursor.getColumnIndex(MediaStore.Video.Media.HEIGHT)),
                                myVideoCursor.getString(myVideoCursor.getColumnIndex(MediaStore.Video.Media.WIDTH)),
                                myThumbnailCursor.getString(myThumbnailCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA)),
                                myThumbnailCursor.getString(myThumbnailCursor.getColumnIndex(MediaStore.Video.Thumbnails.VIDEO_ID))

                        ))
                    }
                }
            }

            myThumbnailCursor.close()
            myVideoCursor.close()

            val myVideoAdapter = VideoCursorAdapter(this, finalCursor)
            lv_video_list.adapter = myVideoAdapter
            lv_video_list.setOnItemClickListener{ _, _, position, _ ->
                finalCursor.moveToPosition(position)
                val myVideoItem = VideoItem(
                        finalCursor.getLong(finalCursor.getColumnIndex(MediaStore.Video.Media._ID)),
                        finalCursor.getString(finalCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)),
                        finalCursor.getLong(finalCursor.getColumnIndex(MediaStore.Video.Media.DURATION)),
                        finalCursor.getString(finalCursor.getColumnIndex(MediaStore.Video.Media.DATA)),
                        finalCursor.getString(finalCursor.getColumnIndex(MediaStore.Video.Media.HEIGHT)),
                        finalCursor.getString(finalCursor.getColumnIndex(MediaStore.Video.Media.WIDTH)),
                        finalCursor.getString(finalCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA))
                )
                val myIntent = Intent(this,VideoActivity::class.java)
                myIntent.putExtra("videoParcel",myVideoItem)

                startActivity(myIntent)
                overridePendingTransition(R.transition.slide_lefttoright, R.transition.hold)
            }
        }
    }
}
