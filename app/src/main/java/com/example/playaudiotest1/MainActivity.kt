package com.example.playaudiotest1

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import com.example.playaudiotest1.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    //媒体播放器
    private val mediaPlayer = MediaPlayer()
    private val timer = Timer()
    /**
     * 标记是否正在滑动中,true不在滑动中，false在滑动中
     */
    private var isTrackingTouch = true
    private val job = Job()
    private val scopeMusic = CoroutineScope(job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initMediaPlayer()
        intMusicSeekBar()

        initVideo()


        mBinding.btnPlayMusic.setOnClickListener {
            if (!mediaPlayer.isPlaying){//isPlaying正在播放
                mediaPlayer.start()//开始播放
                intTimer()
            }
        }

        mBinding.btnPauseMusic.setOnClickListener {
            if (mediaPlayer.isPlaying){
                mediaPlayer.pause()//暂停播放
            }
        }

        mBinding.btnStopMusic.setOnClickListener {
                mediaPlayer.reset()//重置播放器
                initMediaPlayer()//重新准备好音乐资源
        }

        mBinding.musicSeekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                //将拖拽到哪的时间显示到控件上
                mBinding.musicSeekBar.progress = p1
                mBinding.txtMusicProgress.text = p1.toTime()
            }

            override fun onStartTrackingTouch(p0: SeekBar) {
                //停止计时器
                isTrackingTouch=false
            }

            override fun onStopTrackingTouch(p0: SeekBar) {
                //将停止触摸后的时间赋值到播放器
                mediaPlayer.seekTo(p0.progress)
                isTrackingTouch=true
            }

        })

        mBinding.btnPlayVideo.setOnClickListener { if (!mBinding.videoView.isPlaying)mBinding.videoView.start() }

        mBinding.btnPauseVideo.setOnClickListener { if (mBinding.videoView.isPlaying) mBinding.videoView.pause()}

        mBinding.btnResumeVideo.setOnClickListener { mBinding.videoView.resume() }


    }



    /**
     * 定时器任务
     */
    private fun intTimer() {
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (isTrackingTouch){
                        val currentPosition = mediaPlayer.currentPosition
                        runOnUiThread {
                            mBinding.musicSeekBar.progress = currentPosition
                            mBinding.txtMusicProgress.text = currentPosition.toTime()
                        }
                    }
                }
            }, 0, 50)
    }


    /**
     * 初始化音乐资源
     */
    private fun initMediaPlayer(){
        val fd = assets.openFd("music.mp3")//获取资源文件
        mediaPlayer.setDataSource(fd.fileDescriptor,fd.startOffset,fd.length)//设置播放器的数据源
        mediaPlayer.prepare()//客户端准备就绪
    }

    /**
     * 初始化musicSeekBar
     */
    private fun intMusicSeekBar() {
        val musicLength = mediaPlayer.duration
        mBinding.musicSeekBar.max = musicLength
        mBinding.txtMusicMaxProgress.text = musicLength.toTime()//toTime()拓展函数
    }

    /**
     * 初始化视频资源
     */
    private fun initVideo() {
        val uri = Uri.parse("android.resource://$packageName/${R.raw.movie}")
        mBinding.videoView.setVideoURI(uri)
    }

    /**
     * 初始化videoSeekBar
     */
    private fun intVideoSeekBar(){
        val uri = Uri.parse("android.resource://$packageName/${R.raw.movie}")

    }



    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()//该方法是彻底停止播放器，不同于reset()重置方法
        mediaPlayer.release()//释放资源
    }

}