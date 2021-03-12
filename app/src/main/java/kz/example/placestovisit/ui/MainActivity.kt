package kz.example.placestovisit.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.main.ui_core.Screen
import com.main.ui_core.extensions.changeFragment
import com.mobile.telecomapp.utils.NavigationAnimation
import dagger.android.support.DaggerAppCompatActivity
import kz.example.placestovisit.R
import kz.example.placestovisit.ui.main_page.MainPageFragment

class MainActivity : DaggerAppCompatActivity() {

    companion object {
        fun start(context: Context, data: Bundle? = null) {
            val intent = Intent(context, MainActivity::class.java)
            data?.let { intent.putExtras(it) }
            context.startActivity(intent)
        }
    }

    private var currentFragment: Fragment? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        currentFragment?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        currentFragment = MainPageFragment.newInstance(intent.extras)

        if (savedInstanceState == null) {
            navigateTo(
                fragment = currentFragment!!,
                tag = Screen.MAIN_PAGE.name,
                animation = NavigationAnimation.NONE
            )
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount - 1 < supportFragmentManager.backStackEntryCount &&
                supportFragmentManager.backStackEntryCount - 1 >= 0
            ) {
                val tag =
                    supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name
                if (!tag.isNullOrEmpty()) {
                    currentFragment = supportFragmentManager.findFragmentByTag(tag)
                }
            }
        }
    }

    fun navigateTo(
        fragment: Fragment,
        tag: String,
        addToStack: Boolean = false,
        animation: NavigationAnimation = NavigationAnimation.SLIDE
    ) {
        changeFragment(
            layoutId = R.id.menuContainer,
            fragment = fragment,
            tagFragmentName = tag,
            addToStack = addToStack,
            animation = animation
        )
    }
}