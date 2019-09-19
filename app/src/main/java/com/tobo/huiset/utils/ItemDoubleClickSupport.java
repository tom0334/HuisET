package com.tobo.huiset.utils;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.tobo.huiset.R;


/**
 * Created by Hugo Visser.
 * Please see http://www.littlerobots.nl/blog/Handle-Android-RecyclerView-Clicks/
 *
 * Modified by Filippo Beraldo:
 * - Added OnDoubleClickListener in place of the standard OnClickListener.
 */

public class ItemDoubleClickSupport {
    private final RecyclerView mRecyclerView;
    private OnItemClickListener mOnItemClickListener;
    private OnDoubleClickListener mOnDoubleClickListener = new OnDoubleClickListener() {
        @Override
        public void onDoubleClick(View v) {
            if (mOnItemClickListener != null) {
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                mOnItemClickListener.onItemDoubleClicked(mRecyclerView, holder.getAdapterPosition(), v);
            }
        }

        @Override
        public void onSingleClick(View v) {
            if (mOnItemClickListener != null) {
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                mOnItemClickListener.onItemClicked(mRecyclerView, holder.getAdapterPosition(), v);
            }
        }
    };

    private RecyclerView.OnChildAttachStateChangeListener mAttachListener
            = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(View view) {
            if (mOnItemClickListener != null) {
                view.setOnClickListener(mOnDoubleClickListener);
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {

        }
    };

    private ItemDoubleClickSupport(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mRecyclerView.setTag(R.id.item_click_support, this);
        mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener);
    }

    public static ItemDoubleClickSupport addTo(RecyclerView view) {
        ItemDoubleClickSupport support = (ItemDoubleClickSupport) view.getTag(R.id.item_click_support);
        if (support == null) {
            support = new ItemDoubleClickSupport(view);
        }
        return support;
    }

    public static ItemDoubleClickSupport removeFrom(RecyclerView view) {
        ItemDoubleClickSupport support = (ItemDoubleClickSupport) view.getTag(R.id.item_click_support);
        if (support != null) {
            support.detach(view);
        }
        return support;
    }

    public ItemDoubleClickSupport setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
        return this;
    }

    private void detach(RecyclerView view) {
        view.removeOnChildAttachStateChangeListener(mAttachListener);
        view.setTag(R.id.item_click_support, null);
    }

    public interface OnItemClickListener {
        void onItemClicked(RecyclerView recyclerView, int position, View v);
        void onItemDoubleClicked(RecyclerView recyclerView, int position, View v);
    }
}