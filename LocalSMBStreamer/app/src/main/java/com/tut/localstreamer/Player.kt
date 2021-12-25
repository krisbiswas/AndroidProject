package com.tut.localstreamer

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.HandlerThread
import android.view.View
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.VideoView

class Player : AppCompatActivity() {

    private val ERROR_CODE = 102
    private val SUCCESS_CODE = 101

    lateinit var videoView: VideoView
    lateinit var mediaController: MediaController
//    var mediaProgressObserver: Thread? = null
    var showProgress = true
    var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        println("Active Threads: "+HandlerThread.activeCount())
        videoView = findViewById(R.id.videoView)
        progressBar = findViewById(R.id.progressBar)
        videoView.setOnPreparedListener{
            progressBar?.visibility = View.GONE
            videoView.start()
//            mediaProgressObserver!!.start()
        }
        videoView.setOnCompletionListener(MediaPlayer.OnCompletionListener {
            showProgress = false
            Toast.makeText(applicationContext, "Thank You...!!!", Toast.LENGTH_LONG)
                .show() // display a toast when an video is completed
//            Thread.sleep(3000)
            setResult(SUCCESS_CODE)
            finish()
        })
        videoView.setOnErrorListener(MediaPlayer.OnErrorListener { mp, what, extra ->
            Toast.makeText(applicationContext,
                "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show() // display a toast when an error is occured while playing an video
            finish()
            false
        })
    }
    override fun onStart() {
        super.onStart()
        val vid_path = intent.getStringExtra("videoPath").orEmpty()
        if(vid_path.isNotEmpty()){
            mediaController = MediaController(this)
            mediaController.setAnchorView(videoView)

            videoView.setMediaController(mediaController)
            videoView.setVideoPath(vid_path)

        }else{
            setResult(ERROR_CODE)
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        showProgress = false
    }

}