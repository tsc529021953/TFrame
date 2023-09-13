package com.sc.nft.weight;

import android.content.Context;
import android.view.MotionEvent;

public class RotateGestureDetector {

    private static final int INVALID_POINTER_ID = -1;
    private float startAngle;
    private float currentAngle;
    private int activePointerId = INVALID_POINTER_ID;

    public interface OnRotateGestureListener {
        boolean onRotate(RotateGestureDetector detector);
    }

    private OnRotateGestureListener mListener;

    public RotateGestureDetector(Context context, OnRotateGestureListener listener) {
        mListener = listener;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                activePointerId = event.getPointerId(0);
                startAngle = getAngle(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (activePointerId != INVALID_POINTER_ID) {
                    float newAngle = getAngle(event);
                    currentAngle = newAngle - startAngle;
                    if (mListener != null) {
                        return mListener.onRotate(this);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                activePointerId = INVALID_POINTER_ID;
                break;
        }
        return false;
    }

    private float getAngle(MotionEvent event) {
//        int index = event.findPointerIndex(activePointerId);
//        float x = event.getX(index);
//        float y = event.getY(index);
//        double angle = Math.atan2(y, x);
//        return (float) Math.toDegrees(angle);

        int index = event.findPointerIndex(activePointerId);
        float x = event.getX(index);
        float y = event.getY(index);
        double angle = Math.atan2(x, y); // 交换 x 和 y 的位置
        return (float) Math.toDegrees(angle);
    }

    public float getRotationDegreesDelta() {
        return currentAngle;
    }
}
