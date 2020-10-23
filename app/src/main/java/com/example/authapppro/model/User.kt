package com.example.authapppro.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(
    val uid: String,
    val username: String,
    val email: String,
    val profileUrl: String,
    val backgroundUrl: String,
    val status: Boolean,
    val search: String
): Parcelable {
    constructor() : this("", "", "", "", "", false, "")
}