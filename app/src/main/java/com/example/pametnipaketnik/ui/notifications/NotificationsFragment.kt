package com.example.pametnipaketnik.ui.notifications

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pametnipaketnik.databinding.FragmentNotificationsBinding
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.CaptureActivity
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

class PortraitCaptureActivity : CaptureActivity() {
    // No need to override anything here
}

data class OpenBoxRequest(
    val deliveryId: Int,
    val boxId: Int,
    val tokenFormat: Int,
    val latitude: Double,
    val longitude: Double,
    val qrCodeInfo: String,
    val terminalSeed: Int,
    val isMultibox: Boolean,
    val doorIndex: Int,
    val addAccessLog: Boolean
)

data class OpenBoxResponse(val tokenData: String)

interface Direct4meApi {
    @Headers("Content-Type: application/json", "Authorization: Bearer 9ea96945-3a37-4638-a5d4-22e89fbc998f")
    @POST("sandbox/v1/Access/openbox")
    fun openBox(@Body request: OpenBoxRequest): Call<OpenBoxResponse>
}



class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        println("neke")
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // Add QR code scanning button
        val scanButton: Button = binding.scanButton
        scanButton.setOnClickListener {
            println("knof")
            val integrator = IntentIntegrator.forSupportFragment(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setPrompt("Scan QR Code")
            integrator.setCameraId(0)
            integrator.setBeepEnabled(false)
            integrator.setBarcodeImageEnabled(false)
            integrator.setCaptureActivity(PortraitCaptureActivity::class.java) // Use your custom activity here


            // Create the scan intent and launch it using ActivityResultLauncher
            val scanIntent = integrator.createScanIntent()
            resultLauncher.launch(scanIntent)
        }
        // Initialize the ActivityResultLauncher
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val intentResult: IntentResult = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
            if (intentResult.contents == null) {
                println("neDela")
                // Handle cancelled scanning
            } else {
                println("dela")
                val scannedText: TextView = binding.scannedText
                scannedText.text = intentResult.contents
                val qrResult = intentResult.contents
                val boxId = qrResult.split("/")[2].trimStart('0')
                println("ID: "+ boxId.toString())
                openBox(boxId)
            }
        }

        return root
    }

    fun openBox(boxId: String) {
        println("prva")
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api-d4me-stage.direct4.me/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        println("druga")

        val service = retrofit.create(Direct4meApi::class.java)

        val openBoxRequest = OpenBoxRequest(
            deliveryId = 0,
            boxId = boxId.toInt(),
            tokenFormat = 5,
            latitude = 0.0,
            longitude = 0.0,
            qrCodeInfo = "string",
            terminalSeed = 0,
            isMultibox = false,
            doorIndex = 0,
            addAccessLog = false
        )
        println("tretja")

        val openBoxCall = service.openBox(openBoxRequest)
        println("cetrta")

        openBoxCall.enqueue(object: Callback<OpenBoxResponse> {
            override fun onResponse(call: Call<OpenBoxResponse>, response: Response<OpenBoxResponse>) {
                println("peta")
                println(response)
                if (response.isSuccessful) {
                    println("sesta")
                    val tokenData = response.body()?.tokenData
                    println("API KLIC")
                    val qrText: TextView = binding.tokenText
                    qrText.text = tokenData
                    //playToken(tokenData)
                } else {
                    println("neuspesen response")
                    println("Response code: " + response.code())
                    println("Error body: " + response.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<OpenBoxResponse>, t: Throwable) {
                // Handle error
                println("API NAPAKA: " + t.message)

            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
