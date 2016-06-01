package cn.bmob.imdemo.adapter.base;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.bmob.imdemo.base.ImageLoaderFactory;

/**
 * 与BaseRecyclerAdapter一起使用
 *
 */
public class BaseRecyclerHolder extends RecyclerView.ViewHolder {

    private final SparseArray<View> mViews;
    public  int layoutId;

    public BaseRecyclerHolder(int layoutId,View itemView) {
        super(itemView);
        this.layoutId =layoutId;
        this.mViews = new SparseArray<>(8);
    }

    public SparseArray<View> getAllView() {
        return mViews;
    }

    /**
     * @param viewId
     * @return
     */
    protected <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * @param viewId
     * @param text
     * @return
     */
    public BaseRecyclerHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    /**
     * 设置Enabled
     * @param viewId
     * @param enable
     * @return
     */
    public BaseRecyclerHolder setEnabled(int viewId,boolean enable){
        View v = getView(viewId);
        v.setEnabled(enable);
        return this;
    }

    /**
     * 点击事件
     * @param viewId
     * @param listener
     * @return
     */
    public BaseRecyclerHolder setOnClickListener(int viewId, View.OnClickListener listener){
        View v = getView(viewId);
        v.setOnClickListener(listener);
        return this;
    }

    /**
     * @param viewId
     * @param visibility
     * @return
     */
    public BaseRecyclerHolder setVisible(int viewId,int visibility) {
        View view = getView(viewId);
        view.setVisibility(visibility);
        return this;
    }

    /**
     * @param viewId
     * @param drawableId
     * @return
     */
    public BaseRecyclerHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    /**
     * @param viewId
     * @param bm
     * @return
     */
    public BaseRecyclerHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    /**
     * @param avatar
     * @param defaultRes
     * @param viewId
     * @return
     */
    public BaseRecyclerHolder setImageView(String avatar, int defaultRes, int viewId) {
        ImageView iv = getView(viewId);
        ImageLoaderFactory.getLoader().loadAvator(iv,avatar, defaultRes);
        return this;
    }
}