package kz.example.placestovisit.ui.point_details_bottom_sheet

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.main.ui_core.extensions.setHtmlText
import kz.example.placestovisit.R
import kz.example.placestovisit.model.GeoLocationDetailsModel
import kz.example.placestovisit.model.GeoSearchModel
import kz.example.placestovisit.model.Routes
import kz.example.placestovisit.widgets.RouteInfoView

class PlacesInfoAdapter(
    private var geoLocationDetailsModel: GeoLocationDetailsModel?,
    private var routes: Routes?,
    private var geoSearchModel: GeoSearchModel?,
    private var titles: List<String>
) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ScrollView
    }

    override fun getCount(): Int {
        return 2
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View?
        when (position) {
            0 -> {
                itemView = LayoutInflater.from(container.context)
                    .inflate(R.layout.how_to_get_layout, container, false)
                val llMainRoot = itemView.findViewById(R.id.llMain) as LinearLayout
                if (routes?.routes.isNullOrEmpty()) return itemView
                if (routes?.routes?.first()?.legs.isNullOrEmpty()) return itemView
                routes?.routes?.first()?.legs?.first()?.steps?.forEach {
                    val htmlInstruction = it.htmlInstructions
                    if (!htmlInstruction.isNullOrEmpty()) {
                        val textView = RouteInfoView(container.context)
                        textView.getTextView().setHtmlText(htmlInstruction)
                        llMainRoot.addView(textView)
                    }
                }
                if (llMainRoot.childCount > 0) {
                    (llMainRoot.children.last() as RouteInfoView).setDividerVisibility(false)
                }
            }
            else -> {
                itemView = LayoutInflater.from(container.context)
                    .inflate(R.layout.contact_info_layout, container, false)
                val tvCopyright = itemView.findViewById(R.id.tvCopyright) as TextView
                val tvSummary = itemView.findViewById(R.id.tvSummary) as TextView

                if (routes?.routes.isNullOrEmpty()) return itemView

                val route = routes?.routes?.first()
                tvCopyright.text = route?.copyrights
                tvSummary.text = route?.summary
            }
        }

        val viewPager = container as? ViewPager
        viewPager?.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (`object` as? LinearLayout)?.let { container.removeView(it) }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles.get(position)
    }

}