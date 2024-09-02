package com.xs.xs_mediaplay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import com.xs.xs_mediaplay.R;
import com.xs.xs_mediaplay.bean.OneCtrlBean;
import com.xs.xs_mediaplay.bean.OneCtrlPage;
import com.xs.xs_mediaplay.databinding.ItemOneCtrlBinding;
import com.xs.xs_mediaplay.dialog.BYTipDialog;
import com.xs.xs_mediaplay.inter.IOneItem;
import kotlin.jvm.functions.Function0;

/**
 * @Author: wuchaowen
 * @Description:
 * @Time:
 **/
public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    private ArrayList<OneCtrlPage> mData;
    private LayoutInflater mInflater;
    private ViewPager2 viewPager2;
    private IOneItem iOneItem;
    private AppCompatActivity activity;

    public ViewPagerAdapter(Context context, ArrayList<OneCtrlPage> data, ViewPager2 viewPager2, IOneItem iOneItem, AppCompatActivity activity) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.viewPager2 = viewPager2;
        this.iOneItem = iOneItem;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_one_ctrl, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerAdapter.ViewHolder holder, int position) {
        OneCtrlPage page = mData.get(position);
//        holder.myTextView.setText(animal);
//        holder.relativeLayout.setBackgroundResource(colorArray[position]);
        holder.binding.setVm(page);
        int size = page.getCtrlBeans().size();
        if (size > 0) {
            holder.binding.item1.setVm(page.getCtrlBeans().get(0));
            loadImage(holder.binding.item1.imgIv, holder.binding.item1.stateSc, page.getCtrlBeans().get(0), 0, position);
        } else  holder.binding.item1.bgLy.setVisibility(View.GONE);
        if (size > 1) {
            holder.binding.item2.setVm(page.getCtrlBeans().get(1));
            loadImage(holder.binding.item2.imgIv, holder.binding.item2.stateSc, page.getCtrlBeans().get(1), 1, position);
        } else  holder.binding.item2.bgLy.setVisibility(View.GONE);
        if (size > 2) {
            holder.binding.item3.setVm(page.getCtrlBeans().get(2));
            loadImage(holder.binding.item3.imgIv, holder.binding.item3.stateSc, page.getCtrlBeans().get(2), 2, position);
        } else  holder.binding.item3.bgLy.setVisibility(View.GONE);
        if (size > 3) {
            holder.binding.item4.setVm(page.getCtrlBeans().get(3));
            loadImage(holder.binding.item4.imgIv, holder.binding.item4.stateSc, page.getCtrlBeans().get(3), 3, position);
        } else  holder.binding.item4.bgLy.setVisibility(View.GONE);
    }

    void loadImage(ImageView img, SwitchCompat switchCompat, OneCtrlBean ctrlBean, Integer index, int position) {
        OneCtrlPage page = mData.get(position);
        if (ctrlBean != null) {
            if (ctrlBean.getImgType() == 1) {

            } else if (ctrlBean.getImgType() == 2 && ctrlBean.getImg() != null) {
                Glide.with(img)
                        .load(ctrlBean.getImg())
//                        .error(R.mipmap.logo_move)
                        .into(img);
            } else if (ctrlBean.getImg() != null && !ctrlBean.getImg().isEmpty()) {
                try {
                    img.setImageResource(Integer.parseInt(ctrlBean.getImg()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isPressed()) {
                    BYTipDialog.Companion.showInfoTip(
                            activity,
                            b,
                            page.getCtrlBeans().get(index).getName(),
                            b ? activity.getResources().getString(R.string.dialog_tip_by_item_bg_open_title)
                                    : activity.getResources().getString(R.string.dialog_tip_by_item_close_title),
                            activity.getResources().getString(R.string.text_cancel),
                            activity.getResources().getString(R.string.text_sure),
                            new Function0<Boolean>() {
                                @Override
                                public Boolean invoke() {
                                    OneCtrlBean bean = page.getCtrlBeans().get(index);
                                    page.getCtrlBeans().get(index).getSwitchObs().set(b);
                                    // 触发接口
                                    iOneItem.onCheckedChanged(bean, b);
                                    return true;
                                }
                            },
                            new Function0<Boolean>() {
                                @Override
                                public Boolean invoke() {
                                    switchCompat.setChecked(!b);
                                    return true;
                                }
                            }
                    );
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //        TextView myTextView;
//        RelativeLayout relativeLayout;
        ItemOneCtrlBinding binding;

        ViewHolder(View itemView) {
            super(itemView);
            binding = (ItemOneCtrlBinding) DataBindingUtil.bind(itemView);
//            myTextView = itemView.findViewById(R.id.tvTitle);
//            relativeLayout = itemView.findViewById(R.id.container);

        }
    }

    public void notifyXY(int x, int y, OneCtrlPage bean) {
        mData.set(x, bean);
        notifyItemChanged(x);
    }
}
