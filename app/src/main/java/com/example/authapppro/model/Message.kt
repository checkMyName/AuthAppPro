package com.example.authapppro.model

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime

@Parcelize
class Message(
    val messageId: String,
    val senderId: String,
    val receiverId: String,
    val message: String,
    val isSeen: Boolean,
    val url: String,
    val date: LocalDateTime
) : Parcelable {
    @RequiresApi(Build.VERSION_CODES.O)
    constructor() : this("", "", "", "", false, "Default", LocalDateTime.now())
}