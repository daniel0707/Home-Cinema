package com.example.android.homecinema

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.Menu
import android.widget.Toast
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.BaseArFragment
import kotlinx.android.synthetic.main.activity_video.*
import java.lang.Exception

class VideoActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment
    private lateinit var videoRenderable: ModelRenderable
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var customHeight : Any
    private lateinit var customWidth : Any

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        setSupportActionBar(camerabar)


        //set palybutton invisible
        playbutton.hide()

        camerabackButton.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.transition.slide_righttoleft, R.transition.hold)
        }

        arFragment = supportFragmentManager.findFragmentById(R.id.fragment_layout) as ArFragment

        //Fetch video data from passed intent
        val videoItem = intent.getParcelableExtra<VideoItem>("videoParcel")
        val videoURI = Uri.parse(videoItem.path)

        //Set up media player
        mediaPlayer = MediaPlayer.create(this, videoURI)
        val texture = ExternalTexture()
        mediaPlayer.setSurface(texture.surface)

        //Build a renderable for media player
        ModelRenderable.builder()
                .setSource(this, R.raw.chroma_key_video)
                .build()
                .thenAccept { it ->
                    videoRenderable = it
                    videoRenderable.material.setExternalTexture("videoTexture", texture)
                }
                .exceptionally { _ ->
                    val toast = Toast.makeText(this, "Unable to load video", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    null
                }

        arFragment.setOnTapArPlaneListener(
                BaseArFragment.OnTapArPlaneListener { hitResult, _, _ ->
                    if (videoRenderable == null) {
                        //protects app from crashing on failing to load video
                        return@OnTapArPlaneListener
                    }

                    //set up data from sharedpreferences
                    fetchSharedPreferences()

                    arFragment.arSceneView.planeRenderer.isVisible = false

                    val anchor = hitResult.createAnchor()
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(arFragment.arSceneView.scene)

                    val videoNode = Node()
                    videoNode.setParent(anchorNode)
                    videoNode.setLookDirection(videoNode.up)

                    //MediaPlayer can break on initializing and will be not even in error state or any other state
                    //except the fuck you state that is not detectable and throws IllegalStateException
                    //Solved by catching ther error and replacing the MediaPlayer with new instance
                    var videoHeight : Float
                    var videoWidth : Float
                    try {
                        videoWidth = mediaPlayer.videoWidth.toFloat()
                        videoHeight = mediaPlayer.videoHeight.toFloat()
                    }catch (e : Exception){
                        mediaPlayer = MediaPlayer()
                        mediaPlayer.setDataSource(this,videoURI)
                        mediaPlayer.setSurface(texture.surface)
                        mediaPlayer.prepare()
                        videoWidth = mediaPlayer.videoWidth.toFloat()
                        videoHeight = mediaPlayer.videoHeight.toFloat()
                    }

                    //Separate case for vertical and horizontal videos
                    if(videoHeight<videoWidth){
                        //width,height,depth = 1
                        videoNode.localScale = Vector3((customWidth as Int)/100f, (customHeight as Int).toFloat()/100f, 1.0f)
                        //center video in hitresult
                        videoNode.localPosition = Quaternion.rotateVector(videoNode.localRotation,Vector3(0f,0f - (customHeight as Int).toFloat()/200f,0f))
                    }else {
                        //height,width,depth = 1
                        videoNode.localScale = Vector3((customWidth as Int).toFloat() / 100f, (customHeight as Int).toFloat() / 100f, 1.0f)
                        //compensate for video being vertical
                        videoNode.localRotation = Quaternion.multiply(videoNode.localRotation, Quaternion.axisAngle(Vector3(0f, 0f, 1f), 90f))
                        //center video in hitresult
                        videoNode.localPosition = Quaternion.rotateVector(videoNode.localRotation, Vector3(0f, 0f - (customHeight as Int).toFloat() / 200f, 0f))
                    }
                    if (!mediaPlayer.isPlaying) {
                        mediaPlayer.start()
                        playbutton.setImageResource(android.R.drawable.ic_media_pause)
                        playbutton.show()
                        texture.surfaceTexture.setOnFrameAvailableListener { _ ->
                            videoNode.renderable = videoRenderable
                            texture.surfaceTexture.setOnFrameAvailableListener(null)
                        }
                    } else {
                        videoNode.renderable = videoRenderable
                    }
                    //Disable listener to prevent multiple instances of media view
                    arFragment.setOnTapArPlaneListener(null)

                    mediaPlayer.setOnCompletionListener {
                        playbutton.setImageResource(android.R.drawable.ic_media_play)
                    }
                }
        )
        //Play/Pause button
        playbutton.setOnClickListener {
            toggleVideoPlayPause()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Set toolbar title
        camerabar_text.text = getString(R.string.camerabar)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer != null) {
            mediaPlayer.release()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (mediaPlayer != null) {
            mediaPlayer.release()
        }
    }


    private fun fetchSharedPreferences() {
                    val sharedPref = this.getSharedPreferences(getString(R.string.shared_preference_key), Context.MODE_PRIVATE) ?: return
                    val defaultHeight = resources.getInteger(R.integer.default_height)
                    val defaultWidth = resources.getInteger(R.integer.default_width)
                    customHeight = sharedPref.getInt(getString(R.string.height_preference_key), defaultHeight)
                    customWidth = sharedPref.getInt(getString(R.string.width_preference_key), defaultWidth)
                }
    private fun toggleVideoPlayPause() {
        //toggles play & pause on clicks
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            playbutton.setImageResource(android.R.drawable.ic_media_play)
        } else {
            mediaPlayer.start()
            playbutton.setImageResource(android.R.drawable.ic_media_pause)
        }
    }
}
