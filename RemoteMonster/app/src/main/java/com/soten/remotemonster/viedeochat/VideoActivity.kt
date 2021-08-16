package com.soten.remotemonster.viedeochat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.remotemonster.sdk.RemonCall
import com.remotemonster.sdk.RemonClientData
import com.soten.remotemonster.R
import com.soten.remotemonster.databinding.ActivityVideoBinding

class VideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoBinding
    private var remonCall: RemonCall? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        remonCall = RemonCall.builder()
            .context(this)
            .serviceId("SERVICEID1")
            .key("1234567890")
            .videoCodec("VP8")
            .videoWidth(640)
            .videoHeight(480)
            .localView(binding.localView)
            .remoteView(binding.remoteView)
            .build()
        val channelId = intent.getStringExtra("channelId")

        binding.change.setOnClickListener {
            remonCall?.switchCamera()
        }

        remonCall?.connect(channelId)
        remonCall?.onClose {
            finish()
        }
    }

    override fun onDestroy() {
        remonCall?.close()
        super.onDestroy()
    }

}