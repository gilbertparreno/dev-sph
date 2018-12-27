package com.gp.sph

import android.app.Application
import testservice.gp.com.api.service.ServiceFactory

/**
 * Created by gilbert on 12/25/18.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceFactory.init(applicationContext, BuildConfig.BASE_URL, BuildConfig.DEBUG)
    }
}