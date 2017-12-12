package com.hyr.equipment.management.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.gigamole.infinitecycleviewpager.VerticalInfiniteCycleViewPager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hyr.equipment.management.R;
import com.hyr.equipment.management.activity.EquipmentRecordActivity;
import com.hyr.equipment.management.activity.LoginActivity;
import com.hyr.equipment.management.activity.MainActivity;
import com.hyr.equipment.management.activity.NoticeActivity;
import com.hyr.equipment.management.activity.SecondActivity;
import com.hyr.equipment.management.activity.TestActivity;
import com.hyr.equipment.management.activity.UpdatePwdActivity;
import com.hyr.equipment.management.activity.UserInfoActivity;
import com.hyr.equipment.management.domain.LMSResult;
import com.hyr.equipment.management.domain.TbEqUserEquipmentRecord;
import com.hyr.equipment.management.domain.TbEqUserInfo;
import com.hyr.equipment.management.domain.TbUserExt;
import com.hyr.equipment.management.domain.UserInfoVo;
import com.hyr.equipment.management.global.GlobalValue;
import com.hyr.equipment.management.utils.CacheUtils;
import com.hyr.equipment.management.utils.StringUtils;
import com.hyr.equipment.management.utils.UIUtils;
import com.hyr.equipment.management.utils.Utils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Random;

import cn.refactor.lib.colordialog.ColorDialog;


/**
 * Created by GIGAMOLE on 7/27/16.
 */
public class HorizontalPagerAdapter extends PagerAdapter {

    private MainActivity mainActivity = null;

    private static final String TAG = "HorizontalPagerAdapter";

    private boolean isLoading = false; //防止重复请求 false==不在请求中
    private boolean isLoading2 = false; //防止重复请求 false==不在请求中

