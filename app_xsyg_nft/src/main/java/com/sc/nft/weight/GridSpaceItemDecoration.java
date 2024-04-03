package com.sc.nft.weight;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.utils.AutoSizeUtils;
import timber.log.Timber;

/**
 * 描述 : RecyclerView GridLayoutManager 等间距。
 * <p>
 * 等间距需满足两个条件：
 * 1.各个模块的大小相等，即 各列的left+right 值相等；
 * 2.各列的间距相等，即 前列的right + 后列的left = 列间距；
 * <p>
 * 在{@link #getItemOffsets(Rect, View, RecyclerView, RecyclerView.State)} 中针对 outRect 的left 和right 满足这两个条件即可
 * <p>
 * 作者 : shiguotao
 * 版本 : V1
 * 创建时间 : 2020/3/19 4:54 PM
 */
public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final String TAG = "GridSpaceItemDecoration";

    private int mSpanCount;//横条目数量
    private int mRowSpacing;//行间距
    private int mColumnSpacing;// 列间距

    /**
     * @param spanCount     列数
     * @param rowSpacing    行间距
     * @param columnSpacing 列间距
     */
    public GridSpaceItemDecoration(int spanCount, int rowSpacing, int columnSpacing) {
        this.mSpanCount = spanCount;
        this.mRowSpacing = rowSpacing;
        this.mColumnSpacing = columnSpacing;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // 获取view 在adapter中的位置。
        int column = position % mSpanCount; // view 所在的列
        int screenWidth = parent.getLayoutParams().width;
        int width = view.getLayoutParams().width;
        int interval = (screenWidth - width * mSpanCount) / (mSpanCount - 1);
//                view.getResources().getDisplayMetrics().widthPixels;
        Log.e(TAG,"NTAG column " + mSpanCount + " " + column + " " + (interval) + " " + (view.getLayoutParams().width) + " " + screenWidth);

        if (column ==  mSpanCount - 1) {
//            outRect.left = 0;
            outRect.right = screenWidth / mSpanCount - width;
        } else if (column == 0) {
//            outRect.left = screenWidth / mSpanCount - width;
//            outRect.right = screenWidth / mSpanCount ;
            outRect.right = 0;
        } else {
            outRect.left = (width + interval) * column
            - screenWidth / mSpanCount * column;
        }
//        if (column ==  0) {
//            outRect.left = 0;
//            outRect.right = width;
//        } else if (column == mSpanCount - 1) {
//            outRect.left = screenWidth / mSpanCount - width;
////            outRect.right = screenWidth / mSpanCount ;
//        } else {
//            outRect.left = (width + interval) * column
//                    - screenWidth / mSpanCount * column;
//        }
        Log.e(TAG, "position:" + position
                + "    columnIndex: " + column
                + "    left,right ->" + outRect.left + "," + outRect.right
                + " " +  view.getResources().getDisplayMetrics().scaledDensity
        );

        // 如果position > 行数，说明不是在第一行，则不指定行高，其他行的上间距为 top=mRowSpacing
//        if (position >= mSpanCount) {
//            outRect.top = mRowSpacing; // item top
//        }
    }
}
