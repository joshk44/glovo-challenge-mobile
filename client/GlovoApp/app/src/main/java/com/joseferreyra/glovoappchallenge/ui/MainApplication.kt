package com.joseferreyra.glovoappchallenge.ui

import android.app.Application
import com.joseferreyra.glovoappchallenge.data.communication.RestService

class MainApplication : Application() {

    companion object {
        val restClientService by lazy {
            RestService.create()
        }
    }
}