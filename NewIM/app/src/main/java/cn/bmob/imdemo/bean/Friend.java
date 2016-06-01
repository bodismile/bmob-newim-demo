package cn.bmob.imdemo.bean;

import cn.bmob.v3.BmobObject;

/**好友表
 * @author smile
 * @project Friend
 * @date 2016-04-26
 */
public class Friend extends BmobObject{

    private User user;
    private User friendUser;

    //拼音
    private transient String pinyin;

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

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
