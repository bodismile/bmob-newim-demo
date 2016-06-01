package cn.bmob.imdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.imdemo.Config;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.adapter.base.BaseRecyclerAdapter;
import cn.bmob.imdemo.adapter.base.BaseRecyclerHolder;
import cn.bmob.imdemo.adapter.base.BaseViewHolder;
import cn.bmob.imdemo.adapter.base.IMutlipleItem;
import cn.bmob.imdemo.base.ImageLoaderFactory;
import cn.bmob.imdemo.bean.AgreeAddFriendMessage;
import cn.bmob.imdemo.bean.Friend;
import cn.bmob.imdemo.bean.User;
import cn.bmob.imdemo.db.NewFriend;
import cn.bmob.imdemo.db.NewFriendManager;
import cn.bmob.imdemo.model.UserModel;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author :smile
 * @project:NewFriendAdapter
 * @date :2016-04-27-14:18
 */
public class NewFriendAdapter extends BaseRecyclerAdapter<NewFriend>{

    public NewFriendAdapter(Context context, IMutlipleItem<NewFriend> items, Collection<NewFriend> datas) {
        super(context,items,datas);
    }

    @Override
    public void bindView(final BaseRecyclerHolder holder, final NewFriend add, int position) {
        holder.setImageView(add == null ? null : add.getAvatar(), R.mipmap.head, R.id.iv_recent_avatar);
        holder.setText(R.id.tv_recent_name,add==null?"未知":add.getName());
        holder.setText(R.id.tv_recent_msg,add==null?"未知":add.getMsg());
        Integer status =add.getStatus();
        Logger.i("bindData:"+status+","+add.getUid()+","+add.getTime());
        if(status==null || status== Config.STATUS_VERIFY_NONE||status ==Config.STATUS_VERIFY_READED){//未添加/已读未添加
            holder.setText(R.id.btn_aggree,"接受");
            holder.setEnabled(R.id.btn_aggree,true);
            holder.setOnClickListener(R.id.btn_aggree,new View.OnClickListener() {
                @Override
                public void onClick(View v) {//发送消息
                    agreeAdd(add, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            holder.setText(R.id.btn_aggree,"已添加");
                            holder.setEnabled(R.id.btn_aggree,false);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            holder.setEnabled(R.id.btn_aggree,true);
                            toast("添加好友失败:" + s);
                        }
                    });
                }
            });
        }else{
            holder.setText(R.id.btn_aggree,"已添加");
            holder.setEnabled(R.id.btn_aggree,false);
        }
    }

    /**
     * 添加到好友表中...
     * @param add
     * @param listener
     */
    private void agreeAdd(final NewFriend add, final SaveListener listener){
        User user =new User();
        user.setObjectId(add.getUid());
        UserModel.getInstance().agreeAddFriend(user, new SaveListener() {
            @Override
            public void onSuccess() {
                sendAgreeAddFriendMessage(add, listener);
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onFailure(i, s);
            }
        });
    }

    /**
     * 发送同意添加好友的请求
     */
    private void sendAgreeAddFriendMessage(final NewFriend add,final SaveListener listener){
        BmobIMUserInfo info = new BmobIMUserInfo(add.getUid(), add.getName(), add.getAvatar());
        //如果为true,则表明为暂态会话，也就是说该会话仅执行发送消息的操作，不会保存会话和消息到本地数据库中
        BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info,true,null);
        //这个obtain方法才是真正创建一个管理消息发送的会话
        BmobIMConversation conversation = BmobIMConversation.obtain(BmobIMClient.getInstance(),c);
        //而AgreeAddFriendMessage的isTransient设置为false，表明我希望在对方的会话数据库中保存该类型的消息
        AgreeAddFriendMessage msg =new AgreeAddFriendMessage();
        User currentUser = BmobUser.getCurrentUser(context, User.class);
        msg.setContent("我通过了你的好友验证请求，我们可以开始聊天了!");//---这句话是直接存储到对方的消息表中的
        Map<String,Object> map =new HashMap<>();
        map.put("msg",currentUser.getUsername()+"同意添加你为好友");//显示在通知栏上面的内容
        map.put("uid",add.getUid());//发送者的uid-方便请求添加的发送方找到该条添加好友的请求
        map.put("time", add.getTime());//添加好友的请求时间
        msg.setExtraMap(map);
        conversation.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage msg, BmobException e){
                if (e == null) {//发送成功
                    //修改本地的好友请求记录
                    NewFriendManager.getInstance(context).updateNewFriend(add,Config.STATUS_VERIFIED);
                    listener.onSuccess();
                } else {//发送失败
                    listener.onFailure(e.getErrorCode(),e.getMessage());
                }
            }
        });
    }
}
