package com.nbhope.lib_frame.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ywr on 2021/4/13 9:30
 */
public class WrapGridLayoutManager extends GridLayoutManager {
    private Boolean mSv = true;

    public WrapGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public WrapGridLayoutManager(Context context, int spanCount, Boolean sv) {
        super(context, spanCount);
        this.mSv = sv;
    }

    public WrapGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean canScrollVertically() {
        return mSv;
    }
}
