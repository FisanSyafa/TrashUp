package com.bangkit.trashup.ui.analyze

import com.bangkit.trashup.databinding.ActivityUploadBinding
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bangkit.trashup.utils.getImageUri

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPicture()
    }

    private fun setupPicture() {
        binding.btnGallery.setOnClickListener {
            if (allGalleryPermissionsGranted()) {
                startGallery()
            } else {
                requestGalleryPermission()
            }
        }

        binding.btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startCamera()
            } else {
                requestCameraPermission()
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            binding.ivUploadPhoto.setImageURI(uri)
        } else {
            Toast.makeText(this, "Gambar tidak terpilih", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCamera() {
        val uri = getImageUri(this)
        currentImageUri = uri
        launcherCamera.launch(uri)
    }

    private val launcherCamera =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                currentImageUri?.let { uri ->
                    binding.ivUploadPhoto.setImageURI(uri)
                }
            } else {
                currentImageUri = null
                Toast.makeText(this, "Gambar tidak terfoto", Toast.LENGTH_SHORT).show()
            }
        }

    private fun requestGalleryPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        requestGalleryPermissionLauncher.launch(permission)
    }

    private val requestGalleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) startGallery() else Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
    }

    private fun requestCameraPermission() {
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) startCamera() else Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
    }

    private fun allGalleryPermissionsGranted(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }
}
