package com.hyr.equipment.management.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hyr.equipment.management.R;
import com.hyr.equipment.management.adapters.TimestampTypeAdapter;
import com.hyr.equipment.management.domain.TbEqUserInfo;
import com.hyr.equipment.management.domain.TbUserExt;
import com.hyr.equipment.management.domain.TbUserInfo;
import com.hyr.equipment.management.global.GlobalValue;
import com.hyr.equipment.management.utils.CacheUtils;
import com.hyr.equipment.management.utils.StringUtils;
import com.hyr.equipment.management.utils.UIUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.security.Timestamp;
import java.util.Random;
import java.util.UUID;

public class LoginActivity extends BaseActivity {

    private boolean isLoading = false; // 防止重复登录
    private EditText _username;
    private EditText _password;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        Button b = (Button) findViewById(R.id.subBtn);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isLoading) {
                    if (StringUtils.isEmpty(_username.getText().toString())) {
                        MyDynamicToast.errorMessage(UIUtils.getContext(), "请输入用户名!");
                        isLoading = false;
                    } else if (StringUtils.isEmpty(_password.getText().toString())) {
                        MyDynamicToast.errorMessage(UIUtils.getContext(), "请输入密码!");
                        isLoading = false;
                    } else {// 进行登录

                        final String url = GlobalValue.BASE_URL + "/user/login";
                        //String localMacAddress = new PhoneDeviceUtil().getLocalMacAddress(); // 用户手机Mac唯一地址
                        //String login_token = localMacAddress + System.currentTimeMillis(); // 用户登录token
                        String login_token = UUID.randomUUID().toString();
                        RequestParams params = new RequestParams();
                        String uName = _username.getText().toString();
                        String uPassword = _password.getText().toString();
                        params.addQueryStringParameter("username", uName);
                        params.addQueryStringParameter("password", uPassword);
                        params.addQueryStringParameter("login_token", login_token);
                        params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交

                        // TODO 登录逻辑
                        HttpUtils http = new HttpUtils();
                        http.send(HttpRequest.HttpMethod.GET,
                                url, params,
                                new RequestCallBack<String>() {

                                    @Override
                                    public void onSuccess(ResponseInfo<String> responseInfo) {
                                        // 得到图书馆list集合
                                        Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                        TbUserExt userExt = gson.fromJson(responseInfo.result, new TypeToken<TbUserExt>() {
                                        }.getType()); // 登录返回结果
                                        if (userExt == null) { // 登录失败
                                            MyDynamicToast.errorMessage(UIUtils.getContext(), "用户名或密码错误,请重试!");
                                            isLoading = false;
                                        } else { // 登录成功
                                            // 保存用户登录信息
                                            TbEqUserInfo user = userExt.getUser();
                                            String userInfo = gson.toJson(user); // 将用户登录信息存入缓存
                                            CacheUtils.setCacheNotiming(GlobalValue.LOGININFO, userInfo);

                                            // TODO  保存用户相关信息  推送消息 暂未实现
                                            TbUserInfo tbUserInfo = new TbUserInfo();
                                            tbUserInfo.setUserId(user.getUserId());
                                            tbUserInfo.setToken(user.getUserName()); // 推送消息别名
                                            String tbUserInfoJson = gson.toJson(tbUserInfo); // 将用户登录信息存入缓存
                                            CacheUtils.setCacheNotiming(GlobalValue.TBUSERINFO, tbUserInfoJson);

                                            //Log.i(TAG, "onSuccess: " + "开始缓存数据");
                                            // 保存用户token
                                            String userExtJson = gson.toJson(userExt); // 将用户登录信息存入缓存
                                            //Log.i(TAG, "tonken: " + userExtJson);
                                            CacheUtils.setCacheNotiming(GlobalValue.TBUSEREXTINFO, userExtJson);
                                            //Log.i(TAG, "存入Token成功!!!! ");

                                            // 跳转到主页面
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            isLoading = false;
                                            startActivity(intent);
                                            LoginActivity.this.finish(); // 避免回到登录页面
                                        }
                                    }

                                    @Override
                                    public void onFailure(HttpException e, String s) {
                                        MyDynamicToast.errorMessage(UIUtils.getContext(), "登录失败,请检查网络后重试!");
                                        isLoading = false;
                                    }
                                });

                    }

                }


            }
        });
    }

    /**
     * 初始化界面
     */
    private void initView() {
        _username = (EditText) findViewById(R.id.tv_username);
        _password = (EditText) findViewById(R.id.tv_password);
    }
}
