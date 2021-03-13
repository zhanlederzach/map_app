package kz.example.placestovisit.ui.point_details_bottom_sheet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import kz.example.placestovisit.R
import kz.example.placestovisit.model.GeoLocationDetailsModel
import kz.example.placestovisit.model.GeoSearchModel
import kz.example.placestovisit.model.Routes

class PlacesInfoAdapter(
    private var geoLocationDetailsModel: GeoLocationDetailsModel?,
    private var routes: Routes?,
    private var geoSearchModel: GeoSearchModel?,
    private var titles: List<String>
) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ConstraintLayout
    }

    override fun getCount(): Int {
        return 2
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View? = when (position) {
            0 -> {
                LayoutInflater.from(container.context)
                    .inflate(R.layout.how_to_get_layout, container, false)
            }
            else -> {
                LayoutInflater.from(container.context)
                    .inflate(R.layout.contact_info_layout, container, false)
            }
        }
        val viewPager = container as? ViewPager
        viewPager?.addView(itemView)
        return itemView!!
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (`object` as? LinearLayout)?.let { container.removeView(it) }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles.get(position)
    }

}