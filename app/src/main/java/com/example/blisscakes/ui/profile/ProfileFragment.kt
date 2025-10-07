package com.blisscakes.app.ui.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.blisscakes.app.databinding.FragmentProfileBinding
import com.blisscakes.app.ui.auth.LoginActivity
import com.blisscakes.app.utils.ViewState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Handle camera result
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            Snackbar.make(binding.root, "Photo captured successfully", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        binding.btnTakePhoto.setOnClickListener {
            checkCameraPermissionAndTakePicture()
        }

        binding.cardSensorInfo.setOnClickListener {
            // Show sensor information dialog
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.user.collect { state ->
                when (state) {
                    is ViewState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is ViewState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val user = state.data

                        binding.tvUserName.text = "${user.firstName} ${user.lastName}"
                        binding.tvUserEmail.text = user.email
                        binding.tvUsername.text = "@${user.username}"
                        binding.tvPhone.text = user.phone ?: "Not provided"
                        binding.tvAddress.text = user.address ?: "Not provided"
                    }
                    is ViewState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                    is ViewState.Idle -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.logoutState.collect { state ->
                when (state) {
                    is ViewState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is ViewState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        navigateToLogin()
                    }
                    is ViewState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                    is ViewState.Idle -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                viewModel.logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun checkCameraPermissionAndTakePicture() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                takePicture()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // Show explanation dialog
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Camera Permission Required")
                    .setMessage("This feature requires camera access to take photos.")
                    .setPositiveButton("Grant") { _, _ ->
                        requestPermissions(
                            arrayOf(Manifest.permission.CAMERA),
                            CAMERA_PERMISSION_CODE
                        )
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            else -> {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }
        }
    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            cameraLauncher.launch(intent)
        } else {
            Snackbar.make(binding.root, "No camera app found", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture()
            } else {
                Snackbar.make(binding.root, "Camera permission denied", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CAMERA_PERMISSION_CODE = 200
    }
}