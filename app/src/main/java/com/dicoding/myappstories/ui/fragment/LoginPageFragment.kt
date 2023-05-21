package com.dicoding.myappstories.ui.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.dicoding.myappstories.R
import com.dicoding.myappstories.databinding.FragmentLoginPageBinding
import com.dicoding.myappstories.ui.activity.MainActivity
import com.dicoding.myappstories.ui.activity.MainActivity.Companion.EXTRA_TOKEN
import com.dicoding.myappstories.ui.viewmodel.LoginPageViewModel
import com.dicoding.myappstories.utilities.ConstValue
import com.dicoding.myappstories.utilities.ConstValue.KEY_EMAIL
import com.dicoding.myappstories.utilities.SessionManager
import com.dicoding.myappstories.utilities.gone
import com.dicoding.myappstories.utilities.show
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class LoginPageFragment : Fragment() {

    private var _binding: FragmentLoginPageBinding? = null
    private val binding get() = _binding!!
    private var loginJob: Job = Job()
    private val loginPageViewModel: LoginPageViewModel by viewModels()
    private lateinit var pref: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = SessionManager(requireContext())

        playAnimation()
        initAction()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initAction() {
        binding.apply {
            tvToRegister.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_registerFragment)
            )

            btnLogin.setOnClickListener {
                if (edtPassword.error.isNullOrEmpty() && edtEmail.error.isNullOrEmpty()) {
                    loginUser()
                    findNavController().popBackStack(R.id.loginFragment, false)
                } else {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.error_register_login),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgLogo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val welcome =
            ObjectAnimator.ofFloat(binding.tvWelcomeTitle, View.ALPHA, 1f).setDuration(500)
        val welcomeDesc =
            ObjectAnimator.ofFloat(binding.tvWelcomeDesc, View.ALPHA, 1f).setDuration(500)
        val edtEmail = ObjectAnimator.ofFloat(binding.edtEmail, View.ALPHA, 1f).setDuration(500)
        val edtPassword =
            ObjectAnimator.ofFloat(binding.edtPassword, View.ALPHA, 1f).setDuration(500)
        val buttonLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val isHavent =
            ObjectAnimator.ofFloat(binding.tvIsHaventAccount, View.ALPHA, 1f).setDuration(500)
        val toRegister =
            ObjectAnimator.ofFloat(binding.tvToRegister, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(welcome, welcomeDesc)
        }

        AnimatorSet().apply {
            playSequentially(together, edtEmail, edtPassword, buttonLogin, isHavent, toRegister)
            start()
        }

    }

    private fun loginUser() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString()

        showLoading(true)

        lifecycleScope.launchWhenResumed {
            if (loginJob.isActive) loginJob.cancel()

            loginJob = launch {
                loginPageViewModel.loginUser(email, password).collect { result ->
                    result.onSuccess { credentials ->
                        credentials.loginResult?.token?.let { token ->
                            pref.saveAuthToken(token)
                            pref.setStringPreference(KEY_EMAIL, email)
                            pref.setBooleanPreference(ConstValue.KEY_IS_LOGIN, true)

                            Intent(requireContext(), MainActivity::class.java).also { intent ->
                                intent.putExtra(EXTRA_TOKEN, token)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                        }

                        Toast.makeText(
                            requireContext(),
                            getString(R.string.login_success_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    result.onFailure {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.login_error_message),
                            Snackbar.LENGTH_SHORT
                        ).show()

                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.progressBar.show() else binding.progressBar.gone()
        if (isLoading) binding.bgDim.show() else binding.bgDim.gone()
        binding.edtEmail.isClickable = !isLoading
        binding.edtEmail.isEnabled = !isLoading
        binding.edtPassword.isClickable = !isLoading
        binding.edtPassword.isEnabled = !isLoading
        binding.btnLogin.isClickable = !isLoading
    }
}