package cn.bmob.imdemo.bean;

import cn.bmob.v3.BmobObject;

/**
 * 好友表
 * Created by Administrator on 2016/4/26.
 */
public class Friend extends BmobObject{

    private User user;
    private User friendUser;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getFriendUser() {
        return friendUser;
    }

    public void setFriendUser(User friendUser) {
        this.friendUser = friendUser;
    }
}
