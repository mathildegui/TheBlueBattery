package com.mathildeguillossou.thebluebattery

import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation

/**
 * @author mathilde
 * @version 25/03/2018
 */
class Helper {
    companion object {
        fun rotate(view: View?) {
            val rotate = RotateAnimation(
                    0f, 360f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            )
            rotate.duration = 800
            rotate.repeatCount = Animation.INFINITE
            view?.startAnimation(rotate)
        }
    }
}