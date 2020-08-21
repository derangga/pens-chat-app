package com.aldebaran.simplechat.ui.login

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aldebaran.simplechat.R
import com.aldebaran.simplechat.databinding.ActivityLoginBinding
import com.aldebaran.simplechat.goToActivity
import com.aldebaran.simplechat.ui.login.Status.*
import com.aldebaran.simplechat.ui.room.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    private lateinit var progressDialogBuilder: AlertDialog.Builder
    private lateinit var progressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        initLoadingDialog()
        viewClicked()
        subscribeUI()

        if(viewModel.isLogin()){
            goToActivity<MainActivity>()
            finish()
        }
    }

    private fun initLoadingDialog(){
        progressDialogBuilder = AlertDialog.Builder(this)
        progressDialogBuilder.setView(R.layout.dialog_progress)
        progressDialog = progressDialogBuilder.create()
        progressDialog.setCancelable(false)
    }

    private fun showLoading(){
        progressDialog.show()
    }

    private fun hideLoading(){
        progressDialog.dismiss()
    }

    private fun viewClicked(){
        binding.btnLogin.setOnClickListener {
            submitLogin()
        }
    }

    private fun submitLogin(){
        when {
            binding.etEmail.text.toString().isEmpty() -> {
                binding.etEmail.error = "Invalid Email"
            }
            binding.etPassword.text.toString().isEmpty() -> {
                binding.etPassword.error = "Invalid Password"
            }
            else -> {
                viewModel.doLogin(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString()
                )
            }
        }
    }

    private fun subscribeUI(){
        viewModel.loginState.observe(this, Observer { result ->
            result?.let { status ->
                when(status){
                    LOADING -> { showLoading() }
                    SUCCESS -> {
                        hideLoading()
                        goToActivity<MainActivity>()
                        finish()

                    }
                    ERROR -> {
                        hideLoading()
                        Toast.makeText(this, "Wrong password or email", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}