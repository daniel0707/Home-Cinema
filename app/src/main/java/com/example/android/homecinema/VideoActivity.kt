package com.example.android.homecinema

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.Toast
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.BaseArFragment


//this activity should receive settings for Size of screen from somewhere
class VideoActivity : AppCompatActivity(){

    private lateinit var arFragment: ArFragment
    private lateinit var videoRenderable: ModelRenderable
    private lateinit var mediaPlayer: MediaPlayer

// replace this later with settings value
    private val VIDEO_HEIGHT_METERS = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        val videoItem = intent.getParcelableExtra<VideoItem>("videoParcel")

        arFragment = supportFragmentManager.findFragmentById(R.id.fragment_layout) as ArFragment

        var texture = ExternalTexture()

        val videoURI = Uri.parse(videoItem.path)

        mediaPlayer = MediaPlayer.create(this,videoURI)
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
                    null }

        arFragment.setOnTapArPlaneListener (
                BaseArFragment.OnTapArPlaneListener { hitResult, _, _ ->
                    if(videoRenderable == null){
                        return@OnTapArPlaneListener
                    }
                    val anchor = hitResult.createAnchor()
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(arFragment.arSceneView.scene)

                    val videoNode = Node()
                    videoNode.setParent(anchorNode)
                    videoNode.setLookDirection(videoNode.up)


                    val videoWidth  = mediaPlayer.videoWidth.toFloat()
                    val videoHeight = mediaPlayer.videoHeight.toFloat()
                    if(videoHeight<videoWidth){
                        videoNode.localScale = Vector3(VIDEO_HEIGHT_METERS * (videoWidth / videoHeight), VIDEO_HEIGHT_METERS, 1.0f)
                    }else{
                        videoNode.localScale = Vector3(VIDEO_HEIGHT_METERS,VIDEO_HEIGHT_METERS * (videoWidth / videoHeight), 1.0f)
                    }

                    if(!mediaPlayer.isPlaying){
                        mediaPlayer.start()
                        texture.surfaceTexture.setOnFrameAvailableListener { _ ->
                            videoNode.renderable = videoRenderable
                            texture.surfaceTexture.setOnFrameAvailableListener(null) }
                    }else{
                        videoNode.renderable = videoRenderable
                    }
                }
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        if(mediaPlayer !=null){
            mediaPlayer.release()
        }
    }
}
