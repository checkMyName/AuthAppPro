package com.example.authapppro.ui.auth

interface AuthListener {
    fun onSuccess()
    fun onFailure(message: String)
}