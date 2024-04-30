package com.nbhope.lib_frame.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

public class IconFontView extends AppCompatTextView {
    public IconFontView(Context context) {
        super(context);
        Init(context);
    }

    public IconFontView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public IconFontView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context);
    }

    public void Init(Context context) {
        Typeface icon = Typeface.createFromAsset(context.getAssets(), "iconfont.ttf");
        this.setTypeface(icon);
    }

    public void setSelect(boolean select) {
        this.setSelected(select);
    }

}
