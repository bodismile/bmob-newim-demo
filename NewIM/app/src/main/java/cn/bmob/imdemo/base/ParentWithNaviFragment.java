package cn.bmob.imdemo.base;

import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.bmob.imdemo.R;
import cn.bmob.imdemo.base.ParentWithNaviActivity.ToolBarListener;

/**封装了导航条的Fragment类均需继承该类
 * @author :smile
 * @project:ParentWithNaviFragment
 * @date :2015-08-18-14:19
 */
public abstract class ParentWithNaviFragment extends BaseFragment {

    protected View rootView = null;
    private ToolBarListener listener;
    private TextView tv_title;
    public TextView tv_right;
    public ImageView tv_left;
    public LinearLayout ll_navi;

    /**
     * 初始化导航条
     */
    public void initNaviView(){
        tv_title = getView(R.id.tv_title);
        tv_right = getView(R.id.tv_right);
        tv_left = getView(R.id.tv_left);
        setListener(setToolBarListener());
        tv_left.setOnClickListener(clickListener);
        tv_right.setOnClickListener(clickListener);
        tv_title.setText(title());
        refreshTop();
    }

    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_left:
                    if (listener != null){
                        listener.clickLeft();
                    }
                    break;
                case R.id.tv_right:
                    if (listener != null)
                        listener.clickRight();
                    break;

                default:
                    break;
            }
        }
    };

    private void refreshTop() {
        setLeftView(left());
        setValue(R.id.tv_right, right());
        this.tv_title.setText(title());
    }

    private void setLeftView(Object obj){
        if(obj !=null && !obj.equals("")){
            tv_left.setVisibility(View.VISIBLE);
            if(obj instanceof Integer){
                tv_left.setImageResource(Integer.parseInt(obj.toString()));
            }else{
                tv_left.setImageResource(R.drawable.base_action_bar_back_bg_selector);
            }
        }else{
            tv_left.setVisibility(View.INVISIBLE);
        }
    }

    private void setValue(int id,Object obj){
        if (obj != null && !obj.equals("")) {
            ((TextView) getView(id)).setText("");
            getView(id).setBackgroundDrawable(new BitmapDrawable());
            if (obj instanceof String) {
                ((TextView) getView(id)).setText(obj.toString());
            } else if (obj instanceof Integer) {
                getView(id).setBackgroundResource(Integer.parseInt(obj.toString()));
            }
        } else {
            ((TextView) getView(id)).setText("");
            getView(id).setBackgroundDrawable(new BitmapDrawable());
        }
    }

    private void setListener(ToolBarListener listener) {
        this.listener = listener;
    }

    /**导航栏标题
     * @return
     */
    protected abstract String title();

    /**导航栏右边：可以为string或图片资源id，不是必填项
     * @return
     */
    public  Object right(){
        return null;
    }

    /**导航栏左边
     * @return
     */
    public Object left(){return null;}

    /**设置导航条背景色
     * @param color
     */
    public void setNavBackground(int color){
        ll_navi.setBackgroundColor(color);
    }

    /**设置右边按钮的文字大小
     * @param dimenId
     */
    public void setRightTextSize(float dimenId){
        tv_right.setTextSize(dimenId);
    }

    /**设置导航栏监听
     * @return
     */
    public ToolBarListener setToolBarListener(){return null;}

    protected <T extends View> T getView(int id) {
        return (T) rootView.findViewById(id);
    }

}
