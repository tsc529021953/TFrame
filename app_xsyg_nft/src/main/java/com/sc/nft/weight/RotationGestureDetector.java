package com.sc.nft.weight;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import timber.log.Timber;

public class RotationGestureDetector {

    private static final int INVALID_POINTER_ID = -1;
    private PointF mFPoint = new PointF();
    private PointF mSPoint = new PointF();
    private int mPtrID1, mPtrID2;
    private float mAngle;
    private View mView;

    private OnRotationGestureListener mListener;

    public float getAngle() {
        return mAngle;
    }

    public RotationGestureDetector(OnRotationGestureListener listener, View v) {
        mListener = listener;
        mView = v;
        mPtrID1 = INVALID_POINTER_ID;
        mPtrID2 = INVALID_POINTER_ID;
    }

    PointF nfPointR = new PointF();
    PointF nsPointR = new PointF();

    boolean pointEq(PointF nfPoint, PointF nsPoint){
        return Math.abs(nfPointR.x - nfPoint.x) < 0.1f && Math.abs(nfPointR.y - nfPoint.y) < 0.1f
                && Math.abs(nsPointR.x - nsPoint.x) < 0.1f && Math.abs(nsPointR.y - nsPoint.y) < 0.1f;
    }


    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_OUTSIDE:
                break;
            case MotionEvent.ACTION_DOWN:
                mPtrID1 = event.getPointerId(event.getActionIndex());
                Timber.i("KTAG ACTION_DOWN " + mPtrID1);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mPtrID2 = event.getPointerId(event.getActionIndex());
                Timber.i("KTAG ACTION_POINTER_DOWN " + mPtrID2);
                getRawPoint(event, mPtrID1, mSPoint);
                getRawPoint(event, mPtrID2, mFPoint);

                break;
            case MotionEvent.ACTION_MOVE:
                Timber.i("KTAG ACTION_MOVE " + mPtrID1 + " " + mPtrID2 + " " + INVALID_POINTER_ID);
                if (mPtrID1 != INVALID_POINTER_ID && mPtrID2 != INVALID_POINTER_ID) {
                    PointF nfPoint = new PointF();
                    PointF nsPoint = new PointF();

                    getRawPoint(event, mPtrID1, nsPoint);
                    getRawPoint(event, mPtrID2, nfPoint);
                    if (!pointEq(nfPoint, nsPoint)) {
                        Timber.i("KTAG point " + nfPointR + " " + nfPoint + " "+ nsPointR + " " + nsPoint);
                        nfPointR = nfPoint;
                        nsPointR = nsPoint;
                        mAngle += angleBetweenLines(mFPoint, mSPoint, nfPoint, nsPoint) * 0.05;
                        if (mListener != null) {
                            mListener.onRotation(this);
                        }
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
                mPtrID1 = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mPtrID2 = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_CANCEL:
                mPtrID1 = INVALID_POINTER_ID;
                mPtrID2 = INVALID_POINTER_ID;
                break;
            default:
                break;
        }
        return true;
    }

    void getRawPoint(MotionEvent ev, int index, PointF point) {
        final int[] location = { 0, 0 };
        mView.getLocationOnScreen(location);

        float x = ev.getX(index);
        float y = ev.getY(index);

        double angle = Math.toDegrees(Math.atan2(y, x));
        angle += mView.getRotation();

        final float length = PointF.length(x, y);

        x = (float) (length * Math.cos(Math.toRadians(angle))) + location[0];
        y = (float) (length * Math.sin(Math.toRadians(angle))) + location[1];

        point.set(x, y);
    }

    private float angleBetweenLines(PointF fPoint, PointF sPoint, PointF nFpoint, PointF nSpoint) {
        float angle1 = (float) Math.atan2((fPoint.y - sPoint.y), (fPoint.x - sPoint.x));
        float angle2 = (float) Math.atan2((nFpoint.y - nSpoint.y), (nFpoint.x - nSpoint.x));

        float angle = ((float) Math.toDegrees(angle1 - angle2)) % 360;
        if (angle < -180.f) angle += 360.0f;
        if (angle > 180.f) angle -= 360.0f;
        return -angle;
    }

    public interface OnRotationGestureListener {
        void onRotation(RotationGestureDetector rotationDetector);
    }
}

