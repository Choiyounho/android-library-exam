package com.soten.pay

import android.app.Application
import com.iamport.sdk.domain.core.Iamport

class PayApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Iamport.create(this)
    }

}