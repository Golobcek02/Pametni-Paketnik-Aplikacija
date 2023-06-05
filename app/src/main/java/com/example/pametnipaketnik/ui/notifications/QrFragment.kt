package com.example.pametnipaketnik.ui.notifications

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pametnipaketnik.API.AuthenticateUser.AuthenticateUserInterface
import com.example.pametnipaketnik.API.AuthenticateUser.AuthenticateUserRequest
import com.example.pametnipaketnik.API.OpenBox.OpenBoxInterface
import com.example.pametnipaketnik.databinding.FragmentNotificationsBinding
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.CaptureActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.io.File
import java.io.FileOutputStream

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

data class OpenBoxResponse(
    @SerializedName("data") val tokenData: String
)


interface Direct4meApi {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer 9ea96945-3a37-4638-a5d4-22e89fbc998f"
    )
    @POST("sandbox/v1/Access/openbox")
    fun openBox(@Body request: OpenBoxRequest): Call<OpenBoxResponse>
}


class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var mediaPlayer: MediaPlayer? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var openBoxInterface: OpenBoxInterface
    private lateinit var authenticateUserInterface: AuthenticateUserInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        val gson = GsonBuilder().setLenient().create()

        var retrofit = Retrofit.Builder()
            .baseUrl("https://api-d4me-stage.direct4.me/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        openBoxInterface = retrofit.create(OpenBoxInterface::class.java)

        retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.22:5551/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        authenticateUserInterface = retrofit.create(AuthenticateUserInterface::class.java)

    }

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
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val intentResult: IntentResult =
                    IntentIntegrator.parseActivityResult(result.resultCode, result.data)
                if (intentResult.contents == null) {
                    println("neDela")
                    // Handle cancelled scanning
                } else {
                    println("dela")
                    val qrResult = intentResult.contents;
                    println(qrResult)
                    val boxId = qrResult.split("/")[4].toInt();
                    println(boxId)
                    //binding.scannedText.text = "Scanned box id" +boxId.toString();
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val prefs =
                                activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)!!
                            val user_id = prefs.getString("user_id", "0000000000000")

                            val req = AuthenticateUserRequest(
                                UserID = user_id.toString(),
                                BoxID = boxId
                            )
                            println("auth no")
                            val res = authenticateUserInterface.authenticateUser(req)
                            println("auth yes")
                            println(res)


                            if (res) {

                                val requestBody = OpenBoxRequest(
                                    deliveryId = 0,
                                    boxId = boxId,
                                    tokenFormat = 5,
                                    latitude = 0.0,
                                    longitude = 0.0,
                                    qrCodeInfo = "string",
                                    terminalSeed = 0,
                                    isMultibox = false,
                                    doorIndex = 0,
                                    addAccessLog = false
                                );
                                val response = openBoxInterface.openBox(requestBody);
                                println(response)
                                if (response.result == 0) {
                                    val decodedBytes = Base64.decode(response.data, Base64.DEFAULT);
                                    val tempFile = File.createTempFile("temp", ".mp3")
                                    FileOutputStream(tempFile).use { fos ->
                                        fos.write(decodedBytes)
                                    };

                                    val mediaPlayer = MediaPlayer().apply {
                                        setDataSource(tempFile.absolutePath);
                                        prepare();
                                        start();
                                    }

                                    mediaPlayer.setOnCompletionListener {
                                        mediaPlayer.release();
                                    }
                                    tempFile.delete();
                                }
                            } else {
                                println("you are not authenticated")
                            }
                        } catch (e: Exception) {
                            println(e);
                        }
                    }
                }
            }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
