package com.example.authapppro.model

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

internal class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    private val fragmentsList: ArrayList<Fragment> = ArrayList<Fragment>()
    private val titlesList: ArrayList<String> = ArrayList<String>()

    override fun getCount(): Int {
        return fragmentsList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentsList[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragmentsList.add(fragment)
        titlesList.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titlesList[position]
    }
}