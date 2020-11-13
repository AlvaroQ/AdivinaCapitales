package com.alvaroquintana.adivinacapitales.application

import android.app.Application

class AdivinaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initDI()
    }
}