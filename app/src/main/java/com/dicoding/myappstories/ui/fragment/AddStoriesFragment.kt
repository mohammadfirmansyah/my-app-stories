package com.dicoding.myappstories.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.dicoding.myappstories.R
import com.dicoding.myappstories.databinding.FragmentAddStoriesBinding
import com.dicoding.myappstories.ui.viewmodel.AddStoriesViewModel
import com.dicoding.myappstories.utilities.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext", "DEPRECATION")
@AndroidEntryPoint
@ExperimentalPagingApi
class AddStoriesFragment : Fragment() {

    private lateinit var binding: FragmentAddStoriesBinding
    private lateinit var currentPhotoPath: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var pref: SessionManager
    private lateinit var token: String
    private var getFile: File? = null
    private var location: Location? = null
    private val viewModel: AddStoriesViewModel by viewModels()

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val file = File(currentPhotoPath).also { getFile = it }
            val os: OutputStream

            val bitmap = BitmapFactory.decodeFile(getFile?.path)
            val exif = ExifInterface(currentPhotoPath)
            val orientation: Int = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            val rotatedBitmap: Bitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> TransformationUtils.rotateImage(bitmap, 90)
                ExifInterface.ORIENTATION_ROTATE_180 -> TransformationUtils.rotateImage(bitmap, 180)
                ExifInterface.ORIENTATION_ROTATE_270 -> TransformationUtils.rotateImage(bitmap, 270)
                ExifInterface.ORIENTATION_NORMAL -> bitmap
                else -> bitmap
            }

            try {
                os = FileOutputStream(file)
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                os.flush()
                os.close()

                getFile = file
            } catch (e: Exception) {
                e.printStackTrace()
            }

            binding.imgPreview.setImageBitmap(rotatedBitmap)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            uriToFile(selectedImg, requireContext()).also { getFile = it }

            binding.imgPreview.setImageURI(selectedImg)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddStoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        pref = SessionManager(requireContext())
        token = pref.fetchAuthToken().toString()

        initAction()

    }

    private fun initAction() {

        binding.apply {

            btnOpenCamera.setOnClickListener {
                startIntentCamera()
            }
            btnOpenGallery.setOnClickListener {
                startIntentGallery()
            }
            btnUpload.setOnClickListener {
                uploadStory()
            }
            btnAccount.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_addStoriesFragment_to_profileFragment)
            )

        }

        binding.sendLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getLastLocation()
            } else {
                this.location = null
            }
        }

    }

    private fun uploadStory() {
        showLoading(true)

        val etDescription = binding.edtStoriesDesc
        var isValid = true

        if (etDescription.text.toString().isBlank()) {
            etDescription.error = getString(R.string.error_desc_empty)
            isValid = false
        }

        if (getFile == null) {
            showSnackbar(getString(R.string.error_empty_image))
            isValid = false
        }

        if (isValid) {
            lifecycleScope.launchWhenStarted {
                launch {

                    val description =
                        etDescription.text.toString().toRequestBody("text/plain".toMediaType())

                    val file = reduceFileImageMedia(getFile as File)
                    val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo",
                        file.name,
                        requestImageFile
                    )

                    var lat: RequestBody? = null
                    var lon: RequestBody? = null

                    if (location != null) {
                        lat =
                            location?.latitude.toString().toRequestBody("text/plain".toMediaType())
                        lon =
                            location?.longitude.toString().toRequestBody("text/plain".toMediaType())
                    }
                    viewModel.uploadImage(token, imageMultipart, description, lat, lon)
                        .collect { response ->
                            response.onSuccess {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.message_success_upload),
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Navigate back to the same fragment after successful upload
                                findNavController().navigateUp()
                                findNavController().navigate(R.id.navigation_home)
                            }

                            response.onFailure {
                                showLoading(false)
                                showSnackbar(getString(R.string.message_failed_upload))
                            }
                        }
                }
            }

        } else showLoading(false)
    }

    private fun startIntentGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startIntentCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireActivity().packageManager)

        createTempFileMedia(requireActivity().application).also {
            val photoUri = FileProvider.getUriForFile(
                requireContext(),
                "com.dicoding.myappstories",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intent)
        }
    }

    @SuppressLint("LogNotTimber")
    private fun getLastLocation() {
        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requireContext().checkSelfPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                TODO("VERSION.SDK_INT < M")
            }
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    this.location = location
                    Log.d(TAG, "getLastLocation: ${location.latitude}, ${location.longitude}")
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.activate_location),
                        Toast.LENGTH_SHORT
                    ).show()

                    binding.sendLocation.isChecked = false
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    @SuppressLint("LogNotTimber")
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        Log.d(TAG, "$permissions")
        when {
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getLastLocation()
            }

            else -> {
                Snackbar
                    .make(
                        requireView(),
                        getString(R.string.location_permission_denied),
                        Snackbar.LENGTH_SHORT
                    )
                    .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    .setAction(getString(R.string.location_permission_denied)) {
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also { intent ->
                            val uri = Uri.fromParts("package", requireActivity().packageName, null)
                            intent.data = uri

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    .show()

                binding.sendLocation.isChecked = false
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.progressBar.show() else binding.progressBar.gone()
        if (isLoading) binding.bgDim.show() else binding.bgDim.gone()
        binding.apply {
            btnUpload.isClickable = !isLoading
            btnUpload.isEnabled = !isLoading
            btnOpenGallery.isClickable = !isLoading
            btnOpenGallery.isEnabled = !isLoading
            btnOpenCamera.isClickable = !isLoading
            btnOpenCamera.isEnabled = !isLoading
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    companion object {
        private const val TAG = "CreateStoryActivity"
    }
}