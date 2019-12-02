package com.tn3112.nvt.youtubeaudioplayer.listeners;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

// Copypasted from https://gist.github.com/nesquena/d09dc68ff07e845cc622
public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    private int visibleThreshold = 30;
    //Tổng số mục trong tập dữ liệu sau lần tải cuối cùng
    private int previousTotalItemCount = 0;
    // Đúng nếu chúng ta vẫn đang chờ bộ dữ liệu cuối cùng tải.
    private boolean loading = true;

    private RecyclerView.LayoutManager mLayoutManager;

    protected EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
    }

    public EndlessRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
    }

    private int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }


    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        int lastVisibleItemPosition = 0;
        int totalItemCount = mLayoutManager.getItemCount();

        if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
            // get maximum element within the list
            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
        } else if (mLayoutManager instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        } else if (mLayoutManager instanceof LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        }


        if (totalItemCount < previousTotalItemCount) {
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        // Nếu nó hiện đang tải, chúng tôi kiểm tra xem chúng tôi có vi phạm không
 // các visibleThreshold và cần phải tải lại dữ liệu hơn.
 // Nếu chúng tôi cần tải lại một số dữ liệu, chúng tôi sẽ thực thi onLoadMore để lấy dữ liệu.
 // ngưỡng sẽ phản ánh tổng số cột có quá nhiều
        if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
            onLoadMore(totalItemCount, view);
            loading = true;
        }
    }


    public void resetState() {
        this.previousTotalItemCount = 0;
        this.loading = true;
    }

    public abstract void onLoadMore(int totalItemsCount, RecyclerView view);
}
