package cn.bmob.imdemo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.adapter.base.BaseViewHolder;
import cn.bmob.imdemo.base.ImageLoaderFactory;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMImageMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMSendStatus;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;

/**
 * 发送的文本类型
 */
public class SendImageHolder extends BaseViewHolder {

  @Bind(R.id.iv_avatar)
  protected ImageView iv_avatar;

  @Bind(R.id.iv_fail_resend)
  protected ImageView iv_fail_resend;

  @Bind(R.id.tv_time)
  protected TextView tv_time;

  @Bind(R.id.iv_picture)
  protected ImageView iv_picture;

  @Bind(R.id.tv_send_status)
  protected TextView tv_send_status;

  @Bind(R.id.progress_load)
  protected ProgressBar progress_load;
  BmobIMConversation c;

  public SendImageHolder(Context context, ViewGroup root,BmobIMConversation c,OnRecyclerViewListener onRecyclerViewListener) {
    super(context, root, R.layout.item_chat_sent_image,onRecyclerViewListener);
    this.c =c;
  }

  @Override
  public void bindData(Object o) {
    BmobIMMessage msg = (BmobIMMessage)o;
    //用户信息的获取必须在buildFromDB之前，否则会报错'Entity is detached from DAO context'
    final BmobIMUserInfo info = msg.getBmobIMUserInfo();
    ImageLoaderFactory.getLoader().loadAvator(iv_avatar,info != null ? info.getAvatar() : null,R.mipmap.head);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    String time = dateFormat.format(msg.getCreateTime());
    tv_time.setText(time);
    //
    final BmobIMImageMessage message = BmobIMImageMessage.buildFromDB(true, msg);
    int status =message.getSendStatus();
    if (status == BmobIMSendStatus.SENDFAILED.getStatus() ||status == BmobIMSendStatus.UPLOADAILED.getStatus()) {
      iv_fail_resend.setVisibility(View.VISIBLE);
      progress_load.setVisibility(View.GONE);
      tv_send_status.setVisibility(View.INVISIBLE);
    } else if (status== BmobIMSendStatus.SENDING.getStatus()) {
      progress_load.setVisibility(View.VISIBLE);
      iv_fail_resend.setVisibility(View.GONE);
      tv_send_status.setVisibility(View.INVISIBLE);
    } else {
      tv_send_status.setVisibility(View.VISIBLE);
      tv_send_status.setText("已发送");
      iv_fail_resend.setVisibility(View.GONE);
      progress_load.setVisibility(View.GONE);
    }

    //发送的不是远程图片地址，则取本地地址
    ImageLoaderFactory.getLoader().load(iv_picture,TextUtils.isEmpty(message.getRemoteUrl()) ? message.getLocalPath():message.getRemoteUrl(),R.mipmap.ic_launcher,null);
//    ViewUtil.setPicture(TextUtils.isEmpty(message.getRemoteUrl()) ? message.getLocalPath():message.getRemoteUrl(), R.mipmap.ic_launcher, iv_picture,null);

    iv_avatar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        toast("点击" + info.getName() + "的头像");
      }
    });
    iv_picture.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        toast("点击图片:"+(TextUtils.isEmpty(message.getRemoteUrl()) ? message.getLocalPath():message.getRemoteUrl())+"");
        if(onRecyclerViewListener!=null){
          onRecyclerViewListener.onItemClick(getAdapterPosition());
        }
      }
    });

    iv_picture.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        if (onRecyclerViewListener != null) {
          onRecyclerViewListener.onItemLongClick(getAdapterPosition());
        }
        return true;
      }
    });

    //重发
    iv_fail_resend.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        c.resendMessage(message, new MessageSendListener() {
          @Override
          public void onStart(BmobIMMessage msg) {
            progress_load.setVisibility(View.VISIBLE);
            iv_fail_resend.setVisibility(View.GONE);
            tv_send_status.setVisibility(View.INVISIBLE);
          }

          @Override
          public void done(BmobIMMessage msg, BmobException e) {
            if (e == null) {
              tv_send_status.setVisibility(View.VISIBLE);
              tv_send_status.setText("已发送");
              iv_fail_resend.setVisibility(View.GONE);
              progress_load.setVisibility(View.GONE);
            } else {
              iv_fail_resend.setVisibility(View.VISIBLE);
              progress_load.setVisibility(View.GONE);
              tv_send_status.setVisibility(View.INVISIBLE);
            }
          }
        });
      }
    });
  }

  public void showTime(boolean isShow) {
    tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
  }
}
