package com.example.authapppro.ui.chats

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.authapppro.R
import com.example.authapppro.model.Message
import com.example.authapppro.model.User
import com.example.authapppro.ui.search.SearchFragment.Companion.USER_KEY
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
import kotlinx.android.synthetic.main.activity_messaging.*
import java.time.LocalDateTime

const val REQUEST_CODE = 1

@RequiresApi(Build.VERSION_CODES.O)
class MessagingActivity : AppCompatActivity() {

    private val fUser = FirebaseAuth.getInstance().currentUser
    private val reference = FirebaseDatabase.getInstance().reference
    private val chatListSenderReference = FirebaseDatabase.getInstance().reference
    private val chatListReceiverReference = FirebaseDatabase.getInstance().reference
    var visitUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messaging)

        visitUser = intent.getParcelableExtra<User>(USER_KEY)

        val ref =
            FirebaseDatabase.getInstance().reference.child("users").child(visitUser?.uid.toString())
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)

                messaging_username.text = user?.username
                if (user?.profileUrl == "Default") {
                    messaging_profile_image.setImageResource(R.drawable.ic_profile)
                } else {
                    Picasso.get().load(user?.profileUrl).into(messaging_profile_image)
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        messaging_send_button.setOnClickListener {
            val message = messaging_edit_text.text.toString()
            if (message == "") {
                Toast.makeText(this, "Please, enter the message", Toast.LENGTH_SHORT).show()
            } else {
                sendMessage(fUser!!.uid, visitUser?.uid, message)
            }
            messaging_edit_text.text.clear()
        }

        messaging_attach_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.apply {
                type = "image/*"
            }
            startActivityForResult(Intent.createChooser(intent, "Select image"), REQUEST_CODE)
        }

    }


    private fun sendMessage(senderId: String, receiverId: String?, message: String) {
        val messageKey = reference.push().key

        /*val messageHashMap = HashMap<String, Any>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId.toString()
        messageHashMap["is_seen"] = false
        messageHashMap["url"] = "Default"
        messageHashMap["message_id"] = messageKey.toString()*/

        val messageMap = Message(
            messageKey.toString(),
            senderId,
            receiverId.toString(),
            message,
            false,
            "Default",
            LocalDateTime.now()
        )

        reference.child("chats").child(messageKey!!).setValue(messageMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    chatListSenderReference
                        .child("chat_list")
                        .child(fUser?.uid.toString())
                        .child(visitUser?.uid.toString())

                    chatListSenderReference.addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (!snapshot.exists()) {
                                chatListSenderReference.child("id").setValue(visitUser?.uid)
                            }

                            chatListReceiverReference
                                .child("chat_list")
                                .child(visitUser?.uid.toString())
                                .child(fUser?.uid.toString())

                                //chatListReceiverReference.child("id").setValue(fUser?.uid)
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })

                    val ref = FirebaseDatabase.getInstance().reference.child("users")
                        .child(fUser?.uid.toString())

                    //Notifications in progress....
                }

            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data?.data != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Image is sending, please wait...")
            progressDialog.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("chat images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")

            val uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val url = task.result.toString()

                    /*val messageHashMap = HashMap<String, Any>()
                    messageHashMap["sender"] = fUser!!.uid
                    messageHashMap["message"] = "sent you an image"
                    messageHashMap["receiver"] = visitUser?.uid.toString()
                    messageHashMap["is_seen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["message_id"] = messageId.toString()*/

                    val message = Message(
                        messageId.toString(),
                        fUser?.uid.toString(),
                        visitUser?.uid.toString(),
                        "sent you an image",
                        false,
                        "Default",
                        LocalDateTime.now()
                    )


                    ref.child("chats").child(messageId!!).setValue(message)

                    progressDialog.dismiss()
                }
            }
        }
    }

}