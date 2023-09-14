package com.sc.nft.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import timber.log.Timber;

public class ScalableLayout extends FrameLayout implements RotationGestureDetector.OnRotationGestureListener
        , RotateGestureDetector .OnRotateGestureListener {

    private static final int INVALID_POINTER_ID = -1;
    private float scaleFactor = 1.0f;
    private float rotationDegrees = 0.0f;
    private int activePointerId = INVALID_POINTER_ID;
    private ScaleGestureDetector scaleGestureDetector;
    private boolean isDoubleTap = false;
    private PointF pivotPoint = new PointF(0, 0);

    private RotationGestureDetector mRotationDetector;

    public ScalableLayout(Context context) {
        super(context);
        init(context);
    }

    public ScalableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        setOnTouchListener(new TouchListener());
        mRotationDetector = new RotationGestureDetector( this, this);
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        View child = getChildAt(0);
        canvas.save();
        //        canvas.rotate(rotationDegrees, pivotPoint.x, pivotPoint.y); // 以指定点为中心旋转
//        canvas.scale(scaleFactor, scaleFactor);
//        canvas.concat(mMatrix);
        child.setScaleX(scaleFactor);
        child.setScaleY(scaleFactor);
        child.setRotation(rotationDegrees);
//        child.draw(canvas);
                super.dispatchDraw(canvas);
        canvas.restore();
    }

//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        // Layout child views according to scaleFactor and rotationDegrees
//        int childCount = getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            View child = getChildAt(i);
//            int childWidth = child.getMeasuredWidth();
//            int childHeight = child.getMeasuredHeight();
//            int childLeft = (getWidth() - childWidth) / 2;
//            int childTop = (getHeight() - childHeight) / 2;
//            int childRight = childLeft + childWidth;
//            int childBottom = childTop + childHeight;
//            child.layout(childLeft, childTop, childRight, childBottom);
//            child.setScaleX(scaleFactor);
//            child.setScaleY(scaleFactor);
//            child.setRotation(rotationDegrees);
//        }
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Measure child views
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    public void onRotation(RotationGestureDetector rotationDetector) {
        rotationDegrees = rotationDetector.getAngle();
    }

    @Override
    public void onRotate(float angle) {
//        rotationDegrees = angle;
    }

    @Override
    public void onRotateBegin(float initialAngle) {

    }

    @Override
    public void onRotateEnd() {

    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f)); // Limit the scale factor
            pivotPoint.set(detector.getFocusX(), detector.getFocusY());
            requestLayout();
            return true;
        }
    }

    private class TouchListener implements OnTouchListener {
        private float downX, downY;
        private float upX, upY;
        private static final int TOUCH_DISTANCE_THRESHOLD = 10;
        private static final long DOUBLE_TAP_TIME_THRESHOLD = 300;
        private long lastTapTime = 0;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            mRotationDetector.onTouchEvent(event);
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    activePointerId = event.getPointerId(0);
                    downX = event.getX();
                    downY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (activePointerId != INVALID_POINTER_ID) {

//                        if (upX != event.getX() && upY != event.getY())
//                            rotationDegrees -= angleBetweenLines(downX, downY, pivotPoint.x, pivotPoint.y
//                                    , event.getX(), event.getY(), pivotPoint.x, pivotPoint.y) * 0.05;
                        upX = event.getX();
                        upY = event.getY();
                        float distance = calculateDistance(downX, downY, upX, upY);
                        if (distance > TOUCH_DISTANCE_THRESHOLD) {
                            isDoubleTap = false;
                        }
                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    if (event.getPointerId(event.getActionIndex()) == activePointerId) {
                        activePointerId = INVALID_POINTER_ID;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    upX = event.getX();
                    upY = event.getY();
                    if (calculateDistance(downX, downY, upX, upY) <= TOUCH_DISTANCE_THRESHOLD) {
                        // This is a tap
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastTapTime <= DOUBLE_TAP_TIME_THRESHOLD) {
                            // Double tap detected
                            isDoubleTap = true;
                            handleDoubleTap();
                        } else {
                            isDoubleTap = false;
                        }
                        lastTapTime = currentTime;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    activePointerId = INVALID_POINTER_ID;
                    break;
            }
            scaleGestureDetector.onTouchEvent(event);
            return true;
        }
    }

    private float calculateDistance(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private void handleDoubleTap() {
        if (isDoubleTap) {
            // Implement double-tap to restore original state smoothly
            restoreOriginalState();
        }
    }

    public void restoreOriginalState() {
        Animation scaleAnimation = new ScaleAnimation(scaleFactor, 1.0f, scaleFactor, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(300);

        Animation rotateAnimation = new RotateAnimation(rotationDegrees, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(300);

        startAnimation(scaleAnimation);
        startAnimation(rotateAnimation);

        scaleFactor = 1.0f;
        rotationDegrees = 0.0f;
        requestLayout();
    }

    private float angleBetweenLines(float startX1, float startY1, float endX1, float endY1,
                                    float startX2, float startY2, float endX2, float endY2) {
        float angle1 = (float) Math.atan2(endY1 - startY1, endX1 - startX1);
        float angle2 = (float) Math.atan2(endY2 - startY2, endX2 - startX2);
//        Timber.i("NTAG degress " + (float) Math.toDegrees(angle1 - angle2));
        return (float) Math.toDegrees(angle1 - angle2);
//        float angle = ((float) Math.toDegrees(angle1 - angle2)) % 360;
////        if (angle < -180.f) angle += 360.0f;
////        if (angle > 180.f) angle -= 360.0f;
//        return angle;
    }

    private float angleBetweenLines(PointF fPoint, PointF sPoint, PointF nFpoint, PointF nSpoint) {
        float angle1 = (float) Math.atan2((fPoint.y - sPoint.y), (fPoint.x - sPoint.x));
        float angle2 = (float) Math.atan2((nFpoint.y - nSpoint.y), (nFpoint.x - nSpoint.x));

        float angle = ((float) Math.toDegrees(angle1 - angle2)) % 360;
        if (angle < -180.f) angle += 360.0f;
        if (angle > 180.f) angle -= 360.0f;
        return -angle;
    }
}
