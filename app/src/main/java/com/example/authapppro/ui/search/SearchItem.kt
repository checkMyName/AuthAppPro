package com.example.authapppro.ui.search

import com.example.authapppro.R
import com.example.authapppro.model.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.search_item.view.*

class SearchItem(val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
           itemView.username_item_textView.text = user.username

            if (user.profileUrl == "Default") {
                viewHolder.itemView.search_item_profileView.setImageResource(R.drawable.ic_profile)
            } else {
                Picasso.get().load(user.profileUrl).into(itemView.search_item_profileView)
            }

            //FOOL
        }
    }

    override fun getLayout() = R.layout.search_item
}
