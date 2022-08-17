package com.eram.roundvisualizer

import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class MainActivity : AppCompatActivity() {

    private lateinit var visualizerView: VisualizerView

    private val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer().apply {
            resources.openRawResourceFd(R.raw.scooterramp).apply {
                setDataSource(fileDescriptor, startOffset, length)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        visualizerView = findViewById(R.id.visualizerView)
        mediaPlayer.apply {
            setOnPreparedListener {
                visualizerView.initialize(audioSessionId)
                visualizerView.start()
            }
            prepare()
            start()
        }
        findViewById<View>(R.id.albumCard).setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.start()
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        visualizerView.finish()
    }
}
