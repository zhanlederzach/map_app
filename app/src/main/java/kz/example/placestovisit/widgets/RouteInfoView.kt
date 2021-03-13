package kz.example.placestovisit.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kz.example.placestovisit.R

class RouteInfoView : LinearLayout {

    private lateinit var tvTextRoute: TextView
    private lateinit var viewDivider: View

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    fun getDivider(): View {
        return viewDivider
    }

    fun setDividerVisibility(isVisible: Boolean) {
        if (isVisible) viewDivider.visibility = View.VISIBLE
        else viewDivider.visibility = View.GONE
    }

    fun setText(text: String) {
        tvTextRoute.text = text
    }

    fun getTextView(): TextView {
        return tvTextRoute
    }

    private fun init(context: Context) {
        inflate(context, R.layout.item_route_info, this)

        viewDivider = findViewById(R.id.viewDivider)
        tvTextRoute = findViewById(R.id.tvTextRoute)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        init(context)
    }

}
