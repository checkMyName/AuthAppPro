package com.example.authapppro.ui.profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.authapppro.R
import com.example.authapppro.model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import java.util.*

const val REQUEST_CODE = 1

class ProfileFragment : Fragment() {

    private val fUser = FirebaseAuth.getInstance().currentUser?.uid
    private val reference =
        FirebaseDatabase.getInstance().reference.child("users").child(fUser.toString())
    private val storage = FirebaseStorage.getInstance().reference.child("user files")
    private var imageURI: Uri? = null
    private var imageCheck: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val user: User? = snapshot.getValue(User::class.java)

                    if (context != null) {
                        profile_username.text = user?.username
                        profile_email.text = user?.email

                        if (user?.profileUrl == "Default") {
                            profile_imageView.setImageResource(R.drawable.ic_account)
                        } else {
                            Picasso.get().load(user?.profileUrl).into(profile_imageView)
                        }

                        if (user?.backgroundUrl == "Default") {
                            profile_background.setImageResource(R.drawable.background)
                        } else {
                            Picasso.get().load(user?.backgroundUrl).into(profile_background)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        profile_imageView.setOnClickListener {
            imageCheck = false
            setImage()
        }

        profile_background.setOnClickListener {
            imageCheck = true
            setImage()
        }
    }

    private fun setImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.apply {
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data?.data != null) {
            imageURI = data.data
            Toast.makeText(context, "Image uploading", Toast.LENGTH_SHORT).show()
            uploadImage()
        }
    }

    private fun uploadImage() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("Image is uploading, please wait...")
        progressBar.show()

        if (imageURI != null) {
            val fileRef = storage.child(System.currentTimeMillis().toString() + ".jpg")

            val uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageURI!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    val url = task.result.toString()

                    if (imageCheck!!) {
                        val mapBackground = HashMap<String, Any>()
                        mapBackground["backgroundUrl"] = url
                        reference.updateChildren(mapBackground)
                        imageCheck = false
                    } else {
                        val mapProfile = HashMap<String, Any>()
                        mapProfile["profileUrl"] = url
                        reference.updateChildren(mapProfile)
                        imageCheck = false
                    }
                    progressBar.dismiss()
                }

            }
        }
    }
}