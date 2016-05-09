package cn.bmob.imdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;

/**
 * @author :smile
 * @project:ConversationAdapter
 * @date :2016-01-22-14:18
 */
public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<BmobIMConversation> conversations = new ArrayList<>();

    public ConversationAdapter() {}

    /**
     * @param list
     */
    public void bindDatas(List<BmobIMConversation> list) {
        conversations.clear();
        if (null != list) {
            conversations.addAll(list);
        }
    }

    /**移除会话
     * @param position
     */
    public void remove(int position){
        conversations.remove(position);
        notifyDataSetChanged();
    }

    /**获取会话
     * @param position
     * @return
     */
    public BmobIMConversation getItem(int position){
        return conversations.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ConversationHolder(parent.getContext(), parent,onRecyclerViewListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BaseViewHolder)holder).bindData(conversations.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }


    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

}
