package com.sc.nft.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;

public class ScalableRotatableLayout extends FrameLayout {

    private ScaleGestureDetector scaleGestureDetector;
    private RotateGestureDetector rotateGestureDetector;
    private float scaleFactor = 1.0f;
    private float rotationDegrees = 0.0f;

    public ScalableRotatableLayout(Context context) {
        super(context);
        init(context);
    }

    public ScalableRotatableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        rotateGestureDetector = new RotateGestureDetector(context, new RotateListener());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        scaleGestureDetector.onTouchEvent(ev);
        rotateGestureDetector.onTouchEvent(ev);
        return false; // Let child views handle touch events
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        rotateGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // Scale and rotate child views according to scaleFactor and rotationDegrees
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewGroup.LayoutParams params = getChildAt(i).getLayoutParams();
            getChildAt(i).setScaleX(scaleFactor);
            getChildAt(i).setScaleY(scaleFactor);
            getChildAt(i).setRotation(rotationDegrees);
            getChildAt(i).setLayoutParams(params);
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f)); // Limit the scale factor
            requestLayout();
            return true;
        }
    }

    private class RotateListener implements RotateGestureDetector.OnRotateGestureListener {
        @Override
        public boolean onRotate(RotateGestureDetector detector) {
            rotationDegrees -= detector.getRotationDegreesDelta();
            requestLayout();
            return true;
        }
    }

    public void rotate(float degrees) {
        rotationDegrees += degrees;
        requestLayout();
    }
}
