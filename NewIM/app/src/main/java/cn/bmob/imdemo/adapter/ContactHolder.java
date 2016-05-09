package cn.bmob.imdemo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.bean.Friend;
import cn.bmob.imdemo.bean.User;
import cn.bmob.imdemo.util.TimeUtil;
import cn.bmob.imdemo.util.ViewUtil;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;

public class ContactHolder extends BaseViewHolder {

  @Bind(R.id.iv_recent_avatar)
  public ImageView iv_recent_avatar;
  @Bind(R.id.tv_recent_name)
  public TextView tv_recent_name;

  public ContactHolder(Context context, ViewGroup root, OnRecyclerViewListener onRecyclerViewListener) {
    super(context, root, R.layout.item_contact,onRecyclerViewListener);
  }

  @Override
  public void bindData(Object o) {
      Friend friend =(Friend)o;
      User user =friend.getFriendUser();
      //会话图标
      ViewUtil.setAvatar(user==null?null:user.getAvatar(), R.mipmap.head, iv_recent_avatar);
      //会话标题
      tv_recent_name.setText(user==null?"未知":user.getUsername());
  }

}