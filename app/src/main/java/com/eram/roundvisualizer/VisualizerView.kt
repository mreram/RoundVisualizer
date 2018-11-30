package com.eram.roundvisualizer

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.animation.ValueAnimator
import android.media.audiofx.Visualizer
import android.util.Log
import android.view.animation.LinearInterpolator


/**
 * Created by Mohammad Reza Eram (https://github.com/mreram) on 22,October,2018
 */
class VisualizerView @kotlin.jvm.JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), Visualizer.OnDataCaptureListener {

    companion object {
        private const val TAG = ":::VisualizerView:::"
        private const val CAPTURE_SIZE = 512
        private const val DURATION_CHANGE = 100L
    }


    private var rotateDegree = 100f
    private var marginVisualizer = 35f.toDp()
    private val defaultRadius = 70f.toDp()
    private var waveData: ByteArray? = null
    private var visualizer: Visualizer? = null

    private var animator1: ValueAnimator? = null
    private var animator2: ValueAnimator? = null
    private var animator3: ValueAnimator? = null
    private var animator4: ValueAnimator? = null

    private val dataSet = mutableListOf(
            arrayOf(Color.argb(30, 191, 63, 63).toFloat(), 100f.toDp()),
            arrayOf(Color.argb(20, 250, 202, 80).toFloat(), 100f.toDp()),
            arrayOf(Color.argb(20, 240, 66, 14).toFloat(), 100f.toDp()),
            arrayOf(Color.argb(30, 191, 63, 63).toFloat(), 100f.toDp())
    )

    init {
        this.waveData = ByteArray(CAPTURE_SIZE)
    }


    public fun initialize(audioSessionId: Int) {
        visualizer = Visualizer(audioSessionId).apply {
            captureSize = CAPTURE_SIZE
            setDataCaptureListener(this@VisualizerView, Visualizer.getMaxCaptureRate() / 2, true, false)
            try {
                scalingMode = Visualizer.SCALING_MODE_NORMALIZED
            } catch (e: NoSuchMethodError) {
                Log.e(TAG, "Can't set scaling mode", e)
            }
        }
    }

    public fun start() {
        visualizer?.enabled = true
    }


    override fun onFftDataCapture(p0: Visualizer?, p1: ByteArray?, p2: Int) {
        updateWaveData(p1)
    }

    override fun onWaveFormDataCapture(p0: Visualizer?, p1: ByteArray?, p2: Int) {
        updateWaveData(p1)
    }

    public fun pause() {
        visualizer?.enabled = false
    }

    public fun finish() {
        visualizer?.enabled = false
        visualizer?.release()
    }

    private fun drawRandomRectangle(canvas: Canvas?, position: Int) {

        val (color, currentBorderSpace) = dataSet[position - 1]

        val paint = Paint().apply {
            this.color = color.toInt()
        }

        val path = Path()
        val rectF = RectF(marginVisualizer, marginVisualizer, width.toFloat() - marginVisualizer, height.toFloat() - marginVisualizer)
        path.addRoundRect(rectF, currentBorderSpace, currentBorderSpace, Path.Direction.CW)
        canvas?.apply {
            drawPath(path, paint)
            save()
            rotate(rotateDegree * position, (width / 2).toFloat(), (height / 2).toFloat())
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 20000
            interpolator = LinearInterpolator()
            addUpdateListener {
                rotation = animatedValue as Float
            }
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            start()
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for (i in 1..dataSet.size) {
            drawRandomRectangle(canvas, i)
        }
    }


    private fun updateWaveData(waveData: ByteArray?) {
        waveData?.let {
            post {
                if (this.waveData != null && this.waveData?.size != it.size)
                    return@post
                System.arraycopy(it, 0, this.waveData, 0, it.size)
                val chunckSize = it.size / 4
                val (chunked1, chunked2, chunked3, chunked4) = it.asList().chunked(chunckSize)

                animator1?.cancel()
                animator1 = ValueAnimator.ofFloat(dataSet[0][1], Math.abs(defaultRadius - RenderUtils.sum(chunked1.toByteArray()).toFloat() / defaultRadius)).apply {
                    addUpdateListener { animator ->
                        dataSet[0][1] = animator.animatedValue as Float
                        invalidate()
                    }
                    duration = DURATION_CHANGE
                    start()
                }

                animator2?.cancel()
                animator2 = ValueAnimator.ofFloat(dataSet[1][1], Math.abs(defaultRadius - RenderUtils.sum(chunked2.toByteArray()).toFloat() / defaultRadius)).apply {
                    addUpdateListener { animator ->
                        dataSet[1][1] = animator.animatedValue as Float
                        invalidate()
                    }
                    duration = DURATION_CHANGE
                    start()
                }

                animator3?.cancel()
                animator3 = ValueAnimator.ofFloat(dataSet[2][1], Math.abs(defaultRadius - RenderUtils.sum(chunked3.toByteArray()).toFloat() / defaultRadius)).apply {
                    addUpdateListener { animator ->
                        dataSet[2][1] = animator.animatedValue as Float
                        invalidate()
                    }
                    duration = DURATION_CHANGE
                    start()
                }

                animator4?.cancel()
                animator4 = ValueAnimator.ofFloat(dataSet[3][1], Math.abs(defaultRadius - RenderUtils.sum(chunked4.toByteArray()).toFloat() / defaultRadius)).apply {
                    addUpdateListener { animator ->
                        dataSet[3][1] = animator.animatedValue as Float
                        invalidate()
                    }
                    duration = DURATION_CHANGE
                    start()
                }

            }
        }
    }

    private fun Float.toDp(): Float {
        return this * resources.displayMetrics.density
    }

}