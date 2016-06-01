package cn.bmob.imdemo.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.adapter.base.BaseViewHolder;
import cn.bmob.imdemo.base.ImageLoaderFactory;
import cn.bmob.imdemo.bean.User;
import cn.bmob.imdemo.ui.UserInfoActivity;

public class SearchUserHolder extends BaseViewHolder {

  @Bind(R.id.avatar)
  public ImageView avatar;
  @Bind(R.id.name)
  public TextView name;
  @Bind(R.id.btn_add)
  public Button btn_add;

  public SearchUserHolder(Context context, ViewGroup root,OnRecyclerViewListener onRecyclerViewListener) {
    super(context, root, R.layout.item_search_user,onRecyclerViewListener);
  }

  @Override
  public void bindData(Object o) {
    final User user =(User)o;
    ImageLoaderFactory.getLoader().loadAvator(avatar,user.getAvatar(), R.mipmap.head);
    name.setText(user.getUsername());
    btn_add.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {//查看个人详情
          Bundle bundle = new Bundle();
          bundle.putSerializable("u", user);
          startActivity(UserInfoActivity.class,bundle);
        }
    });
  }
}