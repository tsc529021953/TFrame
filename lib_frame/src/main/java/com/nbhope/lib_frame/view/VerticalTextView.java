package com.nbhope.lib_frame.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

public class VerticalTextView extends AppCompatTextView {

    public VerticalTextView(Context context) {
        super(context);
    }

    public VerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        // Rotate the canvas 90 degrees
        canvas.rotate(90, getWidth() / 2f, getHeight() / 2f);
        // Translate the canvas to adjust the position
        canvas.translate(getWidth() / 2f, -getHeight() / 2f);
        super.onDraw(canvas);
        canvas.restore();
    }
}
