package cn.bmob.imdemo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.bean.AddFriendMessage;
import cn.bmob.imdemo.db.NewFriendManager;
import cn.bmob.imdemo.util.ViewUtil;

/**
 * 新朋友按钮
 */
public class ContactNewFriendHolder extends BaseViewHolder {

  @Bind(R.id.iv_msg_tips)
  public ImageView iv_msg_tips;

  public ContactNewFriendHolder(Context context, ViewGroup root, OnRecyclerViewListener onRecyclerViewListener) {
    super(context, root, R.layout.header_new_friend,onRecyclerViewListener);
  }

  @Override
  public void bindData(Object o) {
    //是否有好友添加的请求
    if(NewFriendManager.getInstance(getContext()).hasNewFriendInvitation()){
        iv_msg_tips.setVisibility(View.VISIBLE);
    }else{
        iv_msg_tips.setVisibility(View.GONE);
    }
  }

}