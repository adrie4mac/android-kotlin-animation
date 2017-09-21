package id.web.adrie.designandroidtest

import android.animation.ValueAnimator
import android.graphics.drawable.Animatable
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.animation.OvershootInterpolator
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_bisnis.*

/**
 * Created by adrie_savana on 9/21/17.
 */
class BisnisActivity: AppCompatActivity() {

    companion object {
        val OVERSHOOT = OvershootInterpolator()
        val MAX_SCALE_LEVEL = 10000
        val THUMB_SCALE_DURATION = 600.toLong()
        val THUMB_RELEASE_SCALE_FACTOR = 2
    }

    private val STATE_ZERO = intArrayOf(
            R.attr.state_zero, -R.attr.state_one, -R.attr.state_two
    )

    private val STATE_ONE = intArrayOf(
            -R.attr.state_zero, R.attr.state_one, -R.attr.state_two
    )

    private val STATE_TWO = intArrayOf(
            -R.attr.state_zero, -R.attr.state_one, R.attr.state_two
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bisnis)
        (img_background.drawable as Animatable).start()
        setUpSeekBar()
    }

    private fun setUpSeekBar() {
        val thumbDrawable = ContextCompat.getDrawable(
                this, R.drawable.ll_thumb)

        with(seekbar) {
            thumb = thumbDrawable
            thumb.level = 1
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                splitTrack = false
            }

            setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    handleBuildingAnimationState(progress)
                    handleThumbScale(progress)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    // do nothing
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    animateThumbRelease()
                }
            })
        }
    }

    private fun animateThumbRelease() {
        val thumb = seekbar.thumb
        val initLevel = thumb.level
        val maxLevel = thumb.level * THUMB_RELEASE_SCALE_FACTOR
        val animator = ValueAnimator.ofInt(
                initLevel, maxLevel, initLevel)

        with(animator) {
            interpolator = OVERSHOOT
            duration = THUMB_SCALE_DURATION

            addUpdateListener {
                thumb.level = it.animatedValue as Int
            }

            start()
        }
    }

    private fun handleThumbScale(progress: Int) {
        seekbar.thumb.level = ((progress + 1) *
                (MAX_SCALE_LEVEL / seekbar.max))
    }

    private fun handleBuildingAnimationState(progress: Int) {
        val max = seekbar.max

        val businessType = when(progress) {
            in 0..max/3 -> STATE_ZERO
            in 10..max/2 -> STATE_ONE
            in 20..max -> STATE_TWO
            else -> throw IllegalStateException()
        }

        img_building.setImageState(businessType, true)
    }
}