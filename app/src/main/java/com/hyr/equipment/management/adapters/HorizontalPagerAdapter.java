package com.hyr.equipment.management.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gigamole.infinitecycleviewpager.VerticalInfiniteCycleViewPager;
import com.gigamole.sample.R;
import com.hyr.equipment.management.activity.MainActivity;
import com.hyr.equipment.management.activity.SecondActivity;
import com.hyr.equipment.management.utils.UIUtils;
import com.hyr.equipment.management.utils.Utils;


/**
 * Created by GIGAMOLE on 7/27/16.
 */
public class HorizontalPagerAdapter extends PagerAdapter {

    private MainActivity mainActivity=null;

    private static final String TAG = "HorizontalPagerAdapter";

    // TODO 界面图片和名称设置
    private final Utils.LibraryObject[] LIBRARIES = new Utils.LibraryObject[]{
            new Utils.LibraryObject(
                    R.drawable.ic_strategy,
                    "用户信息"
            ),
            new Utils.LibraryObject(
                    R.drawable.ic_design,
                    "扫码使用"
            ),
            new Utils.LibraryObject(
                    R.drawable.ic_development,
                    "用户使用记录"
            ),
            new Utils.LibraryObject(
                    R.drawable.ic_internet,
                    "注意事项"
            ),
            new Utils.LibraryObject(
                    R.drawable.ic_design,
                    "退出登录"
            ),
    };

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private boolean mIsTwoWay;

    public HorizontalPagerAdapter(final Context context, final boolean isTwoWay, MainActivity mainActivity) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mIsTwoWay = isTwoWay;
        this.mainActivity=mainActivity;
    }

    @Override
    public int getCount() {
        return mIsTwoWay ? 6 : LIBRARIES.length;
    }

    @Override
    public int getItemPosition(final Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View view;
        if (mIsTwoWay) {
            view = mLayoutInflater.inflate(R.layout.two_way_item, container, false);

            final VerticalInfiniteCycleViewPager verticalInfiniteCycleViewPager =
                    (VerticalInfiniteCycleViewPager) view.findViewById(R.id.vicvp);
            verticalInfiniteCycleViewPager.setAdapter(
                    new VerticalPagerAdapter(mContext)
            );
            verticalInfiniteCycleViewPager.setCurrentItem(position);
        } else {
            view = mLayoutInflater.inflate(R.layout.item, container, false);
            // TODO 处理点击事件 0~4
            switch (position){
                case 0: // 查看用户基本信息
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i(TAG, "查看用户基本信息: "+v+position);
                            Toast.makeText(UIUtils.getContext(),"查看用户基本信息",Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case 1: // 扫码使用
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i(TAG, "扫码使用: "+v+position);
                            Toast.makeText(UIUtils.getContext(),"扫码使用",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UIUtils.getContext(), SecondActivity.class);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 打开新的TASK
                            /**
                             * 扫描跳转Activity RequestCode
                             */
                            final int REQUEST_CODE = 111;

                            mainActivity.startActivityForResult(intent,REQUEST_CODE);
                        }
                    });
                    break;
                case 2: // 查看使用记录
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i(TAG, "查看使用记录: "+v+position);
                            Toast.makeText(UIUtils.getContext(),"查看使用记录",Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case 3: // 使用事项
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i(TAG, "使用事项: "+v+position);
                            Toast.makeText(UIUtils.getContext(),"使用事项",Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case 4: // 退出登录
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i(TAG, "退出登录: "+v+position);
                            Toast.makeText(UIUtils.getContext(),"退出登录",Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
            }
            Utils.setupItem(view, LIBRARIES[position]);
        }

        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }
}
