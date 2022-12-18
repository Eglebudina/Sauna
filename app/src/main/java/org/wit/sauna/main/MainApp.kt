package org.wit.sauna.main

import android.app.Application
import org.wit.sauna.models.SaunaMemStore
import org.wit.sauna.models.SaunaModel
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    //val saunas = ArrayList<SaunaModel>()
    val saunas = SaunaMemStore()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("Sauna started")
//        saunas.add(SaunaModel("One", "About one..."))
//        saunas.add(SaunaModel("Two", "About two..."))
//        saunas.add(SaunaModel("Three", "About three..."))
    }
}