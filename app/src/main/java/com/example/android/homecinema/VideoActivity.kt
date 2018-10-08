package com.example.android.homecinema

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.MotionEvent
import android.widget.Toast
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.BaseArFragment
import java.net.URI


//this activity should receive settings for Size of screen from somewhere
//and video source too
class VideoActivity : AppCompatActivity(){

    private lateinit var arFragment: ArFragment
    private lateinit var videoRenderable: ModelRenderable
    private lateinit var mediaPlayer: MediaPlayer

    //private val CHROMA_KEY_COLOR = Color(0.1843f, 1.0f, 0.098f)
    private val VIDEO_HEIGHT_METERS = 1.5f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        val videoItem = intent.getParcelableExtra<VideoItem>("videoParcel")

        arFragment = supportFragmentManager.findFragmentById(R.id.fragment_layout) as ArFragment

        var texture = ExternalTexture()

        val videoURI = Uri.parse(videoItem.path)

        mediaPlayer = MediaPlayer.create(this,videoURI)
        mediaPlayer.setSurface(texture.surface)
        //mediaPlayer.isLooping = true

        ModelRenderable.builder()
                .setSource(this, R.raw.chroma_key_video)
                .build()
                .thenAccept { it ->
                    videoRenderable = it
                    videoRenderable.material.setExternalTexture("videoTexture", texture)
                    //videoRenderable.material.setFloat4("keyColor", CHROMA_KEY_COLOR)
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

                    var videoNode = Node()
                    videoNode.setParent(anchorNode)
                    videoNode.localRotation=(Quaternion.axisAngle(Vector3(1f,0f,0f),90f))

                    val videoWidth  = mediaPlayer.videoWidth.toFloat()
                    val videoHeight = mediaPlayer.videoHeight.toFloat()
                    if(videoHeight<videoWidth){
                        videoNode.localScale = Vector3(VIDEO_HEIGHT_METERS * (videoWidth / videoHeight), VIDEO_HEIGHT_METERS, 1.0f)
                    }else{
                        videoNode.localScale = Vector3(VIDEO_HEIGHT_METERS,VIDEO_HEIGHT_METERS * (videoWidth / videoHeight), 1.0f)
                    }

                    if(!mediaPlayer.isPlaying){
                        mediaPlayer.start()

                        texture
                                .surfaceTexture.setOnFrameAvailableListener { _ ->
                            videoNode.renderable = videoRenderable
                            texture.surfaceTexture.setOnFrameAvailableListener(null)
                        }
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
