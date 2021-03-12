package com.main.ui_core.extensions

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.main.ui_core.R
import com.main.ui_core.base.BaseContainerFragment
import com.mobile.telecomapp.utils.NavigationAnimation

fun Fragment.replaceFragment(containerId: Int, fragment: Fragment, tagFragmentName: String?) {
    val transaction = childFragmentManager.beginTransaction()
    if (tagFragmentName != null) {
        transaction
            .addToBackStack(tagFragmentName)
    }
    transaction
        .replace(containerId, fragment, tagFragmentName)
        .commitAllowingStateLoss()
}

fun Fragment.showToast(text: String?) {
    if (!text.isNullOrEmpty()) Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(resText: Int) {
    Toast.makeText(requireContext(), resText, Toast.LENGTH_SHORT).show()
}

fun BaseContainerFragment.changeFragment(
    fragment: Fragment,
    tagFragmentName: String,
    addToStack: Boolean = false,
    animation: NavigationAnimation = NavigationAnimation.SLIDE
) {
    val fragmentManager = childFragmentManager
    val fragmentTransaction = fragmentManager.beginTransaction()
    if (addToStack) {
        fragmentTransaction.addToBackStack(tagFragmentName)
    }
    setTransactionAnimation(fragmentTransaction, animation)
    val currentFragment = fragmentManager.primaryNavigationFragment
    if (currentFragment != null) {
        fragmentTransaction.hide(currentFragment)
    }
    var fragmentTemp = fragmentManager.findFragmentByTag(tagFragmentName)
    if (fragmentTemp == null) {
        fragmentTemp = fragment
        fragmentTransaction.add(getContainer(), fragmentTemp, tagFragmentName)
    } else {
        fragmentTransaction.show(fragmentTemp)
    }
    fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp)
    fragmentTransaction.setReorderingAllowed(true)
    fragmentTransaction.commitAllowingStateLoss()
}

private fun setTransactionAnimation(transaction: FragmentTransaction, animation: NavigationAnimation) {
    when (animation) {
        NavigationAnimation.SLIDE -> {
            transaction.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
        }
        NavigationAnimation.SLIDE_UP -> {
            transaction.setCustomAnimations(
                R.anim.slide_up,
                R.anim.stay,
                R.anim.stay,
                R.anim.slide_down
            )
        }
        else -> {}
    }
}