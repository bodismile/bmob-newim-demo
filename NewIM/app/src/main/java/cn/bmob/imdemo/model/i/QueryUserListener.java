package cn.bmob.imdemo.model.i;

import cn.bmob.imdemo.bean.User;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobListener;

/**
 * @author :smile
 * @project:QueryUserListener
 * @date :2016-02-01-16:23
 */
public abstract class QueryUserListener extends BmobListener<User> {

    public abstract void done(User s, BmobException e);

    @Override
    protected void postDone(User o, BmobException e) {
        done(o, e);
    }
}
