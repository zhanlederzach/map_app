package kz.example.placestovisit.ui.point_details_bottom_sheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_point_details_dialog_fragment.*
import kz.example.placestovisit.R
import kz.example.placestovisit.model.GeoLocationDetailsModel
import kz.example.placestovisit.model.GeoSearchModel
import kz.example.placestovisit.model.Routes

class BottomSheetDialogDetails : BottomSheetDialogFragment() {

    var drawRouteListener: (() -> Unit)? = null

    companion object {
        private const val GEO_SEARCH_MODEL = "GEO_SEARCH_MODEL"
        private const val GEO_SEARCH_DESCRIPTION = "GEO_SEARCH_DESCRIPTION"
        private const val ROUTES = "ROUTES"


        @JvmStatic
        fun newInstance(
            geoLocationDetailsModel: GeoLocationDetailsModel,
            routes: Routes,
            geoSearchModel: GeoSearchModel
        ): BottomSheetDialogDetails =
            BottomSheetDialogDetails().apply {
                arguments = bundleOf(
                    ROUTES to routes,
                    GEO_SEARCH_DESCRIPTION to geoLocationDetailsModel,
                    GEO_SEARCH_MODEL to geoSearchModel
                )
            }
    }

    private var geoLocationDetailsModel: GeoLocationDetailsModel? = null
    private var routes: Routes? = null
    private var geoSearchModel: GeoSearchModel? = null
    private var isExpanded: Boolean = false

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        routes = args?.getSerializable(ROUTES) as? Routes
        geoLocationDetailsModel = args?.getSerializable(GEO_SEARCH_DESCRIPTION) as? GeoLocationDetailsModel
        geoSearchModel = args?.getSerializable(GEO_SEARCH_MODEL) as? GeoSearchModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setupBehavior(bottomSheetDialog)
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_point_details_dialog_fragment, container, false)
    }

    private fun setupBehavior(bottomSheetDialog: BottomSheetDialog) {
        val behavior = bottomSheetDialog.behavior

        if (!isExpanded) {
            llMainRoot.viewTreeObserver
                .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        llMainRoot.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        val hidden = llMainRoot.getChildAt(1)
                        behavior.peekHeight = hidden.top
                    }
                })
        } else {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                isExpanded = slideOffset == 1f
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvDetails.text = geoSearchModel?.title

        btnSetRout.setOnClickListener {
            drawRouteListener?.invoke()
        }

    }
}