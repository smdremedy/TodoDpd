package pl.szkolaandroida.todoexpert

import android.app.Application
import android.util.Log

class App : Application() {

    var token: String = ""

    override fun onCreate() {
        super.onCreate()
        Log.d("TAG", "App created!")
    }
}