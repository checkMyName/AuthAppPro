package com.example.authapppro.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.authapppro.R
import com.example.authapppro.model.User

/*
class UserAdapter(
    context: Context,
    userList: List<User>,
    isChatCheck: Boolean
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private val context: Context
    private val userList: List<User>
    private val isChatCheck: Boolean

    init {
        this.context = context
        this.userList = userList
        this.isChatCheck = isChatCheck
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView
        var profileImage: ImageView
        var status: ImageView
        var lastMessage: TextView

        init {
            username = itemView.findViewById(R.id.username_item_textView)
            profileImage = itemView.findViewById(R.id.search_item_profileView)
            status = itemView.findViewById(R.id.status_item_imageView)
            lastMessage = itemView.findViewById(R.id.last_message_item_textView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.search_item, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return userList.size
    }
}*/
