package com.example.authapppro.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.authapppro.R
import com.example.authapppro.databinding.ActivityRegisterBinding
import com.example.authapppro.utilities.hideSoftKeyboard
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), AuthListener {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_register
        )

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        binding.authViewModel = viewModel

        viewModel.authListener = this

        binding.rootLayout.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                hideSoftKeyboard()
            }
        }

        binding.toLoginTransition.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onSuccess() {
        Toast.makeText(this, "Register success!", Toast.LENGTH_SHORT).show()
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}