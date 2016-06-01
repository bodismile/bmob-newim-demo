package cn.bmob.imdemo.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.base.BaseActivity;
import cn.bmob.imdemo.bean.User;
import cn.bmob.imdemo.db.NewFriendManager;
import cn.bmob.imdemo.event.RefreshEvent;
import cn.bmob.imdemo.ui.fragment.ContactFragment;
import cn.bmob.imdemo.ui.fragment.ConversationFragment;
import cn.bmob.imdemo.ui.fragment.SetFragment;
import cn.bmob.imdemo.util.IMMLeaks;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.newim.listener.ObseverListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

/**
 * @author :smile
 * @project:MainActivity
 * @date :2016-01-15-18:23
 */
public class MainActivity extends BaseActivity implements ObseverListener{

    @Bind(R.id.btn_conversation)
    Button btn_conversation;
    @Bind(R.id.btn_set)
    Button btn_set;

    @Bind(R.id.btn_contact)
    Button btn_contact;

    @Bind(R.id.iv_conversation_tips)
    ImageView iv_conversation_tips;

    @Bind(R.id.iv_contact_tips)
    ImageView iv_contact_tips;

    private Button[] mTabs;
    private ConversationFragment conversationFragment;
    private SetFragment setFragment;
    ContactFragment contactFragment;
    private Fragment[] fragments;
    private int index;
    private int currentTabIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //connect server
        User user = BmobUser.getCurrentUser(this,User.class);
        BmobIM.connect(user.getObjectId(), new ConnectListener() {
            @Override
            public void done(String uid, BmobException e) {
                if (e == null) {
                    Logger.i("connect success");
                    //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                    EventBus.getDefault().post(new RefreshEvent());
                } else {
                    Logger.e(e.getErrorCode() + "/" + e.getMessage());
                }
            }
        });
        //监听连接状态，也可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
            @Override
            public void onChange(ConnectionStatus status) {
                toast("" + status.getMsg());
            }
        });
        //解决leancanary提示InputMethodManager内存泄露的问题
        IMMLeaks.fixFocusedViewLeak(getApplication());
    }

    @Override
    protected void initView() {
        super.initView();
        mTabs = new Button[3];
        mTabs[0] = btn_conversation;
        mTabs[1] = btn_contact;
        mTabs[2] =btn_set;
        mTabs[0].setSelected(true);
        initTab();
    }

    private void initTab(){
        conversationFragment = new ConversationFragment();
        setFragment = new SetFragment();
        contactFragment=new ContactFragment();
        fragments = new Fragment[] {conversationFragment, contactFragment,setFragment};
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, conversationFragment).
                add(R.id.fragment_container, contactFragment)
                .add(R.id.fragment_container, setFragment)
                .hide(setFragment).hide(contactFragment)
                .show(conversationFragment).commit();
    }

    public void onTabSelect(View view) {
        switch (view.getId()) {
            case R.id.btn_conversation:
                index = 0;
                break;
            case R.id.btn_contact:
                index = 1;
                break;
            case R.id.btn_set:
                index = 2;
                break;
        }
        onTabIndex(index);
    }

    private void onTabIndex(int index){
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        mTabs[currentTabIndex].setSelected(false);
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //显示小红点
        checkRedPoint();
        //进入应用后，通知栏应取消
        BmobNotificationManager.getInstance(this).cancelNotification();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清理导致内存泄露的资源
        BmobIM.getInstance().clear();
    }

    /**注册消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(MessageEvent event){
        checkRedPoint();
    }

    /**注册离线消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event){
        checkRedPoint();
    }

    /**注册自定义消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(RefreshEvent event){
        log("---主页接收到自定义消息---");
        checkRedPoint();
    }

    private void checkRedPoint(){
        int count = (int)BmobIM.getInstance().getAllUnReadCount();
        if(count>0){
            iv_conversation_tips.setVisibility(View.VISIBLE);
        }else{
            iv_conversation_tips.setVisibility(View.GONE);
        }
        //是否有好友添加的请求
        if(NewFriendManager.getInstance(this).hasNewFriendInvitation()){
            iv_contact_tips.setVisibility(View.VISIBLE);
        }else{
            iv_contact_tips.setVisibility(View.GONE);
        }
    }

}
