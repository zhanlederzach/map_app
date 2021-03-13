package kz.example.placestovisit.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import kz.example.placestovisit.R

class ToolbarView : LinearLayout {

    private lateinit var itemTitle: TextView
    private lateinit var ivBack: ImageView

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    fun getBackArrow(): ImageView {
        return ivBack
    }

    private fun init(context: Context) {
        inflate(context, R.layout.item_toolbar, this)

        itemTitle = findViewById(R.id.tvTitleToolbar)
        ivBack = findViewById(R.id.ivBack)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        init(context)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemView)

        itemTitle.apply {
            text = typedArray.getText(R.styleable.ItemView_itemTitleText)
        }
        typedArray.recycle()
    }

}
