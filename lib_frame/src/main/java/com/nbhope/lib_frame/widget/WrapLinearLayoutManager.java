package com.nbhope.lib_frame.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ywr on 2021/4/13 9:30
 */
public class WrapLinearLayoutManager extends LinearLayoutManager {

    private Boolean iScanScrollVertically = true;
    private Boolean iScanScrollHorizontally = true;

    public WrapLinearLayoutManager(Context context) {
        super(context);
    }

    public WrapLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public WrapLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void setCanSroll(boolean iScanScrollVertically, boolean iScanScrollHorizontally) {
        this.iScanScrollVertically = iScanScrollVertically;
        this.iScanScrollHorizontally = iScanScrollHorizontally;
    }

    @Override
    public boolean canScrollVertically() {
        return iScanScrollVertically;
    }

    @Override
    public boolean canScrollHorizontally() {
        return iScanScrollHorizontally;
    }
}
