package com.example.authapppro.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TableLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.authapppro.R
import com.example.authapppro.model.User
import com.example.authapppro.model.ViewPagerAdapter
import com.example.authapppro.ui.auth.LoginActivity
import com.example.authapppro.ui.chats.ChatsFragment
import com.example.authapppro.ui.profile.ProfileFragment
import com.example.authapppro.ui.search.SearchFragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val fUser: FirebaseUser? = mAuth.currentUser
    private val reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users").child(fUser?.uid.toString())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)

        supportActionBar?.title = ""

        val tabLayout: TabLayout = findViewById(R.id.table_layout)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPagerAdapter.apply {
            addFragment(ChatsFragment(), "Chats")
            addFragment(SearchFragment(), "Search")
            addFragment(ProfileFragment(), "Profile")
        }

        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)

                    user_name.text = user?.username

                    if (user?.profileUrl == "Default") {
                        profile_image.setImageResource(R.drawable.ic_account)
                    } else {
                        Picasso.get().load(user?.profileUrl).into(profile_image)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        verifyUserIsLoggedIn(this)
    }

    private fun verifyUserIsLoggedIn(context: Context) {
        val uid = mAuth.uid
        if (uid.isNullOrEmpty()) {
            Intent(context, LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                ContextCompat.startActivity(context, it, null)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                mAuth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

}