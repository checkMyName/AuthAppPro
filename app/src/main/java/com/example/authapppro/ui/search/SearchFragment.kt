package com.example.authapppro.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.authapppro.R
import com.example.authapppro.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_search.*
import java.util.*

class SearchFragment : Fragment() {

    private var groupAdapter = GroupAdapter<GroupieViewHolder>()
    val fUserID = FirebaseAuth.getInstance().currentUser?.uid
    private val reference: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("users")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        retrieveAllUsers()

        search_editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(cs: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchUsers(cs.toString().toLowerCase(Locale.ROOT))
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun retrieveAllUsers() {
        reference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snap: DataSnapshot) {
                groupAdapter.clear()

                if (!search_editText.text.equals("")) {
                    for (snapshot in snap.children) {

                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {
                            if (user.uid != fUserID) {
                                groupAdapter.add(SearchItem(user))
                            }
                        }
                    }
                    search_recyclerView.adapter = groupAdapter
                }

                groupAdapter.setOnItemClickListener { item, view ->
                    (item as SearchItem).let {
                        Toast.makeText(context, "Item clicked!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun searchUsers(username: String) {
        reference.child("users").orderByChild("search").startAt(username).endAt(username + "\uf8ff")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {
                groupAdapter.clear()

                for (snapshot in snap.children) {

                    val user = snapshot.getValue(User::class.java)

                    if (user != null) {
                        if (user.uid != fUserID) {
                            groupAdapter.add(SearchItem(user))
                        }
                    }

                }
                search_recyclerView.adapter = groupAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}