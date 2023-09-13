package com.sc.nft.weight;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class SmoothRotationViewGroup extends ViewGroup {

    private PointF pivotPoint = new PointF();
    private float initialAngle = 0f;
    private float currentAngle = 0f;
    private boolean isRotating = false;

    public SmoothRotationViewGroup(Context context) {
        super(context);
    }

    public SmoothRotationViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void dispatchDraw(android.graphics.Canvas canvas) {
        canvas.save();
        canvas.rotate(currentAngle, pivotPoint.x, pivotPoint.y);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    private PointF pointer1 = new PointF();
    private PointF pointer2 = new PointF();
    private boolean isTwoFingerGesture = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                isTwoFingerGesture = false;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    pointer1.set(event.getX(0), event.getY(0));
                    pointer2.set(event.getX(1), event.getY(1));

                    float centerX = (pointer1.x + pointer2.x) / 2;
                    float centerY = (pointer1.y + pointer2.y) / 2;
                    pivotPoint.set(centerX, centerY);

                    initialAngle = (float) Math.toDegrees(Math.atan2(pointer2.y - pointer1.y, pointer2.x - pointer1.x));
                    isRotating = true;
                    isTwoFingerGesture = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isTwoFingerGesture && isRotating && event.getPointerCount() == 2) {
                    float newAngle = (float) Math.toDegrees(Math.atan2(pointer2.y - pointer1.y, pointer2.x - pointer1.x));
                    float angleDiff = newAngle - initialAngle;
                    currentAngle += angleDiff;
                    initialAngle = newAngle;
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 2) {
                    isRotating = false;
                }
                break;

            case MotionEvent.ACTION_UP:
                isTwoFingerGesture = false;
                break;
        }

        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return isTwoFingerGesture;
    }
}
