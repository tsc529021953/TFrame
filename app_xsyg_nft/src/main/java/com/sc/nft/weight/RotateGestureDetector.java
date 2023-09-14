package com.sc.nft.weight;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

public class RotateGestureDetector {

    public interface OnRotateGestureListener {
        void onRotate(float angle);

        void onRotateBegin(float initialAngle);

        void onRotateEnd();
    }

    private static final int INVALID_POINTER_ID = -1;
    private static final int TOUCH_SLOP = 8;
    private static final float MIN_ROTATION_ANGLE = 5.0f;

    private Context context;
    private OnRotateGestureListener listener;

    private float initialAngle = 0f;
    private float currentAngle = 0f;
    private boolean isRotating = false;
    private int activePointerId = INVALID_POINTER_ID;

    private float pivotX = 0f;
    private float pivotY = 0f;

    public RotateGestureDetector(Context context, OnRotateGestureListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public boolean onTouchEvent(View view, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                activePointerId = event.getPointerId(0);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    initialAngle = calculateAngle(event);
                    pivotX = (event.getX(0) + event.getX(1)) / 2;
                    pivotY = (event.getY(0) + event.getY(1)) / 2;
                    isRotating = true;
                    listener.onRotateBegin(initialAngle);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isRotating) {
                    float newAngle = calculateAngle(event);
                    float angleDiff = newAngle - initialAngle;
                    currentAngle += angleDiff;
                    initialAngle = newAngle;
                    if (Math.abs(angleDiff) >= MIN_ROTATION_ANGLE) {
                        listener.onRotate(currentAngle);
                    }
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 2) {
                    isRotating = false;
                    listener.onRotateEnd();
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                activePointerId = INVALID_POINTER_ID;
                isRotating = false;
                listener.onRotateEnd();
                break;
        }
        return true;
    }

    private float calculateAngle(MotionEvent event) {
        int pointerIndex = event.findPointerIndex(activePointerId);
        float x1 = event.getX(pointerIndex) - pivotX;
        float y1 = event.getY(pointerIndex) - pivotY;
        return (float) Math.toDegrees(Math.atan2(y1, x1));
    }
}
