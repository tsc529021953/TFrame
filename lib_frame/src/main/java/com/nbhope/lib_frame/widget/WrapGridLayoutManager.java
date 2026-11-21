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
    private Boolean mRtl = true;

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

    /**
     * @param rtl 是否强制RTL(竖向网格时会把第0列排到右边)。历史行为固定true,
     *            竖向逐行填充的网格需传false
     */
    public WrapGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout, boolean rtl) {
        super(context, spanCount, orientation, reverseLayout);
        this.mRtl = rtl;
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

    @Override
    protected boolean isLayoutRTL() {
//        return super.isLayoutRTL();
        return mRtl;
    }
}
