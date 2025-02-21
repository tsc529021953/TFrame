package com.sc.tmp_cw.weight;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final RecyclerView.Adapter adapter;
//    private final List<String> dataList;

    // dataList
    public SimpleItemTouchHelperCallback(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
//        this.dataList = dataList;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true; // Enable long press to start drag
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false; // Disable swipe
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
//        Collections.swap(dataList, fromPosition, toPosition);
        adapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // No swipe action
    }
}
