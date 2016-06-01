package cn.bmob.imdemo.adapter.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.bmob.imdemo.adapter.OnRecyclerViewListener;
import cn.bmob.imdemo.base.BaseActivity;


/**
 * 支持添加自定义头部布局；
 * 支持扩展多种item布局；
 * 支持设置recycleview点击/长按事件
 * @param <T>
 * @author smile
 * @link https://github.com/bodismile/BaseRecyclerAdapter
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerHolder> {
    /**
     * 默认布局
     */
    private final int TYPE_DEFAULT = 0;
    /**
     * 当list没有值得时候显示的布局
     */
    private final int TYPE_HEADER = 1;
    /**
     * 多重布局
     */
    private final int TYPE_MUTIPLE = 2;
    /**
     * 带header的多重布局
     */
    private final int TYPE_MUTIPLE_HEADER = 3;

    protected final Context context;
    protected List<T> lists;
    protected IMutlipleItem<T> items;
    protected OnRecyclerViewListener listener;

    /**
     * 支持一种或多种Item布局
     *
     * @param context
     * @param items
     * @param datas
     */
    public BaseRecyclerAdapter(Context context, IMutlipleItem<T> items, Collection<T> datas) {
        this.context = context;
        this.items = items;
        this.lists = datas == null ? new ArrayList<T>() : new ArrayList<T>(datas);
    }

    /**
     * 绑定数据
     * @param datas
     * @return
     */
    public BaseRecyclerAdapter<T> bindDatas(Collection<T> datas) {
        this.lists = datas == null ? new ArrayList<T>() : new ArrayList<T>(datas);
        notifyDataSetChanged();
        return this;
    }

    /**
     * 删除数据
     * @param position
     */
    public void remove(int position) {
        int more = getItemCount() - lists.size();
        lists.remove(position - more);
        notifyDataSetChanged();
    }

    /**
     * 获取指定position的Item
     * @param position
     * @return
     */
    public T getItem(int position) {
        int more = getItemCount() - lists.size();
        return lists.get(position - more);
    }

    @Override
    public BaseRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = items.getItemLayoutId(viewType);
        LayoutInflater inflater = LayoutInflater.from(context);
        View root = inflater.inflate(layoutId, parent, false);
        return new BaseRecyclerHolder(layoutId, root);
    }

    @Override
    public void onBindViewHolder(BaseRecyclerHolder holder, int position) {
        int type = getViewTypeByPosition(position);
        if(type==TYPE_HEADER){
            bindView(holder, null, position);
        }else if(type==TYPE_MUTIPLE){
            bindView(holder, lists.get(position), position);
        }else if(type==TYPE_MUTIPLE_HEADER){
            int headerCount = getItemCount() - lists.size();
            bindView(holder, lists.get(position - headerCount), position);
        }else{
            bindView(holder, null, position);
        }
        holder.itemView.setOnClickListener(getOnClickListener(position));
        holder.itemView.setOnLongClickListener(getOnLongClickListener(position));
    }

    @Override
    public int getItemCount() {
        if (items != null) {//当有多重布局的时候，则采用多重布局
            return items.getItemCount(lists);
        }
        return lists.size();
    }

    @Override
    public int getItemViewType(int position) {
        int type = getViewTypeByPosition(position);
        if(type==TYPE_HEADER){
            return items.getItemViewType(position, null);
        }else if(type==TYPE_MUTIPLE){
            return items.getItemViewType(position, lists.get(position));
        }else if(type==TYPE_MUTIPLE_HEADER){
            int headerCount = getItemCount() - lists.size();
            return items.getItemViewType(position, lists.get(position - headerCount));
        }else{
            return 0;
        }
    }

    /**获取指定position的布局类型
     * @param position
     */
    private int getViewTypeByPosition(int position) {
        if (items == null) {//默认布局
            return TYPE_DEFAULT;
        } else {//多布局
            if (lists != null && lists.size() > 0) {//list有值的时候
                if (getItemCount() > lists.size()) {//是否有自定义的Header
                    int headerCount = getItemCount() - lists.size();
                    if (position >= headerCount) {//当前位置大于header个数
                        return TYPE_MUTIPLE_HEADER;
                    } else {//当前点击的是header
                        return TYPE_HEADER;
                    }
                } else {
                    return TYPE_MUTIPLE;
                }
            } else {//list还没有值的时候
                return TYPE_HEADER;
            }
        }
    }

    /**
     * 设置点击/长按等事件监听器
     * @param onRecyclerViewListener
     */
    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.listener = onRecyclerViewListener;
    }

    public View.OnClickListener getOnClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && v != null) {
                    listener.onItemClick(position);
                }
            }
        };
    }

    public View.OnLongClickListener getOnLongClickListener(final int position) {
        return new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (listener != null && v != null) {
                    listener.onItemLongClick(position);
                }
                return true;
            }
        };
    }

    /**
     * 需实现此方法
     * @param holder
     * @param item
     */
    public abstract void bindView(BaseRecyclerHolder holder, T item, int position);

    private Toast toast;
    public void toast(final Object obj) {
        try {
            ((BaseActivity)context).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (toast == null)
                        toast = Toast.makeText(context,"", Toast.LENGTH_SHORT);
                    toast.setText(obj.toString());
                    toast.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}