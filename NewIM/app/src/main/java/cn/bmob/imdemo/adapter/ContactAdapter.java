package cn.bmob.imdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import cn.bmob.imdemo.bean.Friend;
import cn.bmob.imdemo.bean.User;
import cn.bmob.newim.bean.BmobIMConversation;

/**
 * @author :smile
 * @project:ContactAdapter
 * @date :2016-04-27-14:18
 */
public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int TYPE_NEW_FRIEND = 0;
    private final int TYPE_ITEM = 1;

    private List<Friend> friends = new ArrayList<>();

    public ContactAdapter() {}

    /**
     * @param list
     */
    public void bindDatas(List<Friend> list) {
        friends.clear();
        if (null != list) {
            friends.addAll(list);
        }
    }

    /**移除会话
     * @param position
     */
    public void remove(int position){
        friends.remove(position-1);
        notifyDataSetChanged();
    }

    /**获取好友
     * @param position
     * @return
     */
    public Friend getItem(int position){
        return friends.get(position-1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NEW_FRIEND) {
            return new ContactNewFriendHolder(parent.getContext(), parent, onRecyclerViewListener);
        } else {
            return new ContactHolder(parent.getContext(), parent, onRecyclerViewListener);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContactHolder) {
            ((BaseViewHolder)holder).bindData(getItem(position));
        } else if(holder instanceof ContactNewFriendHolder){
            ((BaseViewHolder)holder).bindData(null);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return TYPE_NEW_FRIEND;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return friends.size()+1;
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

}
