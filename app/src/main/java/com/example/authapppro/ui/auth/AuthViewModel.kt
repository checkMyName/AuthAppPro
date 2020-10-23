package com.example.authapppro.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.authapppro.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class AuthViewModel : ViewModel() {

    var username: String? = null
    var email: String? = null
    var password: String? = null
    var passwordRepeat: String? = null

    var currentUser = MutableLiveData<FirebaseUser>()

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var reference: FirebaseDatabase = FirebaseDatabase.getInstance()

    var authListener: AuthListener? = null

    fun loginUser() {
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            authListener?.onFailure("Invalid email or password")
            return
        } else {
            mAuth.signInWithEmailAndPassword(email.toString(), password.toString())
                .addOnSuccessListener {
                    authListener?.onSuccess()
                    currentUser.value = mAuth.currentUser
                }.addOnFailureListener {
                    authListener?.onFailure(it.message.toString())
                }
        }
    }

    fun registerUser() {
        if (username.isNullOrEmpty() || email.isNullOrEmpty() || password.isNullOrEmpty()) {
            authListener?.onFailure("Invalid username, email or password")
            return
        } else if (password != passwordRepeat) {
            authListener?.onFailure("Passwords are not an equals")
            return
        } else {
            mAuth.createUserWithEmailAndPassword(email.toString(), password.toString())
                .addOnSuccessListener {
                    saveUserData()
                    currentUser.value = mAuth.currentUser
                }.addOnFailureListener {
                    authListener?.onFailure(it.message.toString())
                }
        }
    }

    private fun saveUserData() {
        val uid = mAuth.uid
        val ref = reference.getReference("/users/$uid")
        val user = User(
            uid.toString(),
            username.toString(),
            email.toString(),
            "Default",
            "Default",
            true,
            username.toString().toLowerCase(Locale.ROOT)
        )
        ref.setValue(user).addOnSuccessListener {
            authListener?.onSuccess()
        }.addOnFailureListener {
            authListener?.onFailure(it.message.toString())
        }
    }

}