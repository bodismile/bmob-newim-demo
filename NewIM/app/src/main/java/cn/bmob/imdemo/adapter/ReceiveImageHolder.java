package cn.bmob.imdemo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.adapter.base.BaseViewHolder;
import cn.bmob.imdemo.base.ImageLoaderFactory;
import cn.bmob.newim.bean.BmobIMImageMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;

/**
 * 接收到的文本类型
 */
public class ReceiveImageHolder extends BaseViewHolder {

  @Bind(R.id.iv_avatar)
  protected ImageView iv_avatar;

  @Bind(R.id.tv_time)
  protected TextView tv_time;

  @Bind(R.id.iv_picture)
  protected ImageView iv_picture;
  @Bind(R.id.progress_load)
  protected ProgressBar progress_load;

  public ReceiveImageHolder(Context context, ViewGroup root,OnRecyclerViewListener onRecyclerViewListener) {
    super(context, root, R.layout.item_chat_received_image,onRecyclerViewListener);
  }

  @Override
  public void bindData(Object o) {
    BmobIMMessage msg = (BmobIMMessage)o;
    //用户信息的获取必须在buildFromDB之前，否则会报错'Entity is detached from DAO context'
    final BmobIMUserInfo info = msg.getBmobIMUserInfo();
    ImageLoaderFactory.getLoader().loadAvator(iv_avatar,info != null ? info.getAvatar() : null, R.mipmap.head);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    String time = dateFormat.format(msg.getCreateTime());
    tv_time.setText(time);
    //可使用buildFromDB方法转化为指定类型的消息
    final BmobIMImageMessage message = BmobIMImageMessage.buildFromDB(false,msg);
    //显示图片
    ImageLoaderFactory.getLoader().load(iv_picture,message.getRemoteUrl(),  R.mipmap.ic_launcher,new ImageLoadingListener(){;

    @Override
      public void onLoadingStarted(String s, View view) {
        progress_load.setVisibility(View.VISIBLE);
      }

      @Override
      public void onLoadingComplete(String s, View view, Bitmap bitmap) {
        progress_load.setVisibility(View.INVISIBLE);
      }

      @Override
      public void onLoadingCancelled(String s, View view) {
        progress_load.setVisibility(View.INVISIBLE);
      }

      @Override
      public void onLoadingFailed(String s, View view, FailReason failReason) {
        progress_load.setVisibility(View.INVISIBLE);
      }
    });

    iv_avatar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        toast("点击" + info.getName() + "的头像");
      }
    });

    iv_picture.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        toast("点击图片:"+message.getRemoteUrl()+"");
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

  }

  public void showTime(boolean isShow) {
    tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
  }
}