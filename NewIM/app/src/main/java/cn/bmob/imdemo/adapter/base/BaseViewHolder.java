package cn.bmob.imdemo.adapter.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import cn.bmob.imdemo.adapter.OnRecyclerViewListener;
import cn.bmob.imdemo.base.BaseActivity;

/**
 * 建议使用BaseRecyclerAdapter
 * @param <T>
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

  public OnRecyclerViewListener onRecyclerViewListener;
  protected Context context;

  public BaseViewHolder(Context context, ViewGroup root, int layoutRes,OnRecyclerViewListener listener) {
    super(LayoutInflater.from(context).inflate(layoutRes, root, false));
    this.context=context;
    ButterKnife.bind(this, itemView);
    this.onRecyclerViewListener =listener;
    itemView.setOnClickListener(this);
    itemView.setOnLongClickListener(this);
  }

  public Context getContext() {
    return itemView.getContext();
  }

  public abstract void bindData(T t);

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

  @Override
  public void onClick(View v) {
    if(onRecyclerViewListener!=null){
      onRecyclerViewListener.onItemClick(getAdapterPosition());
    }
  }

  @Override
  public boolean onLongClick(View v) {
    if(onRecyclerViewListener!=null){
      onRecyclerViewListener.onItemLongClick(getAdapterPosition());
    }
    return true;
  }

  /**启动指定Activity
   * @param target
   * @param bundle
   */
  public void startActivity(Class<? extends Activity> target, Bundle bundle) {
    Intent intent = new Intent();
    intent.setClass(getContext(), target);
    if (bundle != null)
      intent.putExtra(getContext().getPackageName(), bundle);
    getContext().startActivity(intent);
  }

}