package com.example.pametnipaketnik.ui.notifications

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pametnipaketnik.API.Login.LoginInterface
import com.example.pametnipaketnik.API.OpenBox.OpenBoxInterface
import com.example.pametnipaketnik.API.OpenBox.OpenBoxRequest
import com.example.pametnipaketnik.databinding.FragmentNotificationsBinding
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.CaptureActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class PortraitCaptureActivity : CaptureActivity() {
    // No need to override anything here
}

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var openBoxInterface: OpenBoxInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api-d4me-stage.direct4.me/")
//            .client(OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        openBoxInterface = retrofit.create(OpenBoxInterface::class.java)
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
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val intentResult: IntentResult =
                    IntentIntegrator.parseActivityResult(result.resultCode, result.data)
                if (intentResult.contents == null) {
                    println("neDela")
                    // Handle cancelled scanning
                } else {
                    println("dela")
                    val qrArray = intentResult.contents.split("/")
                    val scannedText: TextView = binding.scannedText
                    //scannedText.text = intentResult.contents
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val requestBody = OpenBoxRequest(
                                deliveryId = 0,
                                boxId = qrArray[2].toInt(),
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

                            if (response.result == 0) {
                                val decodedBytes = Base64.decode(response.data, Base64.DEFAULT);
                                val tempFile = File.createTempFile("temp", ".mp3")
                                FileOutputStream(tempFile).use { fos ->
                                    fos.write(decodedBytes)
                                };

                                val mediaPlayer = MediaPlayer().apply {
                                    setDataSource(tempFile.absolutePath)
                                    prepare()
                                    start()
                                }

                                mediaPlayer.setOnCompletionListener {
                                    mediaPlayer.release();
                                }
                                tempFile.delete();
                            }
                        } catch (e: Exception) {
                            println(e)
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
