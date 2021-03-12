package kz.example.placestovisit.ui.point_details_bottom_sheet

import android.app.Dialog
import android.content.DialogInterface
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
import kz.example.placestovisit.model.GeoSearchModel
import kz.example.placestovisit.model.Routes

class BottomSheetDialogDetails : BottomSheetDialogFragment() {

    var listener: (() -> Unit)? = null

    companion object {
        private const val GEO_SEARCH = "GEO_SEARCH"
        private const val ROUTES = "ROUTES"

        @JvmStatic
        fun newInstance(geoSearchModel: GeoSearchModel?, routes: Routes): BottomSheetDialogDetails =
            BottomSheetDialogDetails().apply {
                arguments = bundleOf(ROUTES to routes, GEO_SEARCH to geoSearchModel)
            }
    }

    private var geoSearchModel: GeoSearchModel? = null
    private var routes: Routes? = null
    private var geoSearchDetails: String? = ""

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        routes = args?.getSerializable(ROUTES) as? Routes
        geoSearchModel = args?.getSerializable(GEO_SEARCH) as? GeoSearchModel
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

        llMainRoot.viewTreeObserver
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    llMainRoot.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val hidden = llMainRoot.getChildAt(1)
                    behavior.peekHeight = hidden.top
                }
            })

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            var oldOffSet = 0f
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val inRangeExpanding = oldOffSet < slideOffset
                oldOffSet = slideOffset
                if (inRangeExpanding && geoSearchDetails == null) {
                    // TODO: make query to details
                    geoSearchDetails = ""
                }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN ||
                    newState == BottomSheetBehavior.STATE_COLLAPSED ||
                    newState == BottomSheetBehavior.STATE_HALF_EXPANDED
                ) {
                    dismiss()
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvDetails.text = geoSearchModel?.title

        btnSetRout.setOnClickListener {
            listener?.invoke()
        }

        closeImageView.setOnClickListener {
            dismiss()
        }
    }
}