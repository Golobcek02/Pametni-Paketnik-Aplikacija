import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.pametnipaketnik.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import androidx.camera.view.PreviewView
import androidx.navigation.fragment.findNavController
import com.example.pametnipaketnik.API.Login2FA.Login2FAInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Login2FA : Fragment() {

    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val MAX_CAPTURE_COUNT = 3
    private val CAPTURE_DELAY = 200L

    private lateinit var cameraExecutor: ExecutorService
    private var currentCaptureCount = 0
    private lateinit var imageCapture: ImageCapture
    private lateinit var animator: ObjectAnimator
    private var capturedImages = mutableListOf<ByteArray>()
    private var isCapturing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        val navView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        navView.visibility = View.GONE
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login2_f_a, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myImageView: ImageView = view.findViewById(R.id.myImageView)
        animator = ObjectAnimator.ofFloat(myImageView, View.ROTATION, 0f, 360f).apply {
            duration = 2000L // Rotate completely around over 2 seconds.
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE // Repeat indefinitely.
        }
        phaseText("PLEASE WAIT\n WHILE WE SCAN YOUR FACE", 20L)
        openCamera()

    }

    private fun openCamera() {
        // Check if the camera permission is granted
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Camera permission is already granted
            val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
            cameraProviderFuture.addListener({
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                // Set up the preview use case to display the camera preview
                val viewFinder = requireView().findViewById<PreviewView>(R.id.viewFinder)
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }


                // Set up the image capture use case to take pictures
                imageCapture = ImageCapture.Builder().build()

                // Select the back camera as the default camera
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                try {
                    // Unbind any previous use cases before binding new ones
                    cameraProvider.unbindAll()

                    // Bind the camera use cases to the lifecycle of this fragment
                    cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture
                    )
                    cameraExecutor = Executors.newSingleThreadExecutor()
                    // Schedule the capture of images
                    captureImages()
                } catch (exception: Exception) {
                    // Handle any errors that occur during camera setup
                    exception.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(requireContext()))
        } else {
            // Camera permission has not been granted, request it
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun captureImages() {
        CoroutineScope(Dispatchers.Main).launch {
            while (currentCaptureCount < MAX_CAPTURE_COUNT) {
                delay(CAPTURE_DELAY)
                println(currentCaptureCount)
                captureImage()
            }
            sendImagesToApi()
        }
    }

    private fun captureImage() {
        if (isCapturing) return
        isCapturing = true
        currentCaptureCount++
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val buffer = image.planes[0].buffer
                    buffer.rewind()
                    val byteArray = ByteArray(buffer.capacity())
                    buffer.get(byteArray)
                    capturedImages.add(byteArray)
                    image.close()
                    isCapturing = false
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraXApp", "Photo capture failed: ${exception.message}", exception)
                    isCapturing = false
                }
            }
        )

    }

    private suspend fun sendImagesToApi() {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) //do serverja
            .readTimeout(150, TimeUnit.SECONDS) //čaka na response
            .writeTimeout(150, TimeUnit.SECONDS) //čaka na response
            .build()

        val retrofit = Retrofit.Builder().baseUrl("http://192.168.0.22:5551/").client(httpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()

        val apiService = retrofit.create(Login2FAInterface::class.java)

        val imageParts = capturedImages.mapIndexed { index, byteArray ->
            val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
            MultipartBody.Part.createFormData("image$index", "image$index.jpg", requestBody)
        }

        try {
            val userId = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                ?.getString("user_id", "")
            val success = apiService.uploadImages(userId.toString(), imageParts)
            if (success) {
                // Images uploaded successfully
                println("zaj dela upam")
                val navView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
                withContext(Dispatchers.Main) {
                    findNavController().navigate(R.id.navigation_home)
                    navView.visibility = View.VISIBLE
                }
            } else {
                withContext(Dispatchers.Main) {
                    findNavController().navigate(R.id.login_page)
                }
            }
        } catch (e: Exception) {
            println(e)
        }
    }




    private fun closeCamera() {
        // Clean up the camera resources
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    // Handle the case when the camera permission is not granted
                }
            }

            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun phaseText(text: String, delay: Long) {
        val textView = view?.findViewById<TextView>(R.id.wait_text)
        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                for (i in 1..text.length) {
                    delay(delay)
                    textView?.text = text.substring(0, i)
                }
                delay(800)  // Pause for a while before repeating.
            }
        }
    }

    override fun onResume() {
        super.onResume()
        animator.start()
    }

    override fun onPause() {
        super.onPause()
        animator.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        closeCamera()
    }
}