    // TODO 界面图片和名称设置
    private final Utils.LibraryObject[] LIBRARIES = new Utils.LibraryObject[]{
            new Utils.LibraryObject(
                    R.drawable.ic_userinfo,
                    "用户信息"
            ),
            new Utils.LibraryObject(
                    R.drawable.ic_erweima,
                    "扫码使用"
            ),
            new Utils.LibraryObject(
                    R.drawable.ic_usereqrecords,
                    "用户使用记录"
            ),
            new Utils.LibraryObject(
                    R.drawable.ic_warn,
                    "注意事项"
            ),
            new Utils.LibraryObject(
                    R.drawable.ic_updatepwd,
                    "修改密码"
            ),
            new Utils.LibraryObject(
                    R.drawable.ic_logout,
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
        this.mainActivity = mainActivity;
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
            switch (position) {
                case 0: // 查看用户基本信息
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();

                            if (!isLoading) {
                                isLoading = true;
                                // 请求网络数据 获取用户信息和用户设备使用记录
                                final String url = GlobalValue.BASE_URL + "/equipment/userinfo";
                                RequestParams params = new RequestParams();
                                String userLoginInfo = CacheUtils.getCacheNotiming(GlobalValue.LOGININFO);
                                TbEqUserInfo user = gson.fromJson(userLoginInfo, TbEqUserInfo.class);
                                params.addQueryStringParameter("userId", user.getUserId().toString());
                                params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交

                                // 获取网络数据
                                HttpUtils http = new HttpUtils();
                                http.send(HttpRequest.HttpMethod.GET,
                                        url, params,
                                        new RequestCallBack<String>() {
                                            private ProgressDialog dialog = new ProgressDialog(mContext);

                                            @Override
                                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                                // 得到图书馆list集合
                                                UserInfoVo result = gson.fromJson(responseInfo.result, new TypeToken<UserInfoVo>() {
                                                }.getType());
                                                Log.i(TAG, "onSuccess: " + gson.toString());

                                                if (result != null) {// 获取成功
                                                    // 存入缓存
                                                    Intent intent = new Intent(mainActivity, UserInfoActivity.class);
                                                    intent.putExtra("userInfo", result);

                                                    dialog.dismiss();
                                                    mainActivity.startActivity(intent);
                                                    isLoading = false;
                                                }

                                            }

                                            @Override
                                            public void onFailure(HttpException e, String s) {
                                                MyDynamicToast.errorMessage(UIUtils.getContext(), "数据加载失败,请重试!");
                                                dialog.dismiss();
                                                isLoading = false;
                                            }

                                            @Override
                                            public void onStart() {
                                                super.onStart();
                                            }

                                            @Override
                                            public void onLoading(long total, long current, boolean isUploading) {
                                                super.onLoading(total, current, isUploading);
                                                dialog.setTitle("正在加载中");
                                                dialog.show();
                                            }
                                        });

                            }

                        }
                    });
                    break;
                case 1: // 扫码使用
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mainActivity, SecondActivity.class);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 打开新的TASK
                            /**
                             * 扫描跳转Activity RequestCode
                             */
                            final int REQUEST_CODE = 111;

                            mainActivity.startActivityForResult(intent, REQUEST_CODE);
                        }
                    });
                    break;
                case 2: // 查看使用记录
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i(TAG, "查看使用记录: " + v + position);

                            final Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();

                            if (!isLoading2) {
                                isLoading2 = true;
                                // 请求网络数据 获取用户信息和用户设备使用记录
                                final String url = GlobalValue.BASE_URL + "/equipment/finduserrecords";
                                RequestParams params = new RequestParams();
                                String userLoginInfo = CacheUtils.getCacheNotiming(GlobalValue.LOGININFO);
                                if (StringUtils.isEmpty(userLoginInfo)) { // 如果未登录
                                    // 跳转到登录界面 并提示用户未登录
                                    MyDynamicToast.errorMessage(UIUtils.getContext(), "未检测到登录信息,请先登录!");
                                    Intent intent = new Intent(mainActivity, LoginActivity.class);
                                    mainActivity.startActivity(intent);
                                    mainActivity.finish();
                                }

                                TbEqUserInfo user = gson.fromJson(userLoginInfo, TbEqUserInfo.class);
                                if (null == user) {
                                    // 跳转到登录界面 并提示用户未登录
                                    MyDynamicToast.errorMessage(UIUtils.getContext(), "未检测到登录信息,请先登录!");
                                    Intent intent = new Intent(mainActivity, LoginActivity.class);
                                    mainActivity.startActivity(intent);
                                    mainActivity.finish();
                                }

                                // 用户已经登录
                                params.addQueryStringParameter("userId", user.getUserId().toString());
                                params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交

                                // 获取网络数据
                                HttpUtils http = new HttpUtils();
                                http.send(HttpRequest.HttpMethod.GET,
                                        url, params,
                                        new RequestCallBack<String>() {
                                            private ProgressDialog dialog = new ProgressDialog(mContext);

                                            @Override
                                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                                // 得到用户使用记录信息
                                                ArrayList<TbEqUserEquipmentRecord> result = gson.fromJson(responseInfo.result, new TypeToken<ArrayList<TbEqUserEquipmentRecord>>() {
                                                }.getType());

                                                if (result != null) {// 获取成功
                                                    // 存入缓存
                                                    Intent intent = new Intent(mainActivity, EquipmentRecordActivity.class);
                                                    intent.putExtra("recordInfos", result);
                                                    dialog.dismiss();
                                                    mainActivity.startActivity(intent);
                                                    isLoading2 = false;
                                                }

                                            }

                                            @Override
                                            public void onFailure(HttpException e, String s) {
                                                MyDynamicToast.errorMessage(UIUtils.getContext(), "数据加载失败!请重试!");
                                                dialog.dismiss();
                                                isLoading2 = false;
                                            }

                                            @Override
                                            public void onStart() {
                                                super.onStart();
                                            }

                                            @Override
                                            public void onLoading(long total, long current, boolean isUploading) {
                                                super.onLoading(total, current, isUploading);
                                                dialog.setTitle("正在加载中");
                                                dialog.show();
                                            }
                                        });

                            }

                        }
                    });
                    break;
                case 3: // 使用事项
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mainActivity, NoticeActivity.class);
                            mainActivity.startActivity(intent);
                        }
                    });
                    break;
                case 4: // 修改密码
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mainActivity, UpdatePwdActivity.class);
                            mainActivity.startActivity(intent);
                        }
                    });
                    break;
                case 5: // 退出登录
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // TODO
                            ColorDialog dialog = new ColorDialog(mainActivity);
                            dialog.setCancelable(false); // 取消周围点击事件
                            dialog.setTitle("退出登录");
                            dialog.setAnimationEnable(true);
                            dialog.setContentText("确认退出该账号?");
                            dialog.setContentImage(mainActivity.getResources().getDrawable(R.drawable.sample_img));
                            dialog.setPositiveListener("确认", new ColorDialog.OnPositiveListener() {
                                @Override
                                public void onClick(final ColorDialog dialog) {

                                    final Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                    String userExtJson = CacheUtils.getCacheNotiming(GlobalValue.TBUSEREXTINFO);
                                    if (StringUtils.isEmpty(userExtJson)) { // 没有登录记录 表示用户已经退出 直接跳转到登录页面
                                        MyDynamicToast.successMessage(UIUtils.getContext(), "退出成功!");
                                        mainActivity.startActivity(new Intent(mainActivity, LoginActivity.class));
                                        mainActivity.finish();
                                    } else { // 有记录 则进行清除登录信息
                                        TbUserExt userExt = gson.fromJson(userExtJson, new TypeToken<TbUserExt>() {
                                        }.getType());
                                        final String url = GlobalValue.BASE_URL + "/user/logout";
                                        RequestParams params = new RequestParams();
                                        params.addQueryStringParameter("username", String.valueOf(userExt.getUser().getUserStudentNo())); // 学号
                                        params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交

                                        // 访问网络 清除服务端登录记录
                                        HttpUtils http = new HttpUtils();
                                        http.send(HttpRequest.HttpMethod.GET,
                                                url, params,
                                                new RequestCallBack<String>() {

                                                    @Override
                                                    public void onSuccess(ResponseInfo<String> responseInfo) {
                                                        // 得到图书馆list集合
                                                        LMSResult result = gson.fromJson(responseInfo.result, new TypeToken<LMSResult>() {
                                                        }.getType());
                                                        if (result.getStatus() == 200) { // 清除成功
                                                            CacheUtils.setCacheNotiming(GlobalValue.TBUSEREXTINFO, "");// 删除客户端缓存记录
                                                            MyDynamicToast.successMessage(UIUtils.getContext(), "退出成功!");
                                                            //　返回到登录界面
                                                            mainActivity.startActivity(new Intent(mainActivity, LoginActivity.class));
                                                            mainActivity.finish();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(HttpException e, String s) {
                                                        MyDynamicToast.errorMessage(UIUtils.getContext(), "退出登录失败,请检查网络后重试!");
                                                    }
                                                });

                                    }

                                }
                            }).setNegativeListener(mainActivity.getString(R.string.cancel), new ColorDialog.OnNegativeListener() {
                                @Override
                                public void onClick(ColorDialog dialog) {
                                    dialog.dismiss();
                                }
                            }).show();

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
