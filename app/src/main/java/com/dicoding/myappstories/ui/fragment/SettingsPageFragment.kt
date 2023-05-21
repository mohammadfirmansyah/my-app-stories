package com.dicoding.myappstories.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.dicoding.myappstories.R
import com.dicoding.myappstories.databinding.FragmentSettingsPageBinding
import com.dicoding.myappstories.ui.activity.AuthActivity
import com.dicoding.myappstories.utilities.SessionManager
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsPageFragment : Fragment() {

    private var _binding: FragmentSettingsPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var pref: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = SessionManager(requireContext())

        initUI()
        initAction()

    }

    private fun initUI() {
        binding.tvUserName.text = pref.getUserName
        binding.tvUserEmail.text = pref.getEmail
    }

    private fun initAction() {

        binding.apply {

            btnLogout.setOnClickListener {
                initLogoutDialog()
            }

            btnLanguage.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            imgBack.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_profileFragment_to_homeFragment)
            )

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initLogoutDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle(getString(R.string.message_logout_confirm))
            ?.setPositiveButton(getString(R.string.action_yes)) { _, _ ->
                pref.clearAuthToken()
                pref.clearPreferences()
                val intent = Intent(requireContext(), AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            ?.setNegativeButton(getString(R.string.action_cancel), null)
        val alert = alertDialog.create()
        alert.show()
    }

}