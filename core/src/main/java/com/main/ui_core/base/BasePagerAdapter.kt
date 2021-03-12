package com.main.ui_core.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class BasePagerAdapter(
    fm: FragmentManager,
    val fragments: List<Fragment> = arrayListOf(),
    val titles: List<String>? = null
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int): CharSequence? {
        return titles?.get(position)
    }

    fun addFragment(fragment: Fragment) {
        (fragments as ArrayList<Fragment>).add(fragment)
    }
}
