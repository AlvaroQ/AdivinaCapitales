package com.alvaroquintana.adivinacapitales.utils

import android.media.MediaPlayer
import android.view.View
import android.view.animation.AnimationUtils
import com.alvaroquintana.adivinacapitales.R
import com.alvaroquintana.adivinacapitales.utils.listener.SafeClickListener


fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_xy_collapse))
        MediaPlayer.create(context, R.raw.click).start()
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}