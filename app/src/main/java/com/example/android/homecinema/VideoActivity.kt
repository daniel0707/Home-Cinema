package com.example.android.homecinema

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
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
import kotlinx.android.synthetic.main.activity_settings.*


//this activity should receive settings for Size of screen from somewhere
class VideoActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment
    private lateinit var videoRenderable: ModelRenderable
    private lateinit var mediaPlayer: MediaPlayer

    // replace this later with settings value
    private val VIDEO_HEIGHT_METERS = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val defaultHeight = resources.getInteger(R.integer.default_height)
        val defaultWidth = resources.getInteger(R.integer.default_width)
        val customHeight = sharedPref.getInt(getString(R.string.height_preference_key), defaultHeight)
        val customWidth = sharedPref.getInt(getString(R.string.width_preference_key), defaultWidth)
        setSupportActionBar(settingsbar)

        backButton.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.transition.slide_righttoleft, R.transition.hold)
        }

        val videoItem = intent.getParcelableExtra<VideoItem>("videoParcel")

        arFragment = supportFragmentManager.findFragmentById(R.id.fragment_layout) as ArFragment

        var texture = ExternalTexture()

        val videoURI = Uri.parse(videoItem.path)

        mediaPlayer = MediaPlayer.create(this, videoURI)
        mediaPlayer.setSurface(texture.surface)

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
                        return@OnTapArPlaneListener
                    }
                    val anchor = hitResult.createAnchor()
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(arFragment.arSceneView.scene)

                    val videoNode = Node()
                    videoNode.setParent(anchorNode)
                    videoNode.setLookDirection(Quaternion.rotateVector(Quaternion(0.7071f,0.7071f,0f,0f),videoNode.up))
                    //videoNode.localRotation = Quaternion.axisAngle(videoNode.up,90f)

                    val videoWidth = mediaPlayer.videoWidth.toFloat()
                    val videoHeight = mediaPlayer.videoHeight.toFloat()
                    if(videoHeight<videoWidth){
                        videoNode.localScale = Vector3(customWidth.toFloat()/100, customHeight.toFloat()/100, 1.0f)
                    }else{
                        videoNode.localScale = Vector3(customHeight.toFloat()/100,customWidth.toFloat()/100, 1.0f)
                    }

                    if (!mediaPlayer.isPlaying) {
                        mediaPlayer.start()
                        texture.surfaceTexture.setOnFrameAvailableListener { _ ->
                            videoNode.renderable = videoRenderable
                            texture.surfaceTexture.setOnFrameAvailableListener(null)
                        }
                    } else {
                        videoNode.renderable = videoRenderable
                    }
                }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Set toolbar title
        settingsbar_text.text = "Camera"
        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (mediaPlayer != null) {
            mediaPlayer.release()
        }
    }
}
