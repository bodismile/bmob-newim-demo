package cn.bmob.imdemo.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.adapter.ConversationAdapter;
import cn.bmob.imdemo.adapter.OnRecyclerViewListener;
import cn.bmob.imdemo.adapter.base.IMutlipleItem;
import cn.bmob.imdemo.base.ParentWithNaviActivity;
import cn.bmob.imdemo.base.ParentWithNaviFragment;
import cn.bmob.imdemo.bean.Conversation;
import cn.bmob.imdemo.bean.NewFriendConversation;
import cn.bmob.imdemo.bean.PrivateConversation;
import cn.bmob.imdemo.db.NewFriend;
import cn.bmob.imdemo.db.NewFriendManager;
import cn.bmob.imdemo.event.RefreshEvent;
import cn.bmob.imdemo.ui.SearchUserActivity;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;

/**会话界面
 * @author :smile
 * @project:ConversationFragment
 * @date :2016-01-25-18:23
 */
public class ConversationFragment extends ParentWithNaviFragment {

    @Bind(R.id.rc_view)
    RecyclerView rc_view;
    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout sw_refresh;
    ConversationAdapter adapter;
    LinearLayoutManager layoutManager;

    @Override
    protected String title() {
        return "会话";
    }

    @Override
    public Object right() {
        return R.drawable.base_action_bar_add_bg_selector;
    }

    @Override
    public ParentWithNaviActivity.ToolBarListener setToolBarListener() {
        return new ParentWithNaviActivity.ToolBarListener() {
            @Override
            public void clickLeft() {

            }

            @Override
            public void clickRight() {
                startActivity(SearchUserActivity.class,null);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView =inflater.inflate(R.layout.fragment_conversation, container, false);
        initNaviView();
        ButterKnife.bind(this, rootView);
        //单一布局
        IMutlipleItem<Conversation> mutlipleItem = new IMutlipleItem<Conversation>() {

            @Override
            public int getItemViewType(int postion, Conversation c) {
                return 0;
            }

            @Override
            public int getItemLayoutId(int viewtype) {
                return R.layout.item_conversation;
            }

            @Override
            public int getItemCount(List<Conversation> list) {
                return list.size();
            }
        };
        adapter = new ConversationAdapter(getActivity(),mutlipleItem,null);
        rc_view.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getActivity());
        rc_view.setLayoutManager(layoutManager);
        sw_refresh.setEnabled(true);
        setListener();
        return rootView;
    }

    private void setListener(){
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                sw_refresh.setRefreshing(true);
                query();
            }
        });
        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query();
            }
        });
        adapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                adapter.getItem(position).onClick(getActivity());
            }

            @Override
            public boolean onItemLongClick(int position) {
                adapter.getItem(position).onLongClick(getActivity());
                adapter.remove(position);
                return true;
            }
        });
}

    @Override
    public void onResume() {
        super.onResume();
        sw_refresh.setRefreshing(true);
        query();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
      查询本地会话
     */
    public void query(){
//        adapter.bindDatas(BmobIM.getInstance().loadAllConversation());
        adapter.bindDatas(getConversations());
        adapter.notifyDataSetChanged();
        sw_refresh.setRefreshing(false);
    }

    /**
     * 获取会话列表的数据：增加新朋友会话
     * @return
     */
    private List<Conversation> getConversations(){
        //添加会话
        List<Conversation> conversationList = new ArrayList<>();
        conversationList.clear();
        List<BmobIMConversation> list =BmobIM.getInstance().loadAllConversation();
        if(list!=null && list.size()>0){
            for (BmobIMConversation item:list){
                switch (item.getConversationType()){
                    case 1://私聊
                        conversationList.add(new PrivateConversation(item));
                        break;
                    default:
                        break;
                }
            }
        }
        //添加新朋友会话-获取好友请求表中最新一条记录
        List<NewFriend> friends = NewFriendManager.getInstance(getActivity()).getAllNewFriend();
        if(friends!=null && friends.size()>0){
            conversationList.add(new NewFriendConversation(friends.get(0)));
        }
        //重新排序
        Collections.sort(conversationList);
        return conversationList;
    }

    /**注册自定义消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(RefreshEvent event){
        log("---会话页接收到自定义消息---");
        //因为新增`新朋友`这种会话类型
        adapter.bindDatas(getConversations());
        adapter.notifyDataSetChanged();
    }

    /**注册离线消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event){
        //重新刷新列表
        adapter.bindDatas(getConversations());
        adapter.notifyDataSetChanged();
    }

    /**注册消息接收事件
     * @param event
     * 1、与用户相关的由开发者自己维护，SDK内部只存储用户信息
     * 2、开发者获取到信息后，可调用SDK内部提供的方法更新会话
     */
    @Subscribe
    public void onEventMainThread(MessageEvent event){
        //重新获取本地消息并刷新列表
        adapter.bindDatas(getConversations());
        adapter.notifyDataSetChanged();
    }
}
