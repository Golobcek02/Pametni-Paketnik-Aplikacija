import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.pametnipaketnik.API.Login2FA.Login2FAInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors

class Login2FA : Fragment() {

    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val MAX_CAPTURE_COUNT = 3
    private val CAPTURE_DELAY = 1000L

    private lateinit var cameraExecutor: ExecutorService
    private var currentCaptureCount = 0
    private lateinit var imageCapture: ImageCapture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        val navView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        navView.visibility = View.GONE
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login2_f_a, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        openCamera()
    }

    private fun openCamera() {
        // Check if the camera permission is granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
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
                imageCapture = ImageCapture.Builder()
                    .build()

                // Select the back camera as the default camera
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    // Unbind any previous use cases before binding new ones
                    cameraProvider.unbindAll()

                    // Bind the camera use cases to the lifecycle of this fragment
                    cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageCapture
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
        val timer = Timer()
        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    if (currentCaptureCount < MAX_CAPTURE_COUNT) {
                        captureImage()
                        currentCaptureCount++
                    } else {
                        closeCamera()
                        timer.cancel()
                    }
                }
            },
            CAPTURE_DELAY,
            CAPTURE_DELAY
        )
    }

    private fun captureImage() {
        // Delete all files in the output directory
        val outputDirectory = getOutputDirectory()
        outputDirectory?.let { directory ->
            directory.listFiles()?.forEach { file ->
                file.delete()
            }
        }

        // Capture the images
        val images = mutableListOf<File>()
        repeat(MAX_CAPTURE_COUNT) {
            // Create a timestamped file name for the captured image
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "IMG_$timeStamp.jpg"

            // Set up the file to save the captured image
            val photoFile = File(outputDirectory, fileName)
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            // Capture the image
            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(requireContext()),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        // Image saved successfully
                        images.add(photoFile)
                        // Check if it's the last image capture
                        if (currentCaptureCount == MAX_CAPTURE_COUNT - 1) {
                            // Send the captured images to the API
                            CoroutineScope(Dispatchers.IO).launch {
                                sendImagesToApi(images)
                            }
                        } else {
                            currentCaptureCount++
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        // Handle any errors that occur during image capture
                        exception.printStackTrace()
                    }
                }
            )

            // Delay between capturing consecutive images
            Thread.sleep(CAPTURE_DELAY)
        }
    }

    private suspend fun sendImagesToApi(images: List<File>) {
        val okHttpClient = OkHttpClient.Builder().build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5551/")
//            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(Login2FAInterface::class.java)

        val imageParts = images.mapIndexed { index, file ->
            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image$index", file.name, requestBody)
        }

        try {
            val success = apiService.uploadImages(imageParts)
            if (success) {
                // Images uploaded successfully
                println("zaj dela upam")
            } else {
                // Images failed to upload
            }
        } catch (e: Exception) {
            // Handle the failure
        }
    }


    private fun getOutputDirectory(): File {
        // Get the directory where captured images will be saved
        val mediaDir = requireContext().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireContext().filesDir
    }

    private fun closeCamera() {
        // Clean up the camera resources
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, open the camera here
                openCamera()
            } else {
                // Camera permission denied
                // Handle the case where the user denies the camera permission
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        closeCamera()
    }
}
