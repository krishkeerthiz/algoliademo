package com.example.algoliademo1.ui.addproductimage

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.algoliademo1.R
import com.example.algoliademo1.databinding.FragmentAddProductImageBinding
import java.text.SimpleDateFormat
import java.util.*

class AddProductImageFragment : Fragment() {

    private lateinit var binding: FragmentAddProductImageBinding

    private val args: AddProductImageFragmentArgs by navArgs()

    private var imageCapture: ImageCapture? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_product_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddProductImageBinding.bind(view)

        if (allPermissionsGranted()) {
            Log.d(TAG, "onCreate: permission granted")
            startCamera()
        } else {
            Log.d(TAG, "onCreate: permission not granted")
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        binding.captureButton.setOnClickListener {
            takePhoto()
        }

        binding.galleryButton.setOnClickListener {
            startImageChooserIntent()
        }
    }

    private fun startImageChooserIntent() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(Intent.createChooser(intent, "Select a picture"), 10)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == 10) {
                val imageUri = data?.data

                if (imageUri != null)
                    gotoProductPreviewFragment(imageUri)
                else
                    Toast.makeText(requireContext(), "Error processing image", Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

//      Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.UK)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                requireActivity().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

//         Set up image capture listener, which is triggered after photo has  been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val uri = output.savedUri
                    if (uri != null) {
                        gotoProductPreviewFragment(uri)
//                        val msg = "Photo capture succeeded: $uri"
//                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setJpegQuality(50)
            .setTargetResolution(Size(720, 1080))
            .build()

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )


            } catch (exc: Exception) {
            }

        }, ContextCompat.getMainExecutor(requireContext()))

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()

                requireActivity().finish()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun gotoProductPreviewFragment(imageUri: Uri) {
        val action =
            AddProductImageFragmentDirections.actionAddProductImageFragmentToProductPreviewFragment(
                args.productModel,
                imageUri.toString()
            )
        view?.findNavController()?.navigate(action)
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

}