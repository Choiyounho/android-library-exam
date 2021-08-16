package com.soten.pay

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import com.iamport.sdk.data.sdk.IamPortRequest
import com.iamport.sdk.data.sdk.PG
import com.iamport.sdk.data.sdk.PayMethod
import com.iamport.sdk.domain.core.Iamport
import com.soten.pay.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        Iamport.init(this)
    }

    override fun onStart() {
        super.onStart()

        val userCode = "iamport"  // 가맹점식별코드, "iamport" 는 테스트용 코드임

        // SDK 에 결제 요청할 데이터
        val request = IamPortRequest(
            pg = PG.html5_inicis.makePgRawName(""),         // PG사
            pay_method = PayMethod.card,                    // 결제수단
            name = "아임포트 진짜 쉬워요!",                      // 주문명
            merchant_uid = "sample_aos_${Date().time}",     // 주문번호
            amount = "100",                                // 결제금액
            buyer_name = "김개발"
        )

        // 우측 하단 초록색 편지지 버튼 클릭
        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "아임포트에서 결제 해볼까요?", Snackbar.LENGTH_LONG)
                .setAction("결제") {
                    // 아임포트에 결제 요청하기
                    Iamport.payment(userCode, iamPortRequest = request, paymentResultCallback = {
                        // 결제 완료 후 결과 콜백을 토스트 메시지로 보여줌
                        Toast.makeText(this, "결제결과 => $it", Toast.LENGTH_LONG).show()
                    })
                }.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
