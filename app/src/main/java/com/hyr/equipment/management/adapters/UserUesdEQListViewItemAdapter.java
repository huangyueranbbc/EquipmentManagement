package com.hyr.equipment.management.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hyr.equipment.management.R;
import com.hyr.equipment.management.activity.LoginActivity;
import com.hyr.equipment.management.activity.UserInfoActivity;
import com.hyr.equipment.management.domain.LMSResult;
import com.hyr.equipment.management.domain.TbEqUserInfo;
import com.hyr.equipment.management.domain.TbUserExt;
import com.hyr.equipment.management.domain.UserInfoVo;
import com.hyr.equipment.management.global.GlobalValue;
import com.hyr.equipment.management.utils.CacheUtils;
import com.hyr.equipment.management.utils.StringUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.security.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cn.refactor.lib.colordialog.ColorDialog;
import cn.refactor.lib.colordialog.PromptDialog;

//创建自定义adapter
public class UserUesdEQListViewItemAdapter extends BaseAdapter {

    private static final String TAG = "UUU";

    private boolean isLoading2 = false; // 防止重复登录
    private boolean isLoading = false; //防止重复请求 false==不在请求中

    private LayoutInflater mlayoutInflater;
    private List<HashMap<String, String>> mData;  //填充的数据 pojo
    UserInfoActivity _context;

    public UserUesdEQListViewItemAdapter(Context context, List<HashMap<String, String>> data) {
        this._context = (UserInfoActivity) context;
        mlayoutInflater = LayoutInflater.from(context);  //动态布局映射
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = mlayoutInflater.inflate(R.layout.eq_use_list_item, null);  //  根据布局文件实例化view
        TextView tv1 = (TextView) convertView.findViewById(R.id.textView1);
        tv1.setText(mData.get(position).get("eq_name").toString());
        TextView tv2 = (TextView) convertView.findViewById(R.id.textView2);
        tv2.setText(mData.get(position).get("eq_starttime").toString());
        TextView tv3 = (TextView) convertView.findViewById(R.id.textView3);
        tv3.setText(mData.get(position).get("eq_status").toString());

        Button btnStopUse = (Button) convertView.findViewById(R.id.btn_stopUse); // 停止使用按钮
        btnStopUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String equipmentRecordId = mData.get(position).get("record_id"); // 设备记录ID

                Log.i(TAG, "equipmentRecordId: " + equipmentRecordId);

                // TODO 提示
                ColorDialog dialog = new ColorDialog(_context);
                dialog.setAnimationEnable(true);
                dialog.setTitle("结束设备");
                dialog.setContentImage(_context.getResources().getDrawable(R.drawable.sample_img));
                dialog.setContentText("确认结束使用该设备吗?");
                dialog.setCancelable(false); // 取消周围点击事件
                dialog.setPositiveListener("确定", new ColorDialog.OnPositiveListener() {
                    @Override
                    public void onClick(final ColorDialog colorDialog) {

                        // 判断是否登录
                        final Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                        String userExtJson = CacheUtils.getCacheNotiming(GlobalValue.TBUSEREXTINFO);

                        if (!isLoading2) {
                            if (StringUtils.isEmpty(userExtJson)) { // 没有登录 跳转到登录页面
                                _context.startActivity(new Intent(_context, LoginActivity.class));
                            } else { // 如果有登录信息 访问网络进行使用设备
                                TbUserExt userExt = gson.fromJson(userExtJson, new TypeToken<TbUserExt>() {
                                }.getType());

                                final String url = GlobalValue.BASE_URL + "/equipment/cancel";
                                RequestParams params = new RequestParams();
                                params.addQueryStringParameter("username", String.valueOf(userExt.getUser().getUserStudentNo()));
                                params.addQueryStringParameter("login_token", String.valueOf(userExt.getUser_login_token()));
                                params.addQueryStringParameter("recordId", equipmentRecordId);
                                params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交

                                // TODO 登录逻辑
                                HttpUtils http = new HttpUtils();
                                http.send(HttpRequest.HttpMethod.GET,
                                        url, params,
                                        new RequestCallBack<String>() {

                                            @Override
                                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                                // 使用成功
                                                LMSResult result = gson.fromJson(responseInfo.result, new TypeToken<LMSResult>() {
                                                }.getType());
                                                if (result.getStatus() == 200) {
                                                    // 设备使用取消成功
                                                    // 重新获取网络数据 更新ListView
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
                                                                    private ProgressDialog dialog = new ProgressDialog(_context);

                                                                    @Override
                                                                    public void onSuccess(ResponseInfo<String> responseInfo) {
                                                                        // 得到图书馆list集合
                                                                        final UserInfoVo result = gson.fromJson(responseInfo.result, new TypeToken<UserInfoVo>() {
                                                                        }.getType());
                                                                        Log.i(TAG, "onSuccess: " + gson.toString());

                                                                        if (result != null) {// 获取成功
                                                                            isLoading = false;

                                                                            PromptDialog promptDialog = new PromptDialog(_context)
                                                                                    .setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                                                                                    .setAnimationEnable(true)
                                                                                    .setTitleText("操作成功")
                                                                                    .setContentText("停止使用成功!")
                                                                                    .setPositiveListener("确认", new PromptDialog.OnPositiveListener() {
                                                                                        @Override
                                                                                        public void onClick(PromptDialog _dialog) {
                                                                                            _dialog.dismiss();
                                                                                            Intent intent = new Intent(_context, UserInfoActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                            intent.putExtra("userInfo", result);
                                                                                            _context.startActivity(intent);
                                                                                            _context.finish();
                                                                                        }
                                                                                    });

                                                                            promptDialog.setCancelable(false);
                                                                            dialog.dismiss();
                                                                            colorDialog.dismiss();
                                                                            promptDialog.show();


                                                                        } else {
                                                                            dialog.dismiss();
                                                                            MyDynamicToast.errorMessage(_context, "服务错误!");
                                                                        }

                                                                    }

                                                                    @Override
                                                                    public void onFailure(HttpException e, String s) {
                                                                        MyDynamicToast.errorMessage(_context, "数据加载失败,请检查网络后重试!");
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

                                                } else {
                                                    // 设备使用取消失败
                                                    MyDynamicToast.errorMessage(_context, "操作失败,请稍后重试!");
                                                }
                                            }

                                            @Override
                                            public void onFailure(HttpException e, String s) {
                                                MyDynamicToast.errorMessage(_context, "操作失败,请检查网络后重试!");
                                                isLoading2 = false;
                                            }
                                        });

                            }
                        }


                    }
                }).setNegativeListener("取消", new ColorDialog.OnNegativeListener() {
                    @Override
                    public void onClick(ColorDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();

            }
        });

        return convertView;
    }


}